package be.cwa3.nightgame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import be.cwa3.nightgame.Adapters.FriendsAdapter;

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
        fakeData.add("-XXX_PussySlayer_XXX-");
        fakeData.add("test1");
        fakeData.add("JEAN=HOMO");
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
        fakeData.add("test1");

        listView.setAdapter(new FriendsAdapter(this, fakeData));

    }
}
