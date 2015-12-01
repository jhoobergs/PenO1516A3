package be.cwa3.nightgame.Http.Api;

import be.cwa3.nightgame.Data.Empty;
import be.cwa3.nightgame.Data.CreateLobbyRequestData;
import be.cwa3.nightgame.Data.CreateLobbyReturnData;
import be.cwa3.nightgame.Data.CreateNewAccountRequestData;
import be.cwa3.nightgame.Data.FriendAddRequestData;
import be.cwa3.nightgame.Data.FriendAddReturnData;
import be.cwa3.nightgame.Data.FriendListData;
import be.cwa3.nightgame.Data.FriendRemoveRequestData;
import be.cwa3.nightgame.Data.FriendRemoveReturnData;
import be.cwa3.nightgame.Data.FriendSearchRequestData;
import be.cwa3.nightgame.Data.FriendSearchReturnData;

import be.cwa3.nightgame.Data.GameGetDataRequestData;
import be.cwa3.nightgame.Data.GameSendDataRequestData;
import be.cwa3.nightgame.Data.JoinLobbyRequestData;
import be.cwa3.nightgame.Data.JoinLobbyReturnData;
import be.cwa3.nightgame.Data.LobbiesData;
import be.cwa3.nightgame.Data.LobbiesListData;
import be.cwa3.nightgame.Data.LobbySearchRequestData;
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
    Call<ReturnData<FriendListData>> loadFriends();

    @POST("/friends/search")
    Call<ReturnData<FriendSearchReturnData>> searchFriends(@Body FriendSearchRequestData data);

    @POST("/lobbies/search")
    Call<ReturnData<LobbiesListData>> searchLobbies (@Body LobbySearchRequestData data);

    @POST("/friends/add")
    Call<ReturnData<FriendAddReturnData>> addFriend(@Body FriendAddRequestData data);

    @POST("/friends/remove")
    Call<ReturnData<FriendRemoveReturnData>> removeFriend(@Body FriendRemoveRequestData data);

    @GET("/scoreboard/list")
    Call<ReturnData<ScoreboardListData>> loadScoreboard();

    @POST("/user/login")
    Call<ReturnData<LoginReturnData>> sendLoginRequest(@Body LoginRequestData data);

    @POST("/user/create")
    Call<ReturnData<LoginReturnData>> sendCreateNewAccountRequest(@Body CreateNewAccountRequestData data);

    @POST("/game/create")
    Call<ReturnData<CreateLobbyReturnData>> sendCreateLobbyRequest(@Body CreateLobbyRequestData data);

    @POST("/game/sendData")
    Call<ReturnData<LobbiesData>> sendGameDataRequest(@Body GameSendDataRequestData data);

    @GET("/game/list")
    Call<ReturnData<LobbiesListData>> loadLobbyList();

    @POST ("game/join")
    Call<ReturnData<JoinLobbyReturnData>> joinLobby(@Body JoinLobbyRequestData data);

    @POST ("game/getData")
    Call<ReturnData<LobbiesData>> getLobbyData(@Body GameGetDataRequestData data);





}
