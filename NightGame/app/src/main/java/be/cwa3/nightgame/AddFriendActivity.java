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
public class AddFriendActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend);
        listView = (ListView)findViewById(R.id.listViewRecentContact);
        List<String> fakeData = new ArrayList<>();
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

