package kr.co.wegeneration.realshare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.NetController.ParseNetController;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;

public class NotificationSettingActivity extends AppCompatActivity
//        implements ActionBar.TabListener
{
    private static final String LogTag = "NotificationSettingActivity";

    public static NotificationSettingActivity newInstance() {
        return new NotificationSettingActivity();
    }

    static Context thisContext;
    ProgressDialog mPDialog;

    static String user_id = "";
    static String user_status = "";
    static String user_flag  = "";

    static String user_nm="";

    public static String getUser_id() {
        return user_id;
    }

    public static String getUser_nm() {
        return user_nm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        SharedPreferences pref = RSPreference.getPreference(this);
        thisContext = this;
        user_id      = pref.getString(Define.USER_ID, "");
        user_nm      = pref.getString(Define.USER_NM, "");

        user_status  = pref.getString(Define.DB_STATUS, "");
        user_flag    = pref.getString(Define.USER_FLAG, "");



        Typeface typeface = Typeface.createFromAsset(getAssets(), "BRI293.TTF");
        SpannableString s = new SpannableString("Notificatoin Settings");
        s.setSpan(typeface, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_notifcation);
        setSupportActionBar(toolbar);
        setTitle(s);

        final ImageView friendstatus  = (ImageView)findViewById(R.id.friend_message_noti);
        final ImageView commentstatus = (ImageView)findViewById(R.id.comment_status_noti);
        final ImageView commentpost   = (ImageView)findViewById(R.id.comment_post_noti);


        try {

            if(ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getBoolean(Define.FriendStatus)) {
                friendstatus.setImageResource(R.drawable.alarm_icon_on);
            }else{
                friendstatus.setImageResource(R.drawable.alarm_icon_off);
            }

            if(ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getBoolean(Define.CommentStatus)) {
                commentstatus.setImageResource(R.drawable.alarm_icon_on);
            }else{
                commentstatus.setImageResource(R.drawable.alarm_icon_off);
            }

            if(ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getBoolean(Define.CommentPost)) {
                commentpost.setImageResource(R.drawable.alarm_icon_on);
            }else{
                commentpost.setImageResource(R.drawable.alarm_icon_off);
            }

            final ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded();
            friendstatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("friendstatus", "test");
                    if (ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getBoolean(Define.FriendStatus)) {
                        friendstatus.setImageResource(R.drawable.alarm_icon_off);
                        user.put(Define.FriendStatus, false);
                        user.saveInBackground();

                    } else {
                        friendstatus.setImageResource(R.drawable.alarm_icon_on);
                        user.put(Define.FriendStatus, true);
                        user.saveInBackground();
                    }
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(thisContext.getApplicationContext(), "Changed", Toast.LENGTH_SHORT).show();
                            } else e.printStackTrace();
                        }
                    });
                }
            });

            commentstatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("commentstatus", "test");
                    if (ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getBoolean(Define.CommentStatus)) {
                        commentstatus.setImageResource(R.drawable.alarm_icon_off);
                        user.put(Define.CommentStatus, false);

                    } else {
                        commentstatus.setImageResource(R.drawable.alarm_icon_on);
                        user.put(Define.CommentStatus, true);

                    }
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(thisContext.getApplicationContext(), "Changed", Toast.LENGTH_SHORT).show();
                            } else e.printStackTrace();
                        }
                    });
                }
            });

            commentpost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("commentpost", "test");
                    if (ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getBoolean(Define.CommentPost)) {
                        commentpost.setImageResource(R.drawable.alarm_icon_off);
                        user.put(Define.CommentPost, false);

                    } else {
                        commentpost.setImageResource(R.drawable.alarm_icon_on);
                        user.put(Define.CommentPost, true);

                    }
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(thisContext.getApplicationContext(), "Changed", Toast.LENGTH_SHORT).show();
                            } else e.printStackTrace();
                        }
                    });
                }
            });
        }catch(Exception e ){e.printStackTrace();}
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        MenuItem item_version = menu.findItem(R.id.action_version_noti);

        item_version.setTitle("v"+ "("+ String.valueOf(Build.VERSION.SDK_INT)  +")"+getString(R.string.appVersion)+"("+ Build.MODEL+")");
        //SearchManager searchManager= (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView         searchView= (SearchView) menu.findItem(R.id.menu_search).getActionView();

        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false);

        return true;
    }

    private void goBackToMain(){
        NetController.moveToMain(thisContext, "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if( id == R.id.action_logout_noti){
            NetController.getInstance(thisContext)
                    .getRequestQueue()
                    .add(NetController.SignOut(thisContext, user_id));
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {

            goBackToMain();
            return true;
        }
//        else if(id == R.id.action_profile) {
//        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPause() {
        super.onPause();
/*
        NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.ActiveOnOff(thisContext, user_id, "n"));*/
        ParseNetController.ActiveOnOff(thisContext, "n");
        if ( mPDialog != null ) {
            mPDialog.dismiss();
            mPDialog = null;
        }
    }
    @Override
      public void onBackPressed() {
            goBackToMain();
    }

    @Override
    public void onResume() {
        super.onResume();
/*
        NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.ActiveOnOff(thisContext, user_id, "y"));*/
        ParseNetController.ActiveOnOff(thisContext, "y");
        if ( mPDialog != null ) {
            mPDialog.dismiss();
            mPDialog = null;
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }


}
