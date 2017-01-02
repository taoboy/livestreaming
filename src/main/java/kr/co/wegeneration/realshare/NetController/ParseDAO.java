package kr.co.wegeneration.realshare.NetController;

import android.content.Context;
import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import kr.co.wegeneration.realshare.AddFriendActivity;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.models.Friend;
import kr.co.wegeneration.realshare.models.Notification;
import kr.co.wegeneration.realshare.models.Room;
import kr.co.wegeneration.realshare.models.UserInfo;

/**
 * Created by User on 2015-12-14.
 */
public class ParseDAO {


    private static final String LogTag = "ParseDAO";

    private static ParseDAO mInstance = null;
    private static Context  thisContext;

    private ParseDAO(Context context){
            thisContext = context;
    }

    public static ParseDAO getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ParseDAO(context);
        }
        return mInstance;
    }

    public static ParseQuery<Room> timelineListParse(){

        ParseQuery<Room> query          = Room.getQuery();
        ParseQuery<UserInfo> subquery_1 = UserInfo.getQuery();
        ParseQuery<UserInfo> subquery_2 = UserInfo.getQuery();
        List<ParseQuery<UserInfo>> queries = new ArrayList<ParseQuery<UserInfo>>();

        try {

            ParseQuery<Friend> subquery2 = Friend.getQuery();
            subquery2.whereEqualTo(Define.DB_FRIEND_ID, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_USER_ID));
            subquery_1.whereMatchesKeyInQuery(Define.DB_USER_ID, Define.DB_USER_ID, subquery2);
            subquery_2.whereEqualTo(Define.DB_USER_ID, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_USER_ID));
            queries.add(subquery_1);
            queries.add(subquery_2);
            ParseQuery<UserInfo> mainQuery = ParseQuery.or(queries);
            query.whereMatchesKeyInQuery(Define.DB_USER_ID, Define.DB_USER_ID, mainQuery);
            query.include("user");
            //query.include("thumbnailPath");
            query.whereNotEqualTo("isDeleted", true);
            query.whereNotEqualTo("status", "own");
            //query.whereNotEqualTo("status", "own");
            query.orderByDescending("createdAt");

        }catch (Exception e) {e.printStackTrace();}
        return query;
    }


    public static ParseQuery<UserInfo> chatfriendlistParse(){

        ParseQuery<UserInfo> query = UserInfo.getQuery();
        try {
            query.whereEqualTo(Define.DB_ROOM, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_ROOM));

        }catch (Exception e) {e.printStackTrace();}
        return query;
    }

    public static ParseQuery<UserInfo> friendlistParse(){

        ParseQuery<UserInfo> query = UserInfo.getQuery();
        try {
            String text = AddFriendActivity.getTextSearch();
            query.whereNotEqualTo(Define.DB_USER_ID, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_USER_ID));

           ParseQuery<Friend> subquery = Friend.getQuery();
            subquery.whereEqualTo(Define.DB_FRIEND_ID, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_USER_ID));
            query.whereDoesNotMatchKeyInQuery(Define.DB_USER_ID, Define.DB_USER_ID, subquery);

            query.whereContains(Define.DB_USER_FIRST_NM, text);

        }catch (Exception e) {e.printStackTrace();}
        return query;
    }

    public static ParseQuery<UserInfo> friendMainListParse(){

        ParseQuery<UserInfo> query = UserInfo.getQuery();
        try {
            query.whereNotEqualTo(Define.DB_USER_NM, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_USER_NM));

            ParseQuery<Friend> subquery = Friend.getQuery();
            subquery.whereEqualTo(Define.FRIEND_STATUS, "friend");
            subquery.whereEqualTo(Define.DB_FRIEND_ID, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_USER_ID));
            query.whereMatchesKeyInQuery(Define.DB_USER_ID, Define.DB_USER_ID, subquery);

        }catch (Exception e) {e.printStackTrace();}
        return query;
    }

    public static ParseQuery<Notification> notificationListParse(){

        ParseQuery<Notification> query = Notification.getQuery();
        try {
            query.whereEqualTo(Define.DB_FRIEND_ID, ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_USER_ID));
            query.whereNotEqualTo("readVerified", true);
            query.orderByDescending("createdAt");
        }catch (Exception e) {e.printStackTrace();}
        return query;
    }


}
