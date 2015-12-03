package be.cwa3.nightgame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import be.cwa3.nightgame.Data.AccelerometerData;
import be.cwa3.nightgame.Data.Empty;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.GameAttackData;
import be.cwa3.nightgame.Data.GameGetDataRequestData;
import be.cwa3.nightgame.Data.GamePlayerData;
import be.cwa3.nightgame.Data.GameSendDataRequestData;
import be.cwa3.nightgame.Data.LocationData;
import be.cwa3.nightgame.Adapters.MissionsAdapter;
import be.cwa3.nightgame.Data.LobbiesData;
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

    private MapFragment mapFragment;
    private ListView listview;
    private boolean othersShouldBeInvisibile = false;
    private boolean mapHasBeenReady = false;
    private AccelerometerData accelerometerData;
    private String gameId;
    private LobbiesData gameData;
    private double proximity;
    private float lightvalue;

    private Location location, oldLocation;
    private List<Integer> completedMissions = new ArrayList<>();

    private Handler customHandler = new Handler();
    private int delayTimeRequestData = 5000;
    private double altitudeClimbed = 0;
    private double altitudeDescended = 0;
    private float collectedLight = 0;

    private TextView livesTextView;
    private ImageView hasFlagImageView;
    private RelativeLayout gameContainer;
    private CoordinatorLayout coordinatorLayout;

    private FloatingActionsMenu floatingActionsMenuShoot;
    private List<FloatingActionButton> floatingActionButtonList;


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
        makeGetDataCall(new GameGetDataRequestData(gameId)); //Only needed one time, this data is also returned by sendData
        location = getLocation();
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listview = (ListView) findViewById(R.id.missions);
        livesTextView = (TextView) findViewById(R.id.amountOfLivesTextView);
        hasFlagImageView = (ImageView) findViewById(R.id.hasFlagImageView);
        gameContainer = (RelativeLayout) findViewById(R.id.GameRelativeLayout);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        floatingActionsMenuShoot = (FloatingActionsMenu) findViewById(R.id.floatingActionsMenuShoot);
        floatingActionButtonList = new ArrayList<>();
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
                lightvalue = sv;
                boolean before = othersShouldBeInvisibile;
                othersShouldBeInvisibile = (sv < 90 || sv > 1500 || proximity == 0); // >1500 means cheating
                Log.d("test", String.format("%f, %f", sv, proximity));

                if (before != othersShouldBeInvisibile)
                    mapFragment.getMapAsync(GameActivity.this);
                checkLightCollected();
            }

            @Override
            public void locationChanged(Location newLocation) {
                oldLocation = location;

                boolean wasNull = (location == null);
                location = newLocation;
                mapFragment.getMapAsync(GameActivity.this);

                checkAltitudeChange();
                checkLocation();
                checkTeamAssembled();
                checkSpeed();

                if (wasNull) {
                    customHandler.postDelayed(sendData, 100);
                }
            }

            @Override
            public void proximityChanged(float prox) {
                proximity = prox;
                boolean faking_light = othersShouldBeInvisibile;
                othersShouldBeInvisibile = (proximity == 0 || lightvalue < 90 || lightvalue > 1500); // 0 means cheating


                if (faking_light != othersShouldBeInvisibile)
                    mapFragment.getMapAsync(GameActivity.this);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customHandler.removeCallbacks(sendData); //stop Async threads
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
            bitmapDescriptor = getPlayerMapIcon(loc.Team, loc.HasFlag);

            if (loc.Team.equals(gameData.Player.Team) || loc.HasFlag || (!othersShouldBeInvisibile && "Attacker".equals(gameData.Player.Team) ))
                map.addMarker(new MarkerOptions()
                        .title(loc.PlayerName)
                        .snippet(String.format(getString(R.string.name_minutes_ago), loc.Team, (DateTime.now().getMillis() - loc.CreatedOn.getMillis())/(1000*60)))
                                .icon(bitmapDescriptor)
                                .position(latLng));

        }

        if(gameData != null) {
            if(location != null) {
                int type;
                MissionData mission;
                for (int i = 0; i < gameData.Missions.size(); i++) {
                    mission = gameData.Missions.get(i);
                    type = mission.Type;
                    if (!mission.IsFinished && type == 1) {
                        LatLng loc = new LatLng(mission.Location.Latitude, mission.Location.Longitude);
                        String goToLocation;
                        map.addMarker(new MarkerOptions()
                                .title("Challenge location").position(loc));
                    }
                }
            }
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(gameData.CenterLocation.Latitude, gameData.CenterLocation.Longitude))
                    .radius(gameData.CircleRadius); // In meters
            map.addCircle(circleOptions);
            if("Defender".equals(gameData.Player.Team)) {
                circleOptions = new CircleOptions()
                        .center(new LatLng(gameData.DefenderBase.Latitude, gameData.DefenderBase.Longitude))
                        .radius(gameData.CircleRadius / 10); // In meters
                map.addCircle(circleOptions);
            }
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
                if (gameContainer.getVisibility() == View.VISIBLE) {
                    makeGetDataCall(new GameGetDataRequestData(gameId));
                    gameContainer.setVisibility(View.GONE);
                    listview.setVisibility(View.VISIBLE);
                } else {
                    gameContainer.setVisibility(View.VISIBLE);
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
            data.CompletedMissions = completedMissions;
            data.Died = false;
            makeGameDataCall(data);
            }
        }
    };

    private void makeGameDataCall(GameSendDataRequestData data){
        Call<ReturnData<LobbiesData>> call = new ApiUtil().getApiInterface(this).sendGameDataRequest(data);
        RequestUtil<LobbiesData> requestUtil = new RequestUtil<>(this, null, call);
        requestUtil.makeRequest(new RequestInterface<LobbiesData>() {

            @Override
            public void onSucces(LobbiesData body) {
                completedMissions.clear();
                listview.setAdapter(new MissionsAdapter(GameActivity.this, body.Missions));
                gameData = body;
                if(gameData.Player.HasFlag){
                    hasFlagImageView.setVisibility(View.VISIBLE);
                }
                else{
                    hasFlagImageView.setVisibility(View.GONE);
                }
                livesTextView.setText(String.valueOf(gameData.Player.Lives));
                if("Defender".equals(gameData.Player.Team)) {
                    setAttackButtons();
                }
                else{
                    floatingActionsMenuShoot.setVisibility(View.GONE);
                }

                customHandler.postDelayed(sendData, delayTimeRequestData);
                if(gameData.WinningTeam != null || gameData.Player.Lives < 1){
                    customHandler.removeCallbacks(sendData); //stop Async threads
                    String snackbarText;
                    if(gameData.WinningTeam != null){
                        snackbarText = String.format(getString(R.string.game_end), gameData.WinningTeam);
                    }
                    else{
                        snackbarText = getString(R.string.dead);
                    }
                    Snackbar.make(coordinatorLayout, snackbarText, Snackbar.LENGTH_INDEFINITE)
                            .setAction("Close Game", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.GameIDString, "");
                                    Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setActionTextColor(Color.RED)
                            .show();

                }
            }

            @Override
            public void onError(ErrorData error) {
                customHandler.postDelayed(sendData, delayTimeRequestData);
                if (error.Errors.contains(4) || error.Errors.contains(6)) {
                    new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.GameIDString, "");
                    Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                    startActivity(intent);
                    finish();
                }
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Context context, View view, Throwable t) {
                customHandler.postDelayed(sendData, delayTimeRequestData);
                super.onFailure(context, view, t);
            }
        });
    }

    private void makeGetDataCall(GameGetDataRequestData data){
        Call<ReturnData<LobbiesData>> call = new ApiUtil().getApiInterface(this).getLobbyData(data);
        RequestUtil<LobbiesData> requestUtil = new RequestUtil<>(this,null, call);
        requestUtil.makeRequest(new RequestInterface<LobbiesData>() {
            @Override
            public void onSucces(LobbiesData body) {
                listview.setAdapter(new MissionsAdapter(GameActivity.this, body.Missions));
                gameData = body;
                if(gameData.Player.HasFlag){
                    hasFlagImageView.setVisibility(View.VISIBLE);
                }
                else{
                    hasFlagImageView.setVisibility(View.GONE);
                }
                livesTextView.setText(String.valueOf(gameData.Player.Lives));
                if("Defender".equals(gameData.Player.Team)) {
                    setAttackButtons();
                }
                else{
                    floatingActionsMenuShoot.setVisibility(View.GONE);
                }

            }

            @Override
            public void onError(ErrorData error) {
                if (error.Errors.contains(4) || error.Errors.contains(6)) {
                    new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.GameIDString, "");
                    Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                    startActivity(intent);
                    finish();
                }

                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setAttackButtons(){
        floatingActionsMenuShoot.setVisibility(View.VISIBLE);
        for(FloatingActionButton floatingActionButton : floatingActionButtonList){
            floatingActionsMenuShoot.removeButton(floatingActionButton);
        }
        floatingActionButtonList.clear();
        for(final GamePlayerData playerData : gameData.Players) {
            if("Attacker".equals(playerData.Team)) {
                final FloatingActionButton floatingActionButton = new FloatingActionButton(getApplicationContext());
                floatingActionButton.setTitle(playerData.Name);
                floatingActionButton.setBackgroundResource(R.drawable.gun);
                floatingActionButton.setSize(FloatingActionButton.SIZE_NORMAL);
                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GameAttackData data = new GameAttackData();
                        data.GameId = gameId;
                        data.AttackedUser = playerData.Name;
                        makeAttackCall(data);
                        floatingActionButton.setEnabled(false);
                        floatingActionButton.setBackgroundResource(R.drawable.bullet);
                        customHandler.postDelayed(enableButton(floatingActionButton), 5000);
                    }
                });
                floatingActionsMenuShoot.addButton(floatingActionButton);
                floatingActionButtonList.add(floatingActionButton);
            }
        }
    }

    public boolean checkAltitudeChange() {
        if (gameData != null && location != null && oldLocation != null) {
            List<MissionData> missionDatas = getNotFinishedMissionsOfType(3);
            for (MissionData mission : missionDatas) {
                if(oldLocation.hasAltitude() && location.hasAltitude()) {
                    if (oldLocation.getAltitude() < location.getAltitude()) {
                        altitudeClimbed = altitudeClimbed + location.getAltitude() - oldLocation.getAltitude();
                        Log.d("testCl", String.valueOf(altitudeClimbed));
                        Log.d("testCl", String.valueOf(location.getAccuracy()));
                        if (altitudeClimbed > mission.HeightDifference) {
                            completedMissions.add(mission.Id);
                            Toast.makeText(getApplicationContext(), "'Drive a height difference': Mission completed!", Toast.LENGTH_LONG).show();
                        }

                    } else if (oldLocation.getAltitude() > location.getAltitude()) {
                        altitudeDescended = altitudeDescended + oldLocation.getAltitude() - location.getAltitude();
                        Log.d("testDe", String.valueOf(altitudeDescended));
                        Log.d("testDe", String.valueOf(location.getAccuracy()));
                        if (altitudeDescended > mission.HeightDifference) {
                            completedMissions.add(mission.Id);
                            Toast.makeText(getApplicationContext(), "'Drive a height difference': Mission completed!", Toast.LENGTH_LONG).show();

                        }
                    }
                }
            }
        }
        return true;
    }



    public boolean checkLocation(){
        if(gameData != null && location != null) {
            List<MissionData> missionDatas = getNotFinishedMissionsOfType(1);
            for (MissionData mission : missionDatas) {
                    Location loc = new Location("");
                    loc.setLongitude(mission.Location.Longitude);
                    loc.setLatitude(mission.Location.Latitude);

                    if (location.distanceTo(loc) < 10  && !mission.IsFinished && !completedMissions.contains(mission.Id)) {
                        completedMissions.add(mission.Id);
                        Toast.makeText(getApplicationContext(), "'Drive to location': Mission completed!", Toast.LENGTH_LONG).show();
                    }
            }
        }
        return true;

    }
    public boolean checkTeamAssembled() {
        if (gameData != null&& location != null) {
            double maxDistance = 10;
            List<MissionData> missionDatas = getNotFinishedMissionsOfType(2);
            for (MissionData mission : missionDatas) {
                    boolean assembled = true;
                    List<GamePlayerData> players = gameData.Players;
                    for (GamePlayerData player : players) {
                        if(player.Team.equals(gameData.Player.Team)) {
                            if(player.LatestLocation == null){
                                assembled = false;
                                break;
                            }
                            Location loc = new Location("");
                            loc.setLongitude(player.LatestLocation.Longitude);
                            loc.setLatitude(player.LatestLocation.Latitude);
                            if (location.distanceTo(loc) > maxDistance) {
                                assembled = false;
                                break;
                            }
                        }
                    }
                    if (assembled && !mission.IsFinished && !completedMissions.contains(mission.Id)) {
                        completedMissions.add(mission.Id);
                        Toast.makeText(getApplicationContext(), "'Gather with your team': Mission completed!", Toast.LENGTH_LONG).show();
                    }
              }
        }
        return true;
    }
    public boolean checkLightCollected() {
        if (gameData != null) {
            List<MissionData> missionDatas = getNotFinishedMissionsOfType(4);
            for (MissionData mission : missionDatas) {
                    collectedLight = collectedLight + lightvalue;
                    Log.d("test", String.valueOf(collectedLight));
                    if (collectedLight > mission.AmountOfLight && !mission.IsFinished && !completedMissions.contains(mission.Id)) {
                        collectedLight = 0;
                        completedMissions.add(mission.Id);
                        Toast.makeText(getApplicationContext(), "'Search for light': Mission completed!", Toast.LENGTH_LONG).show();
                    }
            }
        }
        return true;
    }


    public boolean checkSpeed(){
        if (gameData != null&& location != null) {
            List<MissionData> missionDatas = getNotFinishedMissionsOfType(5);
            for (MissionData mission : missionDatas) {
                    if (location.getSpeed() > mission.SpeedValue && !mission.IsFinished && !completedMissions.contains(mission.Id)) {
                        completedMissions.add(mission.Id);
                        Toast.makeText(getApplicationContext(), "'Get this speed': Mission completed!", Toast.LENGTH_LONG).show();
                    }
            }
        }
        return true;
    }

    private List<LocationData> getPlayerLocations(){
        List<LocationData> locationDatas= new ArrayList<>();
        if(gameData != null) {
            for (GamePlayerData playerData : gameData.Players) {
                    LocationData loc = playerData.LatestLocation;
                    if(loc != null) {
                        loc.Team = playerData.Team;
                        loc.PlayerName = playerData.Name;
                        loc.HasFlag = playerData.HasFlag;
                        locationDatas.add(loc);
                    }

            }
            LocationData myLocation = new LocationData();
            if(location != null) {
                myLocation.Altitude = location.getAltitude();
                myLocation.Latitude = location.getLatitude();
                myLocation.Longitude = location.getLongitude();
                myLocation.CreatedOn = DateTime.now();
                myLocation.PlayerName = gameData.Player.Name;
                myLocation.Team = gameData.Player.Team;

                locationDatas.add(myLocation);
            }
        }
        return locationDatas;
    }

    public BitmapDescriptor getPlayerMapIcon(String team, boolean hasFlag) {
        if(hasFlag){
            return BitmapDescriptorFactory.fromResource(R.drawable.flag_small);
        }
        if("Defender".equals(team)){
            return BitmapDescriptorFactory.fromResource(R.drawable.defender);
        }
        return BitmapDescriptorFactory.fromResource(R.drawable.attacker);
    }

    public List<MissionData> getNotFinishedMissionsOfType(int type){
        List<MissionData> missionDatas = new ArrayList<>();
        for (int i = 0; i < gameData.Missions.size(); i++) {
            MissionData mission = gameData.Missions.get(i);

            if (mission.Type == type && !mission.IsFinished && mission.IsActive && !completedMissions.contains(mission.Id)) {
                missionDatas.add(mission);
            }
        }
        return missionDatas;
    }
    private void makeAttackCall (GameAttackData data) {
        Call<ReturnData<Empty>> call = new ApiUtil().getApiInterface(this).getAttackData(data);
        RequestUtil<Empty> requestUtil = new RequestUtil<>(this,null, call);
        requestUtil.makeRequest(new RequestInterface<Empty>() {
            @Override
            public void onSucces(Empty body) {
                Snackbar.make(coordinatorLayout,getString(R.string.on_hit),Snackbar.LENGTH_INDEFINITE).show();
            }

            @Override
            public void onError(ErrorData error) {
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private Runnable enableButton(final FloatingActionButton floatingActionButton){
        Runnable aRunnable =  new Runnable() {

            @Override
            public void run() {
                floatingActionButton.setEnabled(true);
                floatingActionButton.setBackgroundResource(R.drawable.gun);
            }
    };
        return aRunnable;
    }
}
