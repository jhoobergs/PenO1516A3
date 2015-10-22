package be.cwa3.nightgame;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateNewAccountActivity extends AppCompatActivity {
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

        enterName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                HandleButtonLogin();
                return false;
            }
        });

        enterEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                HandleButtonLogin();
                return false;
            }
        });

        enterPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                HandleButtonLogin();
                return false;
            }
        });

        repeatPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                HandleButtonLogin();
                return false;
            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(home);
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
}