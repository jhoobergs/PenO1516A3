package be.cwa3.nightgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.lang.reflect.GenericArrayType;

import at.grabner.circleprogress.CircleProgressView;

import be.cwa3.nightgame.Adapters.FriendImageAdapter;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.JoinLobbyRequestData;
import be.cwa3.nightgame.Data.LobbiesData;
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
public class LobbyWaitActivity extends AppCompatActivity implements CircleProgressView.OnProgressChangedListener {

    CircleProgressView mCircleView;
    ListView listView;
    Boolean mShowUnit = true;
    LobbiesData gameData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobbywait);
        listView = (ListView) findViewById(R.id.lobby_players_list);
        mCircleView = (CircleProgressView) findViewById(R.id.circleView);
        mCircleView.setSeekModeEnabled(false);

        String gameId = new SettingsUtil(this).getString(SharedPreferencesKeys.GameIDString);
        makeCall(new JoinLobbyRequestData(gameId));




    }

    @Override
    public void onProgressChanged(float value) {

    }

    private void makeCall(JoinLobbyRequestData data){
        Call<ReturnData<LobbiesData>> call = new ApiUtil().getApiInterface(this).getLobbyData(data);
        RequestUtil<LobbiesData> requestUtil = new RequestUtil<>(this, call);
        requestUtil.makeRequest(new RequestInterface<LobbiesData>() {
            @Override
            public void onSucces(LobbiesData body) {

                gameData = body;
                if(gameData.IsStarted){
                    startGameActivity();
                }
                else if (gameData.TimerDate== null) {
                    mCircleView.setAutoTextSize(true);
                    mCircleView.spin();
                    mCircleView.setText(String.format("Waiting %d / %d", gameData.Players.size(), gameData.MinPlayers));
                    listView.setAdapter(new FriendImageAdapter(LobbyWaitActivity.this, gameData.Players));
                    //automatic refresh?
                }
                else if(gameData.TimerDate.isBefore(DateTime.now().getMillis())){
                    startGameActivity();
                }
                else {
                    mCircleView.setAutoTextSize(true);
                    mCircleView.stopSpinning();
                    mCircleView.setMaxValue(100);
                    Long Tminus = (gameData.TimerDate.getMillis() - DateTime.now().getMillis())/1000;
                    mCircleView.setValue((300 - Tminus) / 3);
                    listView.setAdapter(new FriendImageAdapter(LobbyWaitActivity.this, gameData.Players));
                }
            }

            @Override
            public void onError(ErrorData error) {
                if(error.Errors.contains(4) || error.Errors.contains(6)) {
                    new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.GameIDString, "");
                    Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                    startActivity(intent);
                    finish();
                }

                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void startGameActivity() {
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
