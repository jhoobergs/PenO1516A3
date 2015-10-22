package be.cwa3.nightgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import be.cwa3.nightgame.Adapters.PlayAdapter;
import be.cwa3.nightgame.Adapters.ScoreboardAdapter;

/**
 * Created by Koen on 19/10/2015.
 */
public class PlayActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        List<String> fakeData = new ArrayList<>();
        fakeData.add("Lobby 1");
        fakeData.add("Lobby 2");
        fakeData.add("Lobby 3");
        fakeData.add("Lobby 4");
        fakeData.add("Lobby 5");
        fakeData.add("Lobby 6");
        fakeData.add("Lobby 7");
        fakeData.add("Lobby 8");
        fakeData.add("Lobby 9");
        fakeData.add("Lobby 10");
//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myStringArray);
        ListView listViewLobbies= (ListView) findViewById(R.id.listViewLobbies);
//        listViewLobbies.setAdapter(adapter);

        listViewLobbies.setAdapter(new PlayAdapter(this, fakeData));
        listViewLobbies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout linearLayoutextraData = (LinearLayout) view.findViewById(R.id.lobbies_data);
                if (linearLayoutextraData.getVisibility() == View.VISIBLE)
                    linearLayoutextraData.setVisibility(View.GONE);
                else
                    linearLayoutextraData.setVisibility(View.VISIBLE);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(getApplicationContext(), CreateLobbyActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
