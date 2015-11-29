package be.cwa3.nightgame;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Game;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.cwa3.nightgame.Adapters.FriendImageAdapter;
import be.cwa3.nightgame.Adapters.MissionsAdapter;
import be.cwa3.nightgame.Data.ChallengeRequestData;
import be.cwa3.nightgame.Data.ChallengeReturnData;
import be.cwa3.nightgame.Data.CreateLobbyReturnData;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.JoinLobbyRequestData;
import be.cwa3.nightgame.Data.LobbiesData;
import be.cwa3.nightgame.Data.LocationData;
import be.cwa3.nightgame.Data.MissionData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Utils.ApiUtil;
import be.cwa3.nightgame.Utils.ErrorUtil;
import be.cwa3.nightgame.Utils.RequestInterface;
import be.cwa3.nightgame.Utils.RequestUtil;
import be.cwa3.nightgame.Utils.SensorDataActivity;
import be.cwa3.nightgame.Utils.SensorDataInterface;
import be.cwa3.nightgame.Utils.SettingsUtil;
import be.cwa3.nightgame.Utils.SharedPreferencesKeys;
import retrofit.Call;

/**
 * Created by jesse on 15/10/2015.
 */
public class GameActivity extends SensorDataActivity implements OnMapReadyCallback {

    MapFragment mapFragment;
    LinearLayout game;
    ListView listview;
    private boolean othersShouldBeInvisibile = false;
    private boolean mapHasBeenReady = false;
    LobbiesData gameData;

    public double altitudeClimbed = 0;
    public double altitudeDescended = 0;
    private Location location;
    private Location oldLocation = location;
    public boolean assembled = Boolean.FALSE;
    public float collectedLight;
    public long timeOldLocation = oldLocation.getTime();
    public long timeNewLocation = location.getTime();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //TODO: CHECK IF THERE IS A GAMEID

        location = getLocation();
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listview = (ListView) findViewById(R.id.missions);
        game = (LinearLayout) findViewById(R.id.game);
        setSensorDataInterface(new SensorDataInterface() {
            @Override
            public void accelerometerChanged(float x, float y, float z) {
                super.accelerometerChanged(x, y, z);
            }

            @Override
            public void lightChanged(float sv) {
                boolean before = othersShouldBeInvisibile;
                if (sv < 90)
                    othersShouldBeInvisibile = true;
                else
                    othersShouldBeInvisibile = false;

                if (before != othersShouldBeInvisibile)
                    mapFragment.getMapAsync(GameActivity.this);
                checkLightCollected(sv,collectedLight);
            }

            @Override
            public void locationChanged(Location newLocation) {
                oldLocation = location; //is dit hoe je de oude locatie kan bijhouden?
                location = newLocation;

                mapFragment.getMapAsync(GameActivity.this);
                if (checkAltitudeChange(oldLocation, location, altitudeClimbed,altitudeDescended)<0){
                    altitudeDescended = -checkAltitudeChange(oldLocation, location, altitudeClimbed,altitudeDescended);
                }
                else {
                    altitudeClimbed = checkAltitudeChange(oldLocation, location, altitudeClimbed,altitudeDescended);
                }
                checkLocation(location);
                checkTeamAssembled(assembled);
                checkSpeed(location,oldLocation,timeNewLocation,timeOldLocation);

            }

            @Override
            public void proximityChanged(float proximity) {
                super.proximityChanged(proximity);
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap map) {
        List<LatLng> locations = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        List<String> content = new ArrayList<>();
        if (location != null) {
            locations.add(new LatLng(location.getLatitude(), location.getLongitude()));
            titles.add("Jesse");
            content.add("Verdediger");
        }

        if (locations.size() > 0) {
            map.setMyLocationEnabled(true);
            if (!mapHasBeenReady) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(locations.get(0), 13));
                mapHasBeenReady = true;
            } else
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(map.getCameraPosition().target, map.getCameraPosition().zoom));
        }
        map.clear();
        map.setMyLocationEnabled(false);
        int number = 0;
        for (LatLng loc : locations) {
            BitmapDescriptor bitmapDescriptor;

            if (number % 2 == 0) {
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.defender);
            } else
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.attacker);
            if (!(number % 2 == 1 && othersShouldBeInvisibile))
                map.addMarker(new MarkerOptions()
                        .title(titles.get(number))
                        .snippet(content.get(number))
                        .icon(bitmapDescriptor)
                        .position(loc));
            number += 1;
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.challenges:
                if (game.getVisibility() == View.VISIBLE) {

                    String gameId = new SettingsUtil(this).getString(SharedPreferencesKeys.GameIDString);
                    makeCall(new ChallengeRequestData(gameId));
                    game.setVisibility(View.GONE);
                    listview.setVisibility(View.VISIBLE);
                } else {
                    game.setVisibility(View.VISIBLE);
                    listview.setVisibility(View.GONE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



    private void makeCall(ChallengeRequestData data){
        Call<ReturnData<LobbiesData>> call = new ApiUtil().getApiInterface(this).getChallengeData(data);
        RequestUtil<LobbiesData> requestUtil = new RequestUtil<>(this, call);
        requestUtil.makeRequest(new RequestInterface<LobbiesData>() {
            @Override
            public void onSucces(LobbiesData body) {
                Log.d("test", new Gson().toJson(body.Missions));
                listview.setAdapter(new MissionsAdapter(getApplicationContext(),body.Missions));
                gameData = body;

            }

            @Override
            public void onError(ErrorData error) {
                if(error.Errors.contains(4) || error.Errors.contains(6)) {
                    new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.GameIDString, "");
                    Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                    startActivity(intent);
                    finish();
                }

                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();
            }
        });

    }


    public double checkAltitudeChange(Location oldLocation, Location location, double altitudeClimbed, double altitudeDescended){
        int type;
        MissionData mission;
        for (int i=0;i< body.Missions.size();i++) {

            mission = body.Missions[i];
            type = mission.id;
            if (type == 2) {
                if (this.oldLocation.getAltitude() < this.location.getAltitude()) {
                    this.altitudeClimbed = this.altitudeClimbed + this.location.getAltitude() - this.oldLocation.getAltitude();
                    if (this.altitudeClimbed > mission.HeightDifference) {
                        //delete de mission uit de lijst?
                        Toast.makeText(getApplicationContext(), "Mission completed!", Toast.LENGTH_LONG).show();
                    }
                    return this.altitudeClimbed;
                } else if (this.oldLocation.getAltitude() > this.location.getAltitude()) {
                    this.altitudeDescended = this.altitudeDescended + this.oldLocation.getAltitude() - this.location.getAltitude();
                    if (this.altitudeDescended > mission.HeightDifference) {
                        //delete de mission uit de lijst
                        Toast.makeText(getApplicationContext(), "Mission completed!", Toast.LENGTH_LONG).show();

                    }
                    return -this.altitudeDescended;
                }
            }
        }


    }
    public boolean checkLocation(Location location){ //is boolean ok?
        if (location.distanceTo(LocationData) < 50){
            //delete de missie
            Toast.makeText(getApplicationContext(), "Mission completed!", Toast.LENGTH_LONG).show();
        }
        return true;

    }
    public boolean checkTeamAssembled(Boolean assembled){ //location niet meegegeven, ok?
        //hoe moet je de locatie van anderen opvragen?
        assembled = Boolean.TRUE
        for alle spelers van jouw team {
            if (location.distanceTo(locatie teamspeler)>10){
                assembled = Boolean.FALSE;
            }

        }
        if (assembled = Boolean.TRUE){
            //delete de missie
            Toast.makeText(getApplicationContext(), "Mission completed!", Toast.LENGTH_LONG).show();}
        return Boolean.TRUE;
    }

    public void checkLightCollected(float sv, float collectedLight){
        collectedLight = collectedLight + sv; //is deze variabele nu veranderd?
        if (collectedLight > AmountOfLight){
            //delete de missie
            Toast.makeText(getApplicationContext(), "Mission completed!", Toast.LENGTH_LONG).show();
        }
    }

    public void checkSpeed(Location location,Location oldLocation,long timeNewLocation, long timeOldLocation){
        if (location.distanceTo(oldLocation)/(1000*(timeNewLocation-timeNewLocation))>SpeedValue){
            //delete de missie
            Toast.makeText(getApplicationContext(), "Mission completed!", Toast.LENGTH_LONG).show();
        }
    }



}