package be.cwa3.nightgame;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Koen on 5/11/2015.
 */
public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    TextView xCoor; // declare X axis object
    TextView yCoor; // declare Y axis object
    TextView zCoor; // declare Z axis object
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        xCoor = (TextView) findViewById(R.id.xcoor); // create X axis object
        yCoor = (TextView) findViewById(R.id.ycoor); // create Y axis object
        zCoor = (TextView) findViewById(R.id.zcoor); // create Z axis object

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // add listener. The listener will be  (this) class
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL);

    }
    public void onSensorChanged(SensorEvent event) {

        int type = event.sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER) {
            // assign directions
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            xCoor.setText("X: " + Math.floor(x*100)/100);
            yCoor.setText("Y: " +  Math.floor(y*100)/100);
            zCoor.setText("Z: " +  Math.floor(z*100)/100);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}