package be.cwa3.nightgame;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import be.cwa3.nightgame.Data.CreateLobbyRequestData;
import be.cwa3.nightgame.Data.CreateLobbyReturnData;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Utils.ApiUtil;
import be.cwa3.nightgame.Utils.ErrorUtil;
import be.cwa3.nightgame.Utils.LocationChanged;
import be.cwa3.nightgame.Utils.LocationDataActivity;
import be.cwa3.nightgame.Utils.RequestInterface;
import be.cwa3.nightgame.Utils.RequestUtil;
import be.cwa3.nightgame.Utils.SettingsUtil;
import be.cwa3.nightgame.Utils.SharedPreferencesKeys;
import be.cwa3.nightgame.custom.CustomScrollView;
import retrofit.Call;

/**
 * Created by Gebruiker on 19/10/2015.
 */
public class CreateLobbyActivity extends LocationDataActivity implements OnMapReadyCallback {
    NumberPicker numberPickerMinValue, numberPickerMaxValue;
    EditText editTextGroupName;
    TextView chooseCentre;
    LinearLayout layoutCreate, mapLayout;
    Button buttonNext;
    private boolean mRequestingLocationUpdates = true;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    MapFragment mapFragment;

    private Location location;
    private LatLng locationlatlng;
    private boolean mapHasBeenReady = false;

    boolean clicked = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createlobby);

        layoutCreate = (LinearLayout) findViewById(R.id.layout_create);
        mapLayout = (LinearLayout) findViewById(R.id.map_layout);
        chooseCentre = (TextView) findViewById(R.id.choose_centre_press_next);
        editTextGroupName = (EditText) findViewById(R.id.editTextGroupName);
        buttonNext = (Button) findViewById(R.id.button_next);
        numberPickerMaxValue = (NumberPicker) findViewById(R.id.numberPickerMaxValue);
        numberPickerMinValue = (NumberPicker) findViewById(R.id.numberPickerMinValue);
        setMinAndMaxOfNumberPicker(numberPickerMaxValue, 4, 10);
        setMinAndMaxOfNumberPicker(numberPickerMinValue, 4, 10);

        setLocationChanged(new LocationChanged() {
            @Override
            public void locationChanged() {
                location = getLocation();
                mapFragment.getMapAsync(CreateLobbyActivity.this);
            }
        });
        location = getLocation();
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap map) {

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                clicked = true;

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .draggable(true);
                map.addMarker(markerOptions);

                CircleOptions circleOptions = new CircleOptions()
                        .center(latLng)
                        .radius(1000); // In meters
                map.addCircle(circleOptions);
                buttonNext.setVisibility(View.VISIBLE);
            }
        });
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }
            @Override
            public void onMarkerDrag(Marker marker) {
            }
            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng latLng = marker.getPosition();
                clicked = true;

                CircleOptions circleOptions = new CircleOptions()
                        .center(latLng)
                        .radius(1000); // In meters
                map.addCircle(circleOptions);
            }
        });


        if(location != null && clicked == false) {
            locationlatlng = new LatLng(location.getLatitude(), location.getLongitude());

            map.setMyLocationEnabled(true);
            if (!mapHasBeenReady) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationlatlng, 13));
                mapHasBeenReady = true;
            } else {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(map.getCameraPosition().target, map.getCameraPosition().zoom));
            }
            map.clear();
        }
    }

    public void setMinAndMaxOfNumberPicker(NumberPicker np, int min, int max) {
        np.setMaxValue(max);
        np.setMinValue(min);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_createlobby_activity, menu);
        return super.onCreateOptionsMenu(menu);

    }
    //invalidateOptionsMenu();
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.define_map:
                if (numberPickerMinValue.getValue() > numberPickerMaxValue.getValue()
                        || editTextGroupName.getText().toString().equals(""))
                {
                    Toast.makeText(CreateLobbyActivity.this, "Data not entered correctly!", Toast.LENGTH_LONG).show();
                }
                else {
                    layoutCreate.setVisibility(View.GONE);
                    chooseCentre.setVisibility(View.VISIBLE);
                    mapLayout.setVisibility(View.VISIBLE);



                }
                return true;
            case R.id.start_lobby:
                    CreateLobbyRequestData data = new CreateLobbyRequestData();
                    data.Name = editTextGroupName.getText().toString();
                    data.MinPlayers = numberPickerMinValue.getValue();
                    data.MaxPlayers = numberPickerMaxValue.getValue();
                    Location userLocation = getLocation();
                    data.CenterLocationLatitude = userLocation.getLatitude();
                    data.CenterLocationLongitude = userLocation.getLongitude();
                    makeCreateLobbyCall(data);//
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void makeCreateLobbyCall(CreateLobbyRequestData data){
        Call<ReturnData<CreateLobbyReturnData>> call = new ApiUtil().getApiInterface(this).sendCreateLobbyRequest(data);
        RequestUtil<CreateLobbyReturnData> requestUtil = new RequestUtil<>(this, call);
        requestUtil.makeRequest(new RequestInterface<CreateLobbyReturnData>() {

            @Override
            public void onSucces(CreateLobbyReturnData body) {
                Log.d("test", body.GameId);
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(intent);
                new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.GameIDString, body.GameId);
            }

            @Override
            public void onError(ErrorData error) {
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();
            }
        });
    }


}