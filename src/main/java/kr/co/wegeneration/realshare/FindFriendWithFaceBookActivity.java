package kr.co.wegeneration.realshare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.NetController.ParseNetController;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;
import kr.co.wegeneration.realshare.util.AccountAdapter;

public class FindFriendWithFaceBookActivity extends AppCompatActivity
//        implements ActionBar.TabListener
{
    private static final String LogTag = "AddFriendActivity";
    private AccessToken accessToken;

    public static FindFriendWithFaceBookActivity newInstance() {
        return new FindFriendWithFaceBookActivity();
    }

    static Context thisContext;
    ProgressDialog mPDialog;

    static String user_id = "";
    static String user_status = "";
    static String user_flag  = "";

    static String msgType = "";
    static String senderId = "";
    static String senderNm = "";
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
    com.facebook.login.widget.LoginButton fb_button;

    EditText searchView;
    CallbackManager callbackManager;

    public static String getUser_id() {
        return user_id;
    }

    public static String getUser_nm() {
        return user_nm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        SharedPreferences pref = RSPreference.getPreference(this);
        thisContext = this;
        user_id = pref.getString(Define.USER_ID, "");
        user_nm = pref.getString(Define.USER_NM, "");

        user_status = pref.getString(Define.DB_STATUS, "");
        user_flag = pref.getString(Define.USER_FLAG, "");

        Typeface typeface = Typeface.createFromAsset(getAssets(), "BRI293.TTF");
        SpannableString s = new SpannableString("Find friend");
        s.setSpan(typeface, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(s);

        final ArrayList<String> facebook_friend = new ArrayList<>();

        callbackManager = CallbackManager.Factory.create();
        fb_button = (com.facebook.login.widget.LoginButton)findViewById(R.id.connectBtn);
        fb_button.setReadPermissions(Arrays.asList("user_friends"));
        fb_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                GraphRequestBatch batch = new GraphRequestBatch(

                        GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject jsonObject,
                                            GraphResponse response) {
                                        // Application code for users friends
                                        try {
                                            ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded();
                                            user.put("facebookId", jsonObject.get("id"));
                                            user.saveInBackground();

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })

                );
                batch.addCallback(new GraphRequestBatch.Callback() {
                    @Override
                    public void onBatchCompleted(GraphRequestBatch graphRequests) {
                        // Application code for when the batch finishes
                    }
                });
                batch.executeAsync();


                GraphRequestBatch batch_friendRequest = new GraphRequestBatch(

                        GraphRequest.newMyFriendsRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONArrayCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONArray jsonArray,
                                            GraphResponse response) {
                                        // Application code for users friends


                                        try {

                                            System.out.println("getFriendsData onCompleted : jsonArray " + jsonArray);
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                facebook_friend.add(jsonArray.getJSONObject(i).getString("id"));
                                                Log.d("facebookid", jsonArray.getJSONObject(i).toString());
                                                NetController.moveToFriends(thisContext, facebook_friend);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })

                );
                batch_friendRequest.addCallback(new GraphRequestBatch.Callback() {
                    @Override
                    public void onBatchCompleted(GraphRequestBatch graphRequests) {
                        // Application code for when the batch finishes
                    }
                });
                batch_friendRequest.executeAsync();


            }

            @Override
            public void onCancel() {
                // App code

            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                exception.printStackTrace();
            }
        });




    }



    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_findfriend, menu);
        MenuItem item_version = menu.findItem(R.id.action_version);

        item_version.setTitle("v"+ "("+ String.valueOf(Build.VERSION.SDK_INT)  +")"+getString(R.string.appVersion)+"("+ Build.MODEL+")");

        return true;
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

            NetController.moveToAddFriends(thisContext, "");
            return true;
        }
//        else if(id == R.id.action_profile) {
//        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPause() {
        super.onPause();

        ParseNetController.ActiveOnOff(thisContext, "n");
        /*NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.ActiveOnOff(thisContext, user_id, "n"));*/

        if ( mPDialog != null ) {
            mPDialog.dismiss();
            mPDialog = null;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        ParseNetController.ActiveOnOff(thisContext, "y");
        /*NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.ActiveOnOff(thisContext, user_id, "y"));

        if ( mPDialog != null ) {
            mPDialog.dismiss();
            mPDialog = null;
        }*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        Log.d(LogTag, "activity resume ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public AccessToken getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }
}
