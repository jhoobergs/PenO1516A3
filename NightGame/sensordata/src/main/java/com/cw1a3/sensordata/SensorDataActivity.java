package com.cw1a3.sensordata;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by jesse on 15/10/2015.
 */
public class SensorDataActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SensorEventListener {
    private boolean mRequestingLocationUpdates = true;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private GoogleApiClient mGoogleApiClient;
    private SensorManager sensorManager;
    LocationRequest mLocationRequest;

    SensorDataInterface sensorDataInterface;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createLocationRequest();
        buildGoogleApiClient();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                sensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                sensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                sensorManager.SENSOR_DELAY_NORMAL);

    }

    public void setSensorDataInterface(SensorDataInterface listener){
        sensorDataInterface = listener;
    }

    public Location getLocation() {
        return mCurrentLocation;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        if(sensorDataInterface != null)
            sensorDataInterface.locationChanged(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void stopLocationUpdates() {
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        int type = event.sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if(sensorDataInterface != null)
                sensorDataInterface.accelerometerChanged(x, y, z);
        } else if (type == Sensor.TYPE_PROXIMITY) {

            float proximity = event.values[0];
            if(sensorDataInterface != null)
                sensorDataInterface.proximityChanged(proximity);
        }
        else if (type == Sensor.TYPE_LIGHT) {

            float sv = event.values[0];
            if(sensorDataInterface != null)
                sensorDataInterface.lightChanged(sv);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }
}
