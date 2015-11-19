package be.cwa3.nightgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;

import be.cwa3.nightgame.Adapters.FriendsAdapter;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.FriendAddReturnData;

import be.cwa3.nightgame.Data.FriendData;
import be.cwa3.nightgame.Data.FriendListData;
import be.cwa3.nightgame.Data.FriendRemoveRequestData;
import be.cwa3.nightgame.Data.FriendRemoveReturnData;
import be.cwa3.nightgame.Data.LoginReturnData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Data.ScoreboardData;
import be.cwa3.nightgame.Utils.ApiUtil;
import be.cwa3.nightgame.Utils.ErrorUtil;
import be.cwa3.nightgame.Utils.RequestInterface;
import be.cwa3.nightgame.Utils.RequestUtil;
import retrofit.Call;

/**
 * Created by kevin on 19/10/2015.
 */
public class FriendsActivity extends AppCompatActivity {
    FriendListData friendListData;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        makeCall();


        listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent,final View view, final int position, long id) {
                Button buttonRemove = (Button) view.findViewById(R.id.buttonRemove);
                buttonRemove.setVisibility(View.VISIBLE);

                buttonRemove.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        makeRemoveCall(new FriendRemoveRequestData(friendListData.List.get(position).Name));
                    }
                });
                return false;
            }
        });
    }

    private void makeRemoveCall(FriendRemoveRequestData data) {
        Call<ReturnData<FriendRemoveReturnData>> call = new ApiUtil().getApiInterface(this).removeFriend(data);
        RequestUtil<FriendRemoveReturnData> requestUtil = new RequestUtil<>(this, call);
        requestUtil.makeRequest(new RequestInterface<FriendRemoveReturnData>() {

            @Override
            public void onSucces(FriendRemoveReturnData body) {
                Toast.makeText(getApplicationContext(), "Friend removed!", Toast.LENGTH_LONG).show();
                makeCall();

            }

            @Override
            public void onError(ErrorData error) {
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();

            }
        });
    }





    private void makeCall(){
        Call<ReturnData<FriendListData>> call = new ApiUtil().getApiInterface(this).loadFriends();
        RequestUtil<FriendListData> requestUtil = new RequestUtil<>(this, call);
        requestUtil.makeRequest(new RequestInterface<FriendListData>() {
            @Override
            public void onSucces(FriendListData body) {
                Collections.sort(body.List, new Comparator<FriendData>() {
                    @Override
                    public int compare(FriendData lhs, FriendData rhs) {
                        return Sorting(rhs.Accepted, lhs.Accepted, rhs.IsSender, lhs.IsSender, rhs.Name, lhs.Name);
                    }
                });
                listView.setAdapter(new FriendsAdapter(FriendsActivity.this, body.List));
                friendListData = body;

            }

            @Override
            public void onError(ErrorData error) {
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private int Sorting(Boolean accepted, Boolean accepted1, Boolean isSender, Boolean isSender1, String name, String name1) {
        if (!accepted && !accepted1) {
            if (isSender && isSender1) {
                return name.compareTo(name1);
            }
            else if (!isSender && !isSender1) {
                return name.compareTo(name1);
            }
            else if (isSender && !isSender1) {
                return 1;
            }
            else {
                return -1;
            }
        }
        if(!accepted && accepted1) {
            return 1;
        }
        else {
            return name.compareTo(name1);
            }
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
