package be.cwa3.nightgame.Utils;

import android.content.Context;

import be.cwa3.nightgame.Data.ReturnData;
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
    public RequestUtil(Context context, Call<ReturnData<T>> call){
        this.call = call;
        this.context = context;
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
                    }

                } else {
                    //We didn't get a succesful response of the server: We call onServerError
                    requestInterface.onServerError(context, response.code(), response.errorBody());
                }

            }

            @Override
            public void onFailure(Throwable t) {
                //The request couldn't be made, we call onFailure
                requestInterface.onFailure(context,t);
            }
        });
    }
}
