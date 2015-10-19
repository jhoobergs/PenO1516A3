package be.cwa3.nightgame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.NumberPicker;

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

    public void setMinAndMaxOfNumberPicker(NumberPicker np, int min, int max){
        np.setMaxValue(max);
        np.setMinValue(min);
    }
}
