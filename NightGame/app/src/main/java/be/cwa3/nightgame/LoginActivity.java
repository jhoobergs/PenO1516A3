package be.cwa3.nightgame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
            loadHomeActivity();
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
                    // Hide keyboard when clicked on the button
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "This should not happen!", Toast.LENGTH_LONG).show();
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

    private void loadHomeActivity() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    public boolean HandleButtonLogin() {
        // This function returns whether or not the fields are filled correct and also handles the state of the button.
        if (enterName.getText().toString().equals("") || enterPassword.getText().toString().equals("")){
            buttonLogin.setEnabled(false); //Make sure they don't click twice
            return false;
        }
        else{
            buttonLogin.setEnabled(true);
            return true;
        }
    }

    private void makeLoginCall(LoginRequestData data){
        Call<ReturnData<LoginReturnData>> call = new ApiUtil().getApiInterface(this).sendLoginRequest(data);
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        RequestUtil<LoginReturnData> requestUtil = new RequestUtil<>(this,coordinatorLayout, call);
        requestUtil.makeRequest(new RequestInterface<LoginReturnData>() {
            @Override
            public void onSucces(LoginReturnData body) {
                //Logged in
                new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.TokenString, body.Token);
                new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.UsernameString, body.Username);
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.signed_in_as), body.Username), Toast.LENGTH_LONG).show();
                loadHomeActivity();
            }

            @Override
            public void onError(ErrorData error) {
                Snackbar.make(coordinatorLayout, ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Snackbar.LENGTH_INDEFINITE).show();
            }

        });
    }
}
