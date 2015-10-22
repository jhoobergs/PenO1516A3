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


public class LoginActivity extends AppCompatActivity {
    private Button buttonLogin;
    private EditText enterName;
    private EditText enterPassword;
    private TextView createNewAccount;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        buttonLogin = (Button) findViewById(R.id.button_login);
        enterName = (EditText) findViewById(R.id.enter_name);

        enterPassword = (EditText) findViewById(R.id.enter_password);
        enterPassword.setTypeface(Typeface.DEFAULT);
        enterPassword.setTransformationMethod(new PasswordTransformationMethod());

        createNewAccount = (TextView) findViewById(R.id.textview_create_new_account);

        buttonLogin.setEnabled(false);
        enterName.setOnKeyListener(new View.OnKeyListener() {
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

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HandleButtonLogin()){
                    Toast.makeText(getApplicationContext(), "Ingelogd als xXx_Pussyslayer_69_xXx", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(i);
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
}
