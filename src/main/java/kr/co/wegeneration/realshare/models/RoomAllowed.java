package kr.co.wegeneration.realshare.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by user on 2015-09-01.
 */
@ParseClassName("RoomAllowed")
public class RoomAllowed extends ParseObject {

    private String objectId;
    private String userId;
    private String friendId;
    private boolean draft;


    public String getRoom_owner_id() {
        return getString("userId");
    }

    public void setRoom_owner_id(String room_owner_id) {
        put("userId", room_owner_id);
    }
    public String getAllowed_user_id() {
        return getString("friendId");
    }

    public void setAllowed_user_id(String allowed_user_id) {
        put("friendId ", allowed_user_id);
    }


    public boolean getDraft() {
        return getBoolean("isDraft");
    }

    public void setDraft(boolean draft) {
        put("isDraft", draft);
    }




    public static ParseQuery<RoomAllowed> getQuery() {
        return ParseQuery.getQuery(RoomAllowed.class);
    }
}
