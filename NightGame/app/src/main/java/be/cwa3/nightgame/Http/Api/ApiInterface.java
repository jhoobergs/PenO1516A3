package be.cwa3.nightgame.Http.Api;

import java.util.List;

import be.cwa3.nightgame.Data.FriendData;
import be.cwa3.nightgame.Data.FriendListData;
import be.cwa3.nightgame.Data.ScoreboardListData;
import retrofit.Call;
import retrofit.http.Query;
import retrofit.http.GET;


public interface ApiInterface {
    @GET("/friends/list")
    Call<FriendListData> loadFriends();

    @GET("/scoreboard/list")
    Call<ScoreboardListData> loadScoreboard();
}
