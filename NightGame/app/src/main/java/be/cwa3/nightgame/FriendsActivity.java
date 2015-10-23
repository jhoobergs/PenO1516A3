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
import be.cwa3.nightgame.Data.FriendListData;
import be.cwa3.nightgame.Http.Api.ApiInterface;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<FriendListData> call = apiInterface.loadFriends();
        call.enqueue(new Callback<FriendListData>() {
            @Override
            public void onResponse(Response<FriendListData> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    listView.setAdapter(new FriendsAdapter(FriendsActivity.this, response.body().List));
                }
                else
                    Toast.makeText(getApplicationContext(), "No succes", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
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