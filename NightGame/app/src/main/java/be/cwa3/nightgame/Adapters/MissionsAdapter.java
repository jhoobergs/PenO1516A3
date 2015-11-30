package be.cwa3.nightgame.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import be.cwa3.nightgame.Data.LobbiesData;
import be.cwa3.nightgame.Data.MissionData;
import be.cwa3.nightgame.R;

/**
 * Created by Gebruiker on 26/11/2015.
 */
public class MissionsAdapter extends ArrayAdapter<MissionData> {
    private List<MissionData> data;
    private Context context;

    private static final int layoutResourceId = R.layout.list_missions;

    public MissionsAdapter(Context context, List<MissionData> data){
        super(context, layoutResourceId,data);
        this.context = context;
        this.data = data;
        Log.d("test", String.valueOf(data.size()));
    }

    @Override
    public View getView (final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final Holder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new Holder();
            holder.Mission = (TextView) row.findViewById(R.id.Mission);


            row.setTag(holder);
        }else{
            holder = (Holder) row.getTag();
        }
        MissionData listItem = data.get(position);
        Log.d("test", "in");

        String description ="";

        if(listItem.Type == 1) {
            description = String.format(context.getString(R.string.mission_type_1), String.valueOf(listItem.Location.Latitude),String.valueOf(listItem.Location.Longitude));
        }
        else if(listItem.Type == 2){
            description = context.getString(R.string.mission_type_2);
        }
        else if(listItem.Type==3){
            description = String.format(context.getString(R.string.mission_type_3), String.valueOf(listItem.HeightDifference));
        }
        else if(listItem.Type==4){
            description = context.getString(R.string.mission_type_4);
        }
        else if(listItem.Type==5){
            description = String.format(context.getString(R.string.mission_type_5), String.valueOf(listItem.SpeedValue));
        }


        holder.Mission.setText(description);

        if (listItem.IsFinished){
            holder.Mission.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }

        return row;
    }
    static class Holder {
        TextView Mission;
}
}
