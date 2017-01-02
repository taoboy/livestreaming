package kr.co.wegeneration.realshare.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by user on 2015
 * -09-01.
 */
@ParseClassName("RoomComment")
public class RoomComment extends ParseObject {

    private String content;
    private String userId;
    private String userNm;
    private boolean isDeleted;
    private String roomId;
    private boolean draft;




    public boolean getDraft() {
        return getBoolean("isDraft");
    }

    public void setDraft(boolean draft) {
        put("isDraft", draft);
    }


    public boolean isDeleted() {
        return getBoolean("isDeleted");
    }

    public void isDeleted(boolean isDeleted) {
        put("isDeleted", isDeleted);
    }


    public String getContent() {
        return getString("content");
    }

    public void setContent(String content) {
        put("content", content);
    }

    public String getRoomName() {
        return getString("roomName");
    }

    public void setRoomName(String roomId) {
        put("roomName", roomId);
    }


    public void setUserNm(String userNm) {
        put("userNm", userNm);
    }

    public String getUserNm() {
        return getString("userNm");
    }

    public void setUserId(String objectId) {
        put("userId", objectId);
    }

    public String getUserId() {
        return getString("userId");
    }


    public static ParseQuery<RoomComment> getQuery() {
        return ParseQuery.getQuery(RoomComment.class);
    }
}

