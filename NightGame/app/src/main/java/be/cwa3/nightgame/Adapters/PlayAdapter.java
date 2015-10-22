package be.cwa3.nightgame.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import be.cwa3.nightgame.R;

/**
 * Created by Gebruiker on 22/10/2015.
 */
public class PlayAdapter extends ArrayAdapter<String> {

    private List<String> data;
    private Context context;

    private static final int layoutResourceId = R.layout.list_lobbies;

    public PlayAdapter(Context context, List<String> data){
        super(context, layoutResourceId,data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        View row = convertView;
        Holder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new Holder();
            holder.lobbies = (TextView) row.findViewById(R.id.lobbies);


            row.setTag(holder);
        }else{
            holder = (Holder) row.getTag();
        }

        String menuItem = data.get(position);

        holder.lobbies.setText(menuItem);

        return row;
    }

    static class Holder {
        TextView lobbies;
    }
}



