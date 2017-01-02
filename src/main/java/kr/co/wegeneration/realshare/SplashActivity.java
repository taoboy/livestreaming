package kr.co.wegeneration.realshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

//import com.parse.ParseFacebookUtils;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import kr.co.wegeneration.realshare.NetController.ParseNetController;
import kr.co.wegeneration.realshare.models.Friend;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;

public class SplashActivity extends AppCompatActivity {
    private static final String LogTag = "SplashActivity";

    static Context thisContext;

    public static String object_id ="";
    public static String installation_id ="";

    String user_id = "";
    String email = "";
    String passwd = "";
    String gcm_reg_id = "";
    //String object_id = "";
    //String installation_id = "";

    static String msgType = "";
    static String senderId = "";
    static String senderNm = "";

    Boolean isFirst = true;
    private static SplashActivity mInstance = null;

    public static SplashActivity getInstance(){
        if(mInstance == null){
            mInstance = new SplashActivity();
        }
        return mInstance;
    }

    public void callBadgeCount(){
        try {
            Intent intents = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            // 패키지 네임과 클래스 네임 설정
            intents.putExtra("badge_count_package_name", getComponentName().getPackageName());
            intents.putExtra("badge_count_class_name", getComponentName().getClassName());
            // 업데이트 카운트
            intents.putExtra("badge_count", ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getInt("badge_count"));
            sendBroadcast(intents);

        }catch(Exception e){e.printStackTrace(); }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        thisContext = this;


        new CountDownTimer(1000, 1000) {

            public void onTick(long millisUntilFinished) {
                //nothing
            }

            public void onFinish() {

                patchEOFException();

                // msg를 통해 왔을 때 대비
                Intent intent = getIntent();
                if( intent != null ) {
                    msgType = intent.getStringExtra(Define.MSG_TYPE);
                    senderId = intent.getStringExtra(Define.MSG_SENDER_ID);
                    senderNm = intent.getStringExtra(Define.MSG_SENDER_NM);

                }

                isFirst = RSPreference.newInstance(thisContext).checkFirst();

                if(ParseUser.getCurrentUser()==null || ParseUser.getCurrentUser().isNew() || !ParseUser.getCurrentUser().isAuthenticated()) {
                    Log.d(LogTag, "first action");

                    /*if(ParseUser.getCurrentUser()!=null) {
                        Log.d(LogTag, String.valueOf(ParseUser.getCurrentUser().isAuthenticated()) + ":" + ParseUser.getCurrentUser().getSessionToken());
                    }*/
                    moveToLogin();
                }
                else {

//                    Log.d(LogTag, String.valueOf(ParseUser.getCurrentUser().isAuthenticated()) + ":" + ParseUser.getCurrentUser().getSessionToken());


                    try {

                        callBadgeCount();

                        final String user_status = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_STATUS);

                        ParseQuery<Friend> user = Friend.getQuery();
                        user.whereEqualTo("userId", ParseUser.getCurrentUser().getString(Define.DB_USER_ID));
                        user.whereEqualTo("status", "friend");
                        user.getFirstInBackground(new GetCallback<Friend>() {
                            @Override
                            public void done(Friend friend, ParseException e) {
                                if (e == null) {
                                    ParseNetController.moveToMain(thisContext, user_status);
                                } else {
                                    ParseNetController.moveToFindFriends(thisContext);
                                    e.printStackTrace();
                                }
                            }
                        });
                    }catch (Exception e) { e.printStackTrace();}
                }
                /*if( isFirst ) {

                    Log.d(LogTag, "first action");

                    moveToLogin();

                } else {
                    Log.d(LogTag, "after action");

                    // signin 정보 재확인 후 main으로 이동
                    SharedPreferences pref = RSPreference.getPreference(thisContext);

                    user_id = pref.getString(Define.USER_ID, "");
                    email = pref.getString(Define.USER_EMAIL, "");
                    passwd = pref.getString(Define.USER_PASSWD, "");
                    gcm_reg_id = pref.getString(Define.USER_GCM_ID, "");
                    object_id = pref.getString(Define.DB_OBJECT_ID, "");
                    installation_id = pref.getString(Define.DB_INSTALLATION_ID, "");

                    Map<String, String> param = new HashMap<String, String>();
                    param.put(Define.ACTION, Define.ACTION_SIGNIN);
                    param.put(Define.DB_EMAIL, email);
                    param.put(Define.DB_PASSWD, passwd);
                    param.put(Define.DB_GCM_ID, gcm_reg_id);
                    param.put(Define.DB_OBJECT_ID, object_id);
                    param.put(Define.DB_INSTALLATION_ID, installation_id);

                    param.put(Define.MSG_TYPE, msgType);
                    param.put(Define.MSG_SENDER_ID, senderId);
                    param.put(Define.MSG_SENDER_NM, senderNm);
                    param.put(Define.MSG_TYPE, Define.MSG_TYPE_STATUS);

                    //TODO -*ssh*- check new Library start
                    NetController.getInstance(thisContext)
                            .getRequestQueue()
                            .add(NetController.SignIn(thisContext, param));
                    // -*ssh*- check new Library end

                }*/

            }
        }.start();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void patchEOFException() {
        System.setProperty("http.keepAlive", "false");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    private static void moveToLogin() {
        Intent loginIntent = new Intent( thisContext, LoginActivity.class );
        if( msgType != null && !msgType.isEmpty() ) {
            loginIntent.putExtra(Define.MSG_TYPE, msgType);
            loginIntent.putExtra(Define.MSG_SENDER_ID, senderId);
            loginIntent.putExtra(Define.MSG_SENDER_NM, senderNm);
        }

        thisContext.startActivity(loginIntent);
        ((Activity)thisContext).finish();
    }



}
