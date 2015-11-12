package be.cwa3.nightgame.Utils;

/**
 * Created by jesse on 23/10/2015.
 */
public class SharedPreferencesKeys {
    //This file contains static values, they can't be changed anywhere else but here.
    //We will always use SharedPreferenceKeys.TokenString instead of "Token",
    //so when we want to change "Token" to "token", we will only need to change it here.
    public static final String FILE_NAME = "be.cwa3.nightgame.preferences";
    public static String TokenString = "Token";
    public static String GameIDString = "GameID";
}
