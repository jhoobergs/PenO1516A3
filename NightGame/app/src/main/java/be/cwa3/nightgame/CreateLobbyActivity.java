package be.cwa3.nightgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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

    public void setMinAndMaxOfNumberPicker(NumberPicker np, int min, int max) {
        np.setMaxValue(max);
        np.setMinValue(min);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_createlobby_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}