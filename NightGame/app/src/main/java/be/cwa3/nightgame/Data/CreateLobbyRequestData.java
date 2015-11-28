package be.cwa3.nightgame.Data;

import android.location.Location;

/**
 * Created by Elisabeth Heremans on 9/11/2015.
 */
public class CreateLobbyRequestData {
    public String Name;
    public int MinPlayers;
    public int MaxPlayers;
    public double CenterLocationLatitude;
    public double CenterLocationLongitude;
    public double CircleRadius;
}
