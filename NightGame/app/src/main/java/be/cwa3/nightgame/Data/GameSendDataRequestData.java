package be.cwa3.nightgame.Data;

import java.util.List;

/**
 * Created by Koen on 26/11/2015.
 */
public class GameSendDataRequestData {
    public String GameId;
    public LocationData Location;
    public AccelerometerData Accelerometer;
    public List<Integer> CompletedMissions;
    public boolean Died;
}
