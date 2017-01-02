package kr.co.wegeneration.realshare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.wegeneration.realshare.NetController.NetController;
//import kr.co.wegeneration.realshare.adapter.FriendAddAdapter;
import kr.co.wegeneration.realshare.NetController.ParseNetController;
import kr.co.wegeneration.realshare.adapter.FriendAddParseAdapter;
import kr.co.wegeneration.realshare.adapter.FriendlistParseAdapter;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;
import kr.co.wegeneration.realshare.models.Friend;
import kr.co.wegeneration.realshare.util.AccountAdapter;

import java.util.Locale;

import android.view.WindowManager;

import com.parse.FindCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
//import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class AddFriendActivity extends AppCompatActivity
//        implements ActionBar.TabListener
{
    private static final String LogTag = "AddFriendActivity";

    public static AddFriendActivity newInstance() {
        return new AddFriendActivity();
    }

    static Context thisContext;
    ProgressDialog mPDialog;

    static String user_id = "";
    static String user_status = "";
    static String user_flag  = "";

    static String msgType = "";
    static String senderId = "";
    static String senderNm = "";
    //static TextView txtTotalFriends;

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
    String[] resultsAsString = {""};
    ArrayList<ParseObject> dataset;
    //static FriendAddAdapter friendAddAdapter;
    FriendlistParseAdapter friendlistParseAdapter;
    FriendAddParseAdapter friendAddParseAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    SearchView search;
    LinearLayout profileHolder;
    TextView nameTextView, profileTextView;
    DialogInterface profileChangePopup;
    ArrayAdapter<String> adapter;
    EditText searchView;

    public static void refreshFrendList() {
        callFriendList();
    }

    public static String getUser_id() {
        return user_id;
    }

    public static String getTextSearch() {
        return text;
    }

    public static void  setTextSearch(String texts) {  text=texts;}

    public static String getUser_nm() {
        return user_nm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        SharedPreferences pref = RSPreference.getPreference(this);
        thisContext = this;
        user_id      = pref.getString(Define.USER_ID, "");
        user_nm      = pref.getString(Define.USER_NM, "");

        user_status  = pref.getString(Define.DB_STATUS, "");
        user_flag    = pref.getString(Define.USER_FLAG, "");

        Typeface typeface = Typeface.createFromAsset(getAssets(), "BRI293.TTF");
        SpannableString s = new SpannableString("Add friend");
        s.setSpan(typeface, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(s);

        //dataset = new ArrayList<ParseObject>();
        //friendAddAdapter =  new FriendAddAdapter(thisContext, R.layout.row_add_friend, dataset, false);
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataset);
        /*ParseDAO<ParseObject> query = ParseDAO.getQuery("Friend");

        query.whereEqualTo("user_from","122");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> resultsList, ParseException e) {
                if (e == null) {
                    for (int index = 0; index < resultsList.size(); index++) {
                        //.get => .getString
                        dataset.add(resultsList.get(index));
                        Log.d("test", dataset.get(index));
                    }
                    adapter.notifyDataSetChanged();

                } else {
                    Log.w("Parse query", e.getMessage());
                }
            }
        });*/

        //lstFriend = (ListView) findViewById(R.id.lstAddFriend);
        //lstFriend.setAdapter(adapter);

        profileHolder = (LinearLayout)findViewById(R.id.inviteEveryoneHolder);
        // Set up the Parse query to use in the adapter
        ParseQueryAdapter.QueryFactory<ParseUser> factory_user = new ParseQueryAdapter.QueryFactory<ParseUser>() {
            public ParseQuery<ParseUser> create() {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.orderByDescending("createdAt");
                query.fromLocalDatastore();
                return query;
            }
        };

        ParseQueryAdapter.QueryFactory<Friend> factory_friendship = new ParseQueryAdapter.QueryFactory<Friend>() {
            public ParseQuery<Friend> create() {
                ParseQuery<Friend> query = Friend.getQuery();
                query.orderByDescending("createdAt");
                //query.whereEqualTo("username", text);
                query.fromLocalDatastore();
                return query;
            }
        };


        friendlistParseAdapter = new FriendlistParseAdapter(this);
        friendAddParseAdapter  = new FriendAddParseAdapter(this);

        lstFriend = (ListView) findViewById(R.id.listFriend);
        lstFriend.setAdapter(friendAddParseAdapter);
        //friendAddParseAdapter.loadObjects();

        swipeRefreshLayout  = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        friendAddParseAdapter.notifyDataSetChanged();
                        friendAddParseAdapter.loadObjects();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        /*refreshFriendHandler = new Handler() {
            public void handleMessage(Message msg) {
    //            callFriendList(text);
                //friendAddAdapter.notifyDataSetChanged();
                friendlistParseAdapter.loadObjects();
                friendAddParseAdapter.loadObjects();
                friendlistParseAdapter.notifyDataSetChanged();
                friendAddParseAdapter.notifyDataSetChanged();
                refreshFriendHandler.sendEmptyMessageDelayed(5, 5000);
            }
        };
        refreshFriendHandler.sendEmptyMessage(5);*/

  //      callFriendList();
 //       lstFriend.setAdapter(friendAddAdapter);

        profileHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseNetController.moveToFindFriends(thisContext);
            }
        });
        // Locate the EditText in listview_main.xml
        searchView = (EditText) findViewById(R.id.searchView);

        // Capture Text in EditText
        searchView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                text = searchView.getText().toString().toLowerCase(Locale.getDefault());
                //callFriendList(text);
                if (!text.equals("")) {

                    //syncTodosToParse();
                    //friendlistParseAdapter.notifyDataSetChanged();
                    lstFriend.setAdapter(friendlistParseAdapter);
                    friendlistParseAdapter.loadObjects();


                } else {
                    //syncTodosToParse();
                    //friendAddParseAdapter.notifyDataSetChanged();
                    lstFriend.setAdapter(friendAddParseAdapter);
                    friendAddParseAdapter.loadObjects();

                }

            }

            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });



    }


    private  void syncTodosToParse() {
        // We could use saveEventually here, but we want to have some UI
        // around whether or not the draft has been saved to Parse
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni != null) && (ni.isConnected())) {
            if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
                // If we have a network connection and a current logged in user,
                // sync the
                // todos
                ParseQuery<Friend> query = Friend.getQuery();
                query.fromPin("ALL");
                query.whereEqualTo("isDraft", true);
                query.findInBackground(new FindCallback<Friend>() {
                    public void done(List<Friend> todos, ParseException e) {
                        if (e == null) {
                            for (final Friend todo : todos) {
                                // Set is draft flag to false before
                                // syncing to Parse
                                todo.setDraft(false);
                                todo.saveInBackground(new SaveCallback() {

                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            // Let adapter know to update view
                                            if (!isFinishing()) {
                                                friendlistParseAdapter
                                                        .notifyDataSetChanged();
                                            }
                                        } else {
                                            // Reset the is draft flag locally
                                            // to true
                                            todo.setDraft(true);
                                        }
                                    }

                                });

                            }
                        } else {
                            Log.i("TodoListActivity",
                                    "syncTodosToParse: Error finding pinned todos: "
                                            + e.getMessage());
                        }
                    }
                });
            } else {
                // If we have a network connection but no logged in user, direct
                // the person to log in or sign up.
                NetController.moveToLogin(thisContext);
            }
        } else {
            // If there is no connection, let the user know the sync didn't
            // happen
            loadFromParse();

            Toast.makeText(
                    getApplicationContext(),
                    "Your device appears to be offline. Some todos may not have been synced to Parse.",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void loadFromParse() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        //query.whereEqualTo("author", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> todos, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground((List<ParseUser>) todos,
                            new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        if (!isFinishing()) {
                                            friendlistParseAdapter.loadObjects();
                                        }
                                    } else {
                                        Log.i("TodoListActivity",
                                                "Error pinning todos: "
                                                        + e.getMessage());
                                    }
                                }
                            });
                } else {
                    Log.i("TodoListActivity",
                            "loadFromParse: Error finding pinned todos: "
                                    + e.getMessage());
                }
            }
        });
    }

    public static void callFriendList() {
        Map<String, String> param = new HashMap<String, String>();
        param.put(Define.ACTION, Define.ACTION_ADD_FRIEND);
        param.put(Define.DB_USER_ID, user_id);
        param.put(Define.USER_FLAG, user_flag);
//            param.put(Define.DB_EMAIL, email);
        Log.i("rstest", "friendList calling");
        NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.FriendList(thisContext, user_id, Define.ACTION_ADD_FRIEND, param));
    }

    public static void callAddFriendListCnt() {
        SharedPreferences pref = RSPreference.getPreference(thisContext);
        user_id      = pref.getString(Define.USER_ID, "");

        Map<String, String> param = new HashMap<String, String>();
        param.put(Define.ACTION, Define.ACTION_ADD_FRIEND_COUNT);
        param.put(Define.DB_USER_ID, user_id);
//            param.put(Define.DB_EMAIL, email);
        Log.i("rstest", "callAddFriendListCnt calling : " + user_id);
        NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.FriendList(thisContext, user_id, Define.ACTION_ADD_FRIEND_COUNT, param));
    }


    public static void callFriendList(String keyword) {
        Map<String, String> param = new HashMap<String, String>();
        param.put(Define.ACTION, Define.ACTION_ADD_FRIEND);
        param.put(Define.DB_USER_ID, user_id);
        param.put(Define.USER_FLAG, user_flag);
        param.put(Define.SEARCH_WORD, keyword);
//            param.put(Define.DB_EMAIL, email);
        Log.i("rstest", "friendList calling");
        NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.FriendList(thisContext, user_id, Define.ACTION_ADD_FRIEND, param));
    }
/*
    public static void UserListEvent(JSONObject resJSON , String text) {

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

            //txtTotalFriends.setText(Define.TOTAL_LIST_MSG.concat(String.valueOf(count) + "명"));



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
*/
    public static void UserListEventCnt(JSONObject resJSON) {

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
        getMenuInflater().inflate(R.menu.menu_addfriend, menu);
        MenuItem item_version = menu.findItem(R.id.action_version);

        item_version.setTitle("v" + "(" + String.valueOf(Build.VERSION.SDK_INT) + ")"+getString(R.string.appVersion)+"("+ Build.MODEL+")");
        //SearchManager searchManager= (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView         searchView= (SearchView) menu.findItem(R.id.menu_search).getActionView();

        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false);

        return true;
    }

    private void goBackToMain(){
        // signin 정보 재확인 후 main으로 이동

        NetController.moveToMain(thisContext, "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if( id == R.id.action_next){
            /*NetController.getInstance(thisContext)
                    .getRequestQueue()
                    .add(NetController.SignOut(thisContext, user_id));*/


            SharedPreferences pref = RSPreference.getPreference(thisContext);
            SharedPreferences.Editor editor = pref.edit();
            editor.remove(Define.USER_ID);
            editor.remove(Define.USER_EMAIL);
            editor.remove(Define.USER_PASSWD);
            editor.remove(Define.USER_GCM_ID);

            editor.apply();

            Toast.makeText(thisContext.getApplicationContext(), "Sign-Out Success", Toast.LENGTH_SHORT).show();

            ParseUser.logOut();

            Intent loginIntent = new Intent(thisContext.getApplicationContext(), LoginActivity.class);
            thisContext.startActivity(loginIntent);

            ((Activity)thisContext).finish();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

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

        /*NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.ActiveOnOff(thisContext, user_id, "n"));*/

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

        /*NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.ActiveOnOff(thisContext, user_id, "y"));
*/
        if ( mPDialog != null ) {
            mPDialog.dismiss();
            mPDialog = null;
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            // Sync data to Parse
            syncTodosToParse();
        }

        Log.d(LogTag, "activity resume ");
    }


}
