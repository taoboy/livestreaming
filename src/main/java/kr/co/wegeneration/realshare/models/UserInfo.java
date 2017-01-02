package kr.co.wegeneration.realshare.models;

import com.parse.ParseClassName;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by user on 2015-09-01.
 */
@ParseClassName("UserInfo")
public class UserInfo extends ParseObject {

    private String userId;
    private String statusMessage;
    private String roomName;
    private String active;
    private String mode;
    private String sex;
    private String email;
    private ParseInstallation installationId;

    private boolean friendstatus;
    private boolean commentstatus;
    private boolean commentpost;

    private int badge_count;
    private int friendCount;
    private int followCount;

    private String nickName;
    private String firstName;
    private String lastName;

    private Date statusUpdateDate;
    private String thumbnailPath;
    private String resizePath;
    private String originPath;

    private Boolean privatedchecked;

    private String getUserId() { return getString("userId"); }

    private void setUserId(String userId) { put("userId", userId); }

    private String getStatusMessage() { return getString("userId"); }

    private void setStatusMessage(String userId) { put("userId", userId); }

    private String getRoomName() { return getString("userId"); }

    private void setRoomName(String userId) { put("userId", userId); }

    private String getActive() { return getString("userId"); }

    private void setActive(String userId) { put("userId", userId); }

    private String getMode() { return getString("userId"); }

    private void setMode(String userId) { put("userId", userId); }

    private String getSex() { return getString("userId"); }

    private void setSex(String userId) { put("userId", userId); }

    private String getNickName() { return getString("userId"); }

    private void setNickName(String userId) { put("userId", userId); }


    private String getFirstName() { return getString("userId"); }

    private void setFirstName(String userId) { put("userId", userId); }

    private String getLastName() { return getString("userId"); }

    private void setLastName(String userId) { put("userId", userId); }

    private String getThumbnailPath() { return getString("userId"); }

    private void setThumbnailPath(String userId) { put("userId", userId); }

    private String getResizePath() { return getString("userId"); }

    private void setResizePath(String userId) { put("userId", userId); }

    private String getOriginPath() { return getString("userId"); }

    private void setOriginPath(String userId) { put("userId", userId); }

    private int getFriendCount() { return getInt("userId"); }

    private void setFriendCount(String userId) { put("userId", userId); }

    private int getFollowCount() { return getInt("userId"); }

    private void setFollowCount(String userId) { put("userId", userId); }


    public Boolean getprivateChecked() {
        return getBoolean("privatedchecked");
    }

    public void setprivateChecked(Boolean privatedchecked) {
        put("privatedchecked", privatedchecked);
    }


    public static ParseQuery<UserInfo> getQuery() {
        return ParseQuery.getQuery(UserInfo.class);
    }
}
