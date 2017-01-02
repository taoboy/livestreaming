package kr.co.wegeneration.realshare.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Map;

import kr.co.wegeneration.realshare.NetController.ParseNetController;
import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.app.MyApplication;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.models.StatusComment;
import kr.co.wegeneration.realshare.models.UserInfo;
import kr.co.wegeneration.realshare.widget.CircularNetworkImageView;

/**
 * Created by user on 2015-09-01.
 */
public class CommentParseAdapter extends ParseQueryAdapter<StatusComment> {

    private AssetManager asset;
    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();

    public CommentParseAdapter(Context context, QueryFactory<StatusComment> queryfactory) {
        super(context, queryfactory);
    }

    public CommentParseAdapter(Context context, AssetManager asset, final String ownerId) {
        super(context, new QueryFactory<StatusComment>() {
            public ParseQuery create() {
                ParseQuery<StatusComment> query = StatusComment.getQuery();
                query.whereEqualTo("statusId", ownerId);
                //ParseQuery<UserInfo> subquery = UserInfo.getQuery();
                //query.whereMatchesKeyInQuery("userId", "userId", subquery);
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
    public View getItemView(final StatusComment commentLog, View view, ViewGroup parent) {

        ViewHolder holder;
        super.getItemView(commentLog, view, parent);

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


            if(commentLog.get("thumbnailPath")==null)
                holder.profileImage.setImageResource(R.drawable.default_profile_image);
            else
                holder.profileImage.setImageUrl(commentLog.get("thumbnailPath").toString() ,imageLoader);

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

            holder.comment.setText(commentLog.getComment());
            holder.username.setText(commentLog.getUsername());


            CharSequence timestamp = DateUtils.getRelativeTimeSpanString(commentLog.getCreatedAt().getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
            holder.time.setText(timestamp);


            if (commentLog.getUserId().equals(ParseUser.getCurrentUser().getString(Define.DB_USER_ID))) {
                holder.commentDel.setVisibility(View.VISIBLE);
            } else {
                holder.commentDel.setVisibility(View.INVISIBLE);
            }

            holder.commentDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Map<String, String> param = new HashMap<>();
                    param.put(Define.MSG_TYPE, Define.MSG_TYPE_DELETE_COMMENT);
                    ParseNetController.Comment(getContext(), param, commentLog);

                    loadObjects();
                    notifyDataSetChanged();
                }
            });
        }catch(Exception e ) {e.printStackTrace();}

        return view;
    }

}