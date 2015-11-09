package be.cwa3.nightgame.Adapters;

import android.app.Activity;
import android.content.Context;
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

    private static final int layoutResourceId = R.layout.list_scores;

    public ScoreboardAdapter(Context context, List<ScoreboardData> data, String sorting){
        super(context, layoutResourceId,data);
        this.context = context;
        this.data = data;
        this.sorting = sorting;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        View row = convertView;
        Holder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new Holder();
            holder.textViewName = (TextView) row.findViewById(R.id.TextViewName);
            holder.relevantValue = (TextView) row.findViewById(R.id.RelevantValue);
            holder.textViewMissions = (TextView) row.findViewById(R.id.TextViewMissions);
            holder.textViewGames = (TextView) row.findViewById(R.id.TextViewGames);
            holder.textViewWins = (TextView) row.findViewById(R.id.TextViewWins);
            holder.imageViewProfileImage = (ImageView) row.findViewById(R.id.ImageViewProfileImage);
            holder.linearLayoutExtraData = (LinearLayout) row.findViewById(R.id.LinearLayoutExtraData);

            row.setTag(holder);
        }else{
            holder = (Holder) row.getTag();
        }

        ScoreboardData menuItem = data.get(position);

        holder.textViewName.setText(menuItem.Username);
        if(sorting.equals("Games") && holder.linearLayoutExtraData.getVisibility()==View.GONE){
            holder.relevantValue.setText(menuItem.Games);
        }
        else if(sorting.equals("Wins") && holder.linearLayoutExtraData.getVisibility()==View.GONE){
            holder.relevantValue.setText(menuItem.Wins);
        }
        else if(holder.linearLayoutExtraData.getVisibility()==View.GONE){
            holder.relevantValue.setText(menuItem.Missions);
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
        TextView textViewName,relevantValue, textViewGames, textViewWins, textViewMissions;
        ImageView imageViewProfileImage;
        LinearLayout linearLayoutExtraData;
    }
}
