package be.cwa3.nightgame;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Game;
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
import be.cwa3.nightgame.Utils.SensorDataInterface;

/**
 * Created by jesse on 15/10/2015.
 */
public class GameActivity extends SensorDataActivity implements OnMapReadyCallback {

    MapFragment mapFragment;
    private boolean othersShouldBeInvisibile = false;
    private boolean mapHasBeenReady = false;

    private Location location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //TODO: CHECK IF THERE IS A GAMEID

        location = getLocation();
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setSensorDataInterface(new SensorDataInterface() {
            @Override
            public void accelerometerChanged(float x, float y, float z) {
                super.accelerometerChanged(x, y, z);
            }

            @Override
            public void lightChanged(float sv) {
                boolean before = othersShouldBeInvisibile;
                if (sv < 90)
                    othersShouldBeInvisibile = true;
                else
                    othersShouldBeInvisibile = false;

                if(before != othersShouldBeInvisibile)
                    mapFragment.getMapAsync(GameActivity.this);
            }

            @Override
            public void locationChanged(Location newLocation) {
                location = newLocation;
                mapFragment.getMapAsync(GameActivity.this);
            }

            @Override
            public void proximityChanged(float proximity) {
                super.proximityChanged(proximity);
            }
        });



    }

    @Override
    public void onMapReady(GoogleMap map) {
        List<LatLng> locations = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        List<String> content = new ArrayList<>();
        if(location != null) {
            locations.add(new LatLng(location.getLatitude(), location.getLongitude()));
            titles.add("Jesse");
            content.add("Verdediger");
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
        }

    }
}
