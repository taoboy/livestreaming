package kr.co.wegeneration.realshare.NetController;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.parse.SignUpCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import kr.co.wegeneration.realshare.AddFriendActivity;
import kr.co.wegeneration.realshare.AddFriendWithFaceBookActivity;
import kr.co.wegeneration.realshare.BroadcastActivity;
import kr.co.wegeneration.realshare.FindFriendWithFaceBookActivity;
import kr.co.wegeneration.realshare.LoginActivity;
import kr.co.wegeneration.realshare.MainActivity;
import kr.co.wegeneration.realshare.NotificationSettingActivity;
import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.WatchActivity;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;
import kr.co.wegeneration.realshare.models.RoomAllowed;
import kr.co.wegeneration.realshare.models.StatusComment;
import kr.co.wegeneration.realshare.models.Friend;
import kr.co.wegeneration.realshare.models.Notification;
import kr.co.wegeneration.realshare.models.Room;
import kr.co.wegeneration.realshare.models.RoomComment;
import kr.co.wegeneration.realshare.models.UserInfo;
import kr.co.wegeneration.realshare.util.LruBitmapCache;
import kr.co.wegeneration.realshare.util.RandomStringBuilder;

/**
 * Created by ssh on 15. 8. 25..
 */

// -*ssh*- Check this class is working or not
public class ParseNetController {

    private static final String LogTag = "ParseNetController";


    private static ParseNetController mInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    LruBitmapCache mLruBitmapCache;

    private ParseNetController(Context context){
        mRequestQueue = getRequestQueue(context);
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

    public static ParseNetController getInstance(Context context){
        if(mInstance == null){
            mInstance = new ParseNetController(context);
        }
        return mInstance;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag, Context context) {
        req.setTag(TextUtils.isEmpty(tag) ? LogTag : tag);
        getRequestQueue(context).add(req);
    }



    public ImageLoader getImageLoader(Context context) {
        getRequestQueue(context);
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }



    public RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        return mRequestQueue;
    }


    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }


    public <T> void addToRequestQueue(Request<T> req, Context context) {
        req.setTag(LogTag);
        getRequestQueue(context).add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    ///////////////////////////////////////////해야될 것들
    ////  SignUp          (Finish)
    ////  SignIn          (Finish)
    ////  SignOut         (Finish)
    ////  Room            (Finish)
    ///   FriendLIST      (Finish)
    ////  FriendADD       (Finish)
    ///// CommentList
    ///// Comment         (Finish)
    ///// TimeLineList    TBD
    ///// ActivityList    TBD
    ///// ActiveInOut     (Finish)
    ///// Status          (Finish)
    //////Push            (Finish)
    ///// RecoreStream    TBD
    ///// ThumbNail       TBD

    public static void uploadfILEToS3( Context context,  String userId, String uploadType, File file){

        Map<String, String> param = new HashMap<>();

        param.put(Define.SESSION_ID  , userId);
        param.put(Define.UPLOAD_TYPE , uploadType);

        NetController.getInstance(context)
                .getRequestQueue()
                .add(NetController.beforeFileUpload(context, param, file));


    }


    public static void Comment(final Context thisContext,  final Map<String, String> param) {

        try{

            final String push_type = param.get(Define.MSG_TYPE);
            final String sender_id = param.get(Define.MSG_SENDER_ID);
            final String username  = param.get(Define.DB_USER_NM);
            final String msg       = param.get(Define.MSG);
            final String ownerId  = param.get(Define.MSG_OWNER_ID);
            final String isOwn    = param.get(Define.ISOWN);

            if(push_type.equals(Define.MSG_TYPE_INSERT_COMMENT)) {

                StatusComment comment = new StatusComment();
                comment.put("userId", sender_id);
                comment.put("content", msg);
                comment.put("username", username);
                comment.put("statusId", ownerId);
                if(ParseUser.getCurrentUser().getParseObject(Define.UserInfo).get("thumbnailPath")!=null)
                    comment.put("thumbnailPath", ParseUser.getCurrentUser().getParseObject(Define.UserInfo).get("thumbnailPath"));

                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(thisContext,
                                    "Comment Saved!",
                                    Toast.LENGTH_SHORT).show();

                            if (!isOwn.equals("true")) {
                                Map<String, String> param = new HashMap<String, String>();

                                param.put(Define.ACTION, Define.ACTION_COMMENT);
                                param.put(Define.MSG_TYPE, Define.MSG_TYPE_INSERT_COMMENT);
                                param.put(Define.DB_USER_ID, sender_id);
                                param.put(Define.PARAM_RECEIVER_LIST, "[" + ownerId + "]");
                                param.put(Define.RECEIVER_NAME, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).get(Define.DB_USER_FIRST_NM).toString());

                                ParseNetController.PushSend(thisContext, param);
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }


        }catch(Exception e) {e.printStackTrace();}
    }

    public static void Comment(final Context thisContext,  final Map<String, String> param, StatusComment comment) {

        try{

            final String push_type = param.get(Define.MSG_TYPE);
            final String sender_id = param.get(Define.MSG_SENDER_ID);
            final String msg       = param.get(Define.MSG);
            final String username  = param.get(Define.DB_USER_NM);
            final String ownerId  = param.get(Define.MSG_OWNER_ID);

            if(push_type.equals(Define.MSG_TYPE_INSERT_COMMENT)) {

                comment.put("userId", sender_id);
                comment.put("content", msg);
                comment.put("statusId", ownerId);
                comment.put("username", username);
                if(ParseUser.getCurrentUser().getParseObject(Define.UserInfo).get("thumbnailPath")!=null)
                    comment.put("thumbnailPath", ParseUser.getCurrentUser().getParseObject(Define.UserInfo).get("thumbnailPath"));

                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            Toast.makeText(thisContext,
                                    "Comment Saved!",
                                    Toast.LENGTH_SHORT).show();

                            Map<String, String> param = new HashMap<String, String>();

                            param.put(Define.ACTION, Define.ACTION_COMMENT);
                            param.put(Define.MSG_TYPE, Define.MSG_TYPE_INSERT_COMMENT);
                            param.put(Define.DB_USER_ID, sender_id);
                            param.put(Define.PARAM_RECEIVER_LIST, "[" + ownerId + "]");
                            param.put(Define.RECEIVER_NAME, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).get(Define.DB_USER_FIRST_NM).toString());

                            ParseNetController.PushSend(thisContext, param);
                        }
                        else{
                            e.printStackTrace();
                        }
                    }
                });
            }

            if(push_type.equals(Define.MSG_TYPE_DELETE_COMMENT)) {

                comment.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(thisContext,
                                    "Comment Deleted!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }

        }catch(Exception e) {e.printStackTrace();}
    }



    public static void RoomComment(final Context thisContext,  final Map<String, String> param) {

        try{

            final String push_type = param.get(Define.MSG_TYPE);
            final String sender_id = param.get(Define.MSG_SENDER_ID);
            final String username  = param.get(Define.DB_USER_NM);
            final String ownerId  = param.get(Define.MSG_OWNER_ID);
            final String msg       = param.get(Define.MSG);
            final String roomId   = param.get(Define.DB_ROOM);

            if(push_type.equals(Define.MSG_TYPE_TIMELINE_INSERT_COMMENT)) {


                RoomComment comment = new RoomComment();
                comment.put("userId", sender_id);
                comment.put("content", msg);
                comment.put("userNm", username);
                comment.put("roomName", roomId);
                if(ParseUser.getCurrentUser().getParseObject(Define.UserInfo).get("thumbnailPath")!=null)
                    comment.put("thumbnailPath", ParseUser.getCurrentUser().getParseObject(Define.UserInfo).get("thumbnailPath"));

                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(thisContext,
                                    "Room Comment Saved!",
                                    Toast.LENGTH_SHORT).show();

                            Map<String, String> param = new HashMap<String, String>();

                            param.put(Define.ACTION, Define.ACTION_COMMENT);
                            param.put(Define.MSG_TYPE, Define.MSG_TYPE_TIMELINE_INSERT_COMMENT);
                            param.put(Define.DB_USER_ID, sender_id);
                            param.put(Define.PARAM_RECEIVER_LIST, "[" + ownerId + "]");
                            param.put(Define.RECEIVER_NAME, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).get(Define.DB_USER_FIRST_NM).toString());

                            ParseNetController.PushSend(thisContext, param);
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }


        }catch(Exception e) {e.printStackTrace();}
    }

    public static void RoomComment(final Context thisContext,  final Map<String, String> param, RoomComment comment) {

        try{

            final String push_type = param.get(Define.MSG_TYPE);
            final String sender_id = param.get(Define.MSG_SENDER_ID);
            final String msg       = param.get(Define.MSG);
            final String username  = param.get(Define.DB_USER_NM);
            final String ownerId  = param.get(Define.MSG_OWNER_ID);
            final String roomId   = param.get(Define.DB_ROOM);

            if(push_type.equals(Define.MSG_TYPE_TIMELINE_INSERT_COMMENT)) {

                comment.put("userId", sender_id);
                comment.put("content", msg);
                comment.put("userNm", username);
                comment.put("roomName", roomId);
                if(ParseUser.getCurrentUser().getParseObject(Define.UserInfo).get("thumbnailPath")!=null)
                    comment.put("thumbnailPath", ParseUser.getCurrentUser().getParseObject(Define.UserInfo).get("thumbnailPath"));

                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            Toast.makeText(thisContext,
                                    "Comment Saved!",
                                    Toast.LENGTH_SHORT).show();
                            Map<String, String> param = new HashMap<String, String>();

                            param.put(Define.ACTION, Define.ACTION_COMMENT);
                            param.put(Define.MSG_TYPE, Define.MSG_TYPE_TIMELINE_INSERT_COMMENT);
                            param.put(Define.DB_USER_ID, sender_id);
                            param.put(Define.PARAM_RECEIVER_LIST, "[" + ownerId + "]");
                            param.put(Define.RECEIVER_NAME, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).get(Define.DB_USER_FIRST_NM).toString());

                            ParseNetController.PushSend(thisContext, param);
                        }
                        else{
                            e.printStackTrace();
                        }
                    }
                });
            }

            if(push_type.equals(Define.MSG_TYPE_DELETE_COMMENT)) {

                comment.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {

                            ParseQuery<Room> room = Room.getQuery();
                            room.whereEqualTo(Define.DB_ROOM, roomId);
                            room.getFirstInBackground(new GetCallback<Room>() {
                                @Override
                                public void done(Room room, ParseException e) {
                                    if(e==null){
                                        int count = room.getInt("commentCount");
                                        room.put("commentCount", --count);
                                        room.saveInBackground();
                                    }
                                }
                            });
                            Toast.makeText(thisContext,
                                    "Comment Deleted!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }

        }catch(Exception e) {e.printStackTrace();}
    }



    public static void ActiveOnOff(final Context thisContext,  final String OnOff) {

        try{
            ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded();
            user.put(Define.DB_USER_ACTIVE, OnOff);
            user.saveInBackground();

        }catch(Exception e) {e.printStackTrace();}
    }


    public static void AddFriend(final Context thisContext,  final Map<String, String> param, final ParseObject friendShip ){


        final String msg_type = param.get(Define.MSG_TYPE);


        if(msg_type.equals(Define.MSG_TYPE_ADDFRIEND)) {

            try {
                final ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded();

                ParseObject friend = ParseObject.create("Friend");
                // Add friend and push
                friend.put(Define.DB_USER_ID, user.getString(Define.DB_USER_ID));
                friend.put(Define.DB_FRIEND_ID, friendShip.getString(Define.DB_USER_ID));
                friend.put("user_from_email"        , user.getString(Define.DB_EMAIL));
                friend.put("user_from_name"         , user.getString(Define.DB_USER_NM));
                friend.put("user_to_email"          , friendShip.getString(Define.DB_EMAIL));
                friend.put("user_to_name"           , friendShip.getString(Define.DB_USER_NM));
                friend.put(Define.FRIEND_STATUS, "sent");
                friend.put("isDraft", true);

                friend.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null) {
                            ParseObject friend_back = ParseObject.create("Friend");
                            // Add friend and push
                            friend_back.put(Define.DB_USER_ID, friendShip.getString(Define.DB_USER_ID));
                            friend_back.put(Define.DB_FRIEND_ID, user.getString(Define.DB_USER_ID));
                            friend_back.put("user_to_email", user.getString(Define.DB_EMAIL));
                            friend_back.put("user_to_name",  user.getString(Define.DB_USER_NM));
                            friend_back.put("user_from_email", friendShip.getString(Define.DB_EMAIL));
                            friend_back.put("user_from_name", friendShip.getString(Define.DB_USER_NM));
                            friend_back.put(Define.FRIEND_STATUS, "received");
                            friend_back.put("isDraft", true);

                            friend_back.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    if (e == null) {
                                        Toast.makeText(thisContext,
                                                "Friend Request!",
                                                Toast.LENGTH_LONG).show();
                                        AddFriendActivity.setTextSearch("");

                                        Map<String, String> param = new HashMap<String, String>();

                                        param.put(Define.ACTION, Define.ACTION_ADD_FRIEND);
                                        param.put(Define.MSG_TYPE, Define.MSG_TYPE_ADDFRIEND);
                                        param.put(Define.DB_USER_ID, user.getString(Define.DB_USER_ID));
                                        param.put(Define.PARAM_RECEIVER_LIST, "[" + friendShip.getString(Define.DB_USER_ID) + "]");
                                        param.put(Define.RECEIVER_NAME, friendShip.getString(Define.DB_USER_FIRST_NM).toString());

                                        ParseNetController.PushSend(thisContext, param);

                                    } else {
                                        Toast.makeText(thisContext,
                                                "Error saving: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(thisContext,
                                    "Error saving: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } catch (Exception e){ e.printStackTrace(); }

        }

    }

    public static void AddFriend(final Context thisContext,  final Map<String, String> param, final String friendShip, final String friendShip_firstname , final String fullname, final String email){


        final String msg_type = param.get(Define.MSG_TYPE);


        if(msg_type.equals(Define.MSG_TYPE_ADDFRIEND)) {

            try {
                final ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded();

                ParseObject friend = ParseObject.create("Friend");
                // Add friend and push
                friend.put(Define.DB_USER_ID, user.getString(Define.DB_USER_ID));
                friend.put(Define.DB_FRIEND_ID, friendShip);
                friend.put("user_from_email"        , user.getString(Define.DB_EMAIL));
                friend.put("user_from_name"         , user.getString(Define.DB_USER_NM));
                friend.put("user_to_email"          , email);
                friend.put("user_to_name"           , fullname);
                friend.put(Define.FRIEND_STATUS, "sent");
                friend.put("isDraft", true);

                friend.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null) {
                            ParseObject friend_back = ParseObject.create("Friend");
                            // Add friend and push
                            friend_back.put(Define.DB_USER_ID, friendShip);
                            friend_back.put(Define.DB_FRIEND_ID, user.getString(Define.DB_USER_ID));
                            friend_back.put("user_to_email", user.getString(Define.DB_EMAIL));
                            friend_back.put("user_to_name",  user.getString(Define.DB_USER_NM));
                            friend_back.put("user_from_email" , email);
                            friend_back.put("user_from_name"   , fullname);
                            friend_back.put(Define.FRIEND_STATUS, "received");
                            friend_back.put("isDraft", true);

                            friend_back.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    if (e == null) {
                                        Toast.makeText(thisContext,
                                                "Friend Request!",
                                                Toast.LENGTH_LONG).show();
                                        AddFriendActivity.setTextSearch("");

                                        Map<String, String> param = new HashMap<String, String>();

                                        param.put(Define.ACTION, Define.ACTION_ADD_FRIEND);
                                        param.put(Define.MSG_TYPE, Define.MSG_TYPE_ADDFRIEND);
                                        param.put(Define.DB_USER_ID, user.getString(Define.DB_USER_ID));
                                        param.put(Define.PARAM_RECEIVER_LIST, "[" + friendShip + "]");
                                        param.put(Define.RECEIVER_NAME, friendShip_firstname);

                                        ParseNetController.PushSend(thisContext, param);

                                    } else {
                                        Toast.makeText(thisContext,
                                                "Error saving: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(thisContext,
                                    "Error saving: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } catch (Exception e){ e.printStackTrace(); }

        }

    }



    static  String path="";

    public static void AddFriend(final Context thisContext,  final Map<String, String> param, final Friend friend){


        final String msg_type = param.get(Define.MSG_TYPE);

        if(msg_type.equals(Define.MSG_TYPE_CONFIRMFRIEND)) {


            friend.saveInBackground(
                    new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) {
                                Toast.makeText(thisContext,
                                        "Confirmed!",
                                        Toast.LENGTH_LONG).show();

                                ParseQuery<UserInfo> user = UserInfo.getQuery();
                                user.whereEqualTo(Define.DB_USER_ID, friend.getUserId());
                                user.getFirstInBackground(new GetCallback<UserInfo>() {
                                    @Override
                                    public void done(UserInfo parseUser, ParseException e) {
                                        if (e == null) {
                                            if (TextUtils.isEmpty(parseUser.getString("thumbnailPath"))) {
                                                path = "";
                                            } else path = parseUser.get("thumbnailPath").toString();
                                        } else {
                                            e.printStackTrace();
                                            ;
                                        }
                                    }
                                });

                                Notification noti = new Notification();
                                noti.put("type", "confirmfriend");
                                noti.put("userId", friend.getUserId());
                                noti.put("userNm", friend.getUser_from_name());
                                noti.put("friendId", ParseUser.getCurrentUser().getString(Define.DB_USER_ID));
                                noti.put("thumbnailPath", path);
                                noti.put("content", " accepted your friend request.");
                                noti.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo);
                                            user.increment("badge_count");
                                            user.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) callBadgeCount(thisContext, false);
                                                    else {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        } else e.printStackTrace();
                                    }
                                });

                            } else {
                                Toast.makeText(thisContext,
                                        "Error saving: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });


            ParseQuery<Friend> query = ParseQuery.getQuery("Friend");

            query.whereEqualTo("userId", friend.getFriendId());
            query.whereEqualTo("friendId", friend.getUserId());

//            query.whereEqualTo("user_from_name", friend.getUser_to_name());
//            query.whereEqualTo("user_to_name", friend.getUser_from_name());
            query.getFirstInBackground(new GetCallback<Friend>() {
                public void done(Friend object, ParseException e) {
                    if (e == null) {
                        // object will be your game score
                        object.setFriend_status("friend");
                        object.saveInBackground();

                        /////////Counting////////////////
                        /*ParseQuery<ParseUser> user = ParseUser.getQuery();
                        List<String> user_name = new ArrayList<String>();

                        user_name.add(friend.getUserId());
                        user_name.add(friend.getFriendId());
                        user.whereContainedIn("objectId", user_name);
                        user.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> list, ParseException e) {
                                if(e==null){
                                    int i;
                                    for(i=0; i< list.size(); i++) {
                                        list.get(i).increment("friendTotal");
                                        list.get(i).saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if(e!=null) e.printStackTrace();
                                            }
                                        });
                                    }
                                }else e.printStackTrace();
                            }
                        });*/


                        Toast.makeText(thisContext,
                                "Friend!" + object.getUser_to_name(),
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(thisContext,
                                "Error saving: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        // something went wrong
                    }
                }
            });

        }

        if(msg_type.equals(Define.MSG_TYPE_DELETEFRIEND)) {

            friend.deleteInBackground();

            ParseQuery<Friend> query = ParseQuery.getQuery("Friend");
            //query.whereEqualTo("user_from_name", friend.getUser_to_name());
            //query.whereEqualTo("user_to_name", friend.getUser_from_name());

            query.whereEqualTo("userId", friend.getFriendId());
            query.whereEqualTo("friendId", friend.getUserId());

            query.getFirstInBackground(new GetCallback<Friend>() {
                public void done(Friend object, ParseException e) {
                    if (e == null) {
                        // object will be your game score
                        Toast.makeText(thisContext,
                                "Rejected!" + object.getUser_to_name(),
                                Toast.LENGTH_LONG).show();
                        object.deleteEventually();

                        /*if (friend.getFriend_status().equals("friend")) {
                            ParseQuery<ParseUser> user = ParseUser.getQuery();
                            List<String> user_name = new ArrayList<String>();

                            user_name.add(friend.getUserId());
                            user_name.add(friend.getFriendId());
                            user.whereContainedIn("objectId", user_name);

                            user.findInBackground(new FindCallback<ParseUser>() {
                                @Override
                                public void done(List<ParseUser> list, ParseException e) {
                                    int i;
                                    for (i = 0; i < list.size(); i++) {
                                        int count = list.get(i).getInt("friendTotal");
                                        list.get(i).put("friendTotal", count - 1);
                                        list.get(i).saveInBackground();
                                    }
                                }
                            });
                        }*/


                    } else {
                        Toast.makeText(thisContext,
                                "Error saving: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        // something went wrong
                    }
                }
            });
        }

    }


    public static void AddFriend(final Context thisContext,  final Map<String, String> param){

        final String userId      = param.get(Define.USER_ID);
        final String friendId    = param.get(Define.FRIEND_ID);

        final String msg_type = param.get(Define.MSG_TYPE);

        if(msg_type.equals(Define.MSG_TYPE_CONFIRMFRIEND)) {

            ParseQuery<Friend> friend = Friend.getQuery();
            friend.whereEqualTo("userId"        , userId);
            friend.whereEqualTo("friendId", friendId);
            friend.getFirstInBackground(new GetCallback<Friend>() {
                @Override
                public void done(Friend friend, ParseException e) {
                    if (e == null) {
                        friend.put("status", "friend");
                        friend.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                    ParseQuery<Friend> friendback = Friend.getQuery();
                                    friendback.whereEqualTo("userId", friendId);
                                    friendback.whereEqualTo("friendId", userId);
                                    friendback.getFirstInBackground(new GetCallback<Friend>() {
                                        @Override
                                        public void done(Friend friend, ParseException e) {
                                            if (e == null) {
                                                friend.put("status", "friend");
                                                friend.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            Toast.makeText(thisContext,
                                                                    "Confirmed!",
                                                                    Toast.LENGTH_LONG).show();
                                                            Notification noti = new Notification();
                                                            noti.put("type", "confirmfriend");
                                                            noti.put("userId", ParseUser.getCurrentUser().getString(Define.DB_USER_ID));
                                                            noti.put("userNm", ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_USER_NM));
                                                            noti.put("friendId", friendId);
                                                            noti.put("content", ParseUser.getCurrentUser().getUsername() + " accepted your friend request.");
                                                            //noti.put("thumbnailPath", (ParseUser.getCurrentUser().get("thumbnailPath") == null ? "" : ParseUser.getCurrentUser().get("thumbnailPath").toString()));
                                                            noti.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if (e == null) {
                                                                        ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo);
                                                                        user.increment("badge_count");
                                                                        user.saveInBackground(new SaveCallback() {
                                                                            @Override
                                                                            public void done(ParseException e) {
                                                                                if (e == null)
                                                                                    callBadgeCount(thisContext, false);
                                                                                else {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else e.printStackTrace();
                                                                }
                                                            });


                                                            /*ParseQuery<ParseUser> user = ParseUser.getQuery();
                                                            List<String> user_name = new ArrayList<String>();
                                                            user_name.add(userId);
                                                            user_name.add(friendId);
                                                            user.whereContainedIn("objectId", user_name);
                                                            user.findInBackground(new FindCallback<ParseUser>() {
                                                                @Override
                                                                public void done(List<ParseUser> list, ParseException e) {
                                                                    int i;
                                                                    for (i = 0; i < list.size(); i++) {
                                                                        list.get(i).increment("friendTotal");
                                                                        list.get(i).saveInBackground();
                                                                    }
                                                                }
                                                            });*/


                                                            Map<String, String> params = new HashMap<String, String>();

                                                            params.put(Define.ACTION, Define.ACTION_PULL);
                                                            params.put(Define.MSG_TYPE, Define.MSG_TYPE_ADDFRIENDREFLY);
                                                            params.put(Define.MSG_SENDER_ID, userId);
                                                            params.put(Define.PARAM_RECEIVER_LIST, "[" + friendId + "]");
                                                            params.put(Define.PARAM_ANONYMOUS, "n");

                                                            PushSend(thisContext, params);
                                                        }
                                                    }
                                                });

                                            } else e.printStackTrace();
                                        }
                                    });


                                } else e.printStackTrace();
                            }
                        });
                    } else e.printStackTrace();
                }
            });

        }

    }



    public static void SignUp(final Context thisContext, final Map<String, String> param){


        try {

            final String email      = param.get(Define.DB_EMAIL);
            final String passwd     = param.get(Define.DB_PASSWD);
            final String firstName  = param.get(Define.DB_USER_FIRST_NM);
            final String lastName   = param.get(Define.DB_USER_LAST_NM);
            final String user_nm    = firstName+" "+lastName;

            final String randStr32 =  new RandomStringBuilder().
                    putLimitedChar(RandomStringBuilder.ALPHABET).
                    setLength(32).build();

            final String randStr10 =  new RandomStringBuilder().
                    putLimitedChar(RandomStringBuilder.ALPHABET).
                    setLength(10).build();

            final String userId = thisContext.getString(R.string.app_name) + "_"+ System.currentTimeMillis() +randStr10;


            /////////////////////////////////////////USERInfo 입력 ///////////////////////////

            final UserInfo userInfo = new UserInfo();

            userInfo.put(Define.DB_USER_NM, user_nm);
            userInfo.put(Define.DB_USER_ID, userId);
            userInfo.put(Define.DB_EMAIL, email);
            userInfo.put(Define.DB_USER_FIRST_NM, firstName);
            userInfo.put(Define.DB_USER_LAST_NM, lastName);

            userInfo.put(Define.FriendStatus, true);
            userInfo.put(Define.CommentStatus, true);
            userInfo.put(Define.CommentPost, true);

            userInfo.put(Define.DB_USER_ACTIVE , "y");
            userInfo.put(Define.USER_MODE  , Define.USER_MODE_SIGNIN);

            userInfo.put(Define.USER_PUSH_ID, ParseInstallation.getCurrentInstallation().getObjectId());
            userInfo.put(Define.DB_INSTALLATION_ID, ParseInstallation.getCurrentInstallation());

            userInfo.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {

                        final ParseUser user = new ParseUser();

                        user.setEmail(email);
                        user.setUsername(email);
                        user.setPassword(passwd);

                        user.put(Define.SESSION, randStr32);
                        user.put(Define.DB_USER_ID, userId);
                        user.put(Define.DB_USER_FIRST_NM, firstName);
                        user.put(Define.DB_USER_LAST_NM, lastName);
                        user.put(Define.DB_USER_INFO_ID, userInfo);

                        user.signUpInBackground(new SignUpCallback() {
                            public void done(ParseException e) {
                                if (e == null) {

                                    ////////////////Token Key 입력//////////////////////////////////////////////////////

                                /*NetController.getInstance(thisContext)
                                .getRequestQueue()
                                .add(NetController.getTokenKey(thisContext, randStr32));*/
                                    ///////////////////////////////////////////////////////////////////////////////////

                                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                    installation.put(Define.DB_USER_ID, ParseUser.getCurrentUser().getString(Define.DB_USER_ID));
                                    installation.saveInBackground();

                                    SharedPreferences pref = RSPreference.getPreference(thisContext);

                                    SharedPreferences.Editor editor = pref.edit();

                                    editor.putString(Define.USER_ID, ParseUser.getCurrentUser().getString(Define.DB_USER_ID));
                                    editor.putString(Define.USER_NM, user_nm);
                                    editor.putString(Define.DB_USER_FIRST_NM, firstName);
                                    editor.putString(Define.DB_USER_LAST_NM, lastName);
                                    editor.putString(Define.USER_EMAIL, email);

                                    editor.apply();

                                    Toast.makeText(thisContext.getApplicationContext(), "Sign Up Sucess!", Toast.LENGTH_SHORT).show();
                                    moveToFindFriends(thisContext);

                                } else {
                                    e.printStackTrace();
                                    Toast.makeText(thisContext.getApplicationContext(), "Sign Up Failed!", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    } else
                        e.printStackTrace();
                }
            });

        }catch(Exception e) { e.printStackTrace(); }

    }


    public static void SignOut(final Context thisContext){


        try {
            SharedPreferences pref = RSPreference.getPreference(thisContext);
            SharedPreferences.Editor editor = pref.edit();
            editor.remove(Define.USER_ID);
            editor.remove(Define.USER_EMAIL);
            editor.remove(Define.USER_PASSWD);
            editor.remove(Define.USER_GCM_ID);

            editor.apply();

            Toast.makeText(thisContext.getApplicationContext(), "Sign-Out Success", Toast.LENGTH_SHORT).show();

            ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded();
            user.put(Define.DB_ROOM          ,   "");
            user.put(Define.USER_MODE       , Define.ACTION_SIGNOUT);
            user.put(Define.DB_USER_ACTIVE , "n");
            user.put(Define.USER_PUSH_ID, "");

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {


                        ParseUser.logOut();
                        LoginManager.getInstance().logOut();
                        callBadgeCount(thisContext, true);
                    } else
                        e.printStackTrace();
                }
            });


            Intent loginIntent = new Intent(thisContext.getApplicationContext(), LoginActivity.class);
            thisContext.startActivity(loginIntent);

            ((Activity) thisContext).finish();

        }catch(Exception e) { e.printStackTrace(); }

    }

    public static void Status(final Context thisContext, final Map<String, String> param ){

        try {

            final String status    = param.get(Define.DB_STATUS);

            ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo);
            user.put(Define.DB_STATUS          , status);
            user.put("statusUpdateDate", new Date(System.currentTimeMillis()));
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(thisContext, "Status is updated", Toast.LENGTH_SHORT).show();
                        PushSend(thisContext, param);
                    } else
                        e.printStackTrace();
                }
            });

        }catch (Exception e) {e.printStackTrace();}
    }

    public static void SignIn(final Context thisContext, final Map<String, String> param ){

        try {

            final String username     = param.get(Define.DB_EMAIL);
            final String passwd    = param.get(Define.DB_PASSWD);
            final String msgType   = param.get(Define.MSG_TYPE);



            ParseUser.logInInBackground(username, passwd, new
                    LogInCallback() {

                        public void done(ParseUser user, com.parse.ParseException e) {
                            if (user != null && e == null) {

                                //To do: friend_count==0
                                if (msgType == null || msgType.isEmpty()) {
                                    //        moveToMain(context, user_status);
                                    //moveToAddFriends(context, user_status);
                                    try {
                                        ParseObject login_user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded();

                                        login_user.put(Define.USER_MODE, "signin");
                                        login_user.put(Define.DB_USER_ACTIVE, "y");
                                        login_user.put(Define.DB_ROOM, "");
                                        login_user.put(Define.USER_PUSH_ID, ParseInstallation.getCurrentInstallation().getObjectId());

                                        Log.d("push_id: ", ParseInstallation.getCurrentInstallation().getObjectId());
                                        login_user.saveInBackground();

                                        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                        installation.put(Define.DB_USER_ID, login_user.getString(Define.DB_USER_ID));
                                        installation.saveInBackground();

                                        Toast.makeText(thisContext.getApplicationContext(), "Login Sucess!", Toast.LENGTH_SHORT).show();
                                        moveToFindFriends(thisContext);
                                    }catch(Exception e5){e5.printStackTrace();}

                                }
                                // Hooray! The user is logged in.
                            } else {

                                // Signup failed. Look at the ParseException to see what happened.
                                e.printStackTrace();
                                Toast.makeText(thisContext.getApplicationContext(), "Login Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }catch(Exception e) {e.printStackTrace();}
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

        Intent mainIntent = new Intent( context, AddFriendActivity.class );
        if( msgType != null && !msgType.isEmpty() ) {
            mainIntent.putExtra(Define.MSG_TYPE, msgType);
            mainIntent.putExtra(Define.MSG_SENDER_ID, senderId);
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

        Intent mainIntent = new Intent( context, FindFriendWithFaceBookActivity.class );
        if( msgType != null && !msgType.isEmpty() ) {
            mainIntent.putExtra(Define.MSG_TYPE, msgType);
            mainIntent.putExtra(Define.MSG_SENDER_ID, senderId);

        }

        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(mainIntent);
        ((Activity)context).finish();
    }

    public static void moveToFriends(Context context, ArrayList<String> facebook_friend){
        Intent intent = ((Activity)context).getIntent();
        String msgType = intent.getStringExtra(Define.MSG_TYPE);
        String senderId = intent.getStringExtra(Define.MSG_SENDER_ID);

        Intent mainIntent = new Intent( context, AddFriendWithFaceBookActivity.class );
        if( msgType != null && !msgType.isEmpty() ) {
            mainIntent.putExtra(Define.MSG_TYPE, msgType);
            mainIntent.putExtra(Define.MSG_SENDER_ID, senderId);
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


        Intent mainIntent = new Intent( context, NotificationSettingActivity.class );
        if( msgType != null && !msgType.isEmpty() ) {
            mainIntent.putExtra(Define.MSG_TYPE, msgType);
            mainIntent.putExtra(Define.MSG_SENDER_ID, senderId);

        }
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(mainIntent);
        ((Activity)context).finish();
    }

    public static void callBadgeCount(Context context, boolean isLogout){
        Intent intents = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        // 패키지 네임과 클래스 네임 설정
        intents.putExtra("badge_count_package_name", context.getPackageName());
        intents.putExtra("badge_count_class_name", context.getClass().getName());
        // 업데이트 카운트
        if(!isLogout)
            intents.putExtra("badge_count", ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getInt("badge_count"));
        else
            intents.putExtra("badge_count", 0);
        context.sendBroadcast(intents);
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

    private static JSONObject getPushMessage(final Map<String, String> param){

        final String push_type      =  param.get(Define.MSG_TYPE);
        final String callerActivity = param.get(Define.CALLER_ACTIVITY);
        final String pull_from      = param.get(Define.PARAM_PULL_FROM);
        final String title          = param.get(Define.TITLE);

        Log.d("getPushMsg", push_type);
        JSONObject message = new JSONObject();


        try{
            String username = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_USER_NM);
            String userId  =  ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_USER_ID);
            String userstatus =  TextUtils.isEmpty(ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_STATUS)) ==true  ? ""
                                : ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_STATUS);
            StringBuffer push_msg = new StringBuffer();

            if ( push_type.equals("pull") ) {

                push_msg.append( username + " requested for you to start a live stream.");

            } else if ( push_type.equals("publish") ) {

                push_msg.append(username + " started a live stream.");
                if ( pull_from!=null && !pull_from.equals("") ) {
                    push_msg.append("\n( pulled by " + pull_from +")");
                }

            } else if ( push_type.equals("invite")) {

                push_msg.append( username + " has invited you to a private stream.");

                if ( pull_from!=null && !pull_from.equals("") ) {
                    push_msg.append("\n( pulled by " + pull_from +")");
                }

            } else if ( push_type.equals("status") ) {

                push_msg.append(username +  " changed status to " + userstatus);
            }
            else if (push_type.equals("insert_comment") ) {

                push_msg.append( username + " commented on your status.");
            }
            else if (push_type.equals("timeline_insert_comment") ) {

                push_msg.append( username + " commented on your video."  + (title==null ? "" : title));
            }
            else if (push_type.equals("addfriend")) {

                push_msg.append(username + " sent you a friend request. ");
            }
            else if ( push_type.equals("addfriendreply")) {
                push_msg.append( username + "  accepted your friend request.");
            }

            message.put("alert", push_msg.toString());
            message.put("title", "LIVEO");
            message.put("push_type", push_type);
            message.put("push_msg", push_msg.toString());
            message.put("push_title"     , "LIVEO");
            message.put("sender_id"      , userId);
            message.put("sender_status", userstatus);
            message.put("className", callerActivity);
            message.put("sender_name", username);

         }catch (Exception exception){exception.printStackTrace();}

        return message;
    }


    public static void PushSend(final Context context, final Map<String, String> param){

        final String push_type      =  param.get(Define.MSG_TYPE);
        final String firstname      = param.get(Define.RECEIVER_NAME);
        final String allow_list     = param.get(Define.PARAM_RECEIVER_LIST);


        try{

            ParseQuery parseFriend = Friend.getQuery();
            ParseQuery parseUser   = UserInfo.getQuery();
            ParseQuery pushQuery = ParseInstallation.getQuery();


            parseFriend.whereEqualTo(Define.DB_FRIEND_ID, ParseUser.getCurrentUser().getString(Define.DB_USER_ID));
            parseUser.whereMatchesKeyInQuery(Define.DB_USER_ID, Define.DB_USER_ID, parseFriend);
            //parseUser.whereMatchesQuery(Define.DB_USER_ID, parseFriend);

            if(push_type.equals(Define.MSG_TYPE_STATUS))                     parseUser.whereEqualTo(Define.FriendStatus   , true);
            if(push_type.equals(Define.MSG_TYPE_TIMELINE_INSERT_COMMENT)) parseUser.whereEqualTo(Define.CommentPost    , true);
            if(push_type.equals(Define.MSG_TYPE_INSERT_COMMENT))            parseUser.whereEqualTo(Define.CommentStatus , true);

            pushQuery.whereMatchesKeyInQuery(Define.DB_OBJECT_ID, Define.USER_PUSH_ID, parseUser);
            //pushQuery.whereMatchesQuery("user", parseUser);


            if(push_type.equals(Define.ACTION_ROOM_PRIVATE)) {
                JSONArray receiveList = new JSONArray(allow_list);
                pushQuery.whereContainedIn(Define.DB_USER_ID, Arrays.asList(receiveList));
            }

            if(push_type.equals(Define.MSG_TYPE_PULL) || push_type.equals(Define.MSG_TYPE_ADDFRIEND) || push_type.equals(Define.MSG_TYPE_INSERT_COMMENT) || push_type.equals(Define.MSG_TYPE_TIMELINE_INSERT_COMMENT)) {
                JSONArray receiveList = new JSONArray(allow_list);
                pushQuery.whereEqualTo(Define.DB_USER_ID, receiveList.get(0));
//                pushQuery.whereContainedIn(Define.DB_USER_ID, Arrays.asList(receiveList));
            }

            // Send push notification to query
            ParsePush push = new ParsePush();
            push.setQuery(pushQuery); // Set our Installation query

            final JSONObject message = getPushMessage(param);

            push.setData(message);

            push.sendInBackground(new SendCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        if (push_type.equals(Define.MSG_TYPE_PULL)) {
                            showCustomToast(context, firstname, Toast.LENGTH_SHORT);
                        }
                        Log.d("push", "The push has been created.");
                    } else {
                        Log.d("push", "Error sending push:" + e.getMessage());
                    }
                }
            });
        }catch (Exception exception){exception.printStackTrace();}
    }

    private static String makeRoomName(){

        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
        StringBuffer room_name = new StringBuffer();
        room_name.append("room_");
        room_name.append(sdfNow.format(new Date(System.currentTimeMillis())));
        room_name.append("_");
        room_name.append(ParseUser.getCurrentUser().getString(Define.DB_USER_ID));

        return room_name.toString();
    }

    private static String makeRoomName_Vparka(){

        StringBuffer room_name = new StringBuffer();
        room_name.append(ParseUser.getCurrentUser().getString(Define.DB_USER_ID));
        room_name.append("_");
        room_name.append(System.currentTimeMillis());

        return room_name.toString();
    }

    public static void Room(final Context context, final Map<String, String> param){

        final String action         =  param.get(Define.ACTION);
        final String push_type      =  param.get(Define.MSG_TYPE);
        final String callerActivity = param.get(Define.CALLER_ACTIVITY);
        final String user_id        = param.get(Define.DB_USER_ID);
        final String owner_id       = param.get(Define.OWNER_ID);
        final String ismyapp        = param.get(Define.IS_MY_APP);
        final String room_name      = param.get(Define.DB_ROOM);
        final String allow_list     = param.get(Define.PARAM_ALLOW_LIST);
        final String pull_from      = param.get(Define.PARAM_PULL_FROM);

        try {

            if (action.equals(Define.ACTION_ROOM_CLOSE)) {

                ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded();
                user.put(Define.DB_ROOM, "");
                user.put(Define.USER_MODE, Define.USER_MODE_SIGNIN);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            ParseQuery<RoomAllowed> query = RoomAllowed.getQuery();
                            query.whereEqualTo(Define.DB_USER_ID, ParseUser.getCurrentUser().getString(Define.DB_USER_ID));
                            query.findInBackground(new FindCallback<RoomAllowed>() {
                                @Override
                                public void done(List<RoomAllowed> list, ParseException e) {

                                    if (e == null) {
                                        int index;
                                        for (index = 0; index < list.size(); index++) {
                                            list.get(index).deleteInBackground();
                                        }
                                        closeEvent(context, room_name, callerActivity);
                                    }
                                }
                            });
                        } else {
                            e.printStackTrace();
                        }
                    }
                });


            } else if (action.equals(Define.ACTION_ROOM_PUBLIC)) {

                //final String room_names = makeRoomName();

                final String room_names = makeRoomName_Vparka();

                SharedPreferences pref = RSPreference.getPreference(context);
                SharedPreferences.Editor editor = pref.edit();

                editor.putString(Define.MSG_TYPE, push_type);
                editor.putString(Define.CALLER_ACTIVITY, callerActivity);
                editor.putString(Define.PARAM_PULL_FROM, pull_from);
                editor.putString(Define.PARAM_RECEIVER_LIST, allow_list);

                editor.apply();

                ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded();

                user.put(Define.DB_ROOM, room_names);
                user.put(Define.USER_MODE, Define.ACTION_ROOM_PUBLIC);

                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                          //  PushSend(context, param);
                            publicEvent(context, room_names, callerActivity);
                        } else {
                            Toast.makeText(context.getApplicationContext(), "You are not authenticated user!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });

            } else if (action.equals(Define.ACTION_ROOM_PRIVATE)) {

                SharedPreferences pref = RSPreference.getPreference(context);
                SharedPreferences.Editor editor = pref.edit();

                editor.putString(Define.MSG_TYPE, push_type);
                editor.putString(Define.CALLER_ACTIVITY, callerActivity);
                editor.putString(Define.PARAM_PULL_FROM, pull_from);
                editor.putString(Define.PARAM_RECEIVER_LIST, allow_list);

                editor.apply();

                final String room_names = makeRoomName_Vparka();
                try {
                    JSONArray receiveList = new JSONArray(allow_list);
                    int i;
                    RoomAllowed user = new RoomAllowed();
                    for (i = 0; i < receiveList.length(); i++) {
                        user.put(Define.DB_ROOM_ID    ,   room_names);
                        user.put(Define.DB_USER_ID    , ParseUser.getCurrentUser().getString(Define.DB_USER_ID));
                        user.put(Define.DB_FRIEND_ID, receiveList.get(i).toString());
                        user.saveInBackground();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded();

                user.put(Define.DB_ROOM, room_names);
                user.put(Define.USER_MODE, Define.ACTION_ROOM_PRIVATE);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            //PushSend(context, param);
                            privateEvent(context, room_names, callerActivity);
                        } else e.printStackTrace();

                    }
                });


            } else if (action.equals(Define.ACTION_ROOM_WATCH)) {

                if (push_type.equals(Define.MSG_TYPE_PUBLISH)) {
                    ParseQuery<UserInfo> owner = UserInfo.getQuery();
                    owner.whereEqualTo(Define.DB_USER_ID, owner_id);
                    owner.getFirstInBackground(new GetCallback<UserInfo>() {
                        @Override
                        public void done(final UserInfo parseUser, ParseException e) {
                            try {
                                if(e==null) {
                                    ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded();
                                    user.put(Define.DB_ROOM, parseUser.get(Define.DB_ROOM));
                                    user.put(Define.USER_MODE, Define.ACTION_ROOM_WATCH);
                                    user.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {

                                            watchEvent(context, callerActivity, owner_id, parseUser.get(Define.DB_ROOM).toString());
                                            }
                                    });
                                }
                                else {
                                    Toast.makeText(context.getApplicationContext(), "No Live Stream!", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }catch(Exception ee){ee.printStackTrace(); }
                        }
                    });

                }

                if (push_type.equals(Define.MSG_TYPE_INVITE)) {
                    ParseQuery<RoomAllowed> query = RoomAllowed.getQuery();
                    query.whereEqualTo(Define.DB_USER_ID   , owner_id);
                    query.whereEqualTo(Define.DB_FRIEND_ID, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_USER_ID));

                    Log.d("room_owner_id", owner_id);
                    Log.d("allowed_user_id", user_id);

                    query.getFirstInBackground(new GetCallback<RoomAllowed>() {
                        @Override
                        public void done(RoomAllowed roomAllowed, ParseException e) {
                            if (e == null) {
                                ParseQuery<UserInfo> owner = UserInfo.getQuery();
                                owner.whereEqualTo(Define.DB_USER_ID, owner_id);
                                owner.getFirstInBackground(new GetCallback<UserInfo>() {
                                    @Override
                                    public void done(final UserInfo parseUser, ParseException e) {
                                        try {
                                            ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded();
                                            user.put(Define.DB_ROOM, parseUser.get(Define.DB_ROOM));
                                            user.put(Define.USER_MODE, Define.ACTION_ROOM_WATCH);
                                            user.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {

                                                    watchEvent(context, callerActivity, owner_id, parseUser.get(Define.DB_ROOM).toString());
                                                }
                                            });
                                        }catch (Exception e1){e1.printStackTrace();}
                                    }
                                });
                            } else {
                                Toast.makeText(context.getApplicationContext(), "Not Allowed User!", Toast.LENGTH_SHORT).show();
                                //showCustomToast(context, "Not Allowed User!", Toast.LENGTH_SHORT);
                                e.printStackTrace();
                            }
                        }
                    });
                }

            } else if (action.equals(Define.ACTION_ROOM_LEAVE)) {

                ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded();
                user.put(Define.DB_ROOM, "");
                user.put(Define.USER_MODE, Define.USER_MODE_SIGNIN);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            leaveEvent(context, callerActivity, ismyapp);
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }catch(Exception e ){ e.printStackTrace(); }
    }


    public static void TimeLine(final Context context, final Map<String, String> param){

        final String action           =  param.get(Define.ACTION);
        final String push_type        =  param.get(Define.MSG_TYPE);
        final String room_type        =  param.get(Define.MSG_ROOM);
        final String title            =  param.get(Define.TITLE);
        final String callerActivity   = param.get(Define.CALLER_ACTIVITY);
        final String user_id          = param.get(Define.DB_USER_ID);
        final String owner_id         = param.get(Define.OWNER_ID);
        final String ismyapp          = param.get(Define.IS_MY_APP);
        final String room_name        = param.get(Define.DB_ROOM);
        final String broadcastStatus  = param.get(Define.BROAD_STATUS);
        final String allow_list     = param.get(Define.PARAM_ALLOW_LIST);
        final String record_url     = param.get(Define.MSG_RECORD_URL);
        final String thumbnailPath  = param.get(Define.THUMBNAILPATH);
        final String pull_from      = param.get(Define.PARAM_PULL_FROM);
        final String recordTime      = param.get(Define.RECORDTIME);

        try {
            if (room_type.equals(Define.MSG_WATCH_ROOM)) {

                ParseQuery<Room> user = Room.getQuery();
                user.whereEqualTo(Define.DB_ROOM, room_name);
                user.whereEqualTo("status", "live");
                user.getFirstInBackground(new GetCallback<Room>() {
                    @Override
                    public void done(Room room, ParseException e) {
                        if(e==null) {
                            room.increment("viewCount");
                            room.saveInBackground();
                        }
                        else e.printStackTrace();
                    }
                });
            }

            if (room_type.equals(Define.MSG_ROOM_END)) {
                ParseQuery<Room> user = Room.getQuery();
                user.whereEqualTo(Define.DB_ROOM, room_name);

                user.getFirstInBackground(new GetCallback<Room>() {
                    @Override
                    public void done(Room room, ParseException e) {
                        if (e == null) {
                            room.put("status", broadcastStatus);
                            room.put("thumbnailPath", thumbnailPath);
                            room.put(Define.RECORDTIME     ,  Integer.parseInt(recordTime));
                            room.saveInBackground();
                        } else e.printStackTrace();
                    }
                });
            }
            if (room_type.equals(Define.MSG_ROOM_START)) {
                Room user = new Room();
                try {
                    String userstatus = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_STATUS) == null ? "" : ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_STATUS);
                    String profilePic = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).get("thumbnailPath")==null ? "" : ParseUser.getCurrentUser().getParseObject(Define.UserInfo).get("thumbnailPath").toString();

                    user.put("userId", owner_id);
                    user.put("title", title);
                    user.put("type", push_type);
                    user.put("roomName", room_name);
                    user.put("userName", ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_USER_NM));
                    user.put("profilePath"   , profilePic);
                    user.put("userStatus", userstatus);
                    user.put("status", broadcastStatus);
                    user.put("videoPath", record_url);
                    user.put("thumbnailPath", thumbnailPath);
                    user.put("user", ParseUser.getCurrentUser().getParseObject(Define.UserInfo));
                    user.increment("viewCount");

                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                PushSend(context, param);

                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e ) { e.printStackTrace(); }
    }


    public static void closeEvent(Context context, String room_name, String callerActivity){
            Log.d(LogTag, "caller " + callerActivity);

            if( callerActivity == null || !callerActivity.contains("MainActivity") ) {
                // 다른 경로로 들어 옴
                Intent mainIntent = new Intent(context.getApplicationContext(), MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(mainIntent);
            }

            ((Activity)context).finish();

        }
    private static void publicEvent(Context context, String room_name , String callerActivity){
        Log.d(LogTag, "caller " + callerActivity);

        Intent broadcastIntent = new Intent( context.getApplicationContext(), BroadcastActivity.class );
        broadcastIntent.putExtra(Define.CALLER_ACTIVITY, callerActivity);
        broadcastIntent.putExtra(Define.DB_ROOM, room_name);

        context.startActivity(broadcastIntent);

        if( callerActivity == null || !callerActivity.contains("MainActivity") ) {
            // 다른 경로로 들어 옴
            ((Activity)context).finish();
        }
    }
    private static void privateEvent(Context context, String room_name , String callerActivity){
        Log.d(LogTag, "caller " + callerActivity);

        Intent broadcastIntent = new Intent( context.getApplicationContext(), BroadcastActivity.class );
        broadcastIntent.putExtra(Define.CALLER_ACTIVITY, callerActivity);
        broadcastIntent.putExtra(Define.DB_ROOM, room_name);
        context.startActivity(broadcastIntent);

        if( callerActivity == null || !callerActivity.contains("MainActivity") ) {
            // 다른 경로로 들어 옴
            ((Activity)context).finish();
        }
    }
    private static void leaveEvent(Context context,  String callerActivity, String ismyapp){
        Log.d(LogTag, "caller " + callerActivity + " : " + ismyapp);

        if( callerActivity == null || !callerActivity.contains("MainActivity") ) {

            // 다른 경로로 들어 옴
            Intent mainIntent = new Intent(context.getApplicationContext(), MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(mainIntent);
        }

        ((Activity)context).finish();
    }
    private static void watchEvent(Context context,  String callerActivity, String owner_id, String roomname){
        Log.d(LogTag, "caller " + callerActivity);

        Intent watchIntent = new Intent( context, WatchActivity.class );

        watchIntent.putExtra(Define.OWNER_ID, owner_id);
        watchIntent.putExtra(Define.DB_ROOM , roomname);
        watchIntent.putExtra(Define.CALLER_ACTIVITY, callerActivity);

        context.startActivity(watchIntent);

        if( callerActivity == null || !callerActivity.contains("MainActivity") ) {
            // 다른 경로로 들어 옴
            ((Activity)context).finish();
        }
    }


    private static void showCustomToast(Context context, String msg, int duration){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.result_toast, null);

        TextView msgView = (TextView)layout.findViewById(R.id.text);
        msgView.setText(msg + ", wassup?");
        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }

    private static AsyncHttpClient oAsyncHttpClient = new AsyncHttpClient();

    public static String uploadFileToS3(final Context thisContext, File vMessage) {

        String url="";
        String voiceUploadURL  = "https://s3-us-west-1.amazonaws.com/s3.liveo.me/";
        RequestParams params = new RequestParams();

        try {

            SharedPreferences pref = RSPreference.getPreference(thisContext);

            String key         = pref.getString(Define.KEY, "");
            String policy      = pref.getString(Define.POLICY, "");
            String signature   = pref.getString(Define.SIGNATURE, "");

            params.put("AWSAccessKeyId"             , "AKIAJN6SHS4ZX33YWPFA");
            params.put("success_action_status"     , "201");
            params.put("Content-Type"                , "image/png");

            params.put(Define.ACL        , "private");
            params.put(Define.KEY        , key);
            params.put(Define.POLICY     , policy);
            params.put(Define.SIGNATURE, signature);
            params.put(Define.FILE, vMessage);


            Log.d("file", String.valueOf(vMessage.exists()) + " : " + vMessage.getName());
            Log.d("param", params.toString());
            url = voiceUploadURL.concat(key);

            Log.d(LogTag, url);
        } catch (Exception e){
            // -*ssh*- FileNotFoundException is subcase of Exception
            e.printStackTrace();
        }

        oAsyncHttpClient.post(voiceUploadURL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                Log.d(LogTag, "httpget onStart");
            }


            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                // -*ssh*- avoid to call toString on array
                Log.d(LogTag, "httpget onSuccess " + response.toString());
             //   Toast.makeText(thisContext, "Upload Sucess", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d(LogTag, "httpget onFailure " + statusCode);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Log.d(LogTag, "httpget onRetry");
            }
        });
        return url;
    }


}
