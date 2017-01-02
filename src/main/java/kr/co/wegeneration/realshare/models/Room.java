package kr.co.wegeneration.realshare.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by user on 2015
 * -09-01.
 */
@ParseClassName("Room")
public class Room extends ParseObject {

    private String wowzaToken;
    private String broadcastUrl;
    private String shareUrl;
    private String videoPath;
    private String thumbnailPath;
    private String profilePath;
    private String type;
    private String status;
    private String title;
    private String userId;
    private String userNm;
    private String userStatus;
    private String roomName;
    private int  viewCount;
    private int  heartCount;
    private int commentCount;
    private boolean draft;
    private boolean isDeleted;


    public boolean getIsDeleted() {
        return getBoolean("isDeleted");
    }

    public void setIsDeleted(boolean isDeleted) {
        put("isDeleted", isDeleted);
    }

    public boolean getDraft() {
        return getBoolean("isDraft");
    }

    public void setDraft(boolean draft) {
        put("isDraft", draft);
    }


    public String getWowzaToken() {
        return getString("wowzaToken");
    }

    public void setWowzaToken(String wowzaToken) {
        put("wowzaToken", wowzaToken);
    }

    public String getProfilePath() {
        return getString("profilePath");
    }

    public void setProfilePath(String profilePath) {
        put("profilePath", profilePath);
    }

    public String getThumbnailPath() {
        return getString("thumbnailPath");
    }

    public void setThumbnailPath(String thumbnailPath) {
        put("thumbnailPath", thumbnailPath);
    }

    public String getBroadcastUrl() {
        return getString("broadcastUrl");
    }

    public void setBroadcastUrl(String broadcastUrl) {
        put("broadcastUrl", broadcastUrl);
    }

    public String getShareUrl() {
        return getString("shareUrl");
    }

    public void setShareUrl(String shareUrl) {
        put("shareUrl", shareUrl);
    }

    public String getVideoPath() {
        return getString("videoPath");
    }

    public void setVideoPath(String videoPath) {
        put("videoPath", videoPath);
    }

    public String getType() {
        return getString("type");
    }

    public void setType(String type) {
        put("type", type);
    }

  public String getRoomId() {
        return getString("roomId");
    }

    public void setRoomId(String roomId) {
        put("roomId", roomId);
    }

    public String getRoomName() {
        return getString("roomName");
    }

    public void setRoomName(String roomName) {
        put("roomName", roomName);
    }

    public String getStatus() {
        return getString("status");
    }

    public void setStatus(String status) {
        put("status", status);
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }


    public void setUserNm(String userNm) {
        put("userName", userNm);
    }

    public String getUserNm() {
        return getString("userName");
    }

    public void setUserStatus(String userstatus) {
        put("userStatus", userStatus);
    }

    public String getUserStatus() {
        return getString("userStatus");
    }

    public void setUserId(String objectId) {
        put("userId", objectId);
    }

    public String getUserId() {
        return getString("userId");
    }

    public int getViewCount() {
        return getInt("viewCount");
    }

    public void setViewCount(int viewCount) {
        put("viewCount", viewCount);
    }

    public int getHeartCount() {
        return getInt("heartCount");
    }

    public void setHeartCount(int heartCount) {
        put("heartCount", heartCount);
    }

    public int getCommentCount() {
        return getInt("commentCount");
    }

    public void setIsLikeds(Boolean isLiked) {
        put("isLiked", isLiked);
    }

    public Boolean getIsLikeds() {
        return getBoolean("isLiked");
    }

    public void setCommentCount(int commentCount) {
        put("commentCount", commentCount);
    }

    public static ParseQuery<Room> getQuery() {
        return ParseQuery.getQuery(Room.class);
    }
}

