package kr.co.wegeneration.realshare.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by user on 2015-09-01.
 */
@ParseClassName("Notification")
public class Notification extends ParseObject {

    private String friendId;
    private String friendNm;
    private String userId;
    private String userNm;
    private String type;
    private String title;
    private String content;
    private boolean readVerified;
    private boolean draft;

    public String getFriendId() {
        return getString("friendId");
    }

    public void setFriendId(String friendId) {
        put("friendId", friendId);
    }

    public String getUserNm() {
        return getString("userNm");
    }

    public void setUserNm(String friendNm) {
        put("userNm", friendNm);
    }

    public String getFriendNm() {
        return getString("friendNm");
    }

    public void setFriendNm(String friendNm) {
        put("friendNm", friendNm);
    }

    public String getUserId() {
        return getString("userId");
    }

    public void setUserId(String userId) {
        put("userId ", userId);
    }

    public String getContent() {
        return getString("content");
    }

    public void setContent(String comment) {
        put("content", comment);
    }

    public String getType() {
        return getString("type");
    }

    public void setType(String type) {
        put("type", type);
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }


    public boolean getReadVerified() {
        return getBoolean("readVerified");
    }

    public void setReadVerified(boolean readVerified) {
        put("readVerified", readVerified);
    }


    public boolean getDraft() {
        return getBoolean("isDraft");
    }

    public void setDraft(boolean draft) {
        put("isDraft", draft);
    }



    public static ParseQuery<Notification> getQuery() {
        return ParseQuery.getQuery(Notification.class);
    }
}
