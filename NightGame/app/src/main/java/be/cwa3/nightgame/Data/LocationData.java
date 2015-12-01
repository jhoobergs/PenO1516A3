package be.cwa3.nightgame.Data;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by Koen on 26/11/2015.
 */
public class LocationData {
    public double Latitude;
    public double Longitude;
    public double Altitude;
    public DateTime CreatedOn;

    public transient String PlayerName;
    public transient String Team;
    public transient boolean HasFlag;

}
