package kr.co.wegeneration.realshare;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.app.Fragment;


import com.android.volley.toolbox.ImageLoader;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.NetController.ParseNetController;
import kr.co.wegeneration.realshare.adapter.CommentParseAdapter;
import kr.co.wegeneration.realshare.adapter.FriendMainListParseAdapter;
import kr.co.wegeneration.realshare.app.MyApplication;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;
import kr.co.wegeneration.realshare.models.Room;
import kr.co.wegeneration.realshare.models.UserInfo;
import kr.co.wegeneration.realshare.widget.CircularNetworkImageView;

public class FriendFragment extends Fragment
//        implements ActionBar.TabListener
{
    private static final String LogTag = "FriendFragment";

    public static FriendFragment newInstance() {
        return new FriendFragment();
    }

    static Context           thisContext;
    static FragmentActivity  thisActivity;

    static String               senderstatus="";
    static String               user_id = "";
    static String               user_nm = "";
    public static String      user_status = "";
    static public String      owner_id ="";

    static String msgType = "";
    static String senderId = "";
    static String senderNm = "";
    public static boolean isPrivate = false;
    public static Button swtShare;
    CircularNetworkImageView ownerImage;
    Button btnShare;
    Button btnPull;
    CircularNetworkImageView profileImage;
    TextView pullTextHolder;
    static ListView lstFriend;
    public ListView lstComment;
    public static SharedPreferences pref;
    FriendMainListParseAdapter friendMainListParseAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CommentParseAdapter commentParseAdapter;
    LinearLayout profileHolder;
    TextView nameTextView, profileTextView;
    DialogInterface profileChangePopup;
    ImageLoader imageLoader;
    static Handler refreshFriendHandler;
    EditText editText;

    private static FriendFragment sInstance;

    public static synchronized FriendFragment getInstance() {
        return sInstance;
    }

    public static String getShareMode() {

        return swtShare.getTag()==Define.SHARE_MODE_PRIVATE ? Define.SHARE_MODE_PRIVATE : Define.SHARE_MODE_PUBLIC;
    }


    public static String getUser_id() {
        return user_id;
    }

    public static Context getContextFriend() {
        return thisContext;
    }

    public static FragmentActivity getActivityFriend() {
        return thisActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_main, container, false);

        thisContext   =this.getContext();
        thisActivity  =this.getActivity();
        sInstance     =this;
        imageLoader = MyApplication.getInstance().getImageLoader();

        try {
            pref = RSPreference.getPreference(thisActivity);

            user_id = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_USER_ID);
            user_nm = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_USER_NM);
            user_status = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_STATUS);
            msgType = pref.getString(Define.MSG_TYPE, "");
            senderId = pref.getString(Define.MSG_SENDER_ID, "");
            senderNm = pref.getString(Define.MSG_SENDER_NM, "");
            senderstatus = pref.getString(Define.MSG_SENDER_STATUS, "");

        }catch (Exception e) { e.printStackTrace(); }

        /*if(msgType.equals(Define.MSG_TYPE_INSERT_COMMENT)){

            Log.d("FriendFragment" , senderNm + " : " + senderstatus);
            owner_id = senderId;
            callStatusPopup( rootView, false, ParseUser.getCurrentUser() );
            msgType="";
        }*/

        Intent intent = getActivity().getIntent();
        if( intent != null ) {


            msgType = intent.getStringExtra(Define.MSG_TYPE);
            senderId = intent.getStringExtra(Define.MSG_SENDER_ID);
            senderNm = intent.getStringExtra(Define.MSG_SENDER_NM);

        }

        swipeRefreshLayout  = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        profileImage         = (CircularNetworkImageView)rootView.findViewById(R.id.profileImage);
        profileTextView     = (TextView)rootView.findViewById(R.id.profileTextView);
        nameTextView         = (TextView)rootView.findViewById(R.id.nameTextView);
        swtShare               = (Button)rootView.findViewById(R.id.swtShare);
        btnShare              = (Button)rootView.findViewById(R.id.btnShare);
        profileHolder        = (LinearLayout)rootView.findViewById(R.id.profileHolder);
        lstFriend              = (ListView)rootView.findViewById(R.id.lstFriend);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "BRI293.TTF");
        //profileTextView.setTypeface(typeface);
        //nameTextView.setTypeface(typeface);

        friendMainListParseAdapter = new FriendMainListParseAdapter(thisContext, false, getActivity().getAssets());

        lstFriend.setAdapter(friendMainListParseAdapter);
        //friendMainListParseAdapter.loadObjects();

        profileTextView.setText((TextUtils.isEmpty(user_status)==true) ? "Click to change status" : user_status);
        nameTextView.setText(user_nm);

        /*
        profileHolder.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                final AlertDialog.Builder builder    = new AlertDialog.Builder(thisContext);
                final View profileEditView           = getActivity().getLayoutInflater().inflate(R.layout.dialog_profile, (ViewGroup) v.getRootView(), false);
                Button button                         = (Button)profileEditView.findViewById(R.id.statusChangeButton);

                builder.setTitle("What are you up to?");

                editText = (EditText) profileEditView.findViewById(R.id.editText);
                editText.setText(user_status);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileTextView.setText(editText.getText().toString().trim());

                        Map<String, String> param = new HashMap<>();
                        param.put(Define.DB_STATUS, editText.getText().toString().trim());

                        ParseNetController.Status(thisContext, param);


                        profileChangePopup.dismiss();
                    }
                });

                Button btnCancel = (Button)profileEditView.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileChangePopup.dismiss();
                    }
                });

                ImageView btnClear = (ImageView)profileEditView.findViewById(R.id.btnClear);
                btnClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText("");
                    }
                });

                builder.setView(profileEditView);
                builder.setCancelable(true);
                profileChangePopup = builder.show();
            }
        });
        */

        try {
            if (ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().get("thumbnailPath") == null) {
                profileImage.setImageResource(R.drawable.default_profile_image);
            } else {
                //TODO :: getProfileImage - now, from local device only

                profileImage.setImageUrl(ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().get("thumbnailPath").toString(), imageLoader);
            }
        }catch (Exception e)  { e.printStackTrace(); }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageConfigureActivity.class);
                intent.putExtra("myImage", true);
                intent.putExtra("imageName", pref.getString("profileImage", ""));
                /*intent.putExtra("imageName", ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString("thumbnailPath")==null ?
                        "" :  ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString("thumbnailPath"));*/
                startActivityForResult(intent, 100);
            }
        });

        profileHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    owner_id = user_id;
                    callStatusPopup(v, true, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded());
                }catch(Exception e) {e.printStackTrace();}
            }
        });



        if(isPrivate==false)
            swtShare.setTag(Define.SHARE_MODE_PUBLIC);
        else{
            swtShare.setTag(Define.SHARE_MODE_PRIVATE);
            swtShare.setBackgroundResource(R.drawable.main_option_private);

            friendMainListParseAdapter.setSwitched(true);
            friendMainListParseAdapter.notifyDataSetChanged();
        }


        btnShare.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String shareMode = swtShare.getTag() == Define.SHARE_MODE_PRIVATE ? Define.SHARE_MODE_PRIVATE : Define.SHARE_MODE_PUBLIC;
                btnShare.setClickable(false);

                Map<String, String> param = new HashMap<>();
                param.put(Define.CALLER_ACTIVITY, "MainActivity");
                param.put(Define.DB_USER_ID, user_id);


                if (shareMode.equals(Define.SHARE_MODE_PRIVATE)) {
                    // TODO : intent 로 체크박스 사용자
                    param.put(Define.DB_USER_MODE, Define.USER_MODE_PRIVATE);

                    String friendList = friendMainListParseAdapter.getCheckedIdStr();
                    if (friendList.equals("[]")) {
                        Toast.makeText(thisContext.getApplicationContext(), "Please Select Someone", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    param.put(Define.PARAM_ALLOW_LIST, friendList);
                    param.put(Define.ACTION, Define.ACTION_ROOM_PRIVATE);
                    param.put(Define.MSG_TYPE, Define.MSG_TYPE_INVITE);

                    if (isPrivate) {
                        param.put(Define.PARAM_PULL_FROM, senderNm);
                    }

                } else {
                    param.put(Define.DB_USER_MODE, Define.USER_MODE_PUBLIC);
                    param.put(Define.ACTION, Define.ACTION_ROOM_PUBLIC);
                    param.put(Define.MSG_TYPE, Define.MSG_TYPE_PUBLISH);

                }

                ParseNetController.Room(thisContext, param);
                isPrivate = false;
                Handler clickHandler = new Handler();
                clickHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnShare.setClickable(true);
                    }
                }, 2000);



            }
        });


        swtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean isChecked = false;
                if (v.getTag().equals(Define.SHARE_MODE_PUBLIC)) {
                    v.setTag(Define.SHARE_MODE_PRIVATE);
                    v.setBackgroundResource(R.drawable.main_option_private);
                    isChecked = true;
                } else {
                    v.setTag(Define.SHARE_MODE_PUBLIC);
                    v.setBackgroundResource(R.drawable.main_option_public);
                    isChecked = false;
                }

                friendMainListParseAdapter.setSwitched(isChecked);
                friendMainListParseAdapter.notifyDataSetChanged();
                friendMainListParseAdapter.loadObjects();
                isPrivate = false;

            }
        });

        /*refreshFriendHandler = new Handler() {
            public void handleMessage(Message msg) {
                //            callFriendList(text);
                //friendAddAdapter.notifyDataSetChanged();
                friendMainListParseAdapter.loadObjects();
                friendMainListParseAdapter.notifyDataSetChanged();
                refreshFriendHandler.sendEmptyMessageDelayed(5, 4000);
            }
        };
        refreshFriendHandler.sendEmptyMessage(5);*/


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        friendMainListParseAdapter.notifyDataSetChanged();
                        friendMainListParseAdapter.loadObjects();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });


        return rootView;
    }

    public void pullRequest(ParseObject friend){


        Map<String, String> param = new HashMap<String, String>();

        param.put(Define.ACTION     , Define.ACTION_PULL);
        param.put(Define.MSG_TYPE, Define.MSG_TYPE_PULL);
        param.put(Define.DB_USER_ID, user_id);
        param.put(Define.PARAM_RECEIVER_LIST, "[" + friend.getString(Define.DB_USER_ID) + "]");
        param.put(Define.RECEIVER_NAME, friend.getString(Define.DB_USER_FIRST_NM).toString());

        ParseNetController.PushSend(getContext(), param);

    }

    public void callCommentList(){

    }


    public static void callFriendList() {
        Map<String, String> param = new HashMap<String, String>();
        param.put(Define.ACTION, Define.ACTION_FRIEDNDS);
        param.put(Define.DB_USER_ID, user_id);
//            param.put(Define.DB_EMAIL, email);
        Log.i("rstest", "friendList calling");
        NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.FriendList(thisContext, user_id, "listfriend", param));
    }

    public static void callCommentList(String owner_id, boolean b) {
    }

    public static void CommentListEvent(JSONObject resJSON) {
    }

    class ViewHolder
    {
        Button statusReplyButton;
        TextView statusText;
        TextView nameTextView;
        EditText profileTextViewStatus;
        Button button;
    //    ListView lstComment;
    }

    String ownerId="";

    public void callStatusPopup(View v, final boolean isOwn, final ParseObject user)
    {

        View statusDialog = ((Activity)getActivity()).getLayoutInflater().inflate(R.layout.dialog_status, (ViewGroup) v.getRootView(), false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(statusDialog);

        TextView ownerName = (TextView)statusDialog.findViewById(R.id.ownerName);
        ownerName.setText(user.getString(Define.DB_USER_NM));

        final EditText ownerStatus = (EditText)statusDialog.findViewById(R.id.ownerStatus);
        String text = user.getString(Define.DB_STATUS)==null ? "" :user.getString(Define.DB_STATUS);
        ownerStatus.setText(text);
        if(!isOwn) ownerStatus.setEnabled(false);

        ImageView editImage = (ImageView)statusDialog.findViewById(R.id.editImage);
        if(!isOwn) editImage.setVisibility(View.INVISIBLE);

        ownerImage = (CircularNetworkImageView)statusDialog.findViewById(R.id.ownerImage);

        try {
            if (user.getString("thumbnailPath") == null)
                ownerImage.setImageResource(R.drawable.default_profile_image);
            else {
                if (!isOwn)
                    ownerImage.setImageUrl(user.getString("thumbnailPath"), imageLoader);
                else
                    ownerImage.setImageUrl(ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString("thumbnailPath"), imageLoader);
            }
        }catch(Exception ParseException){ ParseException.printStackTrace();}
        /*ParseFile imageFile = user.getParseFile("thumbnailPath");
        if (imageFile != null) {
            try {
                ownerImage.setParseFile(imageFile);
                ownerImage.setBackgroundResource(R.color.transparent);
                ownerImage.loadInBackground();
            }catch(Exception e ){ e.printStackTrace();}
        }*/

        ownerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileChangePopup.dismiss();
                Intent intent = new Intent(getContext(), ImageConfigureActivity.class);
                intent.putExtra("imageName", "");
                //intent.putExtra("imageName", user.getString("thumbnailPath")==null ? "" :  user.getString("thumbnailPath"));
                intent.putExtra("myImage", isOwn);
                ((Activity) getContext()).startActivity(intent);
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Map<String, String> param = new HashMap<>();
                String text = ownerStatus.getText().toString();
                param.put(Define.MSG_TYPE, Define.MSG_TYPE_STATUS);
                param.put(Define.DB_STATUS, text);
                ParseNetController.Status(thisContext, param);
                profileTextView.setText((text.equals("")) ? "Click to change status" : text);
                user_status = text;
            }
        });

        Button pullButton = (Button)statusDialog.findViewById(R.id.pullLiveButton);
        if(isOwn) pullButton.setVisibility(View.INVISIBLE);

        pullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pullRequest(user);
            }
        });

        lstComment = (ListView)statusDialog.findViewById(R.id.commentsListView);


        if(isOwn)  ownerId = ParseUser.getCurrentUser().getString(Define.DB_USER_ID);
        else       ownerId = user.getString(Define.DB_USER_ID);

        commentParseAdapter = new CommentParseAdapter(thisContext, getActivity().getAssets(), ownerId);
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
                param.put(Define.MSG_TYPE       , Define.MSG_TYPE_INSERT_COMMENT);
                param.put(Define.MSG_SENDER_ID , user_id);
                param.put(Define.MSG_OWNER_ID  , ownerId);
                param.put(Define.DB_USER_NM     , user_nm);
                param.put(Define.MSG              , comment);
                param.put(Define.ISOWN              , String.valueOf(isOwn));

                ParseNetController.Comment(thisContext, param);
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
//                recycleBitmap(ownerImage);

                profileChangePopup.dismiss();
            }
        });
        builder.setCancelable(true);


        profileChangePopup = builder.show();


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 100){
                String path = data.getStringExtra("path");
                if(path != null && !path.equals("")) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                    profileImage.setImageBitmap(bitmap);
                }
            }
        }
    }


    protected void onPostCreate(Bundle savedInstanceState) {
        onPostCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.post(new Runnable() {
            @Override
            public void run() {
                profileImage.getLayoutParams().width = nameTextView.getMeasuredHeight() + profileTextView.getMeasuredHeight();
                profileImage.getLayoutParams().height = nameTextView.getMeasuredHeight() + profileTextView.getMeasuredHeight();
                profileImage.requestLayout();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout) {

            ParseNetController.SignOut(thisContext);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(LogTag, "activity resume ");

    }

    private static void recycleBitmap(ImageView iv) {

        try{
            if(iv!=null) {
                Drawable d = iv.getDrawable();
                if (d instanceof BitmapDrawable) {
                    if(((BitmapDrawable) d).getBitmap()!=null) {
                        Bitmap b = ((BitmapDrawable) d).getBitmap();
                        b.recycle();
                    }
                }
            }
        }catch(Exception e) { e.printStackTrace();}
        //d.setCallback(null);
    }
    @Override
    public void onDestroy() {
        Log.d(LogTag, "onDestroy");

        //recycleBitmap(ownerImage);
        super.onDestroy();
    }
}
