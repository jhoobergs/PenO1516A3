package be.cwa3.nightgame.Utils;

import android.content.Context;

import java.util.Set;

/**
 * Created by jesse on 2/11/2015.
 */
public class Settings {
    private Context context;
    public Settings(Context context){
        this.context = context;
    }
    public String getString(String key){
        return context.getSharedPreferences(SharedPreferencesKeys.FILE_NAME, Context.MODE_PRIVATE).getString(key, "");
    }
    public void setString(String key, String value){
        context.getSharedPreferences(SharedPreferencesKeys.FILE_NAME, Context.MODE_PRIVATE).edit().putString(key, value).apply();
    }
}
