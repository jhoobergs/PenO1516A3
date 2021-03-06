package be.cwa3.nightgame;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.Collections;
import java.util.Comparator;

import be.cwa3.nightgame.Adapters.ScoreboardAdapter;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Data.ScoreboardData;
import be.cwa3.nightgame.Data.ScoreboardListData;
import be.cwa3.nightgame.Utils.ApiUtil;
import be.cwa3.nightgame.Utils.ErrorUtil;
import be.cwa3.nightgame.Utils.OnSwipeTouchListener;
import be.cwa3.nightgame.Utils.RequestInterface;
import be.cwa3.nightgame.Utils.RequestUtil;
import be.cwa3.nightgame.Utils.SettingsUtil;
import be.cwa3.nightgame.Utils.SharedPreferencesKeys;
import retrofit.Call;

/**
 * Improved/Upgraded/Enhanced by Koen en Jean on 9/11/2015.
 */
public class ScoreboardActivity extends AppCompatActivity {
    private ScoreboardListData scoreboardListData;
    ListView listView;
    Button buttonGames, buttonWins, buttonMissions;

    private OnSwipeTouchListener swipeListener;
    private ViewSwitcher viewSwitcher;
    private Animation inAnimationForward;
    private Animation outAnimationForward;
    private Animation inAnimationBackward;
    private Animation outAnimationBackward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        makeGetScoreboardCall();
        listView = (ListView)findViewById(R.id.listview_scores);

        buttonGames = (Button) findViewById(R.id.button_games);
        buttonMissions = (Button) findViewById(R.id.button_missions);
        buttonWins = (Button) findViewById(R.id.button_wins);

        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewswitcher);
        inAnimationForward = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        outAnimationForward = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
        inAnimationBackward = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        outAnimationBackward = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);


        buttonGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortedOnGames();
            }
        });
        buttonWins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortedOnWins();
            }
        });
        buttonMissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortedOnMissions();
            }
        });


        swipeListener = new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                viewSwitcher.setInAnimation(inAnimationForward);
                viewSwitcher.setOutAnimation(outAnimationForward);
                if (!buttonWins.isEnabled()){
                    showSortedOnMissions();
                }else if(!buttonGames.isEnabled()){
                    showSortedOnWins();
                }
            }

            @Override
            public void onSwipeRight() {
                viewSwitcher.setInAnimation(inAnimationBackward);
                viewSwitcher.setOutAnimation(outAnimationBackward);
                if (!buttonMissions.isEnabled()){
                    showSortedOnWins();
                }else if(!buttonWins.isEnabled()){
                    showSortedOnGames();
                }
            }
        };

    }

    private void showSortedOnMissions() {
        buttonGames.setEnabled(true);
        buttonMissions.setEnabled(false);
        buttonWins.setEnabled(true);
        Collections.sort(scoreboardListData.List, new Comparator<ScoreboardData>() {
            @Override
            public int compare(ScoreboardData lhs, ScoreboardData rhs) {
                return isBetter(rhs.Missions, lhs.Missions, lhs.Games, rhs.Games, rhs.Wins, lhs.Wins);
            }
        });
        setListView();
    }

    private void showSortedOnWins() {
        buttonGames.setEnabled(true);
        buttonMissions.setEnabled(true);
        buttonWins.setEnabled(false);
        Collections.sort(scoreboardListData.List, new Comparator<ScoreboardData>() {
            @Override
            public int compare(ScoreboardData lhs, ScoreboardData rhs) {
                return isBetter(rhs.Wins, lhs.Wins, lhs.Games, rhs.Games, rhs.Missions, lhs.Missions);
                //Games omgewisseld omdat minder gespeelde games bij hetzelfde aantal wins beter is
            }
        });
        setListView();
    }

    private void showSortedOnGames() {
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        swipeListener.onTouch(null, ev);
        return super.dispatchTouchEvent(ev);
    }

    private void makeGetScoreboardCall(){
        Call<ReturnData<ScoreboardListData>> call =
                new ApiUtil().getApiInterface(this).loadScoreboard();
        final RequestUtil<ScoreboardListData> requestUtil = new RequestUtil<>(this, null, call);
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
                sorting = "Missions";
            }
            else{
                sorting = "Wins";
            }
            String Username = new SettingsUtil(this).getString(SharedPreferencesKeys.UsernameString);
            listView.setAdapter(new ScoreboardAdapter(ScoreboardActivity.this, scoreboardListData.List, sorting, Username));
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
                    if("Games".equals(sorting)){
                        makeBold(item.Games,textViewRelevantValue,view,R.id.TextViewTextGames,R.id.TextViewGames);
                    }
                    else if("Wins".equals(sorting)){
                        makeBold(item.Wins,textViewRelevantValue,view,R.id.TextViewTextWins,R.id.TextViewWins);
                    }
                    else if("Missions".equals(sorting)){
                        makeBold(item.Missions,textViewRelevantValue,view,R.id.TextViewTextMissions,R.id.TextViewMissions);
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

    private void makeBold(int type,TextView textViewRelevantValue, View view,int text, int number) {
        textViewRelevantValue.setText(String.valueOf(type));

        TextView Text = (TextView) view.findViewById(text);
        Text.setTypeface(null, Typeface.BOLD);
        TextView Number = (TextView) view.findViewById(number);
        Number.setTypeface(null, Typeface.BOLD);
    }


}
