package be.cwa3.nightgame.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.ResponseBody;

import be.cwa3.nightgame.Data.ErrorData;

/**
 * Created by jesse on 5/11/2015.
 */
public abstract class RequestInterface<T> {

    //The onSucces function is called when StatusCode == 1
    public abstract void onSucces(T body);
    //The onError functions is called when StatusCode ==1
    public abstract void onError(ErrorData error);
    //The onServerError is called when the server can't be reached or doesn't respond.
    public void onServerError(Context context, int code, ResponseBody responseBody){
        Toast.makeText(context, "Internal Server Error", Toast.LENGTH_LONG).show();
    };
    //The onFailure function is called when the request failed. (When you don't have internet for example)
    public void onFailure(Context context,Throwable t){
        if(NetworkUtil.isNetworkAvailable(context)){
            Log.d("test", new Gson().toJson(t.getCause()));
            Toast.makeText(context, "Internal Server Error", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(context, "Connect to internet please!", Toast.LENGTH_LONG).show();
        }
    };
}
