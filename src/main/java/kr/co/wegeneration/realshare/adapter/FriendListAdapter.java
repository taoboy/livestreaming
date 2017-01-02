/*
package kr.co.wegeneration.realshare.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.wegeneration.realshare.FriendFragment;
import kr.co.wegeneration.realshare.ImageConfigureActivity;
import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.models.CommentLog;
import kr.co.wegeneration.realshare.widget.CircularNetworkImageView;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

*/
/**
 * Created by user on 2015-09-01.
 *//*

public class FriendListAdapter extends ArrayAdapter<Friend> {

    static Handler refreshCommentHandler;
    private boolean switched;
    DialogInterface popup;

    static Context thisContext;
    static ListView lstComment;
    AlertDialog commendPopup;
    static CommentListAdapter commentListAdapter;


    public FriendListAdapter(Context context, int resource, List<Friend> objects, boolean switched) {
        super(context, resource, objects);
        this.switched = switched;
        thisContext = context;
    }

    public void setSwitched(boolean switched) {
        this.switched = switched;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        if(position >= 0) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.row_friend, parent, false);
        }

        final Friend friend = getItem(position);
        if(friend!=null){
            TextView nameTextView = (TextView)convertView.findViewById(R.id.nameTextView);
            nameTextView.setText(friend.getUserName());

            TextView profileTextView = (TextView)convertView.findViewById(R.id.profileTextView);
            profileTextView.setText(friend.getUserStatus());

            ImageView activeImage = (ImageView)convertView.findViewById(R.id.activeImage);

            final Button pullLiveButton = (Button)convertView.findViewById(R.id.pullLiveButton);

            CircularNetworkImageView profileImageView = (CircularNetworkImageView)convertView.findViewById(R.id.profileImage);
            profileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ImageConfigureActivity.class);
                    intent.putExtra("imageName", "");
                    intent.putExtra("myImage", false);
                    ((Activity)getContext()).startActivity(intent);
                }
            });

            if(friend.getUserMode().equals("public") || friend.getUserMode().equals("private")){
                activeImage.setBackgroundResource(R.drawable.main_dot_red);
                pullLiveButton.setBackgroundResource(R.drawable.main_button_pull_off);
                pullLiveButton.setTag("LIVE");
                pullLiveButton.setVisibility(View.VISIBLE);
                activeImage.setVisibility(View.VISIBLE);
            }
            else if(friend.getUserMode().equals("watch")){
                activeImage.setBackgroundResource(R.drawable.main_dot_green);
                pullLiveButton.setTag("PULL");
                pullLiveButton.setBackgroundResource(R.drawable.main_button_pull_off);
                pullLiveButton.setVisibility(View.VISIBLE);
                activeImage.setVisibility(View.VISIBLE);
            }
            else if(friend.getUserMode().equals("signin") && friend.getUserActive().equals("y")){
                activeImage.setBackgroundResource(R.drawable.main_dot_green);
                pullLiveButton.setBackgroundResource(R.drawable.main_button_pull_off);
                activeImage.setVisibility(View.VISIBLE);
                pullLiveButton.setVisibility(View.VISIBLE);
                pullLiveButton.setTag("PULL");
            }
            else if(friend.getUserMode().equals("signin") && !friend.getUserActive().equals("y")){
                activeImage.setBackgroundResource(R.drawable.main_dot_grey);
                pullLiveButton.setBackgroundResource(R.drawable.main_button_pull_off);
                activeImage.setVisibility(View.VISIBLE);
                pullLiveButton.setVisibility(View.VISIBLE);
                pullLiveButton.setTag("PULL");
            }
            else if(friend.getUserMode().equals("signout")){
                //activeImage.setBackgroundResource(R.drawable.main_dot_grey);
                pullLiveButton.setBackgroundResource(R.drawable.main_button_pull_on);
                activeImage.setVisibility(View.VISIBLE);
                pullLiveButton.setVisibility(View.VISIBLE);
                pullLiveButton.setClickable(false);
                pullLiveButton.setEnabled(false);
                pullLiveButton.setTag("PULL");
            }

            LinearLayout profileHolder = (LinearLayout)convertView.findViewById(R.id.profileHolder);
            profileHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    commentListAdapter =  new CommentListAdapter(getContext(), R.layout.row_status, new ArrayList<CommentLog>(), false, false, String.valueOf(friend.getUserId()));

                    View statusDialog = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.dialog_status, (ViewGroup) v.getRootView(), false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setView(statusDialog);
                    TextView ownerName = (TextView)statusDialog.findViewById(R.id.ownerName);
                    ownerName.setText(friend.getUserName());

                    TextView ownerStatus = (TextView)statusDialog.findViewById(R.id.ownerStatus);
                    ownerStatus.setText(friend.getUserStatus());

                    ImageView ownerImage = (ImageView)statusDialog.findViewById(R.id.ownerImage);
                    ownerImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commendPopup.dismiss();
                            Intent intent = new Intent(getContext(), ImageConfigureActivity.class);
                            intent.putExtra("imageName", "");
                            intent.putExtra("myImage", false);
                            ((Activity) getContext()).startActivity(intent);
                        }
                    });

                    Button pullButton = (Button)statusDialog.findViewById(R.id.pullLiveButton);
                    pullButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String temp = pullLiveButton.getTag().toString().trim();
                            if (temp.equals("LIVE")) {
                                if (friend.getUserMode().equals("private")) {

                                    //Toast.makeText(getContext(), "Private room can not be accessed.", Toast.LENGTH_SHORT).show();
                                    Map<String, String> param = new HashMap<String, String>();

                                    param.put(Define.ACTION, Define.USER_MODE_WATCH);
                                    param.put(Define.CALLER_ACTIVITY, "MainActivity");
                                    param.put(Define.DB_USER_ID, FriendFragment.getUser_id());
                                    param.put(Define.OWNER_ID, String.valueOf(friend.getUserId()));
                                    param.put(Define.DB_USER_MODE, Define.USER_MODE_PRIVATE);

                                    NetController.getInstance(getContext())
                                            .getRequestQueue()
                                            .add(NetController.Room(getContext(), param));
                                } else {
                                    Map<String, String> param = new HashMap<String, String>();

                                    param.put(Define.ACTION, Define.USER_MODE_WATCH);
                                    param.put(Define.CALLER_ACTIVITY, "MainActivity");
                                    param.put(Define.DB_USER_ID, FriendFragment.getUser_id());
                                    param.put(Define.OWNER_ID, String.valueOf(friend.getUserId()));
                                    param.put(Define.DB_USER_MODE, Define.USER_MODE_WATCH);

                                    NetController.getInstance(getContext())
                                            .getRequestQueue()
                                            .add(NetController.Room(getContext(), param));
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

                    ImageView photoIcon = (ImageView)statusDialog.findViewById(R.id.photoIcon);
                    photoIcon.setVisibility(View.INVISIBLE);
                    ImageView editImage = (ImageView)statusDialog.findViewById(R.id.editImage);
                    editImage.setVisibility(View.INVISIBLE);

                    lstComment = (ListView)statusDialog.findViewById(R.id.commentsListView);

                    final EditText commentEditText = (EditText)statusDialog.findViewById(R.id.comment);
                    Button statusReplyButton = (Button)statusDialog.findViewById(R.id.statusReplyButton);
                    statusReplyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String comment = commentEditText.getText().toString().trim();

                            final Map<String, String> param = new HashMap<>();
                            param.put(Define.ACTION, Define.ACTION_COMMENT);
                            param.put(Define.MSG_TYPE, Define.MSG_TYPE_INSERT_COMMENT);
                            param.put(Define.MSG_SENDER_ID, String.valueOf(FriendFragment.getUser_id()));
                            param.put(Define.MSG_OWNER_ID, String.valueOf(friend.getUserId()));
                            param.put(Define.MSG, comment);
                            param.put(Define.MSG_LOG_ID, "");

                            NetController.getInstance(getContext())
                                    .getRequestQueue()
                                    .add(NetController.Comment(getContext(), param, false));

                            commentEditText.setText("");
                        }
                    });

                    ImageView btnCancel = (ImageView)statusDialog.findViewById(R.id.btnClear);
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commendPopup.dismiss();
                        }
                    });
                    builder.setCancelable(true);

                    callCommentList(String.valueOf(friend.getUserId()), false);

                    commendPopup = builder.show();
                }
            });


            */
/*if(friend.getUserActive().equals("y")){
                activeImage.setVisibility(View.VISIBLE);
            } else {
                activeImage.setVisibility(View.INVISIBLE);
            }*//*


           */
/* profileTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View pullDialog = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.dialog_status, parent, false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setView(pullDialog);

                    FriendFragment.lstComment = (ListView)pullDialog.findViewById(R.id.lstComment);

                    TextView nameTextView = (TextView)pullDialog.findViewById(R.id.nameTextView);
                    nameTextView.setText(friend.getUserName());

                    final EditText profileTextViewStatus = (EditText)pullDialog.findViewById(R.id.profileTextViewStatus);
                    profileTextViewStatus.setText(friend.getUserStatus());
                    profileTextViewStatus.setEnabled(false);

                    Button button = (Button)pullDialog.findViewById(R.id.statusChangeButtonDialog);
                    button.setText(R.string.PULL);
                    //editText.setText(thisActivity.getIntent().getStringExtra(Define.DB_STATUS));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pullRequest(v, friend, false);
                        }
                    });

                    final TextView statusText = (TextView)pullDialog.findViewById(R.id.statusText);

                    String owner_id =String.valueOf(friend.getUserId());
                    FriendFragment.owner_id = String.valueOf(friend.getUserId());
                    FriendFragment.callCommentList(owner_id);

                    Button statusReplyButton = (Button)pullDialog.findViewById(R.id.statusReplyButton);


                    statusReplyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String owner_id =String.valueOf(friend.getUserId());
                            final Map<String, String> param = new HashMap<>();
                            param.put(Define.ACTION, Define.ACTION_COMMENT);
                            param.put(Define.MSG_TYPE, Define.MSG_TYPE_INSERT_COMMENT);
                            param.put(Define.MSG_SENDER_ID, FriendFragment.getUser_id());
                            param.put(Define.MSG_OWNER_ID, owner_id);
                            param.put(Define.MSG,statusText.getText().toString().trim());
                            param.put(Define.MSG_LOG_ID, "");

                            NetController.getInstance(getContext())
                                    .getRequestQueue()
                                    .add(NetController.Comment(getContext(), param));


                            Map<String, String> param_push = new HashMap<String, String>();

                            param.put(Define.ACTION, Define.ACTION_COMMENT);
                            param.put(Define.MSG_TYPE, Define.MSG_TYPE_INSERT_COMMENT);
                            param.put(Define.MSG_SENDER_ID, FriendFragment.getUser_id());
                            param.put(Define.MSG_SENDER_STATUS, friend.getUserStatus());
                            param.put(Define.PARAM_RECEIVER_LIST, "[" + owner_id + "]");
                            param.put(Define.PARAM_ANONYMOUS, "n");
                            NetController.getInstance(getContext())
                                    .getRequestQueue()
                                    .add(NetController.Push(getContext(), param));


                            statusText.setText("");
                            FriendFragment.callCommentList(owner_id);
                            FriendFragment.owner_id = String.valueOf(friend.getUserId());
                            FriendFragment.commentListAdapter.notifyDataSetChanged();
                        }
                    });

                    ImageView btnCancel = (ImageView)pullDialog.findViewById(R.id.btnClear);
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popup.dismiss();
                        }
                    });
                    builder.setCancelable(true);
                    popup = builder.show();

                }
            });*//*


            pullLiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Button button = (Button) v;
                    String temp = ((Button) v).getTag().toString().trim();
                    if (temp.equals("LIVE")) {
                        if (friend.getUserMode().equals("private")) {

                            //Toast.makeText(getContext(), "Private room can not be accessed.", Toast.LENGTH_SHORT).show();
                            Map<String, String> param = new HashMap<String, String>();

                            param.put(Define.ACTION, Define.USER_MODE_WATCH);
                            param.put(Define.CALLER_ACTIVITY, "MainActivity");
                            param.put(Define.DB_USER_ID, FriendFragment.getUser_id());
                            param.put(Define.OWNER_ID, String.valueOf(friend.getUserId()));
                            param.put(Define.DB_USER_MODE, Define.USER_MODE_PRIVATE);

                            NetController.getInstance(getContext())
                                    .getRequestQueue()
                                    .add(NetController.Room(getContext(), param));
                            */
/*Map<String, String> param = new HashMap<String, String>();

                            param.put(Define.ACTION, Define.ACTION_KNOCK);
                            param.put(Define.MSG_TYPE, Define.MSG_TYPE_KNOCK);
                            param.put(Define.MSG_SENDER_ID, FriendFragment.getUser_id());
                            param.put(Define.PARAM_RECEIVER_LIST, "[" + friend.getUserId() + "]");
//                        if( pullMode.equals(Define.PULL_MODE_ANONYMOUSLY)) {
//                            param.put(Define.PARAM_ANONYMOUS, "y");
//                        } else {
//                            param.put(Define.PARAM_ANONYMOUS, "n");
//                        }
                            param.put(Define.PARAM_ANONYMOUS, "n");
                            NetController.getInstance(getContext())
                                    .getRequestQueue()
                                    .add(NetController.Push(getContext(), param));

                            button.setClickable(false);

                            Handler clickHandler = new Handler();
                            clickHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    button.setClickable(true);
                                }
                            }, 60000);*//*

                        } else {
                            Map<String, String> param = new HashMap<String, String>();

                            param.put(Define.ACTION, Define.USER_MODE_WATCH);
                            param.put(Define.CALLER_ACTIVITY, "MainActivity");
                            param.put(Define.DB_USER_ID, FriendFragment.getUser_id());
                            param.put(Define.OWNER_ID, String.valueOf(friend.getUserId()));
                            param.put(Define.DB_USER_MODE, Define.USER_MODE_WATCH);

                            NetController.getInstance(getContext())
                                    .getRequestQueue()
                                    .add(NetController.Room(getContext(), param));
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


                        */
/*final View pullDialog = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.dialog_pull, parent, false);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setView(pullDialog);
                        Button btnYes = (Button)pullDialog.findViewById(R.id.btnYes);
                        btnYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pullRequest(v, friend, true);
                                popup.dismiss();
                            }
                        });

                        Button btnNo = (Button)pullDialog.findViewById(R.id.btnNo);
                        btnNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pullRequest(v, friend, false);
                                popup.dismiss();
                            }
                        });

                        Button btnCancel = (Button)pullDialog.findViewById(R.id.btnCancel);
                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popup.dismiss();
                            }
                        });
                        builder.setCancelable(true);
                        popup = builder.show();
                        *//*


                    }
                }
            });

            CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    friend.setPrivateChecked(isChecked);
                    notifyDataSetChanged();
                }
            });

            String mode = friend.getUserMode();
            ImageView privateRoomImage = (ImageView)convertView.findViewById(R.id.privateRoomImage);
            if(mode.equals("public") || mode.equals("private")){
                */
/*pullLiveButton.setVisibility(View.VISIBLE);
                pullLiveButton.setBackgroundColor(Color.RED);
                pullLiveButton.setTextColor(Color.WHITE);
                pullLiveButton.setText("LIVE");*//*

                //pullLiveButton.setLayoutParams(new FrameLayout.LayoutParams(0, 0));
                if(switched){
                    pullLiveButton.setBackgroundResource(R.drawable.main_button_private_live);
                }
                else{
                    pullLiveButton.setBackgroundResource(R.drawable.main_button_live);
                }

                pullLiveButton.setTag("LIVE");
                if(mode.equals("private")){
                    privateRoomImage.setVisibility(View.VISIBLE);
                } else {
                    privateRoomImage.setVisibility(View.INVISIBLE);
                }

            } else {
                privateRoomImage.setVisibility(View.INVISIBLE);
                if(switched){
                    pullLiveButton.setLayoutParams(new FrameLayout.LayoutParams(0, 0));
                    pullLiveButton.setVisibility(View.INVISIBLE);
                    checkBox.setVisibility(View.VISIBLE);
                    if(friend.getPrivateChecked()){
                        checkBox.setChecked(true);
                    } else {
                        checkBox.setChecked(false);
                    }
                } else {
                */
/*    pullLiveButton.setVisibility(View.VISIBLE);
                    pullLiveButton.setBackgroundColor(Color.LTGRAY);
                    pullLiveButton.setTextColor(Color.BLACK);
                    pullLiveButton.setText("PULL");*//*


                    if(friend.getUserMode().equals("signout")){
                        //activeImage.setBackgroundResource(R.drawable.main_dot_grey);
                        pullLiveButton.setBackgroundResource(R.drawable.main_button_pull_on);
                        //activeImage.setVisibility(View.VISIBLE);
                        pullLiveButton.setVisibility(View.VISIBLE);
                        pullLiveButton.setClickable(false);
                        pullLiveButton.setEnabled(false);
                        pullLiveButton.setTag("PULL");
                    }
                    else {
                        pullLiveButton.setBackgroundResource(R.drawable.main_button_pull_off);
                        pullLiveButton.setTag("PULL");
                    }
                    checkBox.setLayoutParams(new FrameLayout.LayoutParams(0,0));
                    checkBox.setVisibility(View.INVISIBLE);
                }
            }

        }
        return convertView;
    }

    public void pullRequest(View v, Friend friend, boolean which){

        final Button button = (Button) v;

        Map<String, String> param = new HashMap<String, String>();

        param.put(Define.ACTION, Define.ACTION_PULL);
        param.put(Define.MSG_TYPE, Define.MSG_TYPE_PULL);
        param.put(Define.MSG_SENDER_ID, FriendFragment.getUser_id());
        param.put(Define.PARAM_RECEIVER_LIST, "[" + friend.getUserId() + "]");
        param.put(Define.RECEIVER_NAME, friend.getUserFirstName());
        param.put(Define.PARAM_ANONYMOUS, (which) ? "y" : "n");
//                        if (pullMode.equals(Define.PULL_MODE_ANONYMOUSLY)) {
//                            param.put(Define.PARAM_ANONYMOUS, "y");
//                        } else {
//                            param.put(Define.PARAM_ANONYMOUS, "n");
//                        }

        NetController.getInstance(getContext())
                .getRequestQueue()
                .add(NetController.Push(getContext(), param));

    }

    public String getCheckedIdStr(){
        String ids = "";
        for(int i = 0; i < getCount(); i++){
            Friend temp = getItem(i);
            if(temp.getPrivateChecked()){
                ids = ids.concat(",").concat(String.valueOf(temp.getUserId()));
            }
        }
        if( !ids.isEmpty() ) {
            ids = ids.substring(1);
        }
        return "["+ids+"]";
    }

    public String getAllIdStr(){
        String ids = "";
        for(int i = 0; i < getCount(); i++){
            Friend temp = getItem(i);
            ids = ids.concat(",").concat(String.valueOf(temp.getUserId()));
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

    public static void CommentListEvent(JSONObject resJSON){

        commentListAdapter.clear();

        if( resJSON == null ) return;
        if( !resJSON.has("commentList") ) return;

        try {
            JSONArray users = resJSON.getJSONArray("commentList");
            if( users == null || users.length() == 0 ) return;

            for(int i = 0; i < users.length(); i++){
                JSONObject temp = users.getJSONObject(i);
                CommentLog comment = new CommentLog(
                        temp.getString("log_id"),
                        temp.getString("user_id"),
                        temp.getString("user_name"),
                        temp.getString("comment"),
                        temp.getLong("reg_date")
                );
                //Log.d("fuck", comment.getComment());
                //Log.d("fuck", comment.getReg_Date().toString());
                //Log.d("fuck", comment.getUser_name());
                commentListAdapter.add(comment);
            }

            lstComment.setAdapter(commentListAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}*/
