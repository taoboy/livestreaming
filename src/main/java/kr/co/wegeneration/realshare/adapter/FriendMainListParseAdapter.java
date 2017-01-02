package kr.co.wegeneration.realshare.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
//import com.parse.ParseImageView;
import com.parse.ParseQuery;
//import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Map;

import kr.co.wegeneration.realshare.FriendFragment;
import kr.co.wegeneration.realshare.ImageConfigureActivity;
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
public class FriendMainListParseAdapter extends ParseQueryAdapter<UserInfo> {

    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
    static Handler refreshCommentHandler;
    private boolean switched;
    DialogInterface popup;

    static boolean imageLoaded= false;
    static Context thisContext;
    static ListView lstComment;
    AlertDialog commendPopup;
    static CommentListAdapter commentListAdapter;
    private AssetManager asset;

    public FriendMainListParseAdapter(Context context, QueryFactory<UserInfo> queryfactory, boolean switched) {
        super(context, queryfactory);
        this.switched = switched;
        thisContext = context;
    }

    public void setSwitched(boolean switched) {
        this.switched = switched;
    }

    public FriendMainListParseAdapter(final Context context, boolean switched, AssetManager asset) {
        super(context, new QueryFactory<UserInfo>() {
            public ParseQuery create() {
                return ParseDAO.getInstance(context).friendMainListParse();
            }
        });
        this.switched = switched;
        thisContext = context;
        this.asset= asset;
    }

    private static class ViewHolder
    {
        TextView nameTextView;
        TextView statusTimestamp;
        ImageView statusNewIcon;
        Button pullLiveButton;
        CircularNetworkImageView profileImageView;
        CheckBox checkBox;
        ImageView privateRoomImage;

        TextView profileTextView;
        ImageView activeImage;
    }


    @Override
    public View getItemView(final UserInfo friend, View view, ViewGroup parent) {


        ViewHolder holder;


        if(view == null ){

             view = View.inflate(getContext(), R.layout.row_friend, null);
             //view = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.row_friend, parent, false);
             holder = new ViewHolder();
             holder.nameTextView         = (TextView)view.findViewById(R.id.nameTextView);
             holder.profileTextView      = (TextView)view.findViewById(R.id.profileTextView);
             holder.activeImage          = (ImageView)view.findViewById(R.id.activeImage);
             holder.pullLiveButton       = (Button)view.findViewById(R.id.pullLiveButton);
             holder.profileImageView     = (CircularNetworkImageView)view.findViewById(R.id.profileImage);
             holder.checkBox              = (CheckBox)view.findViewById(R.id.checkBox);
             holder.privateRoomImage     = (ImageView)view.findViewById(R.id.privateRoomImage);

            holder.statusTimestamp      = (TextView)view.findViewById(R.id.status_timestamp);
            holder.statusNewIcon          = (ImageView)view.findViewById(R.id.status_new_icon);
             view.setTag(holder);
         }
         else{
             holder = (ViewHolder)view.getTag();
         }

        super.getItemView(friend, view, parent);

            Typeface typeface = Typeface.createFromAsset(asset, "BRI293.TTF");
            holder.nameTextView.setTypeface(typeface);
            holder.profileTextView.setTypeface(typeface);
            // Add and download the image

            /*ParseFile imageFile = friend.getParseFile("thumbnailPath");
            if (imageFile != null) {
                try {
                        holder.profileImageView.setParseFile(imageFile);
                        holder.profileImageView.setBackgroundResource(R.color.transparent);
                        holder.profileImageView.loadInBackground();

                }catch(Exception e ){ e.printStackTrace();}
            }*/
            if(friend.get("thumbnailPath")==null)
                holder.profileImageView.setImageResource(R.drawable.default_profile_image);
            else
                holder.profileImageView.setImageUrl(friend.get("thumbnailPath").toString(), imageLoader);

            //holder.pullLiveButton.setVisibility(View.INVISIBLE);
            holder.privateRoomImage.setVisibility(View.INVISIBLE);
            holder.nameTextView.setText(friend.getString(Define.DB_USER_NM));


            if(friend.getString(Define.DB_STATUS)==null) {
                holder.profileTextView.setText(friend.getString(Define.DB_EMAIL));
                holder.statusTimestamp.setVisibility(View.GONE);
                holder.statusNewIcon.setVisibility(View.GONE);
            }
            else {
                try {
                    holder.profileTextView.setText(friend.getString(Define.DB_STATUS));
                    if(friend.get("statusUpdateDate")==null) {
                        holder.statusTimestamp.setVisibility(View.GONE);
                        holder.statusNewIcon.setVisibility(View.GONE);
                    }
                    else {
                        long updateTime =   friend.getDate("statusUpdateDate").getTime();
                        holder.statusTimestamp.setVisibility(View.VISIBLE);
                        CharSequence timestamp = DateUtils.getRelativeTimeSpanString(updateTime, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
                        holder.statusTimestamp.setText(timestamp);
                        Log.d("statusmsg", friend.getString(Define.DB_STATUS));
                        Log.d("statustime", String.valueOf(updateTime));
                        if (System.currentTimeMillis() - updateTime < 60 * 1000 * 60)
                            holder.statusNewIcon.setVisibility(View.VISIBLE);
                        else
                            holder.statusNewIcon.setVisibility(View.INVISIBLE);
                    }
                }catch (Exception e){e.printStackTrace();}


            }

            holder.profileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ImageConfigureActivity.class);
                    intent.putExtra("imageName", "");
                    intent.putExtra("myImage", false);
                    ((Activity) getContext()).startActivity(intent);
                }
            });

        try {

            //CheckBox checkBox  = (CheckBox)view.findViewById(R.id.checkBox);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    friend.put("privatechecked", isChecked);

                    //friend.add("privatechecked", isChecked);
                    //Log.d("fuck1", friend.get("privatechecked").toString());
                    notifyDataSetChanged();
                }
            });




            if (friend.getString(Define.USER_MODE).equals("public") || friend.getString(Define.USER_MODE).equals("private")) {
                holder.activeImage.setBackgroundResource(R.drawable.main_dot_red);
                holder.pullLiveButton.setBackgroundResource(R.drawable.main_button_pull_off);
                holder.pullLiveButton.setTag("LIVE");
                holder.pullLiveButton.setVisibility(View.VISIBLE);
                holder.activeImage.setVisibility(View.VISIBLE);
            } else if (friend.getString(Define.USER_MODE).equals("watch")) {
                holder.activeImage.setBackgroundResource(R.drawable.main_dot_green);
                holder.pullLiveButton.setTag("PULL");
                holder.pullLiveButton.setBackgroundResource(R.drawable.main_button_pull_off);
                holder.pullLiveButton.setVisibility(View.VISIBLE);
                holder.activeImage.setVisibility(View.VISIBLE);
            } else if (friend.getString(Define.USER_MODE).equals("signin") && friend.getString(Define.DB_USER_ACTIVE).equals("y")) {
                holder.activeImage.setBackgroundResource(R.drawable.main_dot_green);
                holder.pullLiveButton.setBackgroundResource(R.drawable.main_button_pull_off);
                holder.activeImage.setVisibility(View.VISIBLE);
                holder.pullLiveButton.setVisibility(View.VISIBLE);
                holder.pullLiveButton.setTag("PULL");
            } else if (friend.getString(Define.USER_MODE).equals("signin") && !friend.getString(Define.DB_USER_ACTIVE).equals("y")) {
                holder.activeImage.setBackgroundResource(R.drawable.main_dot_grey);
                holder.pullLiveButton.setBackgroundResource(R.drawable.main_button_pull_off);
                holder.activeImage.setVisibility(View.VISIBLE);
                holder.pullLiveButton.setVisibility(View.VISIBLE);
                holder.pullLiveButton.setTag("PULL");
            } else if (friend.getString(Define.USER_MODE).equals("signout")) {

                holder.pullLiveButton.setBackgroundResource(R.drawable.main_button_pull_on);
                holder.activeImage.setVisibility(View.VISIBLE);
                holder.pullLiveButton.setVisibility(View.VISIBLE);
                holder.pullLiveButton.setClickable(false);
                holder.pullLiveButton.setEnabled(false);
                holder.pullLiveButton.setTag("PULL");
            }
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            final Button pullLiveButton = holder.pullLiveButton;
            LinearLayout profileHolder = (LinearLayout) view.findViewById(R.id.profileHolder);
            profileHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FriendFragment.getInstance().callStatusPopup(v, false, friend);
                }
            });

            String mode = friend.getString(Define.USER_MODE);

            if (mode.equals("public") || mode.equals("private")) {

                if (switched) {
                    holder.pullLiveButton.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    holder.pullLiveButton.setBackgroundResource(R.drawable.main_button_private_live);
                    holder.pullLiveButton.setVisibility(View.VISIBLE);
                    holder.checkBox.setVisibility(View.INVISIBLE);
                } else {
                    holder.pullLiveButton.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    holder.pullLiveButton.setBackgroundResource(R.drawable.main_button_live);
                    holder.pullLiveButton.setVisibility(View.VISIBLE);
                    holder.checkBox.setVisibility(View.INVISIBLE);
                }
                holder.pullLiveButton.setTag("LIVE");
                if (mode.equals("private")) {
                    holder.privateRoomImage.setVisibility(View.VISIBLE);
                } else {
                    holder.privateRoomImage.setVisibility(View.INVISIBLE);
                }

            } else {
                holder.privateRoomImage.setVisibility(View.INVISIBLE);
                if (switched) {

                    holder.pullLiveButton.setLayoutParams(new FrameLayout.LayoutParams(0, 0));
                    holder.pullLiveButton.setVisibility(View.INVISIBLE);
                    holder.checkBox.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    holder.checkBox.setVisibility(View.VISIBLE);

                    /*try {
                        //Log.d("test", friend.get("privatechecked"));
                        if (friend.getBoolean("privatechecked")){
                            holder.checkBox.setChecked(true);
                        } else {
                            holder.checkBox.setChecked(false);
                        }
                    }catch(Exception e){e.printStackTrace();}*/
                } else {

                    holder.pullLiveButton.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
                    if (friend.get(Define.USER_MODE).equals("signout")) {
                        holder.pullLiveButton.setBackgroundResource(R.drawable.main_button_pull_on);
                        holder.pullLiveButton.setVisibility(View.VISIBLE);
                        holder.pullLiveButton.setClickable(false);
                        holder.pullLiveButton.setEnabled(false);
                        holder.pullLiveButton.setTag("PULL");
                    } else {
                        holder.pullLiveButton.setBackgroundResource(R.drawable.main_button_pull_off);
                        holder.pullLiveButton.setVisibility(View.VISIBLE);
                        holder.pullLiveButton.setTag("PULL");
                    }
                    holder.checkBox.setLayoutParams(new FrameLayout.LayoutParams(0, 0));
                    holder.checkBox.setVisibility(View.INVISIBLE);
                }
            }


            holder.pullLiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Button button = (Button) v;
                    String temp = ((Button) v).getTag().toString().trim();
                    if (temp.equals("LIVE")) {
                        if (friend.getString(Define.USER_MODE).equals("private")) {

                            //Toast.makeText(getContext(), "Private room can not be accessed.", Toast.LENGTH_SHORT).show();
                            Map<String, String> param = new HashMap<String, String>();

                            param.put(Define.ACTION, Define.USER_MODE_WATCH);
                            param.put(Define.MSG_TYPE, Define.MSG_TYPE_INVITE);
                            param.put(Define.CALLER_ACTIVITY, "MainActivity");
                            param.put(Define.DB_USER_ID, FriendFragment.getUser_id());
                            param.put(Define.OWNER_ID, friend.getString(Define.DB_USER_ID));
                            param.put(Define.DB_USER_MODE, Define.USER_MODE_PRIVATE);

                            /*NetController.getInstance(getContext())
                                    .getRequestQueue()
                                    .add(NetController.Room(getContext(), param));*/

                            ParseNetController.Room(thisContext, param);


                        } else {
                            Map<String, String> param = new HashMap<String, String>();

                            param.put(Define.ACTION, Define.USER_MODE_WATCH);
                            param.put(Define.MSG_TYPE, Define.MSG_TYPE_PUBLISH);
                            param.put(Define.CALLER_ACTIVITY, "MainActivity");
                            param.put(Define.DB_USER_ID, FriendFragment.getUser_id());
                            param.put(Define.OWNER_ID, friend.getString(Define.DB_USER_ID));
                            param.put(Define.DB_USER_MODE, Define.USER_MODE_WATCH);

                            /*NetController.getInstance(getContext())
                                    .getRequestQueue()
                                    .add(NetController.Room(getContext(), param));*/

                            ParseNetController.Room(thisContext, param);
                        }
                    } else if (temp.equals("PULL")) {

                        pullRequest(v, friend, false);

                        pullLiveButton.setBackgroundResource(R.drawable.main_button_pull_on);
                        pullLiveButton.setClickable(false);

                        Handler clickHandler = new Handler();
                        clickHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pullLiveButton.setClickable(true);
                                pullLiveButton.setBackgroundResource(R.drawable.main_button_pull_off);
                            }
                        }, 4000);


                    }
                }
            });


        }
        catch(Exception e) { e.printStackTrace();}

            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




        return view;
    }



    public void pullRequest(View v, UserInfo friend, boolean which){

        final Button button = (Button) v;

        Map<String, String> param = new HashMap<String, String>();

        param.put(Define.ACTION, Define.ACTION_PULL);
        param.put(Define.MSG_TYPE, Define.MSG_TYPE_PULL);
        param.put(Define.DB_USER_ID, FriendFragment.getUser_id());
        param.put(Define.PARAM_RECEIVER_LIST, "[" + friend.getString(Define.DB_USER_ID) + "]");
        param.put(Define.RECEIVER_NAME, friend.get(Define.DB_USER_FIRST_NM).toString());

        /*NetController.getInstance(getContext())
                .getRequestQueue()
                .add(NetController.Push(getContext(), param));*/
        ParseNetController.PushSend(getContext(), param);

    }

    public String getCheckedIdStr(){
        String ids = "";
        for(int i = 0; i < getCount(); i++){
            UserInfo temp = getItem(i);
            if(temp.getBoolean("privatechecked")){
                ids = ids.concat(",").concat(String.valueOf(temp.getString(Define.DB_USER_ID)));
            }
        }
        if( !ids.isEmpty() ) {
            ids = ids.substring(1);
        }
        return "["+ids+"]";
    }




    public static void callCommentList(String user_ids, boolean own) {
        Map<String, String> param = new HashMap<String, String>();
        param.put(Define.ACTION, Define.ACTION_COMMENT_LIST);
        //param.put(Define.DB_USER_ID, user_id);

        Log.i("rstest", "commentList calling");
        NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.CommentList(thisContext, user_ids, "commentList", param, own));
    }



}