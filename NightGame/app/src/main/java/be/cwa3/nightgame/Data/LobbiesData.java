package be.cwa3.nightgame.Data;

import java.util.List;

/**
 * Created by kevin on 12/11/2015.
 */
public class LobbiesData {
    public String GameId;
    public String Name;
    public int MinPlayers;
    public int MaxPlayers;
    public List<String> Players;
    public transient boolean isOpen = false; //Transient means that this data isn't server related.
    public CenterLocation CenterLocation;
    public String TimerDate;
}
