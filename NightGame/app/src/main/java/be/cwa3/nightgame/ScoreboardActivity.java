package be.cwa3.nightgame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import be.cwa3.nightgame.Adapters.ScoreboardAdapter;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Data.ScoreboardListData;
import be.cwa3.nightgame.Utils.ApiUtil;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by kevin on 19/10/2015.
 */
public class ScoreboardActivity extends AppCompatActivity {
    private ScoreboardListData scoreboardListData;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        makeCall();

        listView = (ListView)findViewById(R.id.listViewScores);

    }
    private void makeCall(){
        Call<ReturnData<ScoreboardListData>> call =
                new ApiUtil().getApiInterface(this).loadScoreboard();
        call.enqueue(new Callback<ReturnData<ScoreboardListData>>() {
            @Override
            public void onResponse(Response<ReturnData<ScoreboardListData>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    if(response.body().statusCode == 1) {
                        scoreboardListData = response.body().body;
                        setListView();
                    }
                    else{
                        //Should not happen
                        Toast.makeText(getApplicationContext(), "Should not happen", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(getApplicationContext(), "No succes", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setListView(){
        if(scoreboardListData != null) {
            listView.setAdapter(new ScoreboardAdapter(ScoreboardActivity.this, scoreboardListData.List));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LinearLayout linearLayoutextraData = (LinearLayout) view.findViewById(R.id.LinearLayoutExtraData);
                    scoreboardListData.List.get(position).isOpen ^= true; // This change true to false and false to true
                    if (scoreboardListData.List.get(position).isOpen)
                        linearLayoutextraData.setVisibility(View.VISIBLE);
                    else
                        linearLayoutextraData.setVisibility(View.GONE);
                }
            });
        }
    }
}
