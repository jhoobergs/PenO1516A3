package be.cwa3.nightgame.Data;

import java.util.List;

/**
 * Created by Gebruiker on 26/11/2015.
 */
public class ChallengeReturnData {
    public String GameId;
    public String Name;
    public int MinPlayers;
    public int MaxPlayers;
    public List<FriendSearchReturnItemData> Players;
    public List<MissionData> Missions;
}
