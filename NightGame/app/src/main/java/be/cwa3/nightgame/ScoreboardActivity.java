package be.cwa3.nightgame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import be.cwa3.nightgame.Adapters.FriendsAdapter;
import be.cwa3.nightgame.Adapters.ScoreboardAdapter;

/**
 * Created by kevin on 19/10/2015.
 */
public class ScoreboardActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);



        listView = (ListView)findViewById(R.id.listViewScores);
        List<String> fakeData = new ArrayList<>();
        fakeData.add("-xXx_PussySlayer_69_xXx-");
        fakeData.add("test1");
        fakeData.add("Gebruiker=HOMO");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("trut");

        listView.setAdapter(new ScoreboardAdapter(this, fakeData));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout linearLayoutextraData = (LinearLayout) view.findViewById(R.id.extra_data);
                if(linearLayoutextraData.getVisibility() == View.VISIBLE)
                    linearLayoutextraData.setVisibility(View.GONE);
                else
                    linearLayoutextraData.setVisibility(View.VISIBLE);

            }
        });
    }
}
