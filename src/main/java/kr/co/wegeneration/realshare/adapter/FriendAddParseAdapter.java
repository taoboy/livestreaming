package kr.co.wegeneration.realshare.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import java.util.HashMap;
import java.util.Map;

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
public class FriendAddParseAdapter extends ParseQueryAdapter<Friend> {

    private boolean switched;
    DialogInterface popup;
    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
    private HashMap<String, String> profilepath= new HashMap<>();

    public FriendAddParseAdapter(Context context, ParseQueryAdapter.QueryFactory<Friend> queryfactory ) {
        super(context, queryfactory);
    }

    public FriendAddParseAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Friend>() {
            public ParseQuery create() {
                ParseQuery<Friend> query = Friend.getQuery();
                String username = ParseUser.getCurrentUser().getString(Define.DB_USER_ID);
                ParseQuery<UserInfo> subquery = UserInfo.getQuery();
                query.whereMatchesKeyInQuery(Define.DB_USER_ID, Define.DB_USER_ID, subquery);
                query.include(Define.DB_USER_NM);
                query.whereEqualTo(Define.DB_USER_ID, username);


                return query;
            }
        });
    }

    private static class ViewHolder
    {
        TextView nameTextView;
        TextView profileTextView;
        CircularNetworkImageView profileImageView;
        ImageView addFriendButton;
        ImageView rejectFriendButton;
    }


    @Override
    public View getItemView(final Friend friend, View view, ViewGroup parent) {


        ViewHolder holder;


        if(view == null ){

            view = View.inflate(getContext(), R.layout.row_add_friend, null);
             //view = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.row_add_friend, parent, false);
             holder = new ViewHolder();
             holder.nameTextView         = (TextView)view.findViewById(R.id.nameAddTextView);
             holder.profileTextView      = (TextView)view.findViewById(R.id.emailTextView);
             holder.addFriendButton     = (ImageView)view.findViewById(R.id.addFriendButton);
             holder.rejectFriendButton = (ImageView)view.findViewById(R.id.rejectFriendButton);
            holder.profileImageView     = (CircularNetworkImageView)view.findViewById(R.id.profileImage);
             view.setTag(holder);
         }
         else{
             holder = (ViewHolder)view.getTag();
         }
        super.getItemView(friend, view, parent);

        ParseQuery<UserInfo> user = UserInfo.getQuery();
        user.whereEqualTo(Define.DB_USER_ID, friend.getFriendId());
        user.getFirstInBackground(new GetCallback<UserInfo>() {
            @Override
            public void done(UserInfo parseUser, ParseException e) {
                if (e == null) {
                    if (TextUtils.isEmpty(parseUser.getString("thumbnailPath")))
                        profilepath.put(friend.getFriendId(), "");
                    else
                        profilepath.put(friend.getFriendId(), parseUser.get("thumbnailPath").toString());
                } else {
                    e.printStackTrace();
                    Log.d("Error", "ThumbnailPath");
                }
            }
        });
 //       holder.profileImageView.setImageResource(R.drawable.default_profile_image);

        if(TextUtils.isEmpty(profilepath.get(friend.getFriendId())))
            holder.profileImageView.setImageResource(R.drawable.default_profile_image);
        else
            holder.profileImageView.setImageUrl(profilepath.get(friend.getFriendId()),imageLoader);

            /*ParseFile imageFile = friend.getParseFile("thumbnailPath");
            if (imageFile != null) {
                try {

                    holder.profileImageView.setParseFile(imageFile);
                    holder.profileImageView.setBackgroundResource(R.color.transparent);
                    holder.profileImageView.loadInBackground();


                }catch(Exception e ){ e.printStackTrace();}
            }*/

            String getUserRequestConfirm = friend.getFriend_status();

            holder.nameTextView.setText(friend.getUser_to_name());
            holder.profileTextView.setText(friend.getUser_to_email());

            LayoutParams lparams = new LayoutParams(200,100);
            holder.rejectFriendButton.setVisibility(View.INVISIBLE);
            holder.rejectFriendButton.setEnabled(false);
            lparams.weight = 1.0f;


            if(getUserRequestConfirm.equals("friend")){

                holder.addFriendButton.setTag("Unfriend");
                holder.addFriendButton.setImageResource(R.drawable.unfriend);

            }else if(getUserRequestConfirm.equals("sent"))
            {
                holder.addFriendButton.setTag("Request Sent");
                holder.addFriendButton.setImageResource(R.drawable.requestsent);

            }else if(getUserRequestConfirm.equals("received"))
            {
                holder.addFriendButton.setTag("Confirmed");
                holder.addFriendButton.setImageResource(R.drawable.confirm);

                LayoutParams addparams = new LayoutParams(340, 100);
                addparams.weight = 0.5f;
                holder.rejectFriendButton.setVisibility(View.VISIBLE);
                holder.rejectFriendButton.setEnabled(true);
                holder.rejectFriendButton.setImageResource(R.drawable.ignore);

                LayoutParams rejectparams = new LayoutParams(340, 100);
                rejectparams.weight = 0.5f;
                rejectparams.rightMargin =10;

            }
            else
            {
                holder.addFriendButton.setTag("+Send Request");
                holder.addFriendButton.setImageResource(R.drawable.addfriend);
            }

            final ViewHolder holder_temp = holder;
            holder.addFriendButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    final ImageView button = (ImageView) v;
                    String temp = ((ImageView) v).getTag().toString().trim();
                    if (temp.equals("Unfriend")) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext())
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        button.setImageResource(R.drawable.addfriend);

                                        Map<String, String> param = new HashMap<>();
                                        param.put(Define.MSG_TYPE, Define.MSG_TYPE_DELETEFRIEND);
                                        ParseNetController.AddFriend(getContext(), param, friend);
                                        friend.setFriend_status("addfriend");

                                        loadObjects();
                                        notifyDataSetChanged();

                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setMessage("Remove " + friend.getUser_to_name() + " as a friend?");

                        alert.show();


                    } else if (temp.equals("+Send Request")) {

                        button.setImageResource(R.drawable.requestsent);
                        friend.setFriend_status("sent");

                        /*Map<String, String> param = new HashMap<>();
                        param.put(Define.MSG_TYPE, Define.MSG_TYPE_ADDFRIEND);
                        ParseNetController.AddFriend(getContext(), param, friend);*/

                    } else if (temp.equals("Confirmed")) {


                        holder_temp.rejectFriendButton.setVisibility(View.INVISIBLE);
                        holder_temp.rejectFriendButton.setEnabled(false);

                        button.setImageResource(R.drawable.unfriend);

                        LayoutParams lparams = new LayoutParams(200, 100);
                        lparams.weight = 1.0f;
                        //button.setLayoutParams(lparams);
                        friend.setFriend_status("friend");
                        friend.setDraft(true);

                        Map<String, String> param = new HashMap<>();
                        param.put(Define.MSG_TYPE, Define.MSG_TYPE_CONFIRMFRIEND);
                        ParseNetController.AddFriend(getContext(), param, friend);


                        //confirm friend

                    } else if (temp.equals("Request Sent")) {


                        button.setImageResource(R.drawable.addfriend);

                        Map<String, String> param = new HashMap<>();
                        param.put(Define.MSG_TYPE, Define.MSG_TYPE_DELETEFRIEND);
                        ParseNetController.AddFriend(getContext(), param, friend);
                        friend.setFriend_status("addfriend");
                        //delete friend

                    }
                }
            });

            holder.rejectFriendButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    final ImageView button = (ImageView) v;

                    holder_temp.rejectFriendButton.setVisibility(View.INVISIBLE);
                    holder_temp.rejectFriendButton.setEnabled(false);


                    button.setImageResource(R.drawable.addfriend);
                    friend.setFriend_status("addfriend");

                    LayoutParams lparams = new LayoutParams(240, 100);
                    lparams.width = 200;

                    Map<String, String> param = new HashMap<>();
                    param.put(Define.MSG_TYPE, Define.MSG_TYPE_DELETEFRIEND);
                    ParseNetController.AddFriend(getContext(), param, friend);

                }

            });

        return view;
    }

}