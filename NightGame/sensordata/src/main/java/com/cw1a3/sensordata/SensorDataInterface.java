package com.cw1a3.sensordata;

import android.location.Location;

/**
 * Created by jesse on 22/11/2015.
 */
public abstract class SensorDataInterface {
    public void locationChanged(Location newLocation){

    }

    public void accelerometerChanged(float x, float y, float z) {

    }

    public void proximityChanged(float proximity) {
    }

    public void lightChanged(float sv) {

    }
}
