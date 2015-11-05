package be.cwa3.nightgame.Utils;

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
    public RequestUtil(Call<ReturnData<T>> call){
        this.call = call;
    }

    public void makeRequest(final RequestInterface<T> requestInterface){
        call.enqueue(new Callback<ReturnData<T>>() {
            @Override
            public void onResponse(Response<ReturnData<T>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    if (response.body().statusCode == 1) {
                        requestInterface.onSucces(response.body().body);
                    } else if (response.body().statusCode == 2) {
                        requestInterface.onError(response.body().error);
                    }

                } else {
                    requestInterface.onServerError(response.code(), response.errorBody());
                }

            }

            @Override
            public void onFailure(Throwable t) {
                requestInterface.onFailure(t);
            }
        });
    }
}
