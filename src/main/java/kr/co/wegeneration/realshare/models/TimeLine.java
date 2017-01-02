package kr.co.wegeneration.realshare.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by user on 2015
 * -09-01.
 */
@ParseClassName("TimeLine")
public class TimeLine extends ParseObject {

    private String userId;
    private String roomId;
    private boolean draft;




    public boolean getDraft() {
        return getBoolean("isDraft");
    }

    public void setDraft(boolean draft) {
        put("isDraft", draft);
    }





    public String getRoomId() {
        return getString("roomId");
    }

    public void setRoomId(String roomId) {
        put("roomId", roomId);
    }


    public void setUserId(String objectId) {
        put("userId", objectId);
    }

    public String getUserId() {
        return getString("userId");
    }


    public static ParseQuery<TimeLine> getQuery() {
        return ParseQuery.getQuery(TimeLine.class);
    }
}

