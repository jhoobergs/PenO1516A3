package com.example.kevin.sensorapp;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


public class DetailActivity extends AppCompatActivity implements SensorEventListener{
    private SensorManager sensorManager;

    TextView xCoor; // declare X axis object
    TextView yCoor; // declare Y axis object
    TextView zCoor; // declare Z axis object
    TextView singVal;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        xCoor=(TextView)findViewById(R.id.xcoor); // create X axis object
        yCoor=(TextView)findViewById(R.id.ycoor); // create Y axis object
        zCoor=(TextView)findViewById(R.id.zcoor); // create Z axis object
        singVal=(TextView)findViewById(R.id.singval);
        Intent intent = getIntent();
        String key = intent.getStringExtra(MainActivity.KEY);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // add listener. The listener will be  (this) class
        if (key.equals("ACC")){
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                sensorManager.SENSOR_DELAY_NORMAL);
        }else if(key.equals("THERMO")){
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE),
                    sensorManager.SENSOR_DELAY_NORMAL);
        }else if(key.equals("GYRO")){
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                    sensorManager.SENSOR_DELAY_NORMAL);
        }else if(key.equals("LIGHT")){
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                    sensorManager.SENSOR_DELAY_NORMAL);
        }else if(key.equals("MAGNET")){
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                    sensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    public void onAccuracyChanged(Sensor sensor,int accuracy){

    }

    public void onSensorChanged(SensorEvent event){

        // check sensor type
        int type = event.sensor.getType();
        if(type==Sensor.TYPE_ACCELEROMETER){
            // assign directions
            float x=event.values[0];
            float y=event.values[1];
            float z=event.values[2];

            xCoor.setText("X: "+x);
            yCoor.setText("Y: "+y);
            zCoor.setText("Z: "+z);
            xCoor.setVisibility(View.VISIBLE);
            yCoor.setVisibility(View.VISIBLE);
            zCoor.setVisibility(View.VISIBLE);
        }else if (type==Sensor.TYPE_AMBIENT_TEMPERATURE){
            float sv=event.values[0];
            singVal.setText("SV: "+sv);
            singVal.setVisibility(View.VISIBLE);
        }else if (type==Sensor.TYPE_MAGNETIC_FIELD){
            float x=event.values[0];
            float y=event.values[1];
            float z=event.values[2];

            xCoor.setText("X: "+x);
            yCoor.setText("Y: "+y);
            zCoor.setText("Z: "+z);
            xCoor.setVisibility(View.VISIBLE);
            yCoor.setVisibility(View.VISIBLE);
            zCoor.setVisibility(View.VISIBLE);
        }else if (type==Sensor.TYPE_GYROSCOPE){
            float x=event.values[0];
            float y=event.values[1];
            float z=event.values[2];

            xCoor.setText("X: "+x);
            yCoor.setText("Y: "+y);
            zCoor.setText("Z: "+z);
            xCoor.setVisibility(View.VISIBLE);
            yCoor.setVisibility(View.VISIBLE);
            zCoor.setVisibility(View.VISIBLE);
        }else if(type==Sensor.TYPE_LIGHT){
            float sv=event.values[0];
            singVal.setText("SV: "+sv);
            singVal.setVisibility(View.VISIBLE);
        }

    }
}