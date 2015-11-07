package be.cwa3.nightgame;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import be.cwa3.nightgame.Data.LoginRequestData;
import be.cwa3.nightgame.Data.LoginReturnData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Utils.ApiUtil;
import be.cwa3.nightgame.Utils.SettingsUtil;
import be.cwa3.nightgame.Utils.SharedPreferencesKeys;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class LoginActivity extends AppCompatActivity {
    private Button buttonLogin;
    private EditText enterName;
    private EditText enterPassword;
    private TextView createNewAccount;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(!new SettingsUtil(this).getString(SharedPreferencesKeys.TokenString).equals("")){
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        }

        buttonLogin = (Button) findViewById(R.id.button_login);
        enterName = (EditText) findViewById(R.id.enter_name);
        enterPassword = (EditText) findViewById(R.id.enter_password);
        enterPassword.setTypeface(Typeface.DEFAULT);
        enterPassword.setTransformationMethod(new PasswordTransformationMethod());

        createNewAccount = (TextView) findViewById(R.id.textview_create_new_account);

        buttonLogin.setEnabled(false);

        enterName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                HandleButtonLogin();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        enterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                HandleButtonLogin();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HandleButtonLogin()){
                    LoginRequestData data = new LoginRequestData(enterName.getText().toString(), enterPassword.getText().toString());
                    makeLoginCall(data);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Fucking Android", Toast.LENGTH_LONG).show();
                }
            }
        });

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent create = new Intent(getApplicationContext(), CreateNewAccountActivity.class);
                startActivity(create);
            }
        });
    }

        public boolean HandleButtonLogin() {
        if (enterName.getText().toString().equals("") || enterPassword.getText().toString().equals("")){
            buttonLogin.setEnabled(false);
            return false;
        }
        else{
            buttonLogin.setEnabled(true);
            return true;
        }
    }

    private void makeLoginCall(LoginRequestData data){
        Call<ReturnData<LoginReturnData>> call = new ApiUtil().getApiInterface(this).sendLoginRequest(data);
        call.enqueue(new Callback<ReturnData<LoginReturnData>>() {
            @Override
            public void onResponse(Response<ReturnData<LoginReturnData>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    if(response.body().statusCode == 1) {
                        //Logged in
                        new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.TokenString, response.body().body.Token);
                        Toast.makeText(getApplicationContext(), String.format("Ingelogd als %s", response.body().body.Username), Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(i);
                    }
                    else if(response.body().statusCode == 2){
                        String error = "Error".concat(response.body().error.Errors.get(0).toString());
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    }

                } else
                    Toast.makeText(getApplicationContext(), "No succes", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
