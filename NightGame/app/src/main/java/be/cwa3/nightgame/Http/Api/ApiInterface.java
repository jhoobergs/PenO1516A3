package be.cwa3.nightgame.Http.Api;

import be.cwa3.nightgame.Data.CreateNewAccountRequestData;
import be.cwa3.nightgame.Data.FriendListData;
import be.cwa3.nightgame.Data.LoginRequestData;
import be.cwa3.nightgame.Data.LoginReturnData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.Data.ScoreboardListData;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.GET;


public interface ApiInterface {
    @GET("/friends/list")
    Call<FriendListData> loadFriends();

    @GET("/scoreboard/list")
    Call<ScoreboardListData> loadScoreboard();

    @POST("/user/login")
    Call<LoginReturnData> sendLoginRequest(@Body LoginRequestData data);

    @POST("/user/create")
    Call<ReturnData<LoginReturnData>> sendCreateNewAccountRequest(@Body CreateNewAccountRequestData data);
}
