package be.cwa3.nightgame.Data;

/**
 * Created by jesse on 24/10/2015.
 */
public class ReturnData<T> {
    public int statusCode;
    public T body;
    public ErrorData error;
}
