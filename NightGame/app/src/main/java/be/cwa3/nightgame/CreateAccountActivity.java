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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import be.cwa3.nightgame.Data.CreateNewAccountRequestData;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.LoginReturnData;
import be.cwa3.nightgame.Data.ProfileImagesListData;
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

    private ImageView imageViewProfileImage;
    private ProfileImagesListData profileImagesListData;
    private int showedImageId = 0;
    private Button nextButton;
    private Button previousButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        imageViewProfileImage = (ImageView) findViewById(R.id.imageview_profile_image);
        getProfileImagesList();
        nextButton = (Button) findViewById(R.id.nextButton);
        previousButton = (Button) findViewById(R.id.previousButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showedImageId++;
                loadImage();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showedImageId--;
                loadImage();
            }
        });

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

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                HandleButtonCreate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        enterName.addTextChangedListener(textWatcher);

        enterEmail.addTextChangedListener(textWatcher);

        enterPassword.addTextChangedListener(textWatcher);

        repeatPassword.addTextChangedListener(textWatcher);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enterEmail.getText().toString().equals("")
                        || enterName.getText().toString().equals("")
                        || enterPassword.getText().toString().equals("")
                        || !repeatPassword.getText().toString().equals(enterPassword.getText().toString())) {
                    Toast.makeText(CreateAccountActivity.this, getString(R.string.data_not_entered_correctly), Toast.LENGTH_LONG).show();
                }
                else if ( enterName.getText().toString().length()< 3) {
                    Toast.makeText(CreateAccountActivity.this, getString(R.string.name_at_least_three_characters), Toast.LENGTH_LONG).show();
                }
                else{
                    CreateNewAccountRequestData data = new CreateNewAccountRequestData();
                    data.Username = enterName.getText().toString();
                    data.Email = enterEmail.getText().toString();
                    data.Password = enterPassword.getText().toString();
                    data.PasswordRepeat = repeatPassword.getText().toString();
                    if(profileImagesListData != null)
                        data.ImageURL = profileImagesListData.Images.get(showedImageId);
                    makeNewAccountCall(data);
                }

            }

        });

        termsConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateAccountActivity.this, R.string.terms_and_conditions_text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean HandleButtonCreate() {
        // This function returns whether or not all fields are filled and handles the button state
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
                //Logged in
                new SettingsUtil(getApplicationContext()).setString(SharedPreferencesKeys.TokenString, body.Token);
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.signed_in_as), body.Username), Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onError(ErrorData error) {
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(),error.Errors), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void getProfileImagesList(){
        Call<ReturnData<ProfileImagesListData>> call = new ApiUtil().getApiInterface(this).getProfileImagesList();
        final RequestUtil<ProfileImagesListData> requestUtil = new RequestUtil<>(this, null, call);
        requestUtil.makeRequest(new RequestInterface<ProfileImagesListData>() {
            @Override
            public void onSucces(ProfileImagesListData body) {
                profileImagesListData = body;
                showedImageId = 0;
                loadImage();
            }

            @Override
            public void onError(ErrorData error) {
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImage() {
        if(profileImagesListData != null) {
            previousButton.setEnabled(true);
            nextButton.setEnabled(true);
            if(showedImageId < 0)
                showedImageId = 0;
            if(showedImageId == profileImagesListData.Images.size())
                showedImageId -= 1;
            if(showedImageId == 0)
                previousButton.setEnabled(false);
            if(showedImageId == profileImagesListData.Images.size()-1)
                nextButton.setEnabled(false);

            Picasso.with(this).load(profileImagesListData.Images.get(showedImageId)).into(imageViewProfileImage);
        }

    }


}