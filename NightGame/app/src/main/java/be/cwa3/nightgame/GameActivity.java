package be.cwa3.nightgame;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.cwa3.nightgame.Utils.SensorDataActivity;

/**
 * Created by jesse on 15/10/2015.
 */
public class GameActivity extends SensorDataActivity implements OnMapReadyCallback {

    MapFragment mapFragment;
    private boolean othersShouldBeInvisibile = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        /*
        //LatLng sydney = new LatLng(-33.867, 151.206);
        List<LatLng> locations = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        List<String> content = new ArrayList<>();
        if(mCurrentLocation != null) {
            locations.add(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
            titles.add("Jesse");
            content.add("Verdediger");
            locations.add(new LatLng(mCurrentLocation.getLatitude()+0.01, mCurrentLocation.getLongitude()));
            titles.add("Koen");
            content.add("Aanvaller");
            locations.add(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()+0.01));
            titles.add("Jean");
            content.add("Verdediger");
            locations.add(new LatLng(mCurrentLocation.getLatitude()+0.01, mCurrentLocation.getLongitude()+0.01));
            titles.add("Moran");
            content.add("Aanvaller");
            locations.add(new LatLng(mCurrentLocation.getLatitude()+0.02, mCurrentLocation.getLongitude()));
            titles.add("Kevin");
            content.add("Verdediger");
            locations.add(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()+0.02));
            titles.add("Elisabeth");
            content.add("Aanvaller");
            Log.d("url", String.valueOf(mCurrentLocation.getLatitude()));
        }

        if(locations.size() > 0) {
            map.setMyLocationEnabled(true);
            if(!mapHasBeenReady) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(locations.get(0), 13));
                mapHasBeenReady = true;
            }
            else
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(map.getCameraPosition().target, map.getCameraPosition().zoom));
        }
        map.clear();
        map.setMyLocationEnabled(false);
        int number = 0;
        for (LatLng loc : locations) {
            BitmapDescriptor bitmapDescriptor;

            if(number%2 == 0) {
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.defender);
            }
            else
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.attacker);
            if(!(number%2 == 1 && othersShouldBeInvisibile))
                map.addMarker(new MarkerOptions()
                        .title(titles.get(number))
                        .snippet(content.get(number))
                        .icon(bitmapDescriptor)
                        .position(loc));
            number+=1;
        }*/

    }





    /*@Override
    public void onSensorChanged(SensorEvent event) {

        int type = event.sensor.getType();
        if(type==Sensor.TYPE_LIGHT) {
            float sv = event.values[0];
            Log.d("sensor", String.valueOf(sv));
            boolean before = othersShouldBeInvisibile;
            if (sv < 90)
                othersShouldBeInvisibile = true;
            else
                othersShouldBeInvisibile = false;

            if(before != othersShouldBeInvisibile)
                mapFragment.getMapAsync(this);
        }

    }*/
}