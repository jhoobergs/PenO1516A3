package be.cwa3.nightgame.Data;

/**
 * Created by jesse on 22/10/2015.
 */
public class ScoreboardData {
    public String Username;
    public String ImageURL;
    public int Wins;
    public int Missions;
    public int Games;
    public boolean IsFriend;

    public transient boolean isOpen = false; //Transient means that this data isn't server related.
}
