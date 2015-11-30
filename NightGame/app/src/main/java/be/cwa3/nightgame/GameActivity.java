package be.cwa3.nightgame;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

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

import java.util.ArrayList;
import java.util.List;

import be.cwa3.nightgame.Data.AccelerometerData;
import be.cwa3.nightgame.Data.Empty;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.GameGetDataRequestData;
import be.cwa3.nightgame.Data.GamePlayerData;
import be.cwa3.nightgame.Data.GameSendDataRequestData;
import be.cwa3.nightgame.Data.LocationData;
import be.cwa3.nightgame.Adapters.MissionsAdapter;
import be.cwa3.nightgame.Data.LobbiesData;
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
    ListView listview;
    private boolean othersShouldBeInvisibile = false;
    private boolean mapHasBeenReady = false;
    private AccelerometerData accelerometerData;
    private String gameId;
    private String userTeam;
    LobbiesData gameData;

    private Location location;

    private Handler customHandler = new Handler();
    private int delayTimeRequestData = 2000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameId = new SettingsUtil(this).getString(SharedPreferencesKeys.GameIDString);
        if (gameId.equals("")){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        makeCall(new GameGetDataRequestData(gameId));
        location = getLocation();
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listview = (ListView) findViewById(R.id.missions);
        setSensorDataInterface(new SensorDataInterface() {
            @Override
            public void accelerometerChanged(float x, float y, float z) {
                accelerometerData = new AccelerometerData();
                accelerometerData.X = x;
                accelerometerData.Y = y;
                accelerometerData.Z = z;
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
            }

            @Override
            public void locationChanged(Location newLocation) {
                boolean wasNull = (location == null);
                location = newLocation;
                mapFragment.getMapAsync(GameActivity.this);
                if (wasNull) {
                    customHandler.postDelayed(sendData, 100);
                }
            }

            @Override
            public void proximityChanged(float proximity) {
                super.proximityChanged(proximity);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap map) {

        List<LocationData> locations = getPlayerLocations();

        if (locations.size() > 0) {
            map.setMyLocationEnabled(true);
            if (!mapHasBeenReady) {
                LatLng latLng = new LatLng(locations.get(0).Latitude, locations.get(0).Longitude);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                mapHasBeenReady = true;
            } else
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(map.getCameraPosition().target, map.getCameraPosition().zoom));
        }
        map.clear();
        map.setMyLocationEnabled(false);

        for (LocationData loc : locations) {
            BitmapDescriptor bitmapDescriptor;
            LatLng latLng = new LatLng(loc.Latitude, loc.Longitude);
            bitmapDescriptor = getPlayerMapIcon(loc.Team);

            if (loc.Team.equals(userTeam) || othersShouldBeInvisibile)
                map.addMarker(new MarkerOptions()
                        .title(loc.PlayerName)
                        .snippet(String.format("%s (%s minuten geleden)", loc.Team, (DateTime.now().getMillis() - loc.CreatedOn.getMillis())/(1000*60)))
                                .icon(bitmapDescriptor)
                                .position(latLng));

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
                if (mapFragment.getView().getVisibility() == View.VISIBLE) {
                    makeCall(new GameGetDataRequestData(gameId));
                    mapFragment.getView().setVisibility(View.GONE);
                    listview.setVisibility(View.VISIBLE);
                } else {
                    mapFragment.getView().setVisibility(View.VISIBLE);
                    listview.setVisibility(View.GONE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Runnable sendData = new Runnable() {
        @Override
        public void run() {
            if(location != null){
            GameSendDataRequestData data = new GameSendDataRequestData();
            data.GameId = gameId;
            LocationData locationData = new LocationData();
            locationData.Latitude = location.getLatitude();
            locationData.Longitude = location.getLongitude();
            locationData.Altitude = location.getAltitude();
            data.Location = locationData;
            data.Accelerometer = accelerometerData;
            data.CompletedMissions = new ArrayList<>();
            data.Died = false;
            makeGameDataCall(data);
            }
        }
    };

    private void makeGameDataCall(GameSendDataRequestData data){
        Call<ReturnData<Empty>> call = new ApiUtil().getApiInterface(this).sendGameDataRequest(data);
        RequestUtil<Empty> requestUtil = new RequestUtil<>(this, null, call);
        requestUtil.makeRequest(new RequestInterface<Empty>() {

            @Override
            public void onSucces(Empty body) {
                customHandler.postDelayed(sendData, delayTimeRequestData);
            }

            @Override
            public void onError(ErrorData error) {
                customHandler.postDelayed(sendData, delayTimeRequestData);
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Context context, View view, Throwable t) {
                customHandler.postDelayed(sendData, delayTimeRequestData);
                super.onFailure(context,view, t);
            }
        });
    }

    private void makeCall(GameGetDataRequestData data){
        Call<ReturnData<LobbiesData>> call = new ApiUtil().getApiInterface(this).getLobbyData(data);
        RequestUtil<LobbiesData> requestUtil = new RequestUtil<>(this,null, call);
        requestUtil.makeRequest(new RequestInterface<LobbiesData>() {
            @Override
            public void onSucces(LobbiesData body) {
                Log.d("test", new Gson().toJson(body.Missions));
                listview.setAdapter(new MissionsAdapter(GameActivity.this, body.Missions));
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

    private List<LocationData> getPlayerLocations(){
        List<LocationData> locationDatas= new ArrayList<>();
        if(gameData != null) {
            for (GamePlayerData playerData : gameData.Players) {
                if (playerData.IsRequester) {
                    LocationData myLocation = new LocationData();
                    if(location != null) {
                        myLocation.Altitude = location.getAltitude();
                        myLocation.Latitude = location.getLatitude();
                        myLocation.Longitude = location.getLongitude();
                        myLocation.CreatedOn = DateTime.now();
                        myLocation.PlayerName = playerData.Name;
                        userTeam = playerData.Team;
                        myLocation.Team = playerData.Team;

                        locationDatas.add(myLocation);
                    }
                } else {
                    LocationData loc = playerData.LatestLocation;
                    if(loc != null) {
                        loc.Team = playerData.Team;
                        loc.PlayerName = playerData.Name;
                        locationDatas.add(loc);
                    }
                }
            }
        }
        return locationDatas;
    }

    public BitmapDescriptor getPlayerMapIcon(String team) {
        if("Defender".equals(team)){
            return BitmapDescriptorFactory.fromResource(R.drawable.defender);
        }
        return BitmapDescriptorFactory.fromResource(R.drawable.attacker);
    }
}
