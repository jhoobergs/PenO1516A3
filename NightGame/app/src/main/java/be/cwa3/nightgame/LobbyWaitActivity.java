package be.cwa3.nightgame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

import be.cwa3.nightgame.Adapters.FriendImageAdapter;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.FriendSearchReturnData;
import be.cwa3.nightgame.Data.FriendSearchReturnItemData;
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
        setContentView(R.layout.activity_lobywait);


        String gameId = new SettingsUtil(this).getString(SharedPreferencesKeys.GameIDString);
        makeCall(new JoinLobbyRequestData(gameId));

        listView = (ListView) findViewById(R.id.lobby_players_list);
        final Long TminusFinal = (gameData.TimerDate.getMillis() - DateTime.now().getMillis())*1000;

        if(gameData.MinPlayers>=gameData.Players.size()){
            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            startActivity(intent);
        }else {
            if (gameData.TimerDate.equals(null)) {
                mCircleView.setAutoTextSize(true);
                mCircleView.spin();
                mCircleView.setText(String.format("%n Waiting %s / %s", gameData.Players.size(), gameData.MinPlayers));
                List<FriendSearchReturnItemData> empty = new ArrayList(gameData.Players);
                listView.setAdapter(new FriendImageAdapter(LobbyWaitActivity.this, empty));
                //automatic refresh?
            } else {
                mCircleView.setAutoTextSize(true);
                mCircleView.stopSpinning();
                mCircleView.setMaxValue(100);
                Long TminusCurr = (gameData.TimerDate.getMillis() - DateTime.now().getMillis()) * 1000;
                mCircleView.setValue(TminusCurr / TminusFinal);

                List<FriendSearchReturnItemData> empty = new ArrayList(gameData.Players);
                listView.setAdapter(new FriendImageAdapter(LobbyWaitActivity.this, empty));
            }
        }

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
            }

            @Override
            public void onError(ErrorData error) {
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
