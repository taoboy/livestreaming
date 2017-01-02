package kr.co.wegeneration.realshare.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.android.volley.toolbox.ImageLoader;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.wegeneration.realshare.FriendFragment;
import kr.co.wegeneration.realshare.MainActivity;
import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.NetController.ParseDAO;
import kr.co.wegeneration.realshare.NetController.ParseNetController;
//import kr.co.wegeneration.realshare.PlayerActivity;
import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.VideoViewDemo;
import kr.co.wegeneration.realshare.app.MyApplication;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.models.Notification;
import kr.co.wegeneration.realshare.models.Room;
import kr.co.wegeneration.realshare.models.UserInfo;
import kr.co.wegeneration.realshare.util.FeedImageView;
import kr.co.wegeneration.realshare.widget.CircularNetworkImageView;

/**
 * Created by user on 2015-09-01.
 */
public class TImeLineParseAdapter extends ParseQueryAdapter<Room> {

    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
    static android.os.Handler refreshFriendHandler;
    boolean likes= false;
    private AssetManager asset;
    private Firebase mFirebasePeopleRef;
    private Firebase firebase;
    private Context thiscontext=null;
    private HashMap<String, Boolean> path= new HashMap<>();
    private HashMap<String, String> profilepath= new HashMap<>();

    public TImeLineParseAdapter(Context context, QueryFactory<Room> queryfactory) {
        super(context, queryfactory);
    }

    public TImeLineParseAdapter(final Context context, AssetManager asset, Firebase firebase) {
        super(context, new QueryFactory<Room>() {
            public ParseQuery create() {
                return ParseDAO.getInstance(context).timelineListParse();
            }
        });
        this.asset    = asset;
        this.firebase = firebase;
        this.thiscontext = context;
    }

    private static class ViewHolder
    {
        //MLRoundedImageView
        CircularNetworkImageView      profilePic;
        TextView                name;
        TextView                timestamp;
        TextView                statusMsg;
        FeedImageView           feedImageView;
        TextView                numLikes;
        TextView                numComments;
        TextView                numView;
        LinearLayout comment_timeline;
        ImageView               likes;
        Button                  spinner;
        TextView                numLiveViews;
        TextView                labelLiveViews;
        ImageView               livetimeline;
        TextView                duration;
    }


    @Override
    public View getItemView(final Room room, View view, ViewGroup parent) {


        final ViewHolder holder;

        super.getItemView(room, view, parent);


        if(view == null ){

             view = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.feed_item, parent, false);
             holder = new ViewHolder();
             holder.name             = (TextView)view.findViewById(R.id.name);
             holder.timestamp       = (TextView)view.findViewById(R.id.timestamp);
             holder.statusMsg       = (TextView)view.findViewById(R.id.txtStatusMsg);
             holder.profilePic      = (CircularNetworkImageView)view.findViewById(R.id.profilePic);
             holder.feedImageView  = (FeedImageView)view.findViewById(R.id.feedImage1);
             holder.numLikes        = (TextView)view.findViewById(R.id.numLikes);
             holder.numComments    = (TextView)view.findViewById(R.id.numComments);
             holder.numView         = (TextView)view.findViewById(R.id.numView);
             holder.likes            = (ImageView)view.findViewById(R.id.likes);
             holder.comment_timeline = (LinearLayout)view.findViewById(R.id.comment_timeline);
             holder.spinner           = (Button) view.findViewById(R.id.spinner);
            holder.duration            = (TextView)view.findViewById(R.id.duration);
            holder.numLiveViews     = (TextView)view.findViewById(R.id.numLiveViews);
            holder.labelLiveViews   = (TextView)view.findViewById(R.id.labelLiveViews);
            holder.livetimeline      = (ImageView)view.findViewById(R.id.live_timeline);
             view.setTag(holder);
         }
         else{
             holder = (ViewHolder)view.getTag();
         }

        Typeface typeface = Typeface.createFromAsset(asset, "BRI293.TTF");
/*        holder.numLiveViews.setTypeface(typeface);
        holder.labelLiveViews.setTypeface(typeface);

        holder.numLikes.setTypeface(typeface);
        holder.numComments.setTypeface(typeface);
        holder.numLikes.setTypeface(typeface);

        holder.timestamp.setTypeface(typeface);
        holder.name.setTypeface(typeface);
        holder.statusMsg.setTypeface(typeface);*/



        if(room.getStatus().equals("live")){
            holder.numLiveViews.setVisibility(View.VISIBLE);
            holder.labelLiveViews.setVisibility(View.VISIBLE);
            holder.livetimeline.setVisibility(View.VISIBLE);
            holder.livetimeline.bringToFront();
            holder.labelLiveViews.bringToFront();
            holder.numLiveViews.bringToFront();
            holder.duration.setVisibility(View.INVISIBLE);
           mFirebasePeopleRef  = firebase.child("chat").child(room.getRoomName()).child("PeopleList");
           mFirebasePeopleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.numLiveViews.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        }else{
            try {
                long time = room.getInt("recordTime");
                if(time>0) {
                    DateFormat df = new SimpleDateFormat("mm:ss"); // HH=24h, hh=12h
                    String timestamp = df.format(time);
                    holder.duration.setText(timestamp);
                    holder.duration.setVisibility(View.VISIBLE);
                }
                holder.numLiveViews.setVisibility(View.INVISIBLE);
                holder.labelLiveViews.setVisibility(View.INVISIBLE);
                holder.livetimeline.setVisibility(View.INVISIBLE);
            }catch (Exception e) { e.printStackTrace(); }
        }


        holder.numLikes.setText(room.getHeartCount() + " Likes");
        holder.numView.setText(room.getViewCount() + " Views");
        holder.numComments.setText(room.getCommentCount() + " Comments");

        //holder.likes.setEnabled(false);


         getHeart(room, holder);

        //Log.d("fuck1", String.valueOf(room.getIsLikeds()));
        holder.likes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                if (!path.get(room.getRoomName())) {
                    room.increment("heartCount");
                    final ParseRelation<ParseObject> relation = room.getRelation("isLiked");
                    relation.add(ParseUser.getCurrentUser().getParseObject(Define.UserInfo));
                    room.pinInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                holder.likes.setImageResource(R.drawable.icon_like_on);
                                path.put(room.getRoomName(), true);

                                if (!room.getUserId().equals(ParseUser.getCurrentUser().getString(Define.DB_USER_ID))) {
                                    String msg = "";
                                    if (room.getHeartCount() > 1)
                                        msg = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_USER_NM) + " and people like your video.";
                                    else

                                        msg = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_USER_NM) + " likes your video.";

                                    Notification noti = new Notification();
                                    noti.put("type", "likedVideo");
                                    noti.put("userId", ParseUser.getCurrentUser().getString(Define.DB_USER_ID));
                                    noti.put("friendId", room.getUserId());
                                    noti.put("userNm", ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_USER_NM));
                                    noti.put("content", msg);
                                    noti.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                try {
                                                    UserInfo user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded();
                                                    user.increment("badge_count");
                                                    user.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null)
                                                                MainActivity.getInstance().callBadgeCount();
                                                            else {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } catch (Exception e3) {
                                                    e3.printStackTrace();
                                                }
                                            } else e.printStackTrace();
                                        }
                                    });
                                }

                            } else e.printStackTrace();
                        }
                    });

                } else {
                    int heartCount = room.getHeartCount();
                    room.put("heartCount", --heartCount);
                    ParseRelation<ParseObject> relation = room.getRelation("isLiked");
                    relation.remove(ParseUser.getCurrentUser().getParseObject(Define.UserInfo));
                    room.pinInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                holder.likes.setImageResource(R.drawable.icon_like_off);
                                path.put(room.getRoomName(), false);

                                holder.likes.setEnabled(false);

                            } else e.printStackTrace();
                        }
                    });

                }
                loadObjects();
                notifyDataSetChanged();
                }catch(Exception e){e.printStackTrace();}
            }
        });
        holder.spinner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext())
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                room.setIsDeleted(true);
                                room.saveInBackground();
                                loadObjects();
                                dialog.dismiss();     //닫기
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                            }
                        }).setMessage(R.string.exitTimeLine);


                alert.show();
            }
        });

        holder.name.setText(room.getUserNm());
        CharSequence timestamp = DateUtils.getRelativeTimeSpanString(room.getCreatedAt().getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
        holder.timestamp.setText(timestamp);

        if (!TextUtils.isEmpty(room.getTitle())) {
            holder.statusMsg .setText(room.getTitle());
            holder.statusMsg .setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            holder.statusMsg .setVisibility(View.GONE);
        }
        // user profile pic
        //holder.profilePic.setImageUrl(room.getProfilePath(), imageLoader);

        ParseQuery<UserInfo> user = UserInfo.getQuery();
        user.whereEqualTo(Define.DB_USER_ID, room.getUserId());
        user.getFirstInBackground(new GetCallback<UserInfo>() {
            @Override
            public void done(UserInfo parseUser, ParseException e) {
                if (e == null) {
                    if (TextUtils.isEmpty(parseUser.getString("thumbnailPath")))
                        profilepath.put(room.getRoomName(), "");
                    else {
                        profilepath.put(room.getRoomName(), parseUser.get("thumbnailPath").toString());
                    }
                } else {
                    e.printStackTrace();
                    Log.d("Error", "ThumbnailPath");
                }
            }
        });


        if(TextUtils.isEmpty(profilepath.get(room.getRoomName())))
            holder.profilePic.setImageResource(R.drawable.default_profile_image);
        else
            holder.profilePic.setImageUrl(profilepath.get(room.getRoomName()), imageLoader);

        /*ParseFile imageFile = room.getParseFile("profilePath");
        if (imageFile != null) {
            try {
                holder.profilePic.setParseFile(imageFile);
                holder.profilePic.setBackgroundResource(R.color.transparent);
                holder.profilePic.loadInBackground();

            }catch(Exception e ){ e.printStackTrace();}
        }
        else{
            holder.profilePic.setBackgroundResource(R.drawable.default_profile_image);
            holder.profilePic.loadInBackground();
        }
*/
        // Feed image
        if (!TextUtils.isEmpty(room.getThumbnailPath() )) {

            refreshFriendHandler = new Handler() {
                public void handleMessage(Message msg) {
                    holder.feedImageView.setImageUrl(room.getThumbnailPath(), imageLoader);
                    holder.feedImageView.setVisibility(View.VISIBLE);
                    holder.feedImageView
                            .setResponseObserver(new FeedImageView.ResponseObserver() {
                                @Override
                                public void onError() {
                                    Log.d("FeedImage", "Error");
                                    refreshFriendHandler.sendEmptyMessageDelayed(101, 2000);
                                }

                                @Override
                                public void onSuccess() {
                                    Log.d("FeedImage", "Success");

                                    if(room.getStatus().equals("live"))
                                        refreshFriendHandler.sendEmptyMessageDelayed(101, 2000);
                                    else
                                        refreshFriendHandler.removeMessages(101);
                                }
                            });
                }
            };
            refreshFriendHandler.sendEmptyMessage(101);

        } else {
            holder.feedImageView.setBackgroundResource(R.drawable.blank_images);
        }

        holder.feedImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (room.getStatus().equals("live")) {
                    if (room.getType().equals("private")) {

                        //Toast.makeText(getContext(), "Private room can not be accessed.", Toast.LENGTH_SHORT).show();
                        Map<String, String> param = new HashMap<String, String>();

                        param.put(Define.ACTION, Define.USER_MODE_WATCH);
                        param.put(Define.MSG_TYPE, Define.MSG_TYPE_INVITE);
                        param.put(Define.CALLER_ACTIVITY, "MainActivity");
                        param.put(Define.DB_USER_ID, FriendFragment.getUser_id());
                        param.put(Define.OWNER_ID, room.getUserId());
                        param.put(Define.DB_USER_MODE, Define.USER_MODE_PRIVATE);

                        ParseNetController.Room(getContext(), param);


                    } else {
                        Map<String, String> param = new HashMap<String, String>();

                        param.put(Define.ACTION, Define.USER_MODE_WATCH);
                        param.put(Define.MSG_TYPE, Define.MSG_TYPE_PUBLISH);
                        param.put(Define.CALLER_ACTIVITY, "MainActivity");
                        param.put(Define.DB_USER_ID, FriendFragment.getUser_id());
                        param.put(Define.OWNER_ID, room.getUserId());
                        param.put(Define.DB_USER_MODE, Define.USER_MODE_WATCH);

                        ParseNetController.Room(getContext(), param);
                    }
                } else {

                    room.increment("viewCount");
                    room.saveEventually();

                    Intent videoView = new Intent(getContext(), VideoViewDemo.class);
                    videoView.putExtra(Define.VIDEO_PATH, room.getVideoPath());
                    videoView.putExtra(Define.ROOM_ID, room.getRoomName());
                    videoView.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    getContext().startActivity(videoView);

                    //Exoplayer rtsp possible? or MP4
                    /*Intent mpdIntent = new Intent(getContext(), PlayerActivity.class)
                            .setData(Uri.parse(room.getVideoPath()))
                            .putExtra(PlayerActivity.CONTENT_ID_EXTRA   , room.getRoomId())
                            .putExtra(PlayerActivity.CONTENT_TYPE_EXTRA , PlayerActivity.TYPE_OTHER)
                            .putExtra(PlayerActivity.PROVIDER_EXTRA      , "");
                    getContext().startActivity(mpdIntent);*/
                }
            }
        });

        holder.comment_timeline.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                callStatusPopup(v, false, room);
            }
        });


        return view;
    }

    private DialogInterface profileChangePopup;
    private ListView lstComment;
    private TimeLineCommentParseAdapter commentParseAdapter;

    public void callStatusPopup(View v, final boolean isOwn, final Room user)
    {

        View statusDialog = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.timeline_dialog_status, (ViewGroup) v.getRootView(), false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(statusDialog);
        builder.setCancelable(true);

        TextView ownerName = (TextView)statusDialog.findViewById(R.id.ownerName);
        ownerName.setText(user.getHeartCount() + " likes this TimeLine");
        lstComment = (ListView)statusDialog.findViewById(R.id.commentsListView);
        commentParseAdapter = new TimeLineCommentParseAdapter(getContext(), user, getContext().getAssets());

        lstComment.setAdapter(commentParseAdapter);
        commentParseAdapter.notifyDataSetChanged();

        final EditText commentEditText = (EditText)statusDialog.findViewById(R.id.comment);
        Button statusReplyButton = (Button)statusDialog.findViewById(R.id.statusReplyButton);
        statusReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commentEditText.getText().toString().trim();

                final Map<String, String> param = new HashMap<>();
                param.put(Define.ACTION         , Define.ACTION_COMMENT);
                param.put(Define.MSG_TYPE       , Define.MSG_TYPE_TIMELINE_INSERT_COMMENT);
                param.put(Define.MSG_SENDER_ID , ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_USER_ID));
                param.put(Define.MSG_OWNER_ID  ,  user.getUserId());
                param.put(Define.DB_USER_NM     , ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_USER_NM));
                param.put(Define.DB_ROOM          , user.getRoomName());
                param.put(Define.TITLE            , user.getTitle());
                param.put(Define.MSG              , comment);

                ParseNetController.RoomComment(getContext(), param);
                user.increment("commentCount");
                user.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getContext(), "Commented", Toast.LENGTH_SHORT).show();
                        } else e.printStackTrace();
                        ;
                    }
                });
                /*NetController.getInstance(thisContext)
                        .getRequestQueue()
                        .add(NetController.Comment(thisContext, param, true));*/
                commentParseAdapter.loadObjects();
                commentParseAdapter.notifyDataSetChanged();

                commentEditText.setText("");
            }
        });

        ImageView btnCancel = (ImageView)statusDialog.findViewById(R.id.btnClear);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileChangePopup.dismiss();
            }
        });
        builder.setCancelable(true);


        profileChangePopup = builder.show();


    }




    public void callBadgeCount(Context context){
        Intent intents = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        // 패키지 네임과 클래스 네임 설정
        intents.putExtra("badge_count_package_name", context.getPackageName());
        intents.putExtra("badge_count_class_name", context.getClass().getName());
        // 업데이트 카운트
        intents.putExtra("badge_count", ParseUser.getCurrentUser().getInt("badge_count"));
        context.sendBroadcast(intents);
    }


    public void getHeart(final Room room,  final ViewHolder holder){

        ParseQuery<ParseObject> room_query = room.getRelation("isLiked").getQuery();
        //room_query.whereEqualTo(Define.DB_USER_ID, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_USER_ID));
        //room_query.fromLocalDatastore();
        room_query.fromPin("room");
        room_query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if(list.size() > 0 ) {
                        likes = true;
                        path.put(room.getRoomName(), true);
                        holder.likes.setImageResource(R.drawable.icon_like_on);
                        holder.likes.setEnabled(true);
                    }
                    else {
                        //room.setIsLikeds(false);
                        likes = false;
                        holder.likes.setImageResource(R.drawable.icon_like_off);
                        holder.likes.setEnabled(true);
                        path.put(room.getRoomName(), false);
                    }
                } else {
                    Log.d("Error", "Get Liked People");
                    e.printStackTrace();
                }
            }
        });


    }



}
