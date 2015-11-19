package be.cwa3.nightgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
public class PlayActivity extends AppCompatActivity {

    ListView LobbyListView;
    LobbiesListData lobbiesListData;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        makeCall();


        LobbyListView = (ListView) findViewById(R.id.listViewLobbies);

        setListView();

    }

    private void makeCall(){
        Call<ReturnData<LobbiesListData>> call = new ApiUtil().getApiInterface(this).loadLobbyList();
        RequestUtil<LobbiesListData> requestUtil = new RequestUtil<>(this, call);
        requestUtil.makeRequest(new RequestInterface<LobbiesListData>(){
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
            LobbyListView.setAdapter(new LobbyAdapter(PlayActivity.this, lobbiesListData.List));
            LobbyListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
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
