package kr.co.wegeneration.realshare.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by user on 2015-09-01.
 */
@ParseClassName("Friend")
public class Friend extends ParseObject {

    private String user_from_email;
    private String user_to_email;
    private String user_from_name;
    private String user_to_name;
    private String userId;
    private String friendId;
    private String friend_status;
    private boolean draft;


    public String getUser_from_email() {
        return getString("user_from_email");
    }

    public void setUser_from_email(String user_from_email) {
        put("user_from_email", user_from_email);
    }
    public String getUser_from_name() {
        return getString("user_from_name");
    }

    public void setUser_from_name(String user_to_email) {
        put("user_from_name ", user_to_email);
    }

    public String getUserId() {
        return getString("userId");
    }

    public void setUserId(String userId) {
        put("userId ", userId);
    }

    public String getFriendId() {
        return getString("friendId");
    }

    public void setFriendId(String friendId) {
        put("friendId ", friendId);
    }

    public String getFriend_status() {
        return getString("status");
    }

    public void setFriend_status(String friend_status) {
        put("status", friend_status);
    }

    public String getUser_to_email() {
        return getString("user_to_email");
    }

    public void setUser_to_email(String user_to_email) {
        put("user_to_email", user_to_email);
    }

    public String getUser_to_name() {
        return getString("user_to_name");
    }

    public void setUser_to_name(String user_to_name) {
        put("user_to_name", user_to_name);
    }

    public boolean getDraft() {
        return getBoolean("isDraft");
    }

    public void setDraft(boolean draft) {
        put("isDraft", draft);
    }




    public static ParseQuery<Friend> getQuery() {
        return ParseQuery.getQuery(Friend.class);
    }
}
