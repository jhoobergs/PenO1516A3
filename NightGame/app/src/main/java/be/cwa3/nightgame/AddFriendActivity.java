package be.cwa3.nightgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.cwa3.nightgame.Adapters.FriendImageAdapter;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.FriendAddRequestData;
import be.cwa3.nightgame.Data.FriendAddReturnData;
import be.cwa3.nightgame.Data.FriendSearchRequestData;
import be.cwa3.nightgame.Data.FriendSearchReturnData;
import be.cwa3.nightgame.Data.FriendSearchReturnItemData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Utils.ApiUtil;
import be.cwa3.nightgame.Utils.ErrorUtil;
import be.cwa3.nightgame.Utils.RequestInterface;
import be.cwa3.nightgame.Utils.RequestUtil;
import retrofit.Call;

/**
 * Created by kevin on 19/10/2015.
 */
public class AddFriendActivity extends AppCompatActivity {
    private EditText enterFriendName;
    ListView listView;
    FriendSearchReturnData friendSearchReturnData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend);

        enterFriendName = (EditText) findViewById(R.id.editTextFriend);

        listView = (ListView) findViewById(R.id.listViewRecentContact);

        enterFriendName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (enterFriendName.getText().toString().length() >= 3) {
                    FriendSearchRequestData data = new FriendSearchRequestData(enterFriendName.getText().toString());
                    makeSearchCall(data);
                }
                if (enterFriendName.getText().toString().isEmpty()) {
                    List<FriendSearchReturnItemData> empty = new ArrayList<FriendSearchReturnItemData>();
                    friendSearchReturnData.List = empty;
                    listView.setAdapter(new FriendImageAdapter(AddFriendActivity.this, empty));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendAddRequestData data = new FriendAddRequestData(friendSearchReturnData.List.get(position).Name);
                makeAddCall(data);

            }
        });
    }


    private void makeSearchCall(FriendSearchRequestData data) {
        Call<ReturnData<FriendSearchReturnData>> call = new ApiUtil().getApiInterface(this).searchFriends(data);
        RequestUtil<FriendSearchReturnData> requestUtil = new RequestUtil<>(this, call);
        requestUtil.makeRequest(new RequestInterface<FriendSearchReturnData>() {
            @Override
            public void onSucces(FriendSearchReturnData body) {
                friendSearchReturnData = body;
                listView.setAdapter(new FriendImageAdapter(AddFriendActivity.this, body.List));
            }

            @Override
            public void onError(ErrorData error) {
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void makeAddCall(FriendAddRequestData data) {
        Call<ReturnData<FriendAddReturnData>> call = new ApiUtil().getApiInterface(this).addFriend(data);
        RequestUtil<FriendAddReturnData> requestUtil = new RequestUtil<>(this, call);
        requestUtil.makeRequest(new RequestInterface<FriendAddReturnData>() {
            @Override
            public void onSucces(FriendAddReturnData body) {
                Toast.makeText(AddFriendActivity.this, "Friend added!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(ErrorData error) {
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();

            }
        });
    }
}



