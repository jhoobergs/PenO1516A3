package be.cwa3.nightgame.Data;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by kevin on 12/11/2015.
 */
public class LobbiesData {
    public String GameId;
    public String Name;
    public int MinPlayers;
    public int MaxPlayers;
    public List<GamePlayerData> Players;
    public GamePlayerData Player;
    public CenterLocation CenterLocation;
    public CenterLocation DefenderBase;
    public DateTime TimerDate;
    public Boolean IsStarted = false;
    public List<MissionData> Missions;
    public double CircleRadius;
    public String WinningTeam;

    public transient boolean isOpen = false; //Transient means that this data isn't server related.


}
