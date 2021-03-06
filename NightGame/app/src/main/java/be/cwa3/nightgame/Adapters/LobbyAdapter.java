package be.cwa3.nightgame.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.List;

import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.GamePlayerData;
import be.cwa3.nightgame.Data.JoinLobbyRequestData;
import be.cwa3.nightgame.Data.JoinLobbyReturnData;
import be.cwa3.nightgame.Data.LobbiesData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.LobbyWaitActivity;
import be.cwa3.nightgame.R;
import be.cwa3.nightgame.Utils.ApiUtil;
import be.cwa3.nightgame.Utils.ErrorUtil;
import be.cwa3.nightgame.Utils.RequestInterface;
import be.cwa3.nightgame.Utils.RequestUtil;
import be.cwa3.nightgame.Utils.SettingsUtil;
import be.cwa3.nightgame.Utils.SharedPreferencesKeys;
import retrofit.Call;

/**
 * Created by Gebruiker on 22/10/2015.
 */
public class LobbyAdapter extends ArrayAdapter<LobbiesData> {

    private List<LobbiesData> data;
    private Context context;
    private Location myLocation;


    private static final int layoutResourceId = R.layout.list_lobbies;

    public LobbyAdapter(Context context, List<LobbiesData> data, Location myLocation){
        super(context, layoutResourceId, data);
        this.context = context;
        this.data = data;
        this.myLocation = myLocation;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        View row = convertView;
        final Holder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new Holder();
            holder.lobbies = (TextView) row.findViewById(R.id.lobbies);
            holder.playersRatio = (TextView) row.findViewById(R.id.playersratio);
            holder.join_button = (Button) row.findViewById(R.id.join_button);
            holder.lobbies_data = (RelativeLayout) row.findViewById(R.id.Lobbies_data);
            holder.location = (TextView) row.findViewById(R.id.location);
            holder.timer = (TextView) row.findViewById(R.id.timer);
            holder.players = (TextView) row.findViewById(R.id.players);

            row.setTag(holder);
        }else{
            holder = (Holder) row.getTag();
        }

        final LobbiesData menuItem = data.get(position);

        holder.lobbies.setText(menuItem.Name);
        holder.playersRatio.setText(String.format("%s / %s", menuItem.Players.size(), menuItem.MaxPlayers));

        Location location = new Location("");
        location.setLatitude(menuItem.CenterLocation.Latitude);
        location.setLongitude(menuItem.CenterLocation.Longitude);
        if(myLocation != null) {
            holder.location.setVisibility(View.VISIBLE);
            float dist = myLocation.distanceTo(location);
            if(dist < 1000)
                holder.location.setText(String.format("%d m",Math.round(dist)));
            else{
                holder.location.setText(String.format("%f km", Math.round(dist/100)/10.0));
            }
        }
        else{
            holder.location.setVisibility(View.GONE);
        }
        if(menuItem.TimerDate != null) {
            long Tminus;
            Tminus = menuItem.TimerDate.getMillis() - DateTime.now().getMillis();
            if(Tminus <= 0)
                holder.timer.setText(context.getString(R.string.started));
            else {
                holder.timer.setText(menuItem.TimerDate.toString("HH:mm:ss"));
            }
            holder.timer.setVisibility(View.VISIBLE);
        }
        else{
            holder.timer.setVisibility(View.GONE);
        }

        holder.join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.join_button.setEnabled(false);
                JoinLobbyRequestData data = new JoinLobbyRequestData(menuItem.GameId);
                makeJoinLobbyCall(data,menuItem);

            }
        });

        if (menuItem.isOpen)
            holder.lobbies_data.setVisibility(View.VISIBLE);
        else
            holder.lobbies_data.setVisibility(View.GONE);
        holder.players.setText("");
        for(GamePlayerData player : menuItem.Players){
            String subString ="\n";
            if(holder.players.getText().equals(""))
                subString = "";
            holder.players.setText(holder.players.getText() + subString + player.Name);
        }

        return row;

    }

    static class Holder {
        TextView lobbies,playersRatio,timer, location, players;
        Button join_button;
        RelativeLayout lobbies_data;
    }

    private void makeJoinLobbyCall(JoinLobbyRequestData data, final LobbiesData menuItem){
        Call<ReturnData<JoinLobbyReturnData>> call = new ApiUtil().getApiInterface(context).joinLobby(data);
        RequestUtil<JoinLobbyReturnData> requestUtil = new RequestUtil<>(context,null, call);
        requestUtil.makeRequest(new RequestInterface<JoinLobbyReturnData>() {
            @Override
            public void onSucces(JoinLobbyReturnData body) {
                new SettingsUtil(context).setString(SharedPreferencesKeys.GameIDString, menuItem.GameId);
                Intent intent = new Intent(context, LobbyWaitActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }

            @Override
            public void onError(ErrorData error) {
                Toast.makeText(context, ErrorUtil.getErrorText(context, error.Errors), Toast.LENGTH_SHORT).show();
            }
        });

    }
}



