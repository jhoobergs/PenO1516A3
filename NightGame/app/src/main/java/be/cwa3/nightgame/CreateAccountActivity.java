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

import be.cwa3.nightgame.Data.CreateNewAccountRequestData;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.LoginReturnData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Utils.ApiUtil;
import be.cwa3.nightgame.Utils.ErrorUtil;
import be.cwa3.nightgame.Utils.RequestInterface;
import be.cwa3.nightgame.Utils.RequestUtil;
import be.cwa3.nightgame.Utils.SettingsUtil;
import be.cwa3.nightgame.Utils.SharedPreferencesKeys;
import retrofit.Call;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText enterName;
    private EditText enterEmail;
    private EditText enterPassword;
    private EditText repeatPassword;
    private Button buttonCreate;
    private TextView termsConditions;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        enterName = (EditText) findViewById(R.id.enter_name);
        enterEmail = (EditText) findViewById(R.id.enter_email);

        enterPassword = (EditText) findViewById(R.id.enter_password);
        enterPassword.setTypeface(Typeface.DEFAULT);
        enterPassword.setTransformationMethod(new PasswordTransformationMethod());

        repeatPassword = (EditText) findViewById(R.id.repeat_password);
        repeatPassword.setTypeface(Typeface.DEFAULT);
        repeatPassword.setTransformationMethod(new PasswordTransformationMethod());

        buttonCreate = (Button) findViewById(R.id.button_create);
        termsConditions = (TextView) findViewById(R.id.terms_and_conditions);

        buttonCreate.setEnabled(false);

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

        enterEmail.addTextChangedListener(new TextWatcher() {
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

        repeatPassword.addTextChangedListener(new TextWatcher() {
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

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enterEmail.getText().toString().equals("")
                        || enterName.getText().toString().equals("")
                        || enterPassword.getText().toString().equals("")
                        || !repeatPassword.getText().toString().equals(enterPassword.getText().toString())) {
                    Toast.makeText(CreateAccountActivity.this, "Data not entered correctly!", Toast.LENGTH_LONG).show();
                }
                else if ( enterName.getText().toString().length()< 4) {
                    Toast.makeText(CreateAccountActivity.this, "Name must be at least 3 characters!", Toast.LENGTH_LONG).show();
                }
                else{
                    CreateNewAccountRequestData data = new CreateNewAccountRequestData();
                    data.Username = enterName.getText().toString();
                    data.Email = enterEmail.getText().toString();
                    data.Password = enterPassword.getText().toString();
                    data.PasswordRepeat = repeatPassword.getText().toString();
                    makeNewAccountCall(data);
                }

            }

        });

        termsConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateAccountActivity.this, "Our legal department advised us to implement this button, however, it has no use.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean HandleButtonLogin() {
        if (enterName.getText().toString().equals("") ||
                enterEmail.getText().toString().equals("") ||
                enterPassword.getText().toString().equals("") ||
                repeatPassword.getText().toString().equals("")) {
            buttonCreate.setEnabled(false);
            return false;
        }
        else{
            buttonCreate.setEnabled(true);
            return true;
        }
    }

    private void makeNewAccountCall(CreateNewAccountRequestData data){
        Call<ReturnData<LoginReturnData>> call = new ApiUtil().getApiInterface(this).sendCreateNewAccountRequest(data);
        RequestUtil<LoginReturnData> requestUtil = new RequestUtil<>(this,null, call);
        requestUtil.makeRequest(new RequestInterface<LoginReturnData>() {
            @Override
            public void onSucces(LoginReturnData body) {
                //Has to be overriden
                //Logged in
                new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.TokenString, body.Token);
                Toast.makeText(getApplicationContext(), String.format("Ingelogd als %s", body.Username), Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(i);
            }

            @Override
            public void onError(ErrorData error) {
                //Has to be overriden
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(),error.Errors), Toast.LENGTH_SHORT).show();
            }

        });
    }
}