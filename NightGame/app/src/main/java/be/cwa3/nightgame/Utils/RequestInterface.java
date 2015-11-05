package be.cwa3.nightgame.Utils;

import com.squareup.okhttp.ResponseBody;

import be.cwa3.nightgame.Data.ErrorData;

/**
 * Created by jesse on 5/11/2015.
 */
public interface RequestInterface<T> {
    public void onSucces(T body);
    public void onError(ErrorData error);
    public void onServerError(int code, ResponseBody responseBody);
    public void onFailure(Throwable t);
}
