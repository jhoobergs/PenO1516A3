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

import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.LoginRequestData;
import be.cwa3.nightgame.Data.LoginReturnData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Utils.ApiUtil;
import be.cwa3.nightgame.Utils.ErrorUtil;
import be.cwa3.nightgame.Utils.RequestInterface;
import be.cwa3.nightgame.Utils.RequestUtil;
import be.cwa3.nightgame.Utils.SettingsUtil;
import be.cwa3.nightgame.Utils.SharedPreferencesKeys;
import retrofit.Call;


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
                Intent create = new Intent(getApplicationContext(), CreateAccountActivity.class);
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
        RequestUtil<LoginReturnData> requestUtil = new RequestUtil<>(this, call);
        requestUtil.makeRequest(new RequestInterface<LoginReturnData>() {
            @Override
            public void onSucces(LoginReturnData body) {
                //Has to be overriden
                //Logged in
                new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.TokenString, body.Token);
                new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.UsernameString, body.Username);
                Toast.makeText(getApplicationContext(), String.format("Ingelogd als %s", body.Username), Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(i);
            }

            @Override
            public void onError(ErrorData error) {
                //Has to be overriden
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();
            }

        });
    }
}
