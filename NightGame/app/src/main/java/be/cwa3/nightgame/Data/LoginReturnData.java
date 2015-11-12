package be.cwa3.nightgame.Data;

/**
 * Created by jesse on 23/10/2015.
 */

public class LoginReturnData {
    public String Username;
    public String Token;

    public LoginReturnData(String Username, String Password){
        this.Username = Username;
        this.Token = Password;
    }
}
