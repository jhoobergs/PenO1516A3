package be.cwa3.nightgame;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import be.cwa3.nightgame.Data.CreateLobbyRequestData;
import be.cwa3.nightgame.Data.CreateLobbyReturnData;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Utils.ApiUtil;
import be.cwa3.nightgame.Utils.ErrorUtil;
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
    LinearLayout layoutCreate, mapLayout;
    private boolean mRequestingLocationUpdates = true;
    private GoogleApiClient mGoogleApiClient;
    private CustomScrollView customScrollView;
    LocationRequest mLocationRequest;
    MapFragment mapFragment;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createlobby);
        customScrollView = (CustomScrollView) findViewById(R.id.scrollview);

        layoutCreate = (LinearLayout) findViewById(R.id.layout_create);
        mapLayout = (LinearLayout) findViewById(R.id.map_layout);
        editTextGroupName = (EditText) findViewById(R.id.editTextGroupName);
        numberPickerMaxValue = (NumberPicker) findViewById(R.id.numberPickerMaxValue);
        numberPickerMinValue = (NumberPicker) findViewById(R.id.numberPickerMinValue);
        setMinAndMaxOfNumberPicker(numberPickerMaxValue, 4, 10);
        setMinAndMaxOfNumberPicker(numberPickerMinValue, 4, 10);


        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    @Override
    public void onMapReady(GoogleMap map) {
        customScrollView.addInterceptScrollView(mapFragment.getView());
        Log.d("url", "werk");
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
                    CreateLobbyRequestData data = new CreateLobbyRequestData();
                    data.Name = editTextGroupName.getText().toString();
                    data.MinPlayers = numberPickerMinValue.getValue();
                    data.MaxPlayers = numberPickerMaxValue.getValue();
                    Location userLocation = getLocation();
                    data.CenterLocationLatitude = userLocation.getLatitude();
                    data.CenterLocationLongitude = userLocation.getLongitude();
                    makeCreateLobbyCall(data);
                    layoutCreate.setVisibility(View.INVISIBLE);
                    mapLayout.setVisibility(View.INVISIBLE);
                }
//
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