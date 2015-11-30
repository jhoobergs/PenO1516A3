package be.cwa3.nightgame.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.squareup.okhttp.ResponseBody;

import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.HomeActivity;
import be.cwa3.nightgame.LoginActivity;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by jesse on 5/11/2015.
 */
public class RequestUtil<T> {
    Call<ReturnData<T>> call;
    Context context;
    View view;
    public RequestUtil(Context context, View view, Call<ReturnData<T>> call){
        this.call = call;
        this.context = context;
        this.view = view;
    }

    public void makeRequest(final RequestInterface<T> requestInterface){
        //This function actually makes the call to the server.

        //call.enqueue is a retrofit command
        call.enqueue(new Callback<ReturnData<T>>() {
            @Override
            public void onResponse(Response<ReturnData<T>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    if (response.body().statusCode == 1) {
                        //Request was succesful, so we execute the onSucces function
                        requestInterface.onSucces(response.body().body);
                    } else if (response.body().statusCode == 2) {
                        //Request gave an error, so we execute the onError function
                        requestInterface.onError(response.body().error);
                        ErrorData error = response.body().error;
                        for (int err: error.Errors){
                            if (err == 3){
                                new SettingsUtil(context).setString(SharedPreferencesKeys.TokenString,"");
                                Intent i = new Intent(context, LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(i);
                            }
                        }
                    }

                } else {
                    //We didn't get a succesful response of the server: We call onServerError
                    requestInterface.onServerError(context, response.code(),view, response.errorBody());
                }

            }

            @Override
            public void onFailure(Throwable t) {
                //The request couldn't be made, we call onFailure
                requestInterface.onFailure(context, view, t);
            }
        });
    }
}
