package kr.co.wegeneration.realshare.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
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
import kr.co.wegeneration.realshare.models.UserInfo;
import kr.co.wegeneration.realshare.widget.CircularNetworkImageView;

/**
 * Created by user on 2015-09-01.
 */
public class FriendlistParseAdapter extends ParseQueryAdapter<UserInfo> {

    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
    private int Count=0;

    public int getCountTotal(){ return Count; }

    public FriendlistParseAdapter(Context context, ParseQueryAdapter.QueryFactory<UserInfo> queryfactory ) {
        super(context, queryfactory);
        this.Count = getCount();
    }

    public FriendlistParseAdapter(final Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<UserInfo>() {
            public ParseQuery create() {
                return ParseDAO.getInstance(context).friendlistParse();
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
    public View getItemView(final UserInfo friendShip, View view, ViewGroup parent) {


        ViewHolder holder;

        if(view == null ){

             view = View.inflate(getContext(), R.layout.row_add_friend, null);
             //view = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.row_add_friend, parent, false);
             holder = new ViewHolder();
             holder.nameTextView         = (TextView)view.findViewById(R.id.nameAddTextView);
             holder.profileTextView      = (TextView)view.findViewById(R.id.emailTextView);
             holder.addFriendButton     = (ImageView)view.findViewById(R.id.addFriendButton);
             holder.profileImageView     = (CircularNetworkImageView)view.findViewById(R.id.profileImage);
             holder.rejectFriendButton = (ImageView)view.findViewById(R.id.rejectFriendButton);
             view.setTag(holder);
         }
         else{
             holder = (ViewHolder)view.getTag();
         }
        super.getItemView(friendShip, view, parent);

            holder.nameTextView.setText(friendShip.getString(Define.DB_USER_NM));
            holder.profileTextView.setText(friendShip.getString(Define.DB_EMAIL));

        if(TextUtils.isEmpty(friendShip.getString("thumbnailPath")))
            holder.profileImageView.setImageResource(R.drawable.default_profile_image);
        else
            holder.profileImageView.setImageUrl(friendShip.get("thumbnailPath").toString(), imageLoader);
        /*ParseFile imageFile = friendShip.getParseFile("thumbnailPath");
        if (imageFile != null) {
            try {

                holder.profileImageView.setParseFile(imageFile);
                holder.profileImageView.setBackgroundResource(R.color.transparent);
                holder.profileImageView.loadInBackground();


            }catch(Exception e ){ e.printStackTrace();}
        }*/


        holder.addFriendButton.setTag("+Send Request");
            holder.addFriendButton.setImageResource(R.drawable.addfriend);

            holder.rejectFriendButton.setVisibility(View.INVISIBLE);

            final ViewHolder holder_temp = holder;
            holder.addFriendButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    final ImageView button = (ImageView) v;
                    String temp = ((ImageView) v).getTag().toString().trim();

                    if(temp.equals("+Send Request")) {
                        button.setImageResource(R.drawable.requestsent);
                        //friendShip.setFriend_status("sent");
                        Map<String, String> param = new HashMap<>();
                        param.put(Define.MSG_TYPE, Define.MSG_TYPE_ADDFRIEND);
                        ParseNetController.AddFriend(getContext(), param, friendShip);

                        loadObjects();
                        notifyDataSetChanged();
                    }

                    //AddFriend
                    }

            });

        return view;
    }

    public String getAllIdStr(){
        String ids = "";
        for(int i = 0; i < getCount(); i++){
                UserInfo temp = getItem(i);
                ids = ids.concat(",").concat(String.valueOf(temp.getString(Define.DB_USER_ID)));
        }
        if( !ids.isEmpty() ) {
            ids = ids.substring(1);
        }
        return "["+ids+"]";
    }

    public String getAllFullNameStr(){
        String ids = "";
        for(int i = 0; i < getCount(); i++){
            UserInfo temp = getItem(i);
            ids = ids.concat(",").concat(String.valueOf(temp.getString(Define.DB_USER_NM)));
        }
        if( !ids.isEmpty() ) {
            ids = ids.substring(1);
        }
        return "["+ids+"]";
    }

    public String getAllEmailStr(){
        String ids = "";
        for(int i = 0; i < getCount(); i++){
            UserInfo temp = getItem(i);
            ids = ids.concat(",").concat(String.valueOf(temp.getString(Define.DB_EMAIL)));
        }
        if( !ids.isEmpty() ) {
            ids = ids.substring(1);
        }
        return "["+ids+"]";
    }

    public String getAllNameStr(){
        String ids = "";
        for(int i = 0; i < getCount(); i++){
            UserInfo temp = getItem(i);
            ids = ids.concat(",").concat(String.valueOf(temp.getString(Define.DB_USER_FIRST_NM)));
        }
        if( !ids.isEmpty() ) {
            ids = ids.substring(1);
        }
        return "["+ids+"]";
    }

}