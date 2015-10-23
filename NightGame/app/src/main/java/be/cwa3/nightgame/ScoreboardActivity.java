package be.cwa3.nightgame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import be.cwa3.nightgame.Adapters.ScoreboardAdapter;
import be.cwa3.nightgame.Data.ScoreboardListData;
import be.cwa3.nightgame.Http.Api.ApiInterface;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ScoreboardListData> call = apiInterface.loadScoreboard();
        call.enqueue(new Callback<ScoreboardListData>() {
            @Override
            public void onResponse(Response<ScoreboardListData> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    scoreboardListData = response.body();
                    setListView();
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