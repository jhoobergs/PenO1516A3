package be.cwa3.nightgame.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import be.cwa3.nightgame.Data.ScoreboardData;
import be.cwa3.nightgame.R;

/**
 * Created by kevin on 19/10/2015.
 */
public class ScoreboardAdapter extends ArrayAdapter<ScoreboardData>{

    private List<ScoreboardData> data;
    private Context context;
    private String sorting;
    private String username;

    private static final int layoutResourceId = R.layout.list_scores;

    public ScoreboardAdapter(Context context, List<ScoreboardData> data, String sorting,String username){

        super(context, layoutResourceId,data);
        this.context = context;
        this.data = data;
        this.sorting = sorting;
        this.username = username;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        View row = convertView;
        Holder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new Holder();
            holder.textViewName = (TextView) row.findViewById(R.id.textview_name);
            holder.textViewRelevantValue = (TextView) row.findViewById(R.id.TextViewRelevantValue);
            holder.textViewMissions = (TextView) row.findViewById(R.id.TextViewMissions);
            holder.textViewGames = (TextView) row.findViewById(R.id.TextViewGames);
            holder.textViewWins = (TextView) row.findViewById(R.id.TextViewWins);
            holder.imageViewProfileImage = (ImageView) row.findViewById(R.id.imageview_profile_image);
            holder.linearLayoutExtraData = (LinearLayout) row.findViewById(R.id.LinearLayoutExtraData);

            row.setTag(holder);
        }else{
            holder = (Holder) row.getTag();
        }

        ScoreboardData menuItem = data.get(position);

        holder.textViewName.setText(menuItem.Username);
        if (username.equals(menuItem.Username)){
            holder.textViewName.setTextColor(context.getResources().getColor(R.color.color_primary));
        }
        else if(menuItem.IsFriend){
            holder.textViewName.setTextColor(context.getResources().getColor(R.color.green));
        }
        else {
            holder.textViewName.setTextColor(Color.GRAY);
        }
        holder.textViewRelevantValue.setVisibility(View.VISIBLE);
        if(sorting.equals("Games")){
            holder.textViewRelevantValue.setText(String.valueOf(menuItem.Games));
        }
        else if(sorting.equals("Wins")){
            holder.textViewRelevantValue.setText(String.valueOf(menuItem.Wins));
        }
        else if(sorting.equals("Missions")){
            holder.textViewRelevantValue.setText(String.valueOf(menuItem.Missions));
        }
        else{
            holder.textViewRelevantValue.setVisibility(View.GONE);
        }
        if(menuItem.isOpen){
            holder.textViewRelevantValue.setVisibility(View.GONE);
        }
        holder.textViewGames.setText(String.valueOf(menuItem.Games));
        holder.textViewWins.setText(String.valueOf(menuItem.Wins));
        holder.textViewMissions.setText(String.valueOf(menuItem.Missions));
        if(menuItem.ImageURL != null)
            Picasso.with(context).load(menuItem.ImageURL).into(holder.imageViewProfileImage);
        if(menuItem.isOpen)
            holder.linearLayoutExtraData.setVisibility(View.VISIBLE);
        else
            holder.linearLayoutExtraData.setVisibility(View.GONE);

        return row;
    }

    static class Holder {
        TextView textViewName, textViewRelevantValue, textViewGames, textViewWins, textViewMissions;
        ImageView imageViewProfileImage;
        LinearLayout linearLayoutExtraData;
    }
}
