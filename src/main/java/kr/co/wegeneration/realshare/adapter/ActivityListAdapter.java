package kr.co.wegeneration.realshare.adapter;

import android.graphics.Color;
import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import kr.co.wegeneration.realshare.ActivityFragment;
import kr.co.wegeneration.realshare.AddFriendActivity;
import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.models.ActivityLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.Button;
import android.widget.TextView;
import kr.co.wegeneration.realshare.R;

//import kr.co.wegeneration.realshare.models.Video;

/**
 * Created by user on 2015-09-01.
 */
public class ActivityListAdapter extends ArrayAdapter<ActivityLog> {

    private boolean switched;
    DialogInterface popup;

    public ActivityListAdapter(Context context, int resource, List<ActivityLog> objects, boolean switched) {
        super(context, resource, objects);
        this.switched = switched;
    }

    class ViewHolder
    {
        TextView activityTime;
        TextView activityLog;
        Button requestBtn;
    }


    public void setSwitched(boolean switched) {
        this.switched = switched;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        ViewHolder holder =null;

        if(position >= 0) {
            if(convertView == null ){
            convertView = ((android.app.Activity) getContext()).getLayoutInflater().inflate(R.layout.row_activity, parent, false);
                holder = new ViewHolder();
                holder.activityTime = (TextView)convertView.findViewById(R.id.txtActivityTime);
                holder.activityLog = (TextView)convertView.findViewById(R.id.txtActivityName);
                holder.requestBtn = (Button)convertView.findViewById(R.id.btnRequestFriend);
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder)convertView.getTag();
            }
        }

        final ActivityLog activity = getItem(position);

        if(activity!=null) {
            // String SrcPath ="rtsp://1.234.83.232:1935/vod/sample.mp4";


            CharSequence timestamp = DateUtils.getRelativeTimeSpanString(activity.getTimelog(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);

            if (activity.isrequestSent() == false) {
                holder.activityTime.setText(timestamp.toString());
            }
            holder.activityLog.setText(activity.getActivityLog());


            if (activity.getRead_yn() == null) convertView.setBackgroundResource(R.color.grey);

            final ViewHolder holder_inner = holder;

            if (activity.isrequestSent() == true) {
                holder.requestBtn.setVisibility(View.VISIBLE);
                holder.requestBtn.setBackgroundColor(Color.RED);
                holder.requestBtn.setTextColor(Color.WHITE);
                holder.requestBtn.setText("Received");

                holder.requestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, String> param = new HashMap<String, String>();


                        holder_inner.requestBtn.setBackgroundColor(Color.BLUE);
                        holder_inner.requestBtn.setText("Friend");
                        param.put(Define.ACTION, Define.ACTION_CONFIRM_FRIEND);
                        param.put(Define.MSG_TYPE, "[\"" + Define.MSG_TYPE_CONFIRMFRIEND + "\"]");
                        param.put(Define.MSG_SENDER_ID, ActivityFragment.user_id);
                        param.put(Define.MSG_SENDER_NM, ActivityFragment.user_nm);
                        param.put(Define.PARAM_RECEIVER_LIST, "[" + activity.getUser_id() + "]");

                        //param.put(Define.PARAM_ANONYMOUS, "n");
                        NetController.getInstance(getContext())
                                .getRequestQueue()
                                .add(NetController.AddFriend(getContext(), param));


                    }


                });

            } else holder.requestBtn.setVisibility(View.INVISIBLE);
        }
             return convertView;
    }

}