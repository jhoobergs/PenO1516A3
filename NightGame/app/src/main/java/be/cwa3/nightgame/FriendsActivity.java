package be.cwa3.nightgame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

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

        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,fakeData)
        );
    }
}
