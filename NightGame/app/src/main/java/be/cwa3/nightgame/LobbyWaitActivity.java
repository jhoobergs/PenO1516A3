package be.cwa3.nightgame;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.okhttp.ResponseBody;

import org.joda.time.DateTime;

import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;

import at.grabner.circleprogress.TextMode;
import be.cwa3.nightgame.Adapters.FriendImageAdapter;
import be.cwa3.nightgame.Adapters.LobbyWaitAdapter;
import be.cwa3.nightgame.Data.Empty;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.GameGetDataRequestData;
import be.cwa3.nightgame.Data.GameSendDataRequestData;
import be.cwa3.nightgame.Data.JoinLobbyRequestData;
import be.cwa3.nightgame.Data.LobbiesData;
import be.cwa3.nightgame.Data.LocationData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Utils.ApiUtil;
import be.cwa3.nightgame.Utils.ErrorUtil;
import be.cwa3.nightgame.Utils.RequestInterface;
import be.cwa3.nightgame.Utils.RequestUtil;

import be.cwa3.nightgame.Utils.SettingsUtil;
import be.cwa3.nightgame.Utils.SharedPreferencesKeys;
import retrofit.Call;

/**
 * Created by kevin on 12/11/2015.
 */
public class LobbyWaitActivity extends AppCompatActivity{

    private String gameId;
    CircleProgressView mCircleView;
    ListView listView;
    Boolean mShowUnit = true;
    LobbiesData gameData;

    private Handler customHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobbywait);
        gameId = new SettingsUtil(this).getString(SharedPreferencesKeys.GameIDString);
        if (gameId.equals("")){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        listView = (ListView) findViewById(R.id.lobby_players_list);
        mCircleView = (CircleProgressView) findViewById(R.id.circleView);
        mCircleView.setValue(0);
        mCircleView.setSeekModeEnabled(false);
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        String gameId = new SettingsUtil(this).getString(SharedPreferencesKeys.GameIDString);
        makeCall(new GameGetDataRequestData(gameId));
        Snackbar.make(coordinatorLayout,getString(R.string.refresh_data), Snackbar.LENGTH_INDEFINITE).show();



    }

    private void makeCall(GameGetDataRequestData data){
        Call<ReturnData<LobbiesData>> call = new ApiUtil().getApiInterface(this).getLobbyData(data);
        RequestUtil<LobbiesData> requestUtil = new RequestUtil<>(this, null, call);
        requestUtil.makeRequest(new RequestInterface<LobbiesData>() {
            @Override
            public void onSucces(LobbiesData body) {
                customHandler.postDelayed(sendData, 30*1000);
                gameData = body;
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(gameData.Name);
                }
                gameData.Players.add(gameData.Player);
                if(gameData.IsStarted){
                    startGameActivity();
                }
                else if (gameData.TimerDate== null) {
                    mCircleView.setTextMode(TextMode.TEXT);
                    mCircleView.setAutoTextSize(true);
                    mCircleView.spin();
                    mCircleView.setText(String.format("Waiting %d / %d", gameData.Players.size(), gameData.MinPlayers));
                    mCircleView.setShowTextWhileSpinning(true);
                    listView.setAdapter(new LobbyWaitAdapter(LobbyWaitActivity.this, gameData.Players));
                    //automatic refresh?
                }
                else if(gameData.TimerDate.isBefore(DateTime.now().getMillis())){
                    startGameActivity();
                }
                else {
                    mCircleView.setTextMode(TextMode.PERCENT);
                    mCircleView.setAutoTextSize(true);
                    mCircleView.stopSpinning();
                    mCircleView.setMaxValue(100);
                    Long Tminus = (gameData.TimerDate.getMillis() - DateTime.now().getMillis())/1000;
                    mCircleView.setValue((300 - Tminus) / 3);
                    listView.setAdapter(new LobbyWaitAdapter(LobbyWaitActivity.this, gameData.Players));
                }
            }

            @Override
            public void onError(ErrorData error) {
                customHandler.postDelayed(sendData, 30*1000);
                if(error.Errors.contains(4) || error.Errors.contains(6)) {
                    new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.GameIDString, "");
                    Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                    startActivity(intent);
                    finish();
                }

                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Context context, View view, Throwable t) {
                super.onFailure(context, view, t);
                customHandler.postDelayed(sendData, 30*1000);
            }

            @Override
            public void onServerError(Context context, int code, View view, ResponseBody responseBody) {
                customHandler.postDelayed(sendData, 30*1000);
                super.onServerError(context, code, view, responseBody);
            }
        });

    }

    private void startGameActivity() {
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private Runnable sendData = new Runnable() {
        @Override
        public void run() {

            makeCall(new GameGetDataRequestData(gameId));


        }
    };

}
