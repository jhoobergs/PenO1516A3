package be.cwa3.nightgame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import be.cwa3.nightgame.Adapters.FriendsAdapter;

/**
 * Created by kevin on 19/10/2015.
 */
public class FriendsActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        listView = (ListView)findViewById(R.id.listView);
        List<String> fakeData = new ArrayList<>();
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
        fakeData.add("test1");
        fakeData.add("test1");
        fakeData.add("test1");

        listView.setAdapter(new FriendsAdapter(this, fakeData));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.friends_activity, menu);
            return super.onCreateOptionsMenu(menu);
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                return true;
            default:
                return super.onOptionsItemSelected(item);
            }
        }

}
