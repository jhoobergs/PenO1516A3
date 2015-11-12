package be.cwa3.nightgame.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import be.cwa3.nightgame.Data.FriendSearchReturnItemData;
import be.cwa3.nightgame.R;

/**
 * Created by Gebruiker on 12/11/2015.
 */
public class FriendSearchAdapter extends ArrayAdapter<FriendSearchReturnItemData> {

private List<FriendSearchReturnItemData> data;
private Context context;

private static final int layoutResourceId = R.layout.list_friend_search;

public FriendSearchAdapter(Context context, List<FriendSearchReturnItemData> data){
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
        holder.textViewName = (TextView) row.findViewById(R.id.TextViewName);
        holder.imageViewProfileImage = (ImageView) row.findViewById(R.id.ImageViewProfileImage);


        row.setTag(holder);
        }else{
        holder = (Holder) row.getTag();
        }

        FriendSearchReturnItemData menuItem = data.get(position);

        holder.textViewName.setText(menuItem.Name);
        if(menuItem.ImageURL != null)
        Picasso.with(context).load(menuItem.ImageURL).into(holder.imageViewProfileImage);



        return row;
        }

static class Holder {
    TextView textViewName;
    ImageView imageViewProfileImage;
}
}
