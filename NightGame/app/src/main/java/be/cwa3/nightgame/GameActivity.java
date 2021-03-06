package be.cwa3.nightgame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.cw1a3.sensordata.SensorDataActivity;
import com.cw1a3.sensordata.SensorDataInterface;
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

    private Handler handler = new Handler();
    private int delayTimeRequestData = 5000;
    private double altitudeClimbed = 0;
    private double altitudeDescended = 0;
    private float collectedLight = 0;

    private TextView livesTextView;
    private LinearLayout livesHolder;
    private ImageView hasFlagImageView;
    private RelativeLayout gameContainer;
    private CoordinatorLayout coordinatorLayout;

    private FloatingActionsMenu floatingActionsMenuShoot;
    private List<FloatingActionButton> floatingActionButtonList;
    private List<String> shootedPlayers;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        enableAccelerometerSensor();
        enableLocationUpdates(1000, 2000);
        enableProximitySensor();
        enableLightSensor();

        gameId = new SettingsUtil(this).getString(SharedPreferencesKeys.GameIDString);
        if ("".equals(gameId)){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        makeGetDataCall(new GameGetDataRequestData(gameId)); //Only needed one time, this data is also returned by sendData
        location = getLocation();
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listview = (ListView) findViewById(R.id.missions_listview);
        livesTextView = (TextView) findViewById(R.id.amount_of_lives_textview);
        livesHolder = (LinearLayout) findViewById(R.id.livesholder_linearlayout);
        hasFlagImageView = (ImageView) findViewById(R.id.hasflag_imageview);
        gameContainer = (RelativeLayout) findViewById(R.id.game_relativelayout);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        floatingActionsMenuShoot = (FloatingActionsMenu) findViewById(R.id.floatingactionsmenu_shoot);
        floatingActionButtonList = new ArrayList<>();
        shootedPlayers = new ArrayList<>();
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
                    handler.postDelayed(sendData, 100);
                }
            }

            @Override
            public void proximityChanged(float prox) {
                proximity = prox;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(sendData); //stop Async threads
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

            if (loc.Team.equals(gameData.Player.Team) || loc.HasFlag || loc.IsShaking || (!othersShouldBeInvisibile && "Attacker".equals(gameData.Player.Team) ))
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
                                .title(getString(R.string.challenge_location)).position(loc));
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
        getMenuInflater().inflate(R.menu.menu_activity_game, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.challenges:
                if (gameContainer.getVisibility() == View.VISIBLE) {
                    gameContainer.setVisibility(View.GONE);
                    listview.setVisibility(View.VISIBLE);
                    item.setTitle(R.string.game);
                } else {
                    gameContainer.setVisibility(View.VISIBLE);
                    listview.setVisibility(View.GONE);
                    item.setTitle(R.string.challenge);
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
        RequestUtil<LobbiesData> requestUtil = new RequestUtil<>(this, coordinatorLayout, call);
        requestUtil.makeRequest(new RequestInterface<LobbiesData>() {

            @Override
            public void onSucces(LobbiesData body) {
                handler.postDelayed(sendData, delayTimeRequestData);
                completedMissions.clear();
                handleNewGameData(body);
            }

            @Override
            public void onError(ErrorData error) {
                handler.postDelayed(sendData, delayTimeRequestData);
                if (error.Errors.contains(4) || error.Errors.contains(6)) {
                    new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.GameIDString, "");
                    Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                    startActivity(intent);
                    finish();
                }
                Snackbar.make(coordinatorLayout, ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Snackbar.LENGTH_INDEFINITE).show();
            }

            @Override
            public void onFailure(Context context, View view, Throwable t) {
                handler.postDelayed(sendData, delayTimeRequestData);
                super.onFailure(context, view, t);
            }
        });
    }

    public void handleNewGameData(LobbiesData body){
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
            livesHolder.setVisibility(View.GONE);
        }
        else{
            floatingActionsMenuShoot.setVisibility(View.GONE);
        }

        if(gameData.WinningTeam != null || gameData.Player.Lives < 1){
            handler.removeCallbacks(sendData); //stop Async threads
            floatingActionsMenuShoot.setVisibility(View.GONE);
            String snackbarText;
            if(gameData.WinningTeam != null){
                snackbarText = String.format(getString(R.string.game_end), gameData.WinningTeam);
            }
            else{
                snackbarText = getString(R.string.dead);
            }
            Snackbar.make(coordinatorLayout, snackbarText, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.close_game, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.GameIDString, "");
                            Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setActionTextColor(Color.RED)
                    .show();

        }
    }

    private void makeGetDataCall(GameGetDataRequestData data){
        Call<ReturnData<LobbiesData>> call = new ApiUtil().getApiInterface(this).getLobbyData(data);
        RequestUtil<LobbiesData> requestUtil = new RequestUtil<>(this,coordinatorLayout, call);
        requestUtil.makeRequest(new RequestInterface<LobbiesData>() {
            @Override
            public void onSucces(LobbiesData body) {
                handleNewGameData(body);
            }

            @Override
            public void onError(ErrorData error) {
                if (error.Errors.contains(4) || error.Errors.contains(6)) {
                    new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.GameIDString, "");
                    Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                    startActivity(intent);
                    finish();
                }
                Snackbar.make(coordinatorLayout, ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Snackbar.LENGTH_INDEFINITE).show();

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
                if(shootedPlayers.contains(playerData.Name)){
                    floatingActionButton.setBackgroundResource(R.drawable.bullet);
                    floatingActionButton.setEnabled(false);
                }
                else{
                    floatingActionButton.setBackgroundResource(R.drawable.gun);
                }
                floatingActionButton.setTitle(playerData.Name);
                floatingActionButton.setSize(FloatingActionButton.SIZE_NORMAL);
                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GameAttackData data = new GameAttackData();
                        data.GameId = gameId;
                        data.AttackedUser = playerData.Name;
                        makeAttackCall(data);
                        shootedPlayers.add(data.AttackedUser);
                        floatingActionButton.setEnabled(false);
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
                            Snackbar.make(coordinatorLayout, getString(R.string.mission_height_difference_text), Snackbar.LENGTH_INDEFINITE).show();
                        }

                    } else if (oldLocation.getAltitude() > location.getAltitude()) {
                        altitudeDescended = altitudeDescended + oldLocation.getAltitude() - location.getAltitude();
                        Log.d("testDe", String.valueOf(altitudeDescended));
                        Log.d("testDe", String.valueOf(location.getAccuracy()));
                        if (altitudeDescended > mission.HeightDifference) {
                            completedMissions.add(mission.Id);
                            Snackbar.make(coordinatorLayout, getString(R.string.mission_height_difference_text), Snackbar.LENGTH_INDEFINITE).show();

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
                        Snackbar.make(coordinatorLayout, getString(R.string.mission_drive_to_text), Snackbar.LENGTH_INDEFINITE).show();
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
                        Snackbar.make(coordinatorLayout, getString(R.string.mission_gather_text), Snackbar.LENGTH_INDEFINITE).show();
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
                        Snackbar.make(coordinatorLayout, getString(R.string.mission_search_light_text), Snackbar.LENGTH_INDEFINITE).show();
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
                        Snackbar.make(coordinatorLayout, getString(R.string.mission_speed_text), Snackbar.LENGTH_INDEFINITE).show();
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
                        loc.IsShaking = playerData.IsShaking;
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
    private void makeAttackCall (final GameAttackData data) {
        Call<ReturnData<Empty>> call = new ApiUtil().getApiInterface(this).sendAttackData(data);
        RequestUtil<Empty> requestUtil = new RequestUtil<>(this,coordinatorLayout, call);
        requestUtil.makeRequest(new RequestInterface<Empty>() {
            @Override
            public void onSucces(Empty body) {
                Snackbar.make(coordinatorLayout,getString(R.string.on_hit),Snackbar.LENGTH_INDEFINITE).show();
                handler.postDelayed(enableButton(data.AttackedUser), 60 * 1000);
            }

            @Override
            public void onError(ErrorData error) {
                handler.postDelayed(enableButton(data.AttackedUser), 5 * 1000);
                Snackbar.make(coordinatorLayout, ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Snackbar.LENGTH_INDEFINITE).show();
            }
        });
    }
    private Runnable enableButton(final String attackUser){
        Runnable aRunnable =  new Runnable() {

            @Override
            public void run() {
                for(FloatingActionButton floatingActionButton: floatingActionButtonList) {
                    if(floatingActionButton.getTitle().equals(attackUser)) {
                        floatingActionButton.setEnabled(true);
                        floatingActionButton.setBackgroundResource(R.drawable.gun);
                        shootedPlayers.remove(attackUser);
                        break;
                    }
                }

            }
    };
        return aRunnable;
    }
}
