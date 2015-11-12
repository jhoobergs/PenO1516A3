package be.cwa3.nightgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.games.Game;

import be.cwa3.nightgame.Data.CreateLobbyRequestData;
import be.cwa3.nightgame.Data.CreateLobbyReturnData;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Utils.ApiUtil;
import be.cwa3.nightgame.Utils.ErrorUtil;
import be.cwa3.nightgame.Utils.RequestInterface;
import be.cwa3.nightgame.Utils.RequestUtil;
import retrofit.Call;

/**
 * Created by Gebruiker on 19/10/2015.
 */
public class CreateLobbyActivity extends AppCompatActivity {
    NumberPicker numberPickerMinValue, numberPickerMaxValue;
    EditText editTextGroupName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createlobby);
        editTextGroupName = (EditText) findViewById(R.id.editTextGroupName);
        numberPickerMaxValue = (NumberPicker) findViewById(R.id.numberPickerManValue);
        numberPickerMinValue = (NumberPicker) findViewById(R.id.numberPickerMinValue);
        setMinAndMaxOfNumberPicker(numberPickerMaxValue, 4, 10);
        setMinAndMaxOfNumberPicker(numberPickerMinValue, 4, 10);
    }

    public void setMinAndMaxOfNumberPicker(NumberPicker np, int min, int max) {
        np.setMaxValue(max);
        np.setMinValue(min);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_createlobby_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.create:
                if (numberPickerMinValue.getValue() > numberPickerMaxValue.getValue()
                        || editTextGroupName.getText().toString().equals(""))
                {
                    Toast.makeText(CreateLobbyActivity.this, "Data not entered correctly!", Toast.LENGTH_LONG).show();
                }
                else {
                    CreateLobbyRequestData data = new CreateLobbyRequestData();
                    data.Name = editTextGroupName.getText().toString();
                    data.MinPlayers = numberPickerMinValue.getValue();
                    data.MaxPlayers = numberPickerMaxValue.getValue();

                    makeCreateLobbyCall(data);

                }
//
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void makeCreateLobbyCall(CreateLobbyRequestData data){
        Call<ReturnData<CreateLobbyReturnData>> call = new ApiUtil().getApiInterface(this).sendCreateLobbyRequest(data);
        RequestUtil<CreateLobbyReturnData> requestUtil = new RequestUtil<>(this, call);
        requestUtil.makeRequest(new RequestInterface<CreateLobbyReturnData>() {

            @Override
            public void onSucces(CreateLobbyReturnData body) {
                Log.d("test", body.GameId);
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(ErrorData error) {
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();
            }
        });
    }

}