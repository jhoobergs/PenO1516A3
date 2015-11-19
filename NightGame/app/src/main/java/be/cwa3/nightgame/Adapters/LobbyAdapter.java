package be.cwa3.nightgame.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import be.cwa3.nightgame.Data.LobbiesData;
import be.cwa3.nightgame.Data.LobbiesListData;
import be.cwa3.nightgame.LobbyWaitActivity;
import be.cwa3.nightgame.R;

/**
 * Created by Gebruiker on 22/10/2015.
 */
public class LobbyAdapter extends ArrayAdapter<LobbiesData> {

    private List<LobbiesData> data;
    private Context context;


    private static final int layoutResourceId = R.layout.list_lobbies;

    public LobbyAdapter(Context context, List<LobbiesData> data){
        super(context, layoutResourceId,data);
        this.context = context;
        this.data = data;
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



            row.setTag(holder);
        }else{
            holder = (Holder) row.getTag();
        }

        LobbiesData menuItem = data.get(position);

        holder.lobbies.setText(menuItem.Name);
        holder.playersRatio.setText(String.format("%s / %s", menuItem.Players.size(),menuItem.MaxPlayers));


        holder.join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.join_button.setEnabled(false);
                Intent intent = new Intent(context, LobbyWaitActivity.class);
                context.startActivity(intent);
            }
        });

       if(menuItem.isOpen)
            holder.lobbies_data.setVisibility(View.VISIBLE);
        else
            holder.lobbies_data.setVisibility(View.GONE);

        return row;

    }

    static class Holder {
        TextView lobbies,playersRatio;
        Button join_button;
        RelativeLayout lobbies_data;
    }
}



