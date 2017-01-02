/*
package kr.co.wegeneration.realshare.adapter;

import java.util.List;

import android.widget.LinearLayout.LayoutParams;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

import kr.co.wegeneration.realshare.AddFriendActivity;
import kr.co.wegeneration.realshare.FriendFragment;
import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.models.Friend;

*/
/**
 * Created by user on 2015-09-01.
 *//*

public class FriendAddAdapter extends ArrayAdapter<Friend> {

    private boolean switched;
    DialogInterface popup;

    public FriendAddAdapter(Context context, int resource, List<Friend> objects, boolean switched) {
        super(context, resource, objects);
        this.switched = switched;
    }

    class ViewHolder
    {
        TextView nameTextView;
        TextView profileTextView;
        ImageView activeImage;
        ImageView addFriendButton;
        ImageView rejectFriendButton;
    }

    public void setSwitched(boolean switched) {
        this.switched = switched;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
*/
/*        if(position >= 0) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.row_add_friend, parent, false);
        }
*//*

        ViewHolder holder =null;

        if(position >= 0) {
            if(convertView == null ){
                convertView = ((android.app.Activity) getContext()).getLayoutInflater().inflate(R.layout.row_add_friend, parent, false);
                holder = new ViewHolder();
                holder.nameTextView         = (TextView)convertView.findViewById(R.id.nameAddTextView);
                holder.profileTextView      = (TextView)convertView.findViewById(R.id.emailTextView);
                holder.activeImage         = (ImageView)convertView.findViewById(R.id.activeAddImage);
                holder.addFriendButton     = (ImageView)convertView.findViewById(R.id.addFriendButton);
                holder.rejectFriendButton = (ImageView)convertView.findViewById(R.id.rejectFriendButton);
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder)convertView.getTag();
            }
        }

        final Friend friend = getItem(position);
        if(friend!=null){
            //TextView nameTextView = (TextView)convertView.findViewById(R.id.nameAddTextView);
            holder.nameTextView.setText(friend.getUserName());

            //TextView profileTextView = (TextView)convertView.findViewById(R.id.emailTextView);
            holder.profileTextView.setText(friend.getUserEmail());

            //ImageView activeImage = (ImageView)convertView.findViewById(R.id.activeAddImage);
            holder.activeImage.setBackgroundResource(R.drawable.on);

            if(friend.getUserActive().equals("y")){
                holder.activeImage.setVisibility(View.VISIBLE);
            } else {
                holder.activeImage.setVisibility(View.INVISIBLE);
            }


            if(friend.getUserMode().equals("public") || friend.getUserMode().equals("private")){
                holder.activeImage.setBackgroundResource(R.drawable.main_dot_red);
                holder.activeImage.setVisibility(View.VISIBLE);
            }
            else if(friend.getUserMode().equals("watch")){
                holder.activeImage.setBackgroundResource(R.drawable.main_dot_green);
                holder.activeImage.setVisibility(View.VISIBLE);
            }
            else if(friend.getUserMode().equals("signin") && friend.getUserActive().equals("y")){
                holder.activeImage.setBackgroundResource(R.drawable.main_dot_green);
                holder.activeImage.setVisibility(View.VISIBLE);
            }
            else if(friend.getUserMode().equals("signin") && !friend.getUserActive().equals("y")){
                holder.activeImage.setBackgroundResource(R.drawable.main_dot_grey);
                holder.activeImage.setVisibility(View.VISIBLE);
            }
            else if(friend.getUserMode().equals("signout")){
                holder.activeImage.setVisibility(View.VISIBLE);
            }


            //final ImageView addFriendButton    = (ImageView)convertView.findViewById(R.id.addFriendButton);
            //final ImageView rejectFriendButton = (ImageView)convertView.findViewById(R.id.rejectFriendButton);
            String getUserRequestConfirm = ( friend.getUserRequestConfirm()==null ? "null" :  friend.getUserRequestConfirm());

            LayoutParams lparams = new LayoutParams(200,100);
            */
/*if(Build.MODEL.equals("SHV-E210K"))
                holder.rejectFriendButton.setVisibility(View.INVISIBLE);
            else*//*

            holder.rejectFriendButton.setVisibility(View.INVISIBLE);
            holder.rejectFriendButton.setEnabled(false);
            lparams.weight = 1.0f;
            //lparams.leftMargin= 50;
            //holder.addFriendButton.setLayoutParams(lparams);


            if(getUserRequestConfirm.equals("friend")){
                */
/*addFriendButton.setVisibility(View.VISIBLE);
                addFriendButton.setBackgroundColor(Color.BLUE);
                addFriendButton.setTextColor(Color.WHITE);
                addFriendButton.setText("Unfriend");*//*

                holder.addFriendButton.setTag("Unfriend");
                holder.addFriendButton.setImageResource(R.drawable.unfriend);

            }else if(getUserRequestConfirm.equals("sent"))
            {
                */
/*addFriendButton.setVisibility(View.VISIBLE);
                addFriendButton.setBackgroundColor(Color.RED);
                addFriendButton.setTextColor(Color.WHITE);
                addFriendButton.setText("Request Sent");*//*

                holder.addFriendButton.setTag("Request Sent");
                holder.addFriendButton.setImageResource(R.drawable.requestsent);

            }else if(getUserRequestConfirm.equals("received"))
            {
                */
/*addFriendButton.setVisibility(View.VISIBLE);
                addFriendButton.setBackgroundColor(Color.RED);
                addFriendButton.setTextColor(Color.WHITE);
                addFriendButton.setText("Confirmed");*//*

                holder.addFriendButton.setTag("Confirmed");
                holder.addFriendButton.setImageResource(R.drawable.confirm);

                LayoutParams addparams = new LayoutParams(340, 100);
                //LayoutParams lparams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                addparams.weight = 0.5f;
                //holder.addFriendButton.setLayoutParams(addparams);
                holder.rejectFriendButton.setVisibility(View.VISIBLE);
                */
/*rejectFriendButton.setVisibility(View.VISIBLE);
                rejectFriendButton.setBackgroundColor(Color.LTGRAY);
                rejectFriendButton.setTextColor(Color.BLACK);
                rejectFriendButton.setText("Reject");*//*

                holder.rejectFriendButton.setEnabled(true);
                holder.rejectFriendButton.setImageResource(R.drawable.ignore);

                LayoutParams rejectparams = new LayoutParams(340, 100);
                //LayoutParams rejectparams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                rejectparams.weight = 0.5f;
                rejectparams.rightMargin =10;
                //holder.rejectFriendButton.setLayoutParams(rejectparams);

            }
            else
            {

                */
/*addFriendButton.setVisibility(View.VISIBLE);
                addFriendButton.setBackgroundColor(Color.LTGRAY);
                addFriendButton.setTextColor(Color.BLACK);
                addFriendButton.setText("+Send Request");*//*

                holder.addFriendButton.setTag("+Send Request");
                holder.addFriendButton.setImageResource(R.drawable.addfriend);
            }

            final ViewHolder holder_temp = holder;
            holder.addFriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final ImageView button = (ImageView) v;
                    String temp = ((ImageView) v).getTag().toString().trim();
                    if (temp.equals("Unfriend")) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext())
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Map<String, String> param = new HashMap<String, String>();

                                        param.put(Define.ACTION, Define.ACTION_ADD_FRIEND);
                                        param.put(Define.MSG_TYPE, "[\"" + Define.MSG_TYPE_DELETEFRIEND + "\"]");
                                        param.put(Define.MSG_SENDER_ID, AddFriendActivity.getUser_id());
                                        param.put(Define.MSG_SENDER_NM, AddFriendActivity.getUser_nm());
                                        param.put(Define.PARAM_RECEIVER_LIST, "[" + friend.getUserId() + "]");

                                        //param.put(Define.PARAM_ANONYMOUS, "n");
                                        NetController.getInstance(getContext())
                                                .getRequestQueue()
                                                .add(NetController.AddFriend(getContext(), param));



                                 */
/*       button.setVisibility(View.VISIBLE);
                                        button.setBackgroundColor(Color.LTGRAY);
                                        button.setTextColor(Color.BLACK);
                                        button.setText("Add Friend");*//*

                                        button.setImageResource(R.drawable.addfriend);
                                        friend.setUserRequestConfirm("addfriend");
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setMessage("Remove " + friend.getUserName() + " as a friend?");

                        alert.show();


                    } else if (temp.equals("+Send Request")) {
                        */
/*button.setVisibility(View.VISIBLE);
                        button.setBackgroundColor(Color.RED);
                        button.setTextColor(Color.WHITE);
                        button.setText("Request Sent");*//*

                        button.setImageResource(R.drawable.requestsent);
                        friend.setUserRequestConfirm("sent");

                        Map<String, String> param = new HashMap<String, String>();

                        param.put(Define.ACTION, Define.ACTION_ADD_FRIEND);
                        param.put(Define.MSG_TYPE, "[\"" + Define.MSG_TYPE_ADDFRIEND + "\"]");
                        param.put(Define.MSG_SENDER_ID, AddFriendActivity.getUser_id());
                        param.put(Define.MSG_SENDER_NM, AddFriendActivity.getUser_nm());
                        param.put(Define.PARAM_RECEIVER_LIST, "[" + friend.getUserId() + "]");

                        //param.put(Define.PARAM_ANONYMOUS, "n");
                        NetController.getInstance(getContext())
                                .getRequestQueue()
                                .add(NetController.AddFriend(getContext(), param));

                        friendRequest(friend);

                    } else if (temp.equals("Confirmed")) {

                        */
/*if(Build.MODEL.equals("SHV-E210K"))
                            holder_temp.rejectFriendButton.setVisibility(View.INVISIBLE);
                        else*//*

                        holder_temp.rejectFriendButton.setVisibility(View.INVISIBLE);
                        holder_temp.rejectFriendButton.setEnabled(false);

                        */
/*button.setVisibility(View.VISIBLE);
                        button.setBackgroundColor(Color.BLUE);
                        button.setTextColor(Color.WHITE);
                        button.setText("Unfriend");*//*

                        friend.setUserRequestConfirm("friend");
                        button.setImageResource(R.drawable.unfriend);

                        LayoutParams lparams = new LayoutParams(200, 100);
                        lparams.weight = 1.0f;
                        //button.setLayoutParams(lparams);


                        Map<String, String> param = new HashMap<String, String>();

                        param.put(Define.ACTION, Define.ACTION_CONFIRM_FRIEND);
                        param.put(Define.MSG_TYPE, "[\"" + Define.MSG_TYPE_CONFIRMFRIEND + "\"]");
                        param.put(Define.MSG_SENDER_ID, AddFriendActivity.getUser_id());
                        param.put(Define.MSG_SENDER_NM, AddFriendActivity.getUser_nm());
                        param.put(Define.PARAM_RECEIVER_LIST, "[" + friend.getUserId() + "]");

                        //param.put(Define.PARAM_ANONYMOUS, "n");
                        NetController.getInstance(getContext())
                                .getRequestQueue()
                                .add(NetController.AddFriend(getContext(), param));

                    } else if (temp.equals("Request Sent")) {

                     */
/*   button.setVisibility(View.VISIBLE);
                        button.setBackgroundColor(Color.LTGRAY);
                        button.setTextColor(Color.BLACK);
                        button.setText("+Send Request");*//*

                        friend.setUserRequestConfirm("addfriend");
                        button.setImageResource(R.drawable.addfriend);

                        Map<String, String> param = new HashMap<String, String>();

                        param.put(Define.ACTION, Define.ACTION_DELETE_FRIEND);
                        param.put(Define.MSG_TYPE, "[\"" + Define.MSG_TYPE_DELETEFRIEND + "\"]");
                        param.put(Define.MSG_SENDER_ID, AddFriendActivity.getUser_id());
                        param.put(Define.MSG_SENDER_NM, AddFriendActivity.getUser_nm());
                        param.put(Define.PARAM_RECEIVER_LIST, "[" + friend.getUserId() + "]");

                        //param.put(Define.PARAM_ANONYMOUS, "n");
                        NetController.getInstance(getContext())
                                .getRequestQueue()
                                .add(NetController.AddFriend(getContext(), param));

                    }
                }
            });

            holder.rejectFriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final ImageView button = (ImageView) v;
                    //String temp = ((ImageView)v).getTag().toString().trim();

                    */
/*if(Build.MODEL.equals("SHV-E210K"))
                        holder_temp.rejectFriendButton.setVisibility(View.INVISIBLE);
                    else*//*

                    holder_temp.rejectFriendButton.setVisibility(View.INVISIBLE);
                    holder_temp.rejectFriendButton.setEnabled(false);

                        */
/*addFriendButton.setVisibility(View.VISIBLE);
                        addFriendButton.setBackgroundColor(Color.LTGRAY);
                        addFriendButton.setTextColor(Color.BLACK);
                        addFriendButton.setText("+Send Request");*//*


                    //button.setVisibility(View.VISIBLE);
                    button.setImageResource(R.drawable.addfriend);
                    friend.setUserRequestConfirm("addfriend");


                    LayoutParams lparams = new LayoutParams(240, 100);
                    lparams.width = 200;
                    //holder_temp.addFriendButton.setLayoutParams(lparams);

                    Map<String, String> param = new HashMap<String, String>();

                    param.put(Define.ACTION, Define.ACTION_DELETE_FRIEND);
                    param.put(Define.MSG_TYPE, "[\"" + Define.MSG_TYPE_DELETEFRIEND + "\"]");
                    param.put(Define.MSG_SENDER_ID, AddFriendActivity.getUser_id());
                    param.put(Define.MSG_SENDER_NM, AddFriendActivity.getUser_nm());
                    param.put(Define.PARAM_RECEIVER_LIST, "[" + friend.getUserId() + "]");

                    //param.put(Define.PARAM_ANONYMOUS, "n");
                    NetController.getInstance(getContext())
                            .getRequestQueue()
                            .add(NetController.AddFriend(getContext(), param));

                }

            });


        }
        return convertView;
    }

    public void friendRequest(Friend friend){

        Map<String, String> param = new HashMap<String, String>();

        param.put(Define.ACTION, Define.ACTION_ADD_FRIEND);
        param.put(Define.MSG_TYPE, Define.MSG_TYPE_ADDFRIEND);
        param.put(Define.MSG_SENDER_ID, FriendFragment.getUser_id());
        param.put(Define.PARAM_RECEIVER_LIST, "[" + friend.getUserId() + "]");
        param.put(Define.RECEIVER_NAME, friend.getUserName());

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


    public String getAllFriendStr(){
        String ids = "";
        for(int i = 0; i < getCount(); i++){
            Friend temp = getItem(i);
            String flag = "";
            if(temp.getUserRequestConfirm()==null || temp.getUserRequestConfirm().equals("null") )  flag = "addfriend";
            else if(temp.getUserRequestConfirm().equals("received") )                                flag = "confirmfriend";
            ids = ids.concat(",").concat("\""+ flag + "\"");

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


}*/
