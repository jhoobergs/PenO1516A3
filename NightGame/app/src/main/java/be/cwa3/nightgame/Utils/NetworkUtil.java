package be.cwa3.nightgame.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by jesse on 6/11/2015.
 */
public class NetworkUtil {
    //This class is used to check all things that are network related.

    public static boolean isNetworkAvailable(Context context) {
        //This method is declared static. This means that you can do 'NetworkUtil.isNetworkAvailable(..)'
        //instead of 'new NetworkUtil().isNetworkAvailable(..)'
        //This methode means
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }
}
