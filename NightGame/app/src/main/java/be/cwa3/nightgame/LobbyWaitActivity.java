package be.cwa3.nightgame;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.LobbiesListData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Utils.ApiUtil;
import be.cwa3.nightgame.Utils.ErrorUtil;
import be.cwa3.nightgame.Utils.RequestInterface;
import be.cwa3.nightgame.Utils.RequestUtil;
import retrofit.Call;

/**
 * Created by kevin on 12/11/2015.
 */
public class LobbyWaitActivity extends AppCompatActivity implements CircleProgressView.OnProgressChangedListener {

    CircleProgressView mCircleView;
    Switch mSwitchSpin;
    Boolean mShowUnit = true;

    LobbiesListData lobbiesListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobywait);

        mCircleView = (CircleProgressView) findViewById(R.id.circleView);
        mCircleView.setOnProgressChangedListener(this);

        makeCall();

        //value setting
        mCircleView.setMaxValue(100);
        mCircleView.setValue(0);
        mCircleView.setValueAnimated(24);

        //show unit
        mCircleView.setUnit("%");
        mCircleView.setShowUnit(mShowUnit);

        //text sizes
        mCircleView.setTextSize(50); // text size set, auto text size off
        mCircleView.setUnitSize(40); // if i set the text size i also have to set the unit size
        mCircleView.setAutoTextSize(true); // enable auto text size, previous values are overwritten
        //if you want the calculated text sizes to be bigger/smaller you can do so via
        mCircleView.setUnitScale(0.9f);
        mCircleView.setTextScale(0.9f);

//        //custom typeface
//        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/ANDROID_ROBOT.ttf");
//        mCircleView.setTextTypeface(font);
//        mCircleView.setUnitTextTypeface(font);


        //color
        //you can use a gradient
        mCircleView.setBarColor(getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimary));

        //colors of text and unit can be set via
        mCircleView.setTextColor(Color.RED);
        mCircleView.setTextColor(Color.BLUE);
        //or to use the same color as in the gradient
        mCircleView.setAutoTextColor(true); //previous set values are ignored

        //text mode
        mCircleView.setText("Text"); //shows the given text in the circle view
        mCircleView.setTextMode(TextMode.TEXT); // Set text mode to text to show text

        //in the following text modes, the text is ignored
        mCircleView.setTextMode(TextMode.VALUE); // Shows the current value
        mCircleView.setTextMode(TextMode.PERCENT); // Shows current percent of the current value from the max value

        //spinning
        mCircleView.spin(); // start spinning
        mCircleView.stopSpinning(); // stops spinning. Spinner gets shorter until it disappears.
        mCircleView.setValueAnimated(24); // stops spinning. Spinner spins until on top. Then fills to set value.
        mCircleView.setShowTextWhileSpinning(true); // Show/hide text in spinning mode
        //animation callbacks

        //this example shows how to show a loading text if it is in spinning mode, and the current percent value otherwise.
        mCircleView.setText("waiting on players");
        mCircleView.setTextMode(TextMode.PERCENT);
        mCircleView.setAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        switch (_animationState) {
                            case IDLE:
                            case ANIMATING:
                            case START_ANIMATING_AFTER_SPINNING:
                                mCircleView.setTextMode(TextMode.PERCENT); // show percent if not spinning
                                mCircleView.setShowUnit(mShowUnit);
                                break;
                            case SPINNING:
                                mCircleView.setTextMode(TextMode.TEXT); // show text while spinning
                                mCircleView.setShowUnit(false);
                            case END_SPINNING:
                                break;
                            case END_SPINNING_START_ANIMATING:
                                break;

                        }
                    }
                }
        );


        //Setup Switch
        mSwitchSpin = (Switch) findViewById(R.id.switch1);
        mSwitchSpin.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            mCircleView.spin();
                        } else {
                            mCircleView.stopSpinning();
                        }
                    }
                }

        );


    }

    @Override
    public void onProgressChanged(float value) {

    }

    private void makeCall(){
        Call<ReturnData<LobbiesListData>> call = new ApiUtil().getApiInterface(this).loadLobbyList();
        RequestUtil<LobbiesListData> requestUtil = new RequestUtil<>(this, call);
        requestUtil.makeRequest(new RequestInterface<LobbiesListData>() {
            @Override
            public void onSucces(LobbiesListData body) {
                lobbiesListData = body;
                lobbiesListdata.

            }

            @Override
            public void onError(ErrorData error) {
                Toast.makeText(getApplicationContext(), ErrorUtil.getErrorText(getApplicationContext(), error.Errors), Toast.LENGTH_SHORT).show();

            }
        });

    }
}
