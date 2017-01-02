package kr.co.wegeneration.realshare.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
//import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.HashMap;

import kr.co.wegeneration.realshare.MainActivity;
import kr.co.wegeneration.realshare.NetController.ParseDAO;
import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.app.MyApplication;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.models.Notification;
import kr.co.wegeneration.realshare.models.UserInfo;
import kr.co.wegeneration.realshare.widget.CircularNetworkImageView;

/**
 * Created by user on 2015-09-01.
 */
public class NotificationParseAdapter extends ParseQueryAdapter<Notification> {

    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
    static Handler refreshFriendHandler;
    private AssetManager asset;
    private Activity activity;
    private HashMap<String, String> path= new HashMap<>();

    public NotificationParseAdapter(Context context, QueryFactory<Notification> queryfactory) {
        super(context, queryfactory);
    }

    public NotificationParseAdapter(final Context context, AssetManager asset, Activity activity) {
        super(context, new QueryFactory<Notification>() {
            public ParseQuery create() {
                return ParseDAO.getInstance(context).notificationListParse();
            }
        });
        this.asset = asset;
        this.activity = activity;
    }

    private static class ViewHolder {
        //MLRoundedImageView
        CircularNetworkImageView profilePic;
        TextView name;
        TextView timestamp;
        TextView statusMsg;
        LinearLayout activity_feed;
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


    @Override
    public View getItemView(final Notification noti, View view, ViewGroup parent) {


        final ViewHolder holder;

        if (view == null) {

            view = View.inflate(getContext(), R.layout.activity_item, null);
            //view = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.activity_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.name_activity);
            holder.timestamp = (TextView) view.findViewById(R.id.timestamp_activity);
            holder.statusMsg = (TextView) view.findViewById(R.id.txtStatusMsg_activity);
            holder.profilePic = (CircularNetworkImageView) view.findViewById(R.id.profilePic_activity);
            holder.activity_feed = (LinearLayout)view.findViewById(R.id.activity_feed);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        super.getItemView(noti, view, parent);

        Typeface typeface = Typeface.createFromAsset(asset, "BRI293.TTF");
/*        holder.numLiveViews.setTypeface(typeface);
        holder.labelLiveViews.setTypeface(typeface);

        holder.numLikes.setTypeface(typeface);
        holder.numComments.setTypeface(typeface);
        holder.numLikes.setTypeface(typeface);

        holder.timestamp.setTypeface(typeface);
        holder.name.setTypeface(typeface);
        holder.statusMsg.setTypeface(typeface);*/

        holder.activity_feed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                    ParseUser user = ParseUser.getCurrentUser();
                    int count = user.getInt("badge_count");
                    if(count > 0 ){
                        user.put("badge_count", --count);
                        user.saveInBackground();
                    }
                    noti.put("readVerified", true);
                    noti.saveInBackground();
                    Toast.makeText(getContext().getApplicationContext(), "Read", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    loadObjects();

                    MainActivity.getInstance().callBadgeCount();
                //  Badge Count
                  //  callBadgeCount(getContext());

            }
        });

        try {
            holder.name.setText(noti.getUserNm());
            CharSequence timestamp = DateUtils.getRelativeTimeSpanString(noti.getCreatedAt().getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
            holder.timestamp.setText(timestamp);

            ParseQuery<UserInfo> user = UserInfo.getQuery();
            user.whereEqualTo(Define.DB_USER_ID, noti.getUserId());
            user.getFirstInBackground(new GetCallback<UserInfo>() {
                @Override
                public void done(UserInfo parseUser, ParseException e) {
                    if (e == null) {
                        if (parseUser.get("thumbnailPath") == null)
                            path.put(noti.getString(Define.DB_USER_ID), "");
                        else
                            path.put(noti.getString(Define.DB_USER_ID), parseUser.get("thumbnailPath").toString());
                    } else e.printStackTrace();
                }
            });
            //holder.profilePic.setImageResource(R.drawable.default_profile_image);
            if(TextUtils.isEmpty(path.get(noti.getString(Define.DB_USER_ID))))
                holder.profilePic.setImageResource(R.drawable.default_profile_image);
            else
                holder.profilePic.setImageUrl(path.get(noti.getString(Define.DB_USER_ID)), imageLoader);


            if (!TextUtils.isEmpty(noti.getContent())) {

                final SpannableStringBuilder sp = new SpannableStringBuilder(noti.getContent());
                sp.setSpan(new ForegroundColorSpan(Color.RED), 0, noti.getUserNm().length(), Spannable.SPAN_POINT_MARK);
                sp.setSpan(new StyleSpan(Typeface.BOLD), 0, noti.getUserNm().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.statusMsg.setText(sp);
                //holder.statusMsg.setText(noti.getContent());
                holder.statusMsg.setVisibility(View.VISIBLE);
            } else {
                // status is empty, remove from view
                holder.statusMsg.setVisibility(View.GONE);
            }
            // user profile pic
            //holder.profilePic.setImageUrl(room.getProfilePath(), imageLoader);
            /*ParseFile imageFile = noti.getParseFile("profilePath");
            if (imageFile != null) {
                try {
                    holder.profilePic.setParseFile(imageFile);
                    holder.profilePic.setBackgroundResource(R.color.transparent);
                    holder.profilePic.loadInBackground();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                holder.profilePic.setBackgroundResource(R.drawable.default_profile_image);
                holder.profilePic.loadInBackground();
            }*/

        }catch(Exception e) { e.printStackTrace();}
        return view;
    }

}