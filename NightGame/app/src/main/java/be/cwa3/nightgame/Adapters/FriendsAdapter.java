package be.cwa3.nightgame.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import be.cwa3.nightgame.AddFriendActivity;
import be.cwa3.nightgame.Data.ErrorData;
import be.cwa3.nightgame.Data.FriendAddRequestData;
import be.cwa3.nightgame.Data.FriendAddReturnData;
import be.cwa3.nightgame.Data.FriendData;
import be.cwa3.nightgame.Data.ReturnData;
import be.cwa3.nightgame.FriendsActivity;
import be.cwa3.nightgame.R;
import be.cwa3.nightgame.Utils.ApiUtil;

import be.cwa3.nightgame.Utils.ErrorUtil;
import be.cwa3.nightgame.Utils.RequestInterface;
import be.cwa3.nightgame.Utils.RequestUtil;
import retrofit.Call;

/**
 * Created by kevin on 19/10/2015.
 */
public class FriendsAdapter extends ArrayAdapter<FriendData>{

    private List<FriendData> data;
    private Context context;

    private static final int layoutResourceId = R.layout.list_friends;

    public FriendsAdapter(Context context, List<FriendData> data){
        super(context, layoutResourceId,data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView (final int position, View convertView, ViewGroup parent){
        View row = convertView;
        final Holder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new Holder();
            holder.textViewName = (TextView) row.findViewById(R.id.TextViewName);
            holder.imageViewProfileImage = (ImageView) row.findViewById(R.id.ImageViewProfileImage);
            holder.textViewSend = (TextView) row.findViewById(R.id.send);
            holder.button_accept = (Button) row.findViewById(R.id.button_accept);

            row.setTag(holder);
        }else{
            holder = (Holder) row.getTag();
        }

        final FriendData menuItem = data.get(position);

        holder.textViewName.setText(menuItem.Name);
        if(menuItem.ImageURL != null)
            Picasso.with(context).load(menuItem.ImageURL).into(holder.imageViewProfileImage);

        if(!menuItem.IsSender && !menuItem.Accepted){
            holder.textViewSend.setVisibility(View.GONE);
            holder.button_accept.setVisibility(View.VISIBLE);
        }
        else if(!menuItem.Accepted) {
            holder.textViewSend.setVisibility(View.VISIBLE);
            holder.button_accept.setVisibility(View.GONE);
        }
        else {
            holder.textViewSend.setVisibility(View.GONE);
            holder.button_accept.setVisibility(View.GONE);
        }


        holder.button_accept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makeAddCall(new FriendAddRequestData(menuItem.Name), menuItem, position, holder);
            }
        });

        return row;
    }

    static class Holder {
        TextView textViewName;
        ImageView imageViewProfileImage;
        Button button_accept;
        TextView textViewSend;
    }
    private void makeAddCall(FriendAddRequestData friendAddRequestData, final FriendData menuItem, final int position, final Holder holder) {
        Call<ReturnData<FriendAddReturnData>> call = new ApiUtil().getApiInterface(context).addFriend(friendAddRequestData);
        RequestUtil<FriendAddReturnData> requestUtil = new RequestUtil<>(context, call);
        requestUtil.makeRequest(new RequestInterface<FriendAddReturnData>() {
            @Override
            public void onSucces(FriendAddReturnData body) {
                Toast.makeText(context, "Friend accepted!", Toast.LENGTH_LONG).show();
                menuItem.Accepted = true;
                data.set(position, menuItem);
                holder.button_accept.setVisibility(View.GONE);
            }

            @Override
            public void onError(ErrorData error) {
                Toast.makeText(context, ErrorUtil.getErrorText(context, error.Errors), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
