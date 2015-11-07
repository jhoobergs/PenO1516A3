package be.cwa3.nightgame.Utils;

import android.content.Context;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import be.cwa3.nightgame.BuildConfig;
import be.cwa3.nightgame.Http.Api.ApiInterface;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;


/**
 * Created by jesse on 2/11/2015.
 */
public class ApiUtil {

    public ApiInterface getApiInterface(final Context context) {
        //Create an instance of an OkHttpClient
        //This interceptor is used to add an header with name Token to all requests.
        //The server can see by the Token which user requests the data.
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Token", new Settings(context).getString(SharedPreferencesKeys.TokenString))
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        });
        //Create an instance of retrofit
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL) //Check Build.gradle (Module: app -> android -> buildTypes)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build();
     return retrofit.create(ApiInterface.class);
    }
}
