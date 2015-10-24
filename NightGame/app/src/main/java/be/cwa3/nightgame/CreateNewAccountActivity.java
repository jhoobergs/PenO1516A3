package be.cwa3.nightgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import be.cwa3.nightgame.Data.CreateNewAccountRequestData;
import be.cwa3.nightgame.Data.LoginReturnData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Http.Api.ApiInterface;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class CreateNewAccountActivity extends AppCompatActivity {
    private EditText enterName;
    private EditText enterEmail;
    private EditText enterPassword;
    private EditText repeatPassword;
    private Button buttonCreate;
    private TextView termsConditions;
    private SharedPreferences sharedPref;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        sharedPref = getPreferences(Context.MODE_PRIVATE);

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
                    Toast.makeText(CreateNewAccountActivity.this, "Data not entered correct!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(CreateNewAccountActivity.this, "Our legal department advised us to implement this button, however, it has no use.", Toast.LENGTH_LONG).show();
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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ReturnData<LoginReturnData>> call = apiInterface.sendCreateNewAccountRequest(data);
        call.enqueue(new Callback<ReturnData<LoginReturnData>>() {
            @Override
            public void onResponse(Response<ReturnData<LoginReturnData>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    if(response.body().statusCode == 1) {
                        //Logged in
                        sharedPref.edit().putString(SharedPreferencesKeys.TokenString, response.body().body.Token).apply();
                        Toast.makeText(getApplicationContext(), String.format("Ingelogd als %s", response.body().body.Username), Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(i);
                    }
                    else if(response.body().statusCode == 0){
                        Toast.makeText(getApplicationContext(), response.body().error, Toast.LENGTH_SHORT).show();
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