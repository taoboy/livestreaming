package kr.co.wegeneration.realshare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.facebook.AccessToken;
import com.parse.ParseQuery;
//import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.NetController.ParseNetController;
import kr.co.wegeneration.realshare.adapter.FriendlistParseAdapter;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;
import kr.co.wegeneration.realshare.models.Friend;
import kr.co.wegeneration.realshare.models.UserInfo;
import kr.co.wegeneration.realshare.util.AccountAdapter;

public class AddFriendWithFaceBookActivity extends AppCompatActivity
//        implements ActionBar.TabListener
{
    private static final String LogTag = "AddFriendActivity";
    private SwipeRefreshLayout swipeRefreshLayout;

    public static AddFriendWithFaceBookActivity newInstance() {
        return new AddFriendWithFaceBookActivity();
    }

    FriendlistParseAdapter friendlistParseAdapter;

    static Context thisContext;
    ProgressDialog mPDialog;

    static String user_id = "";
    static String user_status = "";
    static String user_flag  = "";

    static String msgType = "";
    static String senderId = "";
    static String senderNm = "";
    //static AccessToken access_token;
    static TextView txtTotalFriends;

    static Spinner spinPullMode;

    Button btnShare, btnPull;
    static ListView lstFriend;
    static AccountAdapter friendAdapter;
    static Handler refreshFriendHandler;

    static String user_nm="";
    String email = "";
    String passwd = "";
    String gcm_reg_id = "";
    String object_id = "";

    static String text="";

    SearchView search;
    LinearLayout profileHolder;
    TextView nameTextView, profileTextView;
    DialogInterface profileChangePopup;

    EditText searchView;

    public static void refreshFrendList() {
        callFriendList();
    }

    public static String getUser_id() {
        return user_id;
    }

    public static String getUser_nm() {
        return user_nm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends_facebook);


        Intent intent = getIntent();
        final ArrayList<String> facebook_friend = intent.getStringArrayListExtra(Define.FACEBOOK_FRIEND_LIST);
        SharedPreferences pref = RSPreference.getPreference(this);
        thisContext = this;

        Typeface typeface = Typeface.createFromAsset(getAssets(), "BRI293.TTF");
        SpannableString s = new SpannableString("Friends");
        s.setSpan(typeface, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(s);

        //sp.setSpan(new ForegroundColorSpan(Color.RED), 14, 21, Spannable.SPAN_POINT_MARK);
        //sp.setSpan(new UnderlineSpan(), 14, 21, Spannable.SPAN_POINT_MARK);

       // friendAddAdapter =  new FriendAddAdapter(thisContext, R.layout.row_add_friend, new ArrayList<Friend>(), false);



        ParseQueryAdapter.QueryFactory<UserInfo> factory_user = new ParseQueryAdapter.QueryFactory<UserInfo>() {
            public ParseQuery<UserInfo> create() {

                ParseQuery<UserInfo> query = UserInfo.getQuery();
                try {
                    query.orderByDescending("createdAt");
                    query.whereNotEqualTo(Define.DB_USER_NM, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_USER_NM));
                    query.whereContainedIn("facebookId", facebook_friend);
                    ParseQuery<Friend> subquery = Friend.getQuery();
                    subquery.whereEqualTo(Define.DB_FRIEND_ID, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_USER_ID));

                    query.whereDoesNotMatchKeyInQuery(Define.DB_USER_ID, Define.DB_USER_ID, subquery);

                //query.fromLocalDatastore();

                }catch(Exception e) {e.printStackTrace();}
                return query;
            }
        };

        //friendlistParseAdapter = new FriendlistParseAdapter(this);
        friendlistParseAdapter = new FriendlistParseAdapter(this, factory_user);

        lstFriend = (ListView) findViewById(R.id.lstAddFacebookFriend);
        lstFriend.setAdapter(friendlistParseAdapter);
        friendlistParseAdapter.loadObjects();



        btnPull = (Button)findViewById(R.id.btnAddFriends);
        //lstFriend = (ListView)findViewById(R.id.lstAddFacebookFriend);
//        txtTotalFriends = (TextView)findViewById(R.id.txtTotalFriends);
//        txtTotalFriends.setVisibility(View.GONE);

        btnPull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> param = new HashMap<>();
                param.put(Define.ACTION, Define.ACTION_PULL);
                param.put(Define.MSG_TYPE, Define.MSG_TYPE_ADDFRIEND);
                param.put(Define.MSG_SENDER_ID, user_id);
                param.put(Define.MSG_SENDER_NM, user_nm);
                param.put(Define.PARAM_RECEIVER_LIST, friendlistParseAdapter.getAllIdStr());

                try {

                    JSONArray array            = new JSONArray(friendlistParseAdapter.getAllIdStr());
                    JSONArray array_name       = new JSONArray(friendlistParseAdapter.getAllNameStr());
                    JSONArray array_full_name  = new JSONArray(friendlistParseAdapter.getAllFullNameStr());
                    JSONArray array_email      = new JSONArray(friendlistParseAdapter.getAllEmailStr());

                    if(array.length()==0) {
                        Toast.makeText(thisContext,
                                "No User!",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    for(int i = 0 ; i < array.length() ; i ++) {
                        ParseNetController.AddFriend(thisContext, param, array.get(i).toString(), array_name.get(i).toString(), array_full_name.get(i).toString(), array_email.get(i).toString());
                    }

                }catch(Exception e){e.printStackTrace();}

                btnPull.setClickable(false);

                Handler clickhandler = new Handler();
                clickhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnPull.setClickable(true);
                    }
                }, 9000);

                lstFriend.setAdapter(friendlistParseAdapter);
                friendlistParseAdapter.loadObjects();
            }
        });

        try {

            Handler clickhandler = new Handler();
            clickhandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    txtTotalFriends = (TextView) findViewById(R.id.total_friend);
                    final SpannableStringBuilder sp1 = new SpannableStringBuilder(String.valueOf(friendlistParseAdapter.getCount() + " "));
                    final SpannableStringBuilder sp2 = new SpannableStringBuilder(getString(R.string.statusFriendMsg1));
                    sp1.append(sp2);
                    txtTotalFriends.setText(sp1);
                }
            }, 3000);

        }catch(Exception e) {e.printStackTrace();}

        swipeRefreshLayout  = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        friendlistParseAdapter.notifyDataSetChanged();
                        friendlistParseAdapter.loadObjects();

                        Handler clickhandler = new Handler();
                        clickhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                txtTotalFriends = (TextView) findViewById(R.id.total_friend);
                                final SpannableStringBuilder sp1 = new SpannableStringBuilder(String.valueOf(friendlistParseAdapter.getCount() + " "));
                                final SpannableStringBuilder sp2 = new SpannableStringBuilder(getString(R.string.statusFriendMsg1));
                                sp1.append(sp2);
                                txtTotalFriends.setText(sp1);
                            }
                        }, 2000);


                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        /*refreshFriendHandler = new Handler() {
            public void handleMessage(Message msg) {
                callFriendList(text);
                friendAddAdapter.notifyDataSetChanged();
                refreshFriendHandler.sendEmptyMessageDelayed(5, 4000);
            }
        };

        callFriendList();*/


        //lstFriend.setAdapter(friendAddAdapter);


        //refreshFriendHandler.sendEmptyMessage(5);

    }

    public static void callFriendList() {
        Map<String, String> param = new HashMap<String, String>();
        param.put(Define.ACTION, Define.ACTION_ADD_FACEBOOK_FRIEND);
        param.put(Define.DB_USER_ID, user_id);
        param.put(Define.USER_FLAG, user_flag);
//            param.put(Define.DB_EMAIL, email);
        Log.i("AddFriendWithFaceBook", "friendList calling");
        NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.FriendList(thisContext, user_id, Define.ACTION_ADD_FRIEND, param));
    }

    public static void callAddFriendListCnt() {
        SharedPreferences pref = RSPreference.getPreference(thisContext);
        user_id      = pref.getString(Define.USER_ID, "");

        Map<String, String> param = new HashMap<String, String>();
        param.put(Define.ACTION, Define.ACTION_ADD_FACE_BOOK_FRIEND_COUNT);
        param.put(Define.DB_USER_ID, user_id);
//            param.put(Define.DB_EMAIL, email);
        Log.i("rstest", "callAddFriendListCnt calling : " + user_id);
        NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.FriendList(thisContext, user_id, Define.ACTION_ADD_FRIEND_COUNT, param));
    }


    public static void callFriendList(String keyword) {
        Map<String, String> param = new HashMap<String, String>();
        param.put(Define.ACTION, Define.ACTION_ADD_FACEBOOK_FRIEND);
        param.put(Define.DB_USER_ID, user_id);
        param.put(Define.USER_FLAG, user_flag);
        param.put(Define.SEARCH_WORD, keyword);
//            param.put(Define.DB_EMAIL, email);
        Log.i("rstest", "friendList calling");
        NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.FriendList(thisContext, user_id, Define.ACTION_ADD_FRIEND, param));
    }

/*    public static void UserListEvent(JSONObject resJSON , String text) {

        Log.d(LogTag, " : UserListEvent ");

        friendAddAdapter.clear();

        if( resJSON == null ) return;
        if( !resJSON.has("userList") ) return;

        try {
            JSONArray users = resJSON.getJSONArray("userList");
            if( users == null || users.length() == 0 ) return;

            int count=0;
            for(int i = 0; i < users.length(); i++){
                JSONObject temp = users.getJSONObject(i);
                if((text!="" && text!=null &&
                        (temp.getString("user_email").toLowerCase(Locale.getDefault()).contains(text)
                        || temp.getString("user_name").toLowerCase(Locale.getDefault()).contains(text))) ||(text=="" || text==null)) {

                      Friend friend = new Friend(
                            temp.getInt("user_id"),
                            temp.getString("user_email"),
                            temp.getString("user_name"),
                            temp.getString("user_status"),
                            temp.getString("user_active"),
                            temp.getString("user_mode"),
                            temp.getString("room_name"),
                              temp.getString("friend_status"),
                              temp.getString("first_name"),
                              temp.getString("last_name")
                      );
                    friendAddAdapter.add(friend);
                    count++;
                }
            }

//            txtTotalFriends.setText(Define.TOTAL_LIST_MSG.concat(String.valueOf(count) + "명"));



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    public static void UserListEventCnt(JSONObject resJSON ) {

        Log.d(LogTag, " : UserListEventCnt ");

        //friendAddAdapter.clear();


        if( resJSON == null ) return;
        if( !resJSON.has("userList") ) return;

        try {
            JSONArray users = resJSON.getJSONArray("userList");
            if( users == null || users.length() == 0 ) return;

            if(users.length()==0)
            {
                MainActivity.tv.setText("");
                MainActivity.tv_img.setVisibility(View.GONE);
            }
            else {
                MainActivity.tv.setText(String.valueOf(users.length()));
                MainActivity.tv_img.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        MenuItem item_version = menu.findItem(R.id.action_version);

        item_version.setTitle("v"+ "("+ String.valueOf(Build.VERSION.SDK_INT)  +")"+getString(R.string.appVersion)+"("+ Build.MODEL+")");

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

        if( id == R.id.action_next){

            ParseNetController.SignOut(thisContext);
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

            goBackToMain();
            return true;
        }
        //else if(id == R.id.action_version) {
        //}
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPause() {
        super.onPause();

/*        NetController.getInstance(thisContext)
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

        ParseNetController.ActiveOnOff(thisContext, "y");
        /*NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.ActiveOnOff(thisContext, user_id, "y"));*/

        if ( mPDialog != null ) {
            mPDialog.dismiss();
            mPDialog = null;
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        Log.d(LogTag, "activity resume ");
    }


}
