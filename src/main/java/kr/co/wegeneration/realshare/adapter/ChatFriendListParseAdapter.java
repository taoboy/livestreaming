package kr.co.wegeneration.realshare.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
//import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Map;

import kr.co.wegeneration.realshare.FriendFragment;
import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.NetController.ParseDAO;
import kr.co.wegeneration.realshare.NetController.ParseNetController;
import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.app.MyApplication;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.models.Friend;
import kr.co.wegeneration.realshare.models.UserInfo;
import kr.co.wegeneration.realshare.widget.CircularNetworkImageView;

/**
 * Created by user on 2015-09-01.
 */
public class ChatFriendListParseAdapter extends ParseQueryAdapter<UserInfo> {

    private AssetManager asset;
    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();

    public ChatFriendListParseAdapter(Context context, QueryFactory<UserInfo> queryfactory) {
        super(context, queryfactory);
    }

    public ChatFriendListParseAdapter(final Context context, AssetManager asset) {
        super(context, new QueryFactory<UserInfo>() {
            public ParseQuery create() {
                return ParseDAO.getInstance(context).chatfriendlistParse();
            }
        });
        this.asset = asset;
    }

    private static class ViewHolder
    {
        TextView nameTextView;
        CircularNetworkImageView chatprofileImage;
        ImageView addFriendButton;
    }


    @Override
    public View getItemView(final UserInfo friendShip, View view, ViewGroup parent) {


        final ViewHolder holder;

        super.getItemView(friendShip, view, parent);

        if(view == null ){

             view = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.row_chat_friend, parent, false);
             holder = new ViewHolder();
             holder.nameTextView         = (TextView)view.findViewById(R.id.nameChatTextView);
             holder.chatprofileImage     = (CircularNetworkImageView)view.findViewById(R.id.chatprofileImage);
             holder.addFriendButton     = (ImageView)view.findViewById(R.id.addChatFriendButton);
             view.setTag(holder);
         }
         else{
             holder = (ViewHolder)view.getTag();
         }

        /*ParseFile imageFile = friendShip.getParseFile("thumbnailPath");
        if (imageFile != null) {
            try {
                holder.chatprofileImage.setParseFile(imageFile);
                holder.chatprofileImage.setBackgroundResource(R.color.transparent);
                holder.chatprofileImage.loadInBackground();
                //}

            }catch(Exception e ){ e.printStackTrace();}
        }*/
        if(friendShip.get("thumbnailPath")==null)
            holder.chatprofileImage.setImageResource(R.drawable.default_profile_image);
        else
            holder.chatprofileImage.setImageUrl(friendShip.get("thumbnailPath").toString() ,imageLoader);

            Typeface typeface = Typeface.createFromAsset(asset, "BRI293.TTF");
            holder.nameTextView.setTypeface(typeface);
            holder.nameTextView.setText(friendShip.getString(Define.DB_USER_NM));


            if(friendShip.getString(Define.DB_USER_NM).equals(ParseUser.getCurrentUser().getUsername()))holder.addFriendButton.setVisibility(View.INVISIBLE);
            ParseQuery<Friend> friend= Friend.getQuery();
            friend.whereEqualTo(Define.DB_USER_ID, friendShip.getString(Define.DB_USER_ID));
            friend.whereEqualTo(Define.DB_FRIEND_ID, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_USER_ID));
            friend.getFirstInBackground(new GetCallback<Friend>() {
                @Override
                public void done(Friend friend, ParseException e) {
                    if(e==null) {
                        if (!friend.getFriend_status().equals("friend"))
                            holder.addFriendButton.setVisibility(View.VISIBLE);
                    } else e.printStackTrace();

                }
            });

            holder.addFriendButton.setTag("+Send Request");
            holder.addFriendButton.setImageResource(R.drawable.add_chat_icon);

            final ViewHolder holder_temp = holder;
            holder.addFriendButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    final ImageView button = (ImageView) v;
                    String temp = ((ImageView) v).getTag().toString().trim();

                    button.setImageResource(R.drawable.requestsent);
                        //friendShip.setFriend_status("sent");
                    Map<String, String> param = new HashMap<>();
                    param.put(Define.MSG_TYPE             , Define.MSG_TYPE_ADDFRIEND);
                    ParseNetController.AddFriend(getContext(), param, friendShip);
                    //AddFriend
                    }

            });

        return view;
    }


}