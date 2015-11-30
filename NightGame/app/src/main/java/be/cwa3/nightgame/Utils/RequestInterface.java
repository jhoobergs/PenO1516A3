package be.cwa3.nightgame.Utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.ResponseBody;

import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.R;

/**
 * Created by jesse on 5/11/2015.
 */
public abstract class RequestInterface<T> {

    //The onSucces function is called when StatusCode == 1
    public abstract void onSucces(T body);
    //The onError functions is called when StatusCode ==1
    public abstract void onError(ErrorData error);
    //The onServerError is called when the server can't be reached or doesn't respond.
    public void onServerError(Context context, int code, View view, ResponseBody responseBody){
        if(view == null) {
            Toast.makeText(context, R.string.internal_server_error, Toast.LENGTH_LONG).show();
        }
        else {
            Snackbar.make(view, R.string.internal_server_error, Snackbar.LENGTH_LONG).show();
        }
    };
    //The onFailure function is called when the request failed. (When you don't have internet for example)
    public void onFailure(Context context, View view, Throwable t){
        if(NetworkUtil.isNetworkAvailable(context)){
            Log.d("test", new Gson().toJson(t.getCause()));
            if(view == null) {
                Toast.makeText(context, R.string.internal_server_error, Toast.LENGTH_LONG).show();
            }
            else {
                Snackbar.make(view, R.string.internal_server_error, Snackbar.LENGTH_INDEFINITE).show();
            }
        }
        else {
            if(view == null) {
                Toast.makeText(context, R.string.connect_to_internet_please, Toast.LENGTH_LONG).show();
            }
            else {
                Snackbar.make(view, R.string.connect_to_internet_please, Snackbar.LENGTH_INDEFINITE).show();
            }
        }
    };
}
