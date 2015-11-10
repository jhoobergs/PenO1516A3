package be.cwa3.nightgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;

import be.cwa3.nightgame.Adapters.ScoreboardAdapter;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.LoginReturnData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Data.ScoreboardData;
import be.cwa3.nightgame.Data.ScoreboardListData;
import be.cwa3.nightgame.Utils.ApiUtil;
import be.cwa3.nightgame.Utils.ErrorUtil;
import be.cwa3.nightgame.Utils.RequestInterface;
import be.cwa3.nightgame.Utils.RequestUtil;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Improved/Upgraded/Enhanced by Koen en Jean on 9/11/2015.
 */
public class ScoreboardActivity extends AppCompatActivity {
    private ScoreboardListData scoreboardListData;
    ListView listView;
    Button buttonGames, buttonWins, buttonMissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        makeCall();
        listView = (ListView)findViewById(R.id.listViewScores);

        buttonGames = (Button) findViewById(R.id.button_games);
        buttonMissions = (Button) findViewById(R.id.button_missions);
        buttonWins = (Button) findViewById(R.id.button_wins);

        buttonGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonGames.setEnabled(false);
                buttonMissions.setEnabled(true);
                buttonWins.setEnabled(true);
                Collections.sort(scoreboardListData.List, new Comparator<ScoreboardData>() {
                    @Override
                    public int compare(ScoreboardData lhs, ScoreboardData rhs) {
                        return isBetter(rhs.Games, lhs.Games, rhs.Wins, lhs.Wins, rhs.Missions, lhs.Missions);
                    }
                });
                setListView();
            }
        });
        buttonWins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonGames.setEnabled(true);
                buttonMissions.setEnabled(true);
                buttonWins.setEnabled(false);
                Collections.sort(scoreboardListData.List, new Comparator<ScoreboardData>() {
                    @Override
                    public int compare(ScoreboardData lhs, ScoreboardData rhs) {
                        return isBetter(rhs.Wins, lhs.Wins, lhs.Games, rhs.Games, rhs.Missions, lhs.Missions); //Games omgewisseld omdat minder gespeelde games bij hetzelfde aantal wins beter is
                    }
                });
                setListView();
            }
        });
        buttonMissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonGames.setEnabled(true);
                buttonMissions.setEnabled(false);
                buttonWins.setEnabled(true);
                Collections.sort(scoreboardListData.List, new Comparator<ScoreboardData>() {
                    @Override
                    public int compare(ScoreboardData lhs, ScoreboardData rhs) {
                        return isBetter(rhs.Missions, lhs.Missions, rhs.Games, lhs.Games, rhs.Wins, lhs.Wins);
                    }
                });
                setListView();
            }
        });
    }
    private void makeCall(){
        Call<ReturnData<ScoreboardListData>> call =
                new ApiUtil().getApiInterface(this).loadScoreboard();
        final RequestUtil<ScoreboardListData> requestUtil = new RequestUtil<>(this, call);
        requestUtil.makeRequest(new RequestInterface<ScoreboardListData>() {
            @Override
            public void onSucces(ScoreboardListData body) {
                scoreboardListData = body;
                Collections.sort(scoreboardListData.List, new Comparator<ScoreboardData>() {
                    @Override
                    public int compare(ScoreboardData lhs, ScoreboardData rhs) {
                        return isBetter(rhs.Games, lhs.Games, rhs.Wins, lhs.Wins, rhs.Missions, lhs.Missions);
                    }
                });
                buttonGames.setEnabled(false);
                setListView();
            }

            @Override
            public void onError(ErrorData error) {
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setListView(){
        if(scoreboardListData != null) {
            for (ScoreboardData scoreboardData : scoreboardListData.List){
                scoreboardData.isOpen = false;
            }
            final String sorting;
            if(!buttonGames.isEnabled()){
                sorting = "Games";
            }
            else if(!buttonMissions.isEnabled()){
                sorting = "Missiosn";
            }
            else{
                sorting = "Wins";
            }
            listView.setAdapter(new ScoreboardAdapter(ScoreboardActivity.this, scoreboardListData.List, sorting));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LinearLayout linearLayoutExtraData = (LinearLayout) view.findViewById(R.id.LinearLayoutExtraData);
                    TextView textViewRelevantValue = (TextView) view.findViewById(R.id.TextViewRelevantValue);
                    scoreboardListData.List.get(position).isOpen ^= true; // This change true to false and false to true
                    ScoreboardData item = scoreboardListData.List.get(position);
                    if (item.isOpen)
                        linearLayoutExtraData.setVisibility(View.VISIBLE);
                    else
                        linearLayoutExtraData.setVisibility(View.GONE);
                    textViewRelevantValue.setVisibility(View.VISIBLE);
                    if(sorting.equals("Games")){
                        textViewRelevantValue.setText(String.valueOf(item.Games));
                    }
                    else if(sorting.equals("Wins")){
                        textViewRelevantValue.setText(String.valueOf(item.Wins));

                    }
                    else if(sorting.equals("Missions")){
                        textViewRelevantValue.setText(String.valueOf(item.Missions));
                    }
                    else{
                        textViewRelevantValue.setVisibility(View.GONE);
                    }
                    if(linearLayoutExtraData.getVisibility() == View.VISIBLE){
                        textViewRelevantValue.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    public int isBetter(int rhs1, int lhs1, int rhs2, int lhs2, int rhs3, int lhs3) {
        if (rhs1 > lhs1) {
            return 1;
        }
        else if (lhs1 > rhs1) {
            return -1;
        }
        if (rhs2 > lhs2) {
            return 1;
        }
        else if (lhs2 > rhs2) {
            return -1;
        }
        if (rhs3 > lhs3) {
            return 1;
        }
        else {
            return -1;
        }
    }
}
