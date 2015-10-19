package be.cwa3.nightgame;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });
        buttonScoreBoard = (Button) findViewById(R.id.button_scoreboard);
        buttonScoreBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScoreboardActivity.class);
                startActivity(intent);
            }
        });

        buttonFriend = (Button) findViewById(R.id.button_friends);
        buttonFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
                startActivity(intent);
            }
        });
    }
}
