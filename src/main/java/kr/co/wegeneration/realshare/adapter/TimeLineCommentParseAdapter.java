package kr.co.wegeneration.realshare.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.text.format.DateUtils;
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

import kr.co.wegeneration.realshare.NetController.ParseNetController;
import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.app.MyApplication;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.models.Room;
import kr.co.wegeneration.realshare.models.RoomComment;
import kr.co.wegeneration.realshare.widget.CircularNetworkImageView;

/**
 * Created by user on 2015-09-01.
 */
public class TimeLineCommentParseAdapter extends ParseQueryAdapter<RoomComment> {

    private AssetManager asset;
    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();

    public TimeLineCommentParseAdapter(Context context, QueryFactory<RoomComment> queryfactory) {
        super(context, queryfactory);
    }

    public TimeLineCommentParseAdapter(Context context, final Room room,  AssetManager asset) {
        super(context, new QueryFactory<RoomComment>() {
            public ParseQuery create() {
                ParseQuery<RoomComment> query = RoomComment.getQuery();
             //   ParseQuery<ParseUser> subquery = ParseUser.getQuery();
              //  query.whereMatchesKeyInQuery("userId", "objectId", subquery);
                query.whereEqualTo("roomName", room.getRoomName());
                //query.include("thumbnailPath");
                query.orderByAscending("createdAt");
                return query;
            }
        });
        this.asset = asset;
    }

    class ViewHolder
    {
        TextView username;
        TextView comment;
        TextView time;
        ImageView commentDel;
        CircularNetworkImageView profileImage;
    }


    @Override
    public View getItemView(final RoomComment
                                        comment, View view, ViewGroup parent) {

        ViewHolder holder;
        super.getItemView(comment, view, parent);

        if(view == null ){

             view = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.row_comment, parent, false);
             holder = new ViewHolder();
             holder.profileImage     = (CircularNetworkImageView)view.findViewById(R.id.profileImage);
             holder.comment     = (TextView)view.findViewById(R.id.comment);
             holder.username    = (TextView)view.findViewById(R.id.username);
             holder.time        = (TextView)view.findViewById(R.id.timeOfComment);
             holder.commentDel = (ImageView)view.findViewById(R.id.btnCommentDel);

             view.setTag(holder);
         }
         else{
             holder = (ViewHolder)view.getTag();
         }
        try {

            if(comment.get("thumbnailPath")==null)
                holder.profileImage.setImageResource(R.drawable.default_profile_image);
            else
                holder.profileImage.setImageUrl(comment.get("thumbnailPath").toString() ,imageLoader);
            /*ParseFile imageFile = commentLog.getParseFile("thumbnailPath");
            if (imageFile != null) {
                try {
                    holder.profileImage.setParseFile(imageFile);
                    holder.profileImage.setBackgroundResource(R.color.transparent);
                    holder.profileImage.loadInBackground();

                }catch(Exception e ){ e.printStackTrace();}
            }*/

            Typeface typeface = Typeface.createFromAsset(asset, "BRI293.TTF");
            holder.username.setTypeface(typeface);
            holder.comment.setTypeface(typeface);
            holder.time.setTypeface(typeface);

            holder.comment.setText(comment.getContent());
            holder.username.setText(comment.getUserNm());


            CharSequence timestamp = DateUtils.getRelativeTimeSpanString(comment.getCreatedAt().getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
            holder.time.setText(timestamp);


            if (comment.getUserId().equals(ParseUser.getCurrentUser().getString(Define.DB_USER_ID))) {
                holder.commentDel.setVisibility(View.VISIBLE);
            } else {
                holder.commentDel.setVisibility(View.INVISIBLE);
            }

            holder.commentDel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Map<String, String> param = new HashMap<>();
                    param.put(Define.MSG_TYPE, Define.MSG_TYPE_DELETE_COMMENT);
                    param.put(Define.DB_ROOM, comment.getRoomName());
                    ParseNetController.RoomComment(getContext(), param, comment);

                    loadObjects();
                    notifyDataSetChanged();
                }
            });
        }catch(Exception e ) {e.printStackTrace();}

        return view;
    }

}