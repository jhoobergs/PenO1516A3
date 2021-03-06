package be.cwa3.nightgame;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cw1a3.sensordata.SensorDataActivity;
import com.cw1a3.sensordata.SensorDataInterface;

import java.util.Collections;
import java.util.Comparator;

import be.cwa3.nightgame.Adapters.LobbyAdapter;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.LobbiesData;
import be.cwa3.nightgame.Data.LobbiesListData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Utils.ApiUtil;
import be.cwa3.nightgame.Utils.ErrorUtil;
import be.cwa3.nightgame.Utils.RequestInterface;
import be.cwa3.nightgame.Utils.RequestUtil;
import retrofit.Call;

/**
 * Created by Koen on 19/10/2015.
 * Updated by Kevin on 12/11/2015
 */
public class LobbyActivity extends SensorDataActivity {

    ListView listView;
    private boolean hasLocation = false;

    LobbiesListData lobbiesListData;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        enableLocationUpdates(1000, 2000);
        makeLoadLobbyListCall();

        listView = (ListView) findViewById(R.id.listViewLobbies);
        setListView();

        setSensorDataInterface(new SensorDataInterface() {
            @Override
            public void locationChanged(Location newLocation) {
                if (lobbiesListData != null && !hasLocation) {
                    hasLocation = true;
                    setListView();
                }
            }
        });
    }

    private void makeLoadLobbyListCall(){
        Call<ReturnData<LobbiesListData>> call = new ApiUtil().getApiInterface(this).loadLobbyList();
        RequestUtil<LobbiesListData> requestUtil = new RequestUtil<>(this, null, call);
        requestUtil.makeRequest(new RequestInterface<LobbiesListData>() {
            @Override
            public void onSucces(LobbiesListData body) {
                lobbiesListData = body;
                setListView();
            }

            @Override
            public void onError(ErrorData error) {
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_lobby, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(getApplicationContext(), CreateLobbyActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setListView() {
        if (lobbiesListData != null) {
            final Location myLocation = getLocation();
            if(myLocation != null && !hasLocation)
                hasLocation = true;
            Collections.sort(lobbiesListData.List, new Comparator<LobbiesData>() {

                @Override
                public int compare(LobbiesData lhs, LobbiesData rhs) {

                    Location locationLhs = new Location("");
                    locationLhs.setLatitude(lhs.CenterLocation.Latitude);
                    locationLhs.setLongitude(lhs.CenterLocation.Longitude);

                    Location locationRhs = new Location("");
                    locationRhs.setLatitude(rhs.CenterLocation.Latitude);
                    locationRhs.setLongitude(rhs.CenterLocation.Longitude);
                    if (hasLocation) {
                        if (locationRhs.distanceTo(myLocation) < locationLhs.distanceTo(myLocation)) {
                            return 1;
                        } else if (locationRhs.distanceTo(myLocation) >= locationLhs.distanceTo(myLocation)) {
                            return -1;
                        } else
                            return 0;
                    }
                    else{
                        return 0;
                    }
                }
            });
            listView.setAdapter(new LobbyAdapter(LobbyActivity.this, lobbiesListData.List, getLocation()));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    RelativeLayout relativeLayoutExtraData = (RelativeLayout) view.findViewById(R.id.Lobbies_data);
                    lobbiesListData.List.get(position).isOpen ^= true; // This change true to false and false to true
                    LobbiesData item = lobbiesListData.List.get(position);
                    if (item.isOpen)
                        relativeLayoutExtraData.setVisibility(View.VISIBLE);
                    else
                        relativeLayoutExtraData.setVisibility(View.GONE);

                }
            });

        }
    }

}
