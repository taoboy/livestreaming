package kr.co.wegeneration.realshare.NetController;
import com.android.volley.*;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.facebook.AccessToken;
import com.firebase.client.Firebase;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import kr.co.wegeneration.realshare.AddFriendActivity;
import kr.co.wegeneration.realshare.AddFriendWithFaceBookActivity;
import kr.co.wegeneration.realshare.BroadcastActivity;
import kr.co.wegeneration.realshare.FindFriendWithFaceBookActivity;
import kr.co.wegeneration.realshare.LoginActivity;
import kr.co.wegeneration.realshare.MainActivity;
import kr.co.wegeneration.realshare.FriendFragment;
import kr.co.wegeneration.realshare.ActivityFragment;
import kr.co.wegeneration.realshare.NotificationSettingActivity;
import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.TimeLineFragment;
import kr.co.wegeneration.realshare.WatchActivity;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;
import kr.co.wegeneration.realshare.util.LruBitmapCache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ssh on 15. 8. 25..
 */

// -*ssh*- Check this class is working or not
public class NetController {

    private static final String LogTag = "NetController";

    private static final String RESULT_CODE     = "resultCode";
    private static final String ROOM_NAME       = "room_name";
    private static final String RESULT_SUCCESS = "success";
    private static final String RESULT_MESSAGE = "message";

    static Map<String, String> urls = new HashMap<>();

    // url 목록
    private static final String SERVER_URL = "http://1.234.83.232:3333/";
    private static final String URL_SIGNUP = "signupProc"; // GET
    private static final String URL_SIGNIN = "signinProc"; // GET
    private static final String URL_SIGNOUT = "signoutProc"; // GET

    private static final String URL_ADD_FRIEND        = "addfriendProc"; // POST
    private static final String URL_CONFIRM_FRIEND   = "confirmfriendProc"; // GET
    private static final String URL_COMMENT_LIST     = "commentList"; // GET

//    private static final String URL_USERS = "userList";

    private static final String URL_REGISTRATION = "registerProc"; // GET
    private static final String URL_RECORD_STREAM = "recordStreamProc"; // GET
    private static final String URL_STREAM_LIST = "recordUrlList"; // GET
    private static final String URL_FRIEDNDS = "friendList"; // GET
    private static final String URL_ACTIVITY    = "activityList"; // GET
    private static final String URL_ACTIVITY_READ = "activityProc"; // GET
    private static final String URL_ROOM = "roomProc"; // POST
    private static final String URL_PUSH = "pushProc"; // POST
    private static final String URL_STATUS = "statusProc"; // GET
    private static final String URL_COMMENT = "commentProc"; // GET
    private static final String URL_ACTIVE = "activeProc"; // GET
    private static final String URL_SHARE_LINK = "sharelinkProc"; // POST
    private static final String URL_SCREEN_ON = "screenOn"; // POST
    private static final String URL_BEFORE_FILE_UPLOAD = "file"; //POST


    static {
        urls.put(Define.ACTION_SIGNUP, URL_SIGNUP);
        urls.put(Define.ACTION_SIGNIN, URL_SIGNIN);
        urls.put(Define.ACTION_SIGNOUT, URL_SIGNOUT);
        urls.put(Define.ACTION_FRIEDNDS, URL_FRIEDNDS);
        urls.put(Define.ACTION_REGISTRATION, URL_REGISTRATION);
        urls.put(Define.ACTION_ROOM_PUBLIC, URL_ROOM);
        urls.put(Define.ACTION_ROOM_PRIVATE, URL_ROOM);
        urls.put(Define.ACTION_ROOM_WATCH, URL_ROOM);
        urls.put(Define.ACTION_ROOM_LEAVE, URL_ROOM);
        urls.put(Define.ACTION_ROOM_CLOSE, URL_ROOM);
        urls.put(Define.ACTION_RECORD_STREAM, URL_RECORD_STREAM);
        urls.put(Define.ACTION_ADD_FRIEND, URL_ADD_FRIEND);
        urls.put(Define.ACTION_CONFIRM_FRIEND, URL_CONFIRM_FRIEND);
        urls.put(Define.ACTION_PULL, URL_PUSH);
        urls.put(Define.ACTION_KNOCK, URL_PUSH);
        urls.put(Define.ACTION_COMMENT, URL_COMMENT);
        urls.put(Define.ACTION_COMMENT_LIST, URL_COMMENT_LIST);
        urls.put(Define.ACTION_ACTIVITY, URL_ACTIVITY);
        urls.put(Define.ACTION_STATUS, URL_STATUS);
        urls.put(Define.ACTION_APP_OUT, URL_ACTIVE);
        urls.put(Define.ACTION_COMMENT, URL_COMMENT);
        urls.put(Define.ACTION_COMMENT, URL_ACTIVITY_READ);
        urls.put(Define.ACTION_SCREEN_ON, URL_SCREEN_ON);

    }

//    private static ProgressDialog taskPDialog;
//    private static Handler mHandler;

    private static NetController mInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    LruBitmapCache mLruBitmapCache;

    private NetController(Context context){
        mRequestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(this.mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache mCache = new LruCache(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return (Bitmap)mCache.get(url);
            }
        });
    }

    public static NetController getInstance(Context context){
        if(mInstance == null){
            mInstance = new NetController(context);
        }
        return mInstance;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? LogTag : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(LogTag);
        getRequestQueue().add(req);
    }


    public RequestQueue getRequestQueue() {

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }


    public static StringRequest ReigisterToBackEnd(final Context context, Map<String, String> param){
        //TODO -*ssh*- check new Library start

        final String user_id      = param.get(Define.DB_USER_ID);
        final String device_reg_id = param.get(Define.DB_GCM_ID);
        final String object_id = param.get(Define.DB_OBJECT_ID);
        final String app_ver   = param.get(Define.GCM_APP_VERSION);


        String signup_url = SERVER_URL + URL_REGISTRATION;
        signup_url = signup_url.concat("&device_reg_id=");
        signup_url = signup_url.concat(device_reg_id);
        signup_url = signup_url.concat("&object_id=");
        signup_url = signup_url.concat(object_id);
        signup_url = signup_url.concat("&appVersion=");
        signup_url = signup_url.concat(app_ver);
        signup_url = signup_url.concat("&user_id=");
        signup_url = signup_url.concat(user_id);


        Log.d(URL_REGISTRATION, signup_url);

        return new StringRequest(Request.Method.GET, signup_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    Log.d(LogTag, response);

                    JSONObject resJSON = new JSONObject(response);
                    if( !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                        if(resJSON.has(RESULT_MESSAGE)){
                            Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context.getApplicationContext(), "Registration Error!", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }


                    SharedPreferences pref = RSPreference.getPreference(context);

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(Define.USER_GCM_ID, device_reg_id);
                    editor.putString(Define.DB_OBJECT_ID, object_id);
                    editor.apply();
                    //Toast.makeText(context.getApplicationContext(), "Sign-Up Success, Please Sign-In", Toast.LENGTH_SHORT).show();
                    //((Activity)context).finish();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
    }





    public static StringRequest SignUp(final Context context, Map<String, String> param){
        //TODO -*ssh*- check new Library start


        final String email = param.get(Define.DB_EMAIL);
        final String passwd = param.get(Define.DB_PASSWD);

        final String msgType = param.get(Define.MSG_TYPE);
        final String device_reg_id = param.get(Define.DB_GCM_ID);
        final String object_id = param.get(Define.DB_OBJECT_ID);
        final String senderId = param.get(Define.MSG_SENDER_ID);
        final String senderNm = param.get(Define.MSG_SENDER_NM);
        final String firstName = param.get(Define.DB_USER_FIRST_NM);
        final String lastName = param.get(Define.DB_USER_LAST_NM);
        final String user_name = firstName+":"+lastName;

        String signup_url = SERVER_URL + URL_SIGNUP;

        signup_url = signup_url.concat("?user_email=");
        signup_url = signup_url.concat(param.get(Define.DB_EMAIL));
        signup_url = signup_url.concat("&user_name=");
        signup_url = signup_url.concat(user_name);
        signup_url = signup_url.concat("&user_pw=");
        signup_url = signup_url.concat(param.get(Define.DB_PASSWD));
        signup_url = signup_url.concat("&device_type=Android");
        signup_url = signup_url.concat("&device_reg_id=");
        signup_url = signup_url.concat(device_reg_id);
        signup_url = signup_url.concat("&object_id=");
        signup_url = signup_url.concat(object_id);
        signup_url = signup_url.concat("&firstName=");
        signup_url = signup_url.concat(firstName);
        signup_url = signup_url.concat("&lastName=");
        signup_url = signup_url.concat(lastName);



        return new StringRequest(Request.Method.GET, signup_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    Log.d(LogTag, response);

                    JSONObject resJSON = new JSONObject(response);
                    if( !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                        if(resJSON.has(RESULT_MESSAGE)){
                            Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }


                    String user_id = resJSON.getString(Define.DB_USER_ID);
                    String user_nm = resJSON.getString(Define.DB_USER_NM);
                    String user_status = resJSON.getString(Define.DB_STATUS);
                    String server_url     = resJSON.getString(Define.SERVER_URL);


                    SharedPreferences pref = RSPreference.getPreference(context);

                    SharedPreferences.Editor editor = pref.edit();

                    editor.putString(Define.CHAT_SERVER_URL , server_url);
                    editor.putString(Define.LOGIN_URL       , server_url+"login");
                    editor.putString(Define.VOICE_UPLOAD_URL, server_url+"upload");
                    editor.putString(Define.IMAGE_UPLOAD_URL, server_url+"upload-image");

                    editor.putString(Define.USER_ID, user_id);
                    editor.putString(Define.USER_NM, user_nm);
                    editor.putString(Define.DB_USER_FIRST_NM, firstName);
                    editor.putString(Define.DB_USER_LAST_NM, lastName);
                    editor.putString(Define.USER_EMAIL, email);
                    editor.putString(Define.USER_PASSWD, passwd);
                    editor.putString(Define.USER_GCM_ID, device_reg_id);
                    editor.putString(Define.DB_STATUS, user_status);

                    editor.apply();

                    moveToFindFriends(context);

                    //moveToAddFriends(context, user_status);
                    //Toast.makeText(context.getApplicationContext(), "Sign-Up Success, Please Sign-In", Toast.LENGTH_SHORT).show();
                    //((Activity)context).finish();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

    }


    public static StringRequest SignIn(final Context context, Map<String, String> param) {

        //TODO -*ssh*- check new Library start
        final String email = param.get(Define.DB_EMAIL);
        final String passwd = param.get(Define.DB_PASSWD);
        final String device_reg_id = param.get(Define.DB_GCM_ID);
        final String object_id = param.get(Define.DB_OBJECT_ID);
        final String msgType = param.get(Define.MSG_TYPE);
        final String senderId = param.get(Define.MSG_SENDER_ID);
        final String senderNm = param.get(Define.MSG_SENDER_NM);
        //final String installation_id = param.get(Define.DB_INSTALLATION_ID);
        Log.d(LogTag, "device_reg_id : " + device_reg_id);
        Log.d(LogTag, "object_id : " + object_id);
        //Log.d(LogTag, "installation_id : " + installation_id);

        String signin_url = SERVER_URL + URL_SIGNIN;
        signin_url = signin_url.concat("?user_email=");
        signin_url = signin_url.concat(email);
        signin_url = signin_url.concat("&user_pw=");
        signin_url = signin_url.concat(passwd);
        signin_url = signin_url.concat("&device_type=Android");
        signin_url = signin_url.concat("&device_reg_id=");
        signin_url = signin_url.concat(device_reg_id);
        //signin_url = signin_url.concat("&installation_id=");
        //signin_url = signin_url.concat(installation_id);
        signin_url = signin_url.concat("&object_id=");

        if(object_id!=null)
        signin_url = signin_url.concat(object_id);

        Log.d(LogTag, signin_url);

        //Map<String, String> param = new HashMap<>();

        return new StringRequest(Request.Method.GET, signin_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{

                    Log.d(LogTag, response);
                    JSONObject resJSON = new JSONObject(response);
                    if( !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                        if(resJSON.has(RESULT_MESSAGE)){
                            Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }

                    try {
                        String user_id       = resJSON.getString(Define.DB_USER_ID);
                        String user_nm       = resJSON.getString(Define.DB_USER_NM);
                        String user_status   = resJSON.getString(Define.DB_STATUS);
                        String friend_count  = resJSON.getString(Define.FRIEND_COUNT);
                        String user_flag     = resJSON.getString(Define.USER_FLAG);
                        String server_url     = resJSON.getString(Define.SERVER_URL);

                        String first_name     = resJSON.getString(Define.DB_USER_FIRST_NM);
                        String last_name     = resJSON.getString(Define.DB_USER_LAST_NM);



                        SharedPreferences pref = RSPreference.getPreference(context);

                        SharedPreferences.Editor editor = pref.edit();

                        editor.putString(Define.CHAT_SERVER_URL , server_url);
                        editor.putString(Define.LOGIN_URL       , server_url+"login");
                        editor.putString(Define.VOICE_UPLOAD_URL, server_url+"upload");
                        editor.putString(Define.IMAGE_UPLOAD_URL, server_url+"upload-image");

                        editor.putString(Define.USER_ID, user_id);
                        editor.putString(Define.USER_NM, user_nm);

                        editor.putString(Define.DB_USER_FIRST_NM, first_name);
                        editor.putString(Define.DB_USER_LAST_NM, last_name);

                        editor.putString(Define.USER_EMAIL, email);
                        editor.putString(Define.USER_PASSWD, passwd);
                        editor.putString(Define.USER_GCM_ID, device_reg_id);
                        editor.putString(Define.DB_STATUS, user_status);
                        editor.putString(Define.USER_FLAG, user_flag);

                        editor.apply();

                        if( msgType == null || msgType.isEmpty() || friend_count.equals("0")) {
                            //        moveToMain(context, user_status);
                            //moveToAddFriends(context, user_status);
                            moveToFindFriends(context);

                        } else if( msgType.equals(Define.MSG_TYPE_PULL)) {

                            Map<String, String> param = new HashMap<>();
                            param.put(Define.ACTION, Define.ACTION_ROOM_PUBLIC);
                            param.put(Define.DB_USER_ID, user_id);
                            param.put(Define.PARAM_PULL_FROM, senderNm);

                            NetController.getInstance(context)
                                    .getRequestQueue()
                                    .add(NetController.Room(context, param));

                        } else if( msgType.equals(Define.MSG_TYPE_PUBLISH)) {

                            Map<String, String> param = new HashMap<>();
                            param.put(Define.ACTION, Define.USER_MODE_WATCH);
                            param.put(Define.DB_USER_ID, user_id);
                            param.put(Define.OWNER_ID, senderId);

                            NetController.getInstance(context)
                                    .getRequestQueue()
                                    .add(NetController.Room(context, param));

                        } else if( msgType.equals(Define.MSG_TYPE_KNOCK )) {
                            moveToMain(context, user_status);

                        } else if( msgType.equals(Define.MSG_TYPE_INVITE)) {

                            Map<String, String> param = new HashMap<>();
                            param.put(Define.ACTION, Define.USER_MODE_WATCH);
                            param.put(Define.DB_USER_ID, user_id);
                            param.put(Define.OWNER_ID, senderId);

                            NetController.getInstance(context)
                                    .getRequestQueue()
                                    .add(NetController.Room(context, param));

                        } else if( msgType.equals(Define.MSG_TYPE_STATUS )) {
                            moveToMain(context, user_status);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }catch (Exception e){
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        // new Library end

    }

    public static void moveToLogin(Context context){
        Intent intent = ((Activity)context).getIntent();

        Intent mainIntent = new Intent( context, LoginActivity.class );
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(mainIntent);
        ((Activity)context).finish();
    }


    public static void moveToAddFriends(Context context, String user_status){
        Intent intent = ((Activity)context).getIntent();
        String msgType = intent.getStringExtra(Define.MSG_TYPE);
        String senderId = intent.getStringExtra(Define.MSG_SENDER_ID);
//        String room = intent.getStringExtra(Define.DB_ROOM);

        Intent mainIntent = new Intent( context, AddFriendActivity.class );
        if( msgType != null && !msgType.isEmpty() ) {
            mainIntent.putExtra(Define.MSG_TYPE, msgType);
            mainIntent.putExtra(Define.MSG_SENDER_ID, senderId);
//            mainIntent.putExtra(Define.DB_ROOM, room);
        }
        mainIntent.putExtra(Define.DB_STATUS, user_status);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(mainIntent);
        ((Activity)context).finish();
    }

    public static void moveToFindFriends(Context context){
        Intent intent = ((Activity)context).getIntent();
        String msgType = intent.getStringExtra(Define.MSG_TYPE);
        String senderId = intent.getStringExtra(Define.MSG_SENDER_ID);
//        String room = intent.getStringExtra(Define.DB_ROOM);

        Intent mainIntent = new Intent( context, FindFriendWithFaceBookActivity.class );
        if( msgType != null && !msgType.isEmpty() ) {
            mainIntent.putExtra(Define.MSG_TYPE, msgType);
            mainIntent.putExtra(Define.MSG_SENDER_ID, senderId);
//            mainIntent.putExtra(Define.DB_ROOM, room);
        }
        //mainIntent.putExtra(Define.DB_STATUS, user_status);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(mainIntent);
        ((Activity)context).finish();
    }

    public static void moveToFriends(Context context, ArrayList<String> facebook_friend){
        Intent intent = ((Activity)context).getIntent();
        String msgType = intent.getStringExtra(Define.MSG_TYPE);
        String senderId = intent.getStringExtra(Define.MSG_SENDER_ID);
//        String room = intent.getStringExtra(Define.DB_ROOM);

        Intent mainIntent = new Intent( context, AddFriendWithFaceBookActivity.class );
        if( msgType != null && !msgType.isEmpty() ) {
            mainIntent.putExtra(Define.MSG_TYPE, msgType);
            mainIntent.putExtra(Define.MSG_SENDER_ID, senderId);
//            mainIntent.putExtra(Define.DB_ROOM, room);
        }
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.putStringArrayListExtra(Define.FACEBOOK_FRIEND_LIST, facebook_friend);
        context.startActivity(mainIntent);
        ((Activity)context).finish();
    }


    public static void moveToNotificationSetting(Context context, String user_id){
        Intent intent = ((Activity)context).getIntent();
        String msgType = intent.getStringExtra(Define.MSG_TYPE);
        String senderId = intent.getStringExtra(Define.MSG_SENDER_ID);
//        String room = intent.getStringExtra(Define.DB_ROOM);

        Intent mainIntent = new Intent( context, NotificationSettingActivity.class );
        if( msgType != null && !msgType.isEmpty() ) {
            mainIntent.putExtra(Define.MSG_TYPE, msgType);
            mainIntent.putExtra(Define.MSG_SENDER_ID, senderId);
//            mainIntent.putExtra(Define.DB_ROOM, room);
        }
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(mainIntent);
        ((Activity)context).finish();
    }


    public static void moveToMain(Context context, String user_status){
        Intent intent = ((Activity)context).getIntent();
        String msgType = intent.getStringExtra(Define.MSG_TYPE);
        String senderId = intent.getStringExtra(Define.MSG_SENDER_ID);
//        String room = intent.getStringExtra(Define.DB_ROOM);

        Intent mainIntent = new Intent( context, MainActivity.class );
        if( msgType != null && !msgType.isEmpty() ) {
            mainIntent.putExtra(Define.MSG_TYPE, msgType);
            mainIntent.putExtra(Define.MSG_SENDER_ID, senderId);
//            mainIntent.putExtra(Define.DB_ROOM, room);
        }
        mainIntent.putExtra(Define.DB_STATUS, user_status);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(mainIntent);
        ((Activity)context).finish();
    }

    public static StringRequest SignOut(final Context context, String user_id){
        String signout_url = SERVER_URL + URL_SIGNOUT + "?user_id=" + user_id;
        return new StringRequest(Request.Method.GET, signout_url,
                    new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response){
                            try {
                                JSONObject resJSON = new JSONObject(response);
                                if( !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                                    if(resJSON.has(RESULT_MESSAGE)){
                                        Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                    return;
                                }

                                SharedPreferences pref = RSPreference.getPreference(context);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.remove(Define.USER_ID);
                                editor.remove(Define.USER_EMAIL);
                                editor.remove(Define.USER_PASSWD);
                                editor.remove(Define.USER_GCM_ID);

                                editor.apply();

                                Toast.makeText(context.getApplicationContext(), "Sign-Out Success", Toast.LENGTH_SHORT).show();

                                Intent loginIntent = new Intent(context.getApplicationContext(), LoginActivity.class);
                                context.startActivity(loginIntent);

                                ((Activity)context).finish();
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
    }

    public static StringRequest Room(final Context context, final Map<String, String> param){

        String room_url = SERVER_URL + URL_ROOM;

        final String action = param.get(Define.ACTION);
        final String callerActivity = param.get(Define.CALLER_ACTIVITY);
        final String user_id = param.get(Define.DB_USER_ID);
        final String owner_id = param.get(Define.OWNER_ID);
        final String ismyapp = param.get(Define.IS_MY_APP);


        Log.d(LogTag, "PARAM_ALLOW_LIST : " + param.get(Define.PARAM_ALLOW_LIST));
        Log.d(LogTag, "action : " + action + " caller : " + callerActivity);


        return new StringRequest(Request.Method.POST, room_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(LogTag, "response : " + response);

                        try {
                            JSONObject resJSON = new JSONObject(response);
                            if(  !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                if(callerActivity.equals("ShowMsg")) ((Activity)context).finish();
                                return;
                            }

                            if(action.equals( Define.ACTION_ROOM_CLOSE )){
                                closeEvent(context, resJSON, callerActivity);

                            }else if(action.equals( Define.ACTION_ROOM_PUBLIC )){
                                publicEvent(context, resJSON, callerActivity);

                            }else if(action.equals( Define.ACTION_ROOM_PRIVATE )){
                                privateEvent(context, resJSON, callerActivity);

                            }else if(action.equals( Define.ACTION_ROOM_WATCH )){
                                watchEvent(context, resJSON, callerActivity, owner_id);

                            }else if(action.equals( Define.ACTION_ROOM_LEAVE )){
                                leaveEvent(context, resJSON, callerActivity, ismyapp);
                            }

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> postParam = new HashMap<>();
                postParam.put(Define.ACTION, action);
                postParam.put(Define.DB_USER_ID, user_id);

                if( owner_id != null && !owner_id.isEmpty())
                    postParam.put(Define.OWNER_ID, owner_id);

                if( param.get(Define.PARAM_PULL_FROM) != null && !param.get(Define.PARAM_PULL_FROM).isEmpty()) {
                    postParam.put(Define.PARAM_PULL_FROM, param.get(Define.PARAM_PULL_FROM));
                }

                if( param.get(Define.PARAM_ALLOW_LIST) != null && !param.get(Define.PARAM_ALLOW_LIST).isEmpty())
                    postParam.put(Define.PARAM_ALLOW_LIST, param.get(Define.PARAM_ALLOW_LIST));

                return postParam;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

    }

    public static void closeEvent(Context context, JSONObject resJSON, String callerActivity){
            Log.d(LogTag, "caller " + callerActivity);

            if( callerActivity == null || !callerActivity.contains("MainActivity") ) {
                // 다른 경로로 들어 옴
                Intent mainIntent = new Intent(context.getApplicationContext(), MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(mainIntent);
            }

            ((Activity)context).finish();

        }
    public static void publicEvent(Context context, JSONObject resJSON, String callerActivity){
        Log.d(LogTag, "caller " + callerActivity);

        ;
        Intent broadcastIntent = new Intent( context.getApplicationContext(), BroadcastActivity.class );
        broadcastIntent.putExtra(Define.CALLER_ACTIVITY, callerActivity);
        try {
            broadcastIntent.putExtra(Define.DB_ROOM, resJSON.getString(ROOM_NAME));
        }catch(JSONException e){}
        context.startActivity(broadcastIntent);

        if( callerActivity == null || !callerActivity.contains("MainActivity") ) {
            // 다른 경로로 들어 옴
            ((Activity)context).finish();
        }
    }
    public static void privateEvent(Context context, JSONObject resJSON, String callerActivity){
        Log.d(LogTag, "caller " + callerActivity );

        Intent broadcastIntent = new Intent( context.getApplicationContext(), BroadcastActivity.class );
        broadcastIntent.putExtra(Define.CALLER_ACTIVITY, callerActivity);
        try {
            broadcastIntent.putExtra(Define.DB_ROOM, resJSON.getString(ROOM_NAME));
        }catch(JSONException e){}
        context.startActivity(broadcastIntent);

        if( callerActivity == null || !callerActivity.contains("MainActivity") ) {
            // 다른 경로로 들어 옴
            ((Activity)context).finish();
        }
    }
    public static void leaveEvent(Context context, JSONObject resJSON, String callerActivity, String ismyapp){
        Log.d(LogTag, "caller " + callerActivity + " : " + ismyapp);

        if( callerActivity == null || !callerActivity.contains("MainActivity") ) {

            // 다른 경로로 들어 옴
            Intent mainIntent = new Intent(context.getApplicationContext(), MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(mainIntent);
        }

        ((Activity)context).finish();
    }
    public static void watchEvent(Context context, JSONObject resJSON, String callerActivity, String owner_id){
        Log.d(LogTag, "caller " + callerActivity);

        Intent watchIntent = new Intent( context, WatchActivity.class );

        watchIntent.putExtra(Define.OWNER_ID, owner_id);
        watchIntent.putExtra(Define.CALLER_ACTIVITY, callerActivity);

        context.startActivity(watchIntent);

        if( callerActivity == null || !callerActivity.contains("MainActivity") ) {
            // 다른 경로로 들어 옴
            ((Activity)context).finish();
        }
    }


    public static void showCustomToast(Context context, String msg, int duration){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        //Parameter 2 - Custom layout ID present in linearlayout tag of XML
        View layout = inflater.inflate(R.layout.result_toast, null);

        TextView msgView = (TextView)layout.findViewById(R.id.text);
        msgView.setText(msg+", wassup?");
        //Return the application context
        Toast toast = new Toast(context.getApplicationContext());
        //Set toast gravity to bottom
        toast.setGravity(Gravity.CENTER, 0, 0);
        //Set toast duration
        toast.setDuration(duration);
        //Set the custom layout to Toast
        toast.setView(layout);
        //Display toast
        toast.show();
    }

    public static StringRequest Push(final Context context, final Map<String, String> param) {
        String push_url = SERVER_URL + URL_PUSH;

        final String action = param.get(Define.ACTION);
        final String push_type = param.get(Define.MSG_TYPE);
        final String sender_id = param.get(Define.MSG_SENDER_ID);
        final String room_name = param.get(Define.MSG_ROOM_NM);
        final String receiver_list = param.get(Define.PARAM_RECEIVER_LIST);
        final String sender_status = param.get(Define.MSG_SENDER_STATUS);
        final String receiver_name = param.get(Define.RECEIVER_NAME);

        Log.d(LogTag, "room_name : " + room_name);
        Log.d(LogTag, "receiver_list : " + receiver_list);
        Log.d(LogTag, "sender_status : " + sender_status);

        return new StringRequest(Request.Method.POST, push_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LogTag, "response : " + response);
                        try {
                            JSONObject resJSON = new JSONObject(response);
                            if(  !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }

                            if(resJSON.has(RESULT_MESSAGE)){
                                Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                            } else {
                                if(push_type.equals(Define.MSG_TYPE_PULL))
                                    showCustomToast(context.getApplicationContext(), receiver_name, Toast.LENGTH_SHORT);
                                else Toast.makeText(context.getApplicationContext(), "sent pull message", Toast.LENGTH_SHORT).show();


                            }

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> postParam = new HashMap<>();
                postParam.put(Define.MSG_TYPE, push_type);
                postParam.put(Define.MSG_SENDER_ID, sender_id);
                postParam.put(Define.PARAM_RECEIVER_LIST, receiver_list);

                if(param.get(Define.MSG_SENDER_STATUS)!=null && !param.get(Define.MSG_SENDER_STATUS).isEmpty())
                    postParam.put(Define.MSG_SENDER_STATUS, param.get(Define.MSG_SENDER_STATUS));

                if( param.get(Define.PARAM_ANONYMOUS) != null && !param.get(Define.PARAM_ANONYMOUS).isEmpty())
                    postParam.put(Define.PARAM_ANONYMOUS, param.get(Define.PARAM_ANONYMOUS));

                if( param.get(Define.MSG_ROOM_NM) != null && !param.get(Define.MSG_ROOM_NM).isEmpty())
                    postParam.put(Define.MSG_ROOM_NM, param.get(Define.MSG_ROOM_NM));

                return postParam;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

    }

    public static StringRequest RecordStream(final Context context, final Map<String, String> param){
        String push_url = SERVER_URL + URL_RECORD_STREAM;
        final String push_type   = param.get(Define.MSG_TYPE);
        final String user_id     = param.get(Define.MSG_USER_ID);
        final String record_url  = param.get(Define.MSG_RECORD_URL);
        final String user_nm     = param.get(Define.DB_USER_NM);
        final String status_msg  = param.get(Define.DB_STATUS);

        Log.d("Class", "record_stream");
        Log.d("push_type " , push_type);
        Log.d("user_id " , user_id);
        Log.d("record_url " , record_url);
        Log.d("user_nm " , user_nm);
        Log.d("status_msg " , status_msg);

        return new StringRequest(Request.Method.POST, push_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LogTag, "response : " + response);
                        try {
                            JSONObject resJSON = new JSONObject(response);
                            if(  !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }

                            if(resJSON.has(RESULT_MESSAGE)){
                                Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context.getApplicationContext(), "sent record url message", Toast.LENGTH_SHORT).show();
                            }

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> postParam = new HashMap<>();
                postParam.put(Define.MSG_TYPE, push_type);
                postParam.put(Define.MSG_USER_ID, user_id);
                postParam.put(Define.DB_USER_NM, user_nm);
                postParam.put(Define.DB_STATUS, status_msg);
                postParam.put(Define.MSG_RECORD_URL, record_url);

                return postParam;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

    }


    public static StringRequest ShareLink(final Context context, final Map<String, String> param){
        String push_url = SERVER_URL + URL_SHARE_LINK;
        final String push_type = param.get(Define.MSG_TYPE);
        final String hash_url  = param.get(Define.MSG_HASH_URL);
        final String share_url = param.get(Define.MSG_SHARE_URL);

        return new StringRequest(Request.Method.POST, push_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LogTag, "response : " + response);
                        try {
                            JSONObject resJSON = new JSONObject(response);
                            if(  !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }

                            if(resJSON.has(RESULT_MESSAGE)){
                                Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context.getApplicationContext(), "sent share url message", Toast.LENGTH_SHORT).show();
                            }

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> postParam = new HashMap<>();
                postParam.put(Define.MSG_TYPE, push_type);
                postParam.put(Define.MSG_HASH_URL, hash_url);
                postParam.put(Define.MSG_SHARE_URL, share_url);

                return postParam;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

    }


    public static StringRequest AddFriend(final Context context, final Map<String, String> param){
        String push_url = SERVER_URL + URL_ADD_FRIEND;
        final String push_type = param.get(Define.MSG_TYPE);
        final String sender_id = param.get(Define.MSG_SENDER_ID);
        final String sender_nm = param.get(Define.MSG_SENDER_NM);
        final String receiver_list = param.get(Define.PARAM_RECEIVER_LIST);

        Log.d(LogTag, "push_type : " + push_type);
        Log.d(LogTag, "sender_nm : " + sender_nm);
        Log.d(LogTag, "receiver_list : " + receiver_list);


        Log.d(LogTag, "url : " + push_url);

        return new StringRequest(Request.Method.POST, push_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LogTag, "response : " + response);
                        try {
                            JSONObject resJSON = new JSONObject(response);
                            if(  !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }

                       /*     if(push_type.equals(Define.MSG_TYPE_CONFIRMFRIEND)) {
                                AddFriendActivity.UserListEvent(resJSON, "");
                            }*/

                            if(resJSON.has(RESULT_MESSAGE)){
                                Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context.getApplicationContext(), "sent add friend message", Toast.LENGTH_SHORT).show();
                            }

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> postParam = new HashMap<>();
                postParam.put(Define.MSG_TYPE, push_type);
                postParam.put(Define.MSG_SENDER_ID, sender_id);
                postParam.put(Define.MSG_SENDER_NM, sender_nm);
                postParam.put(Define.PARAM_RECEIVER_LIST, receiver_list);

                return postParam;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params =     new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

    }

    public static StringRequest beforeFileUpload(final Context context,final Map<String, String> param, final File file ){

        String beforeFileUploadUrl = "http://api.liveo.me/file";
        final String sessionId   = param.get(Define.SESSION_ID);
        final String uploadType  = param.get(Define.UPLOAD_TYPE);


        Log.d(LogTag, "beforeFileUploadUrl : " + beforeFileUploadUrl);

        return new StringRequest(Request.Method.POST, beforeFileUploadUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LogTag, "response : " + response);
                        try {
                            JSONObject resJSON = new JSONObject(response);
                            if(  !resJSON.has("success") || !resJSON.getString("success").equals("YES") ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }

                            String resJson = resJSON.getString("result");
                            JSONObject result = new JSONObject(resJson);

                            try {
                                String key         = result.getString(Define.KEY);
                                String policy      = result.getString(Define.POLICY);
                                String signature   = result.getString(Define.SIGNATURE);

                                SharedPreferences pref = RSPreference.getPreference(context);
                                SharedPreferences.Editor editor = pref.edit();

                                editor.putString(Define.KEY, key);
                                editor.putString(Define.POLICY       , policy);
                                editor.putString(Define.SIGNATURE    , signature);

                                editor.apply();

                                String uploadPath = ParseNetController.uploadFileToS3(context, file);

                                ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded();
                                user.put("thumbnailPath", uploadPath);
                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                        } else e.printStackTrace();
                                    }
                                });

                            }catch(Exception e) { e.printStackTrace(); }

                            /*if(resJSON.has(RESULT_MESSAGE)){
                                Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context.getApplicationContext(), "before Upload Image!", Toast.LENGTH_SHORT).show();
                            }*/

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> postParam = new HashMap<>();
                postParam.put(Define.SESSION_ID           , sessionId);
                postParam.put(Define.UPLOAD_TYPE          , uploadType);

                return postParam;
            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }*/
        };
    }

    public static StringRequest beforeFileUpload(final Context context,final Map<String, String> param){

        String beforeFileUploadUrl = "http://api.liveo.me/file";
        final String sessionId   = param.get(Define.SESSION_ID);
        final String uploadType  = param.get(Define.UPLOAD_TYPE);


        Log.d(LogTag, "beforeFileUploadUrl : " + beforeFileUploadUrl);

        return new StringRequest(Request.Method.POST, beforeFileUploadUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LogTag, "response : " + response);
                        try {
                            JSONObject resJSON = new JSONObject(response);
                            if(  !resJSON.has("success") || !resJSON.getString("success").equals("YES") ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }

                            String resJson = resJSON.getString("result");
                            JSONObject result = new JSONObject(resJson);

                            try {
                                String key         = result.getString(Define.KEY);
                                String policy      = result.getString(Define.POLICY);
                                String signature   = result.getString(Define.SIGNATURE);

                                SharedPreferences pref = RSPreference.getPreference(context);
                                SharedPreferences.Editor editor = pref.edit();

                                editor.putString(Define.KEY, key);
                                editor.putString(Define.POLICY, policy);
                                editor.putString(Define.SIGNATURE, signature);

                                editor.apply();

                                /*if(uploadType.equals("voice"))
                                    WatchActivity.getInstance().sendVoiceMessage();*/

                            }catch(Exception e) { e.printStackTrace(); }

                            /*if(resJSON.has(RESULT_MESSAGE)){
                                Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context.getApplicationContext(), "before Upload Image!", Toast.LENGTH_SHORT).show();
                            }*/

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> postParam = new HashMap<>();
                postParam.put(Define.SESSION_ID           , sessionId);
                postParam.put(Define.UPLOAD_TYPE          , uploadType);

                return postParam;
            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }*/
        };
    }


    public static StringRequest getTokenKey(final Context context, String session){

        String sessionTokenURL = "http://api.liveo.me/key";
        final String sessionToken   = session;


        Log.d(LogTag, "sessionToken : " + sessionToken);

        return new StringRequest(Request.Method.POST, sessionTokenURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LogTag, "response : " + response);
                        try {
                            JSONObject resJSON = new JSONObject(response);
                            if(  !resJSON.has("success") || !resJSON.getString("success").equals("YES") ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> postParam = new HashMap<>();
                postParam.put(Define.SESSION_TOKEN           , sessionToken);

                return postParam;
            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }*/
        };
    }

    public static StringRequest beforeFileUpload(final Context context,final Map<String, String> param, final Firebase mfireBaase){

        String beforeFileUploadUrl = "http://api.liveo.me/file";
        final String sessionId   = param.get(Define.SESSION_ID);
        final String uploadType  = param.get(Define.UPLOAD_TYPE);


        Log.d(LogTag, "beforeFileUploadUrl : " + beforeFileUploadUrl);

        return new StringRequest(Request.Method.POST, beforeFileUploadUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LogTag, "response : " + response);
                        try {
                            JSONObject resJSON = new JSONObject(response);
                            if(  !resJSON.has("success") || !resJSON.getString("success").equals("YES") ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }

                            String resJson = resJSON.getString("result");
                            JSONObject result = new JSONObject(resJson);

                            try {
                                String key         = result.getString(Define.KEY);
                                String policy      = result.getString(Define.POLICY);
                                String signature   = result.getString(Define.SIGNATURE);

                                SharedPreferences pref = RSPreference.getPreference(context);
                                SharedPreferences.Editor editor = pref.edit();

                                editor.putString(Define.KEY, key);
                                editor.putString(Define.POLICY, policy);
                                editor.putString(Define.SIGNATURE, signature);

                                editor.apply();

                                if(uploadType.equals("voice"))
                                    WatchActivity.getInstance().sendVoiceMessage();

                            }catch(Exception e) { e.printStackTrace(); }

                            /*if(resJSON.has(RESULT_MESSAGE)){
                                Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context.getApplicationContext(), "before Upload Image!", Toast.LENGTH_SHORT).show();
                            }*/

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> postParam = new HashMap<>();
                postParam.put(Define.SESSION_ID           , sessionId);
                postParam.put(Define.UPLOAD_TYPE          , uploadType);

                return postParam;
            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }*/
        };
    }

    public static StringRequest ActiveOnOff(final Context context, String user_id, String user_active){

        String active_url = SERVER_URL + URL_ACTIVE;
        active_url = active_url.concat("?user_id=");
        active_url = active_url.concat(user_id);
        active_url = active_url.concat("&user_active=");
        active_url = active_url.concat(user_active);


        Log.d(LogTag, "active_url : " + active_url);

        return new StringRequest(Request.Method.GET, active_url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){

                        Log.d(LogTag, "response : " + response);

                        try {

                            JSONObject resJSON = new JSONObject(response);
                            if(  !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }

                            /*if(resJSON.has(RESULT_MESSAGE)){
                                Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context.getApplicationContext(), "Go Liveo", Toast.LENGTH_SHORT).show();
                            }*/


                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
    }

    public static StringRequest SCREENON(final Context context, String room_name, String command){

        String status_url = SERVER_URL + URL_SCREEN_ON;
        status_url = status_url.concat("?room_name=");
        status_url = status_url.concat(room_name);
        status_url = status_url.concat("&command=");
        status_url = status_url.concat(command);


        Log.d(LogTag, "screen_on : " + status_url);

        return new StringRequest(Request.Method.GET, status_url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){

                        Log.d(LogTag, "response : " + response);

                        try {

                            JSONObject resJSON = new JSONObject(response);
                            if(  !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }

                            if(resJSON.has(RESULT_MESSAGE)){
                                Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context.getApplicationContext(), "Screen is Off", Toast.LENGTH_SHORT).show();
                            }


                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
    }


    public static StringRequest Status(final Context context, String user_id, String status){

        final String msg = status;
        String status_url = SERVER_URL + URL_STATUS;
        status_url = status_url.concat("?user_id=");
        status_url = status_url.concat(user_id);
        status_url = status_url.concat("&user_status=");

        try {
            status_url = status_url.concat(URLEncoder.encode(status, "utf-8").replace("+", "%20"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d(LogTag, "status_url : " + status_url);

        return new StringRequest(Request.Method.GET, status_url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){

                        Log.d(LogTag, "response : " + response);

                        try {

                            JSONObject resJSON = new JSONObject(response);
                            if(  !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }

                            Intent mainIntent = new Intent( context, MainActivity.class );
                            mainIntent.putExtra(Define.DB_STATUS, msg);

                            if(resJSON.has(RESULT_MESSAGE)){
                                Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context.getApplicationContext(), "Status is updated", Toast.LENGTH_SHORT).show();
                            }


                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
    }




    public static StringRequest Comment(final Context context, final Map<String, String> param, final boolean own){
        String push_url = SERVER_URL + URL_COMMENT;
        final String push_type = param.get(Define.MSG_TYPE);
        final String sender_id = param.get(Define.MSG_SENDER_ID);
        final String msg       = param.get(Define.MSG);
        final String owner_id  = param.get(Define.MSG_OWNER_ID);
        final String log_id    = param.get(Define.MSG_LOG_ID);

        Log.d(LogTag, "push_type : " + push_type);
        Log.d(LogTag, "msg       : " + msg);
        Log.d(LogTag, "owner_id  : " + owner_id);
        Log.d(LogTag, "log_id    : " + log_id);


        return new StringRequest(Request.Method.POST, push_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LogTag, "response : " + response);
                        try {
                            JSONObject resJSON = new JSONObject(response);
                            if(  !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }

                            if(resJSON.has(RESULT_MESSAGE)){
                                Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context.getApplicationContext(), "save comment message", Toast.LENGTH_SHORT).show();
                            }

                            if(own){
                                FriendFragment.callCommentList(owner_id, true);
                            } else {
                                //FriendListAdapter.callCommentList(owner_id, false);
                            }

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> postParam = new HashMap<>();
                postParam.put(Define.MSG_TYPE, push_type);
                postParam.put(Define.MSG_SENDER_ID, sender_id);
                postParam.put(Define.MSG, msg);
                postParam.put(Define.MSG_OWNER_ID, owner_id);
                postParam.put(Define.MSG_LOG_ID, log_id);

                return postParam;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

    }

    public static StringRequest ActivityRead(final Context context, final Map<String, String> param){
        String push_url = SERVER_URL + URL_ACTIVITY_READ;

        final String log_id    = param.get(Define.MSG_LOG_ID);

        Log.d(LogTag, "log_id    : " + log_id);


        return new StringRequest(Request.Method.POST, push_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LogTag, "response : " + response);
                        try {
                            JSONObject resJSON = new JSONObject(response);
                            if(  !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }

                            if(resJSON.has(RESULT_MESSAGE)){
                                Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context.getApplicationContext(), "Read Activity message", Toast.LENGTH_SHORT).show();
                            }

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> postParam = new HashMap<>();
                postParam.put(Define.MSG_LOG_ID, log_id);

                return postParam;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

    }


    public static StringRequest RecordStreamList(final Context context, String user_id, String listParam, final Map<String, String> param){
        String activityList_url = SERVER_URL + URL_STREAM_LIST;
        activityList_url = activityList_url.concat("?user_id=");
        activityList_url = activityList_url.concat(user_id);
        activityList_url = activityList_url.concat("&list_param=");
        activityList_url = activityList_url.concat(listParam);


        //final String search_word = param.get(Define.SEARCH_WORD);

        return new StringRequest(Request.Method.GET, activityList_url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){

                        Log.d(LogTag, "response : " + response);

                        try {

                            JSONObject resJSON = new JSONObject(response);
                            if( !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                            final String action_type = param.get(Define.ACTION);

//                            TimeLineFragment.RecordStreamListEvent(resJSON);

                            Log.d(LogTag, " : RecordStreamListEvent " + action_type);

                            //Log.d(LogTag, resJSON.getString("userList"));

                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
    }


    public static StringRequest ActivityList(final Context context, String user_id, String listParam, final Map<String, String> param){
        String activityList_url = SERVER_URL + URL_ACTIVITY;
        activityList_url = activityList_url.concat("?user_id=");
        activityList_url = activityList_url.concat(user_id);
        activityList_url = activityList_url.concat("&list_param=");
        activityList_url = activityList_url.concat(listParam);


        //final String search_word = param.get(Define.SEARCH_WORD);

        return new StringRequest(Request.Method.GET, activityList_url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){

                        Log.d(LogTag, "response : " + response);

                        try {

                            JSONObject resJSON = new JSONObject(response);
                            if( !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                            final String action_type = param.get(Define.ACTION);

                            if(action_type.equals(Define.ACTION_ACTIVITY)) {
                                //TODO -*ssh*- Change response Event
                                ActivityFragment.ActivityListEvent(resJSON);
                            }

                            Log.d(LogTag, " : ActivityListEvent " + action_type);

                            //Log.d(LogTag, resJSON.getString("userList"));

                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
    }


    public static StringRequest CommentList(final Context context, String user_id, String listParam, final Map<String, String> param, final boolean own){
        String commentList_url = SERVER_URL + URL_COMMENT_LIST;
        commentList_url = commentList_url.concat("?user_id=");
        commentList_url = commentList_url.concat(user_id);
        commentList_url = commentList_url.concat("&list_param=");
        commentList_url = commentList_url.concat(listParam);


        //final String search_word = param.get(Define.SEARCH_WORD);

        return new StringRequest(Request.Method.GET, commentList_url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){

                        Log.d(LogTag, "response : " + response);

                        try {

                            JSONObject resJSON = new JSONObject(response);
                            if( !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                            final String action_type = param.get(Define.ACTION);

                            if(action_type.equals(Define.ACTION_COMMENT_LIST)) {
                                //TODO -*ssh*- Change response Event
                                if(own) {
                                    FriendFragment.CommentListEvent(resJSON);
                                } else {
                                    //FriendListAdapter.CommentListEvent(resJSON);
                                }
                            }

                            Log.d(LogTag, " : CommentListEvent " + action_type);

                            //Log.d(LogTag, resJSON.getString("userList"));

                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
    }
    public static StringRequest FriendList(final Context context, String user_id, String listParam, final Map<String, String> param){
        String friendList_url = SERVER_URL + URL_FRIEDNDS;

        try {
            friendList_url = friendList_url.concat("?user_id=");
            friendList_url = friendList_url.concat(user_id);
            friendList_url = friendList_url.concat("&list_param=");
            friendList_url = friendList_url.concat(listParam);
            if(param.get(Define.USER_FLAG)!=null)
            {
                friendList_url = friendList_url.concat("&user_flag=");
                friendList_url = friendList_url.concat(param.get(Define.USER_FLAG));
            }

            Log.d(LogTag, "url : " + friendList_url);

        }catch(Exception e) { e.printStackTrace();}

        final String search_word = param.get(Define.SEARCH_WORD);

        return new StringRequest(Request.Method.GET, friendList_url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){

                        Log.d(LogTag, "response : " + response);

                        try {

                            JSONObject resJSON = new JSONObject(response);
                            if( !resJSON.has(RESULT_CODE) || !resJSON.getString(RESULT_CODE).equals(RESULT_SUCCESS) ) {
                                if(resJSON.has(RESULT_MESSAGE)){
                                    Toast.makeText(context.getApplicationContext(), resJSON.getString(RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                            final String action_type = param.get(Define.ACTION);

                            if(action_type.equals(Define.ACTION_FRIEDNDS)) {
                                //TODO -*ssh*- Change response Event
 //                               FriendFragment.UserListEvent(resJSON);
                            }
                            else if(action_type.equals(Define.ACTION_ADD_FACEBOOK_FRIEND)) {
    //                            AddFriendWithFaceBookActivity.UserListEvent(resJSON, search_word);
                            }
                            else if(action_type.equals(Define.ACTION_ADD_FACE_BOOK_FRIEND_COUNT)) {
      //                          AddFriendWithFaceBookActivity.UserListEventCnt(resJSON);
                            }
                            else if(action_type.equals(Define.ACTION_ADD_FRIEND)) {
        //                        AddFriendActivity.UserListEvent(resJSON, search_word);
                            }
                            else if(action_type.equals(Define.ACTION_ADD_FRIEND_COUNT)) {
          //                      AddFriendActivity.UserListEventCnt(resJSON);
                            }
                            Log.d(LogTag, " : UserListEvent " + action_type);

                            //Log.d(LogTag, resJSON.getString("userList"));

                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
    }



}
