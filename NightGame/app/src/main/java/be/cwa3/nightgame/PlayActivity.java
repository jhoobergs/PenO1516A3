package be.cwa3.nightgame;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import be.cwa3.nightgame.Adapters.LobbyAdapter;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.LobbiesData;
import be.cwa3.nightgame.Data.LobbiesListData;
import be.cwa3.nightgame.Data.LobbySearchRequestData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Utils.ApiUtil;
import be.cwa3.nightgame.Utils.ErrorUtil;
import be.cwa3.nightgame.Utils.RequestInterface;
import be.cwa3.nightgame.Utils.RequestUtil;
import be.cwa3.nightgame.Utils.SensorDataActivity;
import be.cwa3.nightgame.Utils.SensorDataInterface;
import retrofit.Call;

/**
 * Created by Koen on 19/10/2015.
 * Updated by Kevin on 12/11/2015
 */
public class PlayActivity extends SensorDataActivity {

    private EditText enterLobbyName;
    ListView listView;

    LobbiesListData lobbiesListData;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        makeCall();

        enterLobbyName = (EditText) findViewById(R.id.editTextLobby);

        listView = (ListView) findViewById(R.id.listViewLobbies);

        setListView();

        enterLobbyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (enterLobbyName.getText().toString().length() >= 3) {
                    LobbySearchRequestData data = new LobbySearchRequestData(enterLobbyName.getText().toString());
                    makeSearchCall(data);
                } if (enterLobbyName.getText().toString().isEmpty()) {
                    List<LobbiesData> empty = new ArrayList<LobbiesData>();
                    lobbiesListData.List = empty;
                    listView.setAdapter(new LobbyAdapter(PlayActivity.this, empty));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void makeSearchCall(LobbySearchRequestData data) {
        Call<ReturnData<LobbiesListData>> call = new ApiUtil().getApiInterface(this).searchLobbies(data);
        RequestUtil<LobbiesListData> requestUtil = new RequestUtil<>(this, call);
        requestUtil.makeRequest(new RequestInterface<LobbiesListData>() {
            @Override
            public void onSucces(LobbiesListData body) {
                lobbiesListData = body;
                listView.setAdapter(new LobbyAdapter(PlayActivity.this, body.List));
            }

            @Override
            public void onError(ErrorData error) {
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void makeCall(){
        Call<ReturnData<LobbiesListData>> call = new ApiUtil().getApiInterface(this).loadLobbyList();
        RequestUtil<LobbiesListData> requestUtil = new RequestUtil<>(this, call);
        requestUtil.makeRequest(new RequestInterface<LobbiesListData>() {
            @Override
            public void onSucces(LobbiesListData body) {
                lobbiesListData = body;
                final Location myLocation = getLocation();
                /*final Location myLocation = new Location("");
                myLocation.setLatitude(54);
                myLocation.setLongitude(5);*/
                Collections.sort(lobbiesListData.List, new Comparator<LobbiesData>() {

                    @Override
                    public int compare(LobbiesData lhs, LobbiesData rhs) {

                        Location locationLhs = new Location("");
                        locationLhs.setLatitude(lhs.CenterLocation.Latitude);
                        locationLhs.setLongitude(lhs.CenterLocation.Longitude);

                        Location locationRhs = new Location("");
                        locationRhs.setLatitude(rhs.CenterLocation.Latitude);
                        locationRhs.setLongitude(rhs.CenterLocation.Longitude);
                        if(locationRhs.distanceTo(myLocation)> locationLhs.distanceTo(myLocation)){
                            return 1;
                        }
                        else if(locationRhs.distanceTo(myLocation) <= locationLhs.distanceTo(myLocation)){
                            return -1;
                        }
                        else
                            return 0;
                    }
                });

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
        getMenuInflater().inflate(R.menu.menu_play_activity, menu);
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

            listView.setAdapter(new LobbyAdapter(PlayActivity.this, lobbiesListData.List));
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
