package com.example.kevin.sensorapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public static String KEY = "type";
    private SensorManager mSensorManager;
    private Button button1,button2,button3,button4,button5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAccelerometerButtonClick(v);
            }
        });

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onThermometerButtonClick(v);
            }
        });

        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGyroscopeButtonClick(v);
            }
        });

        button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMagneticButtonClick(v);
            }
        });

        button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLightButtonClick(v);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onAccelerometerButtonClick(View view) {
        if (((ColorDrawable) view.getBackground()).getColor()==Color.GREEN) {
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra(KEY, "ACC");
            startActivity(intent);
        }
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            view.setBackgroundColor(Color.GREEN);
        } else {
            view.setBackgroundColor(Color.RED);
        }
    }

    private void onThermometerButtonClick(View view) {
        if (((ColorDrawable) view.getBackground()).getColor()==Color.GREEN){
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra(KEY,"THERMO");
            startActivity(intent);
        }
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            view.setBackgroundColor(Color.GREEN);
        } else {
            view.setBackgroundColor(Color.RED);
        }
    }

    private void onGyroscopeButtonClick(View view) {
        if (((ColorDrawable) view.getBackground()).getColor()==Color.GREEN){
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra(KEY,"GYRO");
            startActivity(intent);
        }
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            view.setBackgroundColor(Color.GREEN);
        } else {
            view.setBackgroundColor(Color.RED);
        }
    }

    private void onMagneticButtonClick(View view) {
        if (((ColorDrawable) view.getBackground()).getColor()==Color.GREEN){
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra(KEY,"MAGNET");
            startActivity(intent);
        }
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            view.setBackgroundColor(Color.GREEN);
        } else {
            view.setBackgroundColor(Color.RED);
        }
    }

    private void onLightButtonClick(View view) {
        if (((ColorDrawable) view.getBackground()).getColor()==Color.GREEN){
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra(KEY,"LIGHT");
            startActivity(intent);
        }
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            view.setBackgroundColor(Color.GREEN);
        } else {
            view.setBackgroundColor(Color.RED);
        }
    }

}
