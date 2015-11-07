package be.cwa3.nightgame.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by jesse on 2/11/2015.
 */
public class Settings {
    //We use this Settings class to make it easier to save data to the local storagefile.
    private Context context;
    private SharedPreferences sharedPreferences;
    public Settings(Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(SharedPreferencesKeys.FILE_NAME, Context.MODE_PRIVATE);
    }
    public String getString(String key){
        return sharedPreferences.getString(key, "");
    }
    public void setString(String key, String value){
        sharedPreferences.edit().putString(key, value).apply();
    }
}
