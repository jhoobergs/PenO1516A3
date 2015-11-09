package be.cwa3.nightgame;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.util.List;

import be.cwa3.nightgame.Data.FriendData;
import be.cwa3.nightgame.Data.FriendListData;
import be.cwa3.nightgame.Http.Api.ApiInterface;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import be.cwa3.nightgame.BuildConfig;
/**
 * Created by jesse on 12/10/2015.
 */
public class HomeActivity extends AppCompatActivity {
    Button buttonPlay, buttonScoreBoard, buttonFriend;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        buttonPlay = (Button) findViewById(R.id.button_play);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPlay.setEnabled(false);
                Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                startActivity(intent);
            }
        });
        buttonScoreBoard = (Button) findViewById(R.id.button_scoreboard);
        buttonScoreBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonScoreBoard.setEnabled(false);
                Intent intent = new Intent(getApplicationContext(), ScoreboardActivity.class);
                startActivity(intent);
            }
        });

        buttonFriend = (Button) findViewById(R.id.button_friends);
        buttonFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonFriend.setEnabled(false);
                Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sound:
                Intent intent = new Intent(getApplicationContext(), SoundActivity.class);
                startActivity(intent);
                return true;
            case R.id.accelerometer:
                Intent i = new Intent(getApplicationContext(), AccelerometerActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        buttonPlay.setEnabled(true);
        buttonFriend.setEnabled(true);
        buttonScoreBoard.setEnabled(true);
    }
}
