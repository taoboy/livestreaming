package kr.co.wegeneration.realshare.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by user on 2015-09-01.
 */
@ParseClassName("StatusComment")
public class StatusComment extends ParseObject {

    private String objectId;
    private String userId;
    private String comment;
    private String ownerId;
    private String username;
    private boolean draft;

    public String getObectId() {
        return getString("objectId");
    }

    public void setObjectId(String objectId) {
        put("objectId", objectId);
    }
    public String getUserId() {
        return getString("userId");
    }

    public void setUserId(String userId) {
        put("userId ", userId);
    }

    public String getComment() {
        return getString("content");
    }

    public void setComment(String comment) {
        put("content", comment);
    }

    public String getUsername() {
        return getString("username");
    }

    public void setUsername(String username) {
        put("username", username);
    }

    public String getOwnerId() {
        return getString("statusId");
    }

    public void setOwnerId(String ownerId) {
        put("statusId", ownerId);
    }


    public boolean getDraft() {
        return getBoolean("isDraft");
    }

    public void setDraft(boolean draft) {
        put("isDraft", draft);
    }



    public static ParseQuery<StatusComment> getQuery() {
        return ParseQuery.getQuery(StatusComment.class);
    }
}
