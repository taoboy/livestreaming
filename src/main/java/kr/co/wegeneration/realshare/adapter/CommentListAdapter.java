package kr.co.wegeneration.realshare.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.format.DateUtils;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import kr.co.wegeneration.realshare.FriendFragment;
import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.models.CommentLog;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
//import kr.co.wegeneration.realshare.models.Video;

/**
 * Created by user on 2015-09-01.
 */
public class CommentListAdapter extends ArrayAdapter<CommentLog> {

    private boolean switched;
    DialogInterface popup;
    private boolean own;
    private String owner_id;

    public CommentListAdapter(Context context, int resource, List<CommentLog> objects, boolean switched, boolean own, String owner_id) {
        super(context, resource, objects);
        this.switched = switched;
        this.owner_id = owner_id;
        this.own = own;
    }

    public void setSwitched(boolean switched) {
        this.switched = switched;
    }

    class ViewHolder
    {
        TextView commentUserNm;
        TextView commentMsg;
        ImageView delButton;
        TextView commentTime;
    }
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if(position >= 0){
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.row_comment, parent, false);
        }
        final CommentLog commentLog = getItem(position);
        if(commentLog != null){
            TextView comment = (TextView)convertView.findViewById(R.id.comment);
            comment.setText(Html.fromHtml("<b>" + commentLog.getUser_name() + "</b> " + commentLog.getComment()));

            Timestamp timestamp = new Timestamp(commentLog.getReg_Date());
            TextView time = (TextView)convertView.findViewById(R.id.timeOfComment);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
            time.setText(sdf.format(new Date(timestamp.getTime())));

            ImageView commentDel = (ImageView)convertView.findViewById(R.id.btnCommentDel);
            if(commentLog.getUser_id().equals(FriendFragment.getUser_id())){
                commentDel.setVisibility(View.VISIBLE);
            } else {
                commentDel.setVisibility(View.INVISIBLE);
            }

            commentDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Map<String, String> param = new HashMap<>();
                    param.put(Define.ACTION, Define.ACTION_COMMENT);
                    param.put(Define.MSG_TYPE, Define.MSG_TYPE_DELETE_COMMENT);
                    param.put(Define.MSG_SENDER_ID, "test");
                    param.put(Define.MSG_OWNER_ID, owner_id);
                    param.put(Define.MSG            , "TEST");
                    param.put(Define.MSG_LOG_ID, commentLog.getLog_id());

                    NetController.getInstance(getContext())
                            .getRequestQueue()
                            .add(NetController.Comment(getContext(), param, own));
                }
            });

        }
        return convertView;

//        ViewHolder holder =null;
//
//        if(position >= 0) {
//            if(convertView == null ){
//                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.row_status, parent, false);
//                holder = new ViewHolder();
//                holder.commentUserNm = (TextView)convertView.findViewById(R.id.nameCommentText1);
//                holder.commentMsg = (TextView)convertView.findViewById(R.id.commentText1);
//                holder.delButton = (ImageView)convertView.findViewById(R.id.btnCommentDel1);
//                holder.commentTime = (TextView)convertView.findViewById(R.id.commentTime1);
//                convertView.setTag(holder);
//            }
//            else{
//                holder = (ViewHolder)convertView.getTag();
//            }
//        }
//
//
//        final CommentLog activity = getItem(position);
//
//        if(activity!=null) {
//           // String SrcPath ="rtsp://1.234.83.232:1935/vod/sample.mp4";
//
//            //CharSequence timestamp = DateUtils.getRelativeTimeSpanString(getContext(), activity.getReg_Date());
//
//            CharSequence timestamp = DateUtils.getRelativeTimeSpanString(activity.getReg_Date(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
////            CharSequence timestamp = DateUtils.getRelativeTimeSpanString( activity.getReg_Date());
//
//            holder.commentUserNm.setText(activity.getUser_name());
//            holder.commentMsg.setText(activity.getComment());
//            holder.commentTime.setText(timestamp.toString());
//
//            Log.d("fuck4" ,activity.getUser_id() + " : " +  FriendFragment.getUser_id() );
//            if(activity.getUser_id().equals(FriendFragment.getUser_id()))
//                holder.delButton.setVisibility(View.VISIBLE);
//            else
//                holder.delButton.setVisibility(View.INVISIBLE);
//
//            holder.delButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    final Map<String, String> param = new HashMap<>();
//                    param.put(Define.ACTION, Define.ACTION_COMMENT);
//                    param.put(Define.MSG_TYPE, Define.MSG_TYPE_DELETE_COMMENT);
//                    param.put(Define.MSG_SENDER_ID, "test");
//                    param.put(Define.MSG_OWNER_ID, "test");
//                    param.put(Define.MSG            , "TEST");
//                    param.put(Define.MSG_LOG_ID, activity.getLog_id());
//
//                    NetController.getInstance(getContext())
//                            .getRequestQueue()
//                            .add(NetController.Comment(getContext(), param));
//
////                    FriendFragment.callCommentList(FriendFragment.owner_id);
//
//                }
//            });
//
//         /*   Button requestBtn = (Button)convertView.findViewById(R.id.btnRequestFriend);
//            if(activity.isrequestSent()==true) requestBtn.setVisibility(View.VISIBLE);
//            else requestBtn.setVisibility(View.INVISIBLE);*/
//        }
//
//
//        return convertView;
    }

}