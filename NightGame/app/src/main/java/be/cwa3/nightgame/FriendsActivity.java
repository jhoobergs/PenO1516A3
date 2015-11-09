package be.cwa3.nightgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import be.cwa3.nightgame.Adapters.FriendsAdapter;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.FriendListData;
import be.cwa3.nightgame.Data.LoginReturnData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Utils.ApiUtil;
import be.cwa3.nightgame.Utils.ErrorUtil;
import be.cwa3.nightgame.Utils.RequestInterface;
import be.cwa3.nightgame.Utils.RequestUtil;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by kevin on 19/10/2015.
 */
public class FriendsActivity extends AppCompatActivity {

    Button buttonAdd;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        makeCall();


        listView = (ListView)findViewById(R.id.listView);

    }

    private void makeCall(){
        Call<ReturnData<FriendListData>> call = new ApiUtil().getApiInterface(this).loadFriends();
        RequestUtil<FriendListData> requestUtil = new RequestUtil<>(this, call);
        requestUtil.makeRequest(new RequestInterface<FriendListData>() {
            @Override
            public void onSucces(FriendListData body) {
                listView.setAdapter(new FriendsAdapter(FriendsActivity.this, body.List));
            }

            @Override
            public void onError(ErrorData error) {
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();

            }
        });
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
                Intent intent = new Intent(getApplicationContext(), AddFriendActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
            }
        }


}
