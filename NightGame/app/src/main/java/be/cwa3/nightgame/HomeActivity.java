package be.cwa3.nightgame;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import be.cwa3.nightgame.Utils.SettingsUtil;
import be.cwa3.nightgame.Utils.SharedPreferencesKeys;

/**
 * Created by jesse on 12/10/2015.
 */
public class HomeActivity extends AppCompatActivity {
    Button buttonPlay, buttonScoreBoard, buttonFriend;
    int MY_PERMISSIONS;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        buttonPlay = (Button) findViewById(R.id.button_play);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPlay.setEnabled(false);
                Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
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

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.TokenString, "");
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                return true;

            /*case R.id.sound:
                Intent intenta = new Intent(getApplicationContext(), SoundActivity.class);
                startActivity(intenta);
                return true;
            case R.id.accelerometer:
                Intent intentb = new Intent(getApplicationContext(), AccelerometerActivity.class);
                startActivity(intentb);
                return true;*/

            case R.id.rules:
                Intent intent1 = new Intent(getApplicationContext(), RulesActivity.class);
                startActivity(intent1);
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
