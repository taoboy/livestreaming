package kr.co.wegeneration.realshare.common;

/**
 * Created by systran on 2015-08-07.
 */
public class Define {

    public static final String GCM_REG_ID = "registration_id";
    public static final String GCM_APP_VERSION = "appVersion";
    public static final String TOTAL_LIST_MSG = "RealShare에 있는 회원";
    public static final String NOTIF_YN = "notif_yn";
    public static final String ROOM_ID   = "roomId";
    public static final String DB_OBJECT_ID = "objectId";
    public static final String VIDEO_PATH    = "videoPath";
    public static final String ISOWN            = "isOwn";
    public static final String UserInfo            = "userInfoId";
    public static final String ROOM_OWNER_ID = "room_owner_id";
    public static final String BROAD_STATUS     = "status";
    public static final String MSG_ROOM          = "msg_room";
    public static final String MSG_WATCH_ROOM   = "msg_watch_room";
    public static final String FRIEND_STATUS     = "status";

    public static final String MSG_ROOM_START      = "start";
    public static final String MSG_ROOM_END        = "end";
    public static final String THUMBNAILPATH        = "thumbnailPath";
    public static final String SESSION_TOKEN        = "sessionToken";
    public static final String RECORDTIME           = "recordTime";
    public static final String MSG_TYPE_TIMELINE_INSERT_COMMENT           = "timeline_insert_comment";

    public static final String ACL                   = "acl";
    public static final String KEY                   = "key";
    public static final String POLICY               = "policy";
    public static final String SIGNATURE            = "signature";
    public static final String FILE                 = "file";

    public static final String SESSION   = "session";
    public static final String SESSION_ID   = "sessionId";
    public static final String UPLOAD_TYPE  = "uploadType";

    public static final String ACCESS_TOKEN = "access_token";
    public static final String FRIEND_COUNT = "friend_count";
    public static final String IS_MY_APP = "ismyapp";
    public static final String ADD_FRIEND_SEEN = "isseen";
    public static final String SCREEN_ON = "screen_on";
    public static final String MSG_ROOM_NM = "room_name";
    public static final String STREAM_PAUSE = "stream_pause";
    public static final String STREAM_PLAY = "stream_play";
    public static final String DB_USER_ID = "userId";
    public static final String DB_ROOM_ID = "roomId";
    public static final String DB_USER_INFO_ID = "userInfoId";
    public static final String DB_EMAIL = "email";
    public static final String DB_PASSWD = "user_pw";
    public static final String DB_USER_NM = "username";
    public static final String DB_USER_FIRST_NM = "firstName";
    public static final String DB_USER_LAST_NM = "lastName";
    public static final String DB_USER_MODE = "mode"; // signin, signout, public, private, watch
    public static final String DB_USER_ACTIVE = "active"; // y, n

    public static final String FriendStatus = "friendstatus";
    public static final String CommentStatus  = "commentstatus";
    public static final String CommentPost = "commentpost";

    public static final String DB_STATUS = "statusMessage";

    public static final String TITLE         = "title";
    public static final String DB_ROOM = "roomName";
    public static final String USER_FLAG = "user_flag";
    public static final String DB_GCM_ID = "device_reg_id";
    public static final String DB_INSTALLATION_ID = "installationId";

    public static final String SERVER_URL = "server_url";
    public static final String FACEBOOK_FRIEND_LIST = "facebook_friend_list";

    public static final String CHAT_SERVER_URL  = "chat_server_url";
    public static final String LOGIN_URL         = "login_url";
    public static final String VOICE_UPLOAD_URL = "voice_upload_url";
    public static final String IMAGE_UPLOAD_URL = "image_upload_url";


    public static final String DB_DEVICE = "device_type";


    public static final String MSG_HASH_URL = "hash_url";
    public static final String MSG_SHARE_URL = "share_url";
    public static final String MSG_OWNER_ID = "statusId";
    public static final String MSG_LOG_ID    = "log_id";

    public static final String USER_ID = "user_id";
    public static final String USER_NM = "user_name";
    public static final String USER_SELECTED = "userSelected";
    public static final String OWNER_ID = "room_owner_id";


    public static final String USER_MODE_SIGNOUT = "signout"; // 명시적 사인아웃
    public static final String USER_MODE_SIGNIN = "signin"; // 방송에 참여하지 않는 활동 중
    public static final String USER_MODE_PUBLIC = "public"; // 공개방송
    public static final String USER_MODE_PRIVATE = "private"; // 비공개방송
    public static final String USER_MODE_WATCH = "watch"; // 타채널시청


    public static final String MSG_TYPE = "push_type"; // publish, pull, knock, invite, status, addfriend
    public static final String MSG_TITLE = "push_title";
    public static final String MSG_CONTENTS    = "push_msg";
    public static final String MSG_SENDER_ID   = "sender_id";
    public static final String MSG_USER_ID      = "user_id";
    public static final String MSG_RECORD_URL   = "record_url";
    public static final String MSG_SENDER_STATUS   = "sender_status";
    public static final String MSG_SENDER_NM = "sender_name";
    //public static final String MSG_SENDER_NAME = "sender_name";

    public static final String MSG   = "msg";
    public static final String MSG_TYPE_PUBLISH = "publish";
    public static final String MSG_TYPE_PULL = "pull";
    public static final String MSG_TYPE_KNOCK = "knock";
    public static final String MSG_TYPE_INVITE = "invite";
    public static final String MSG_TYPE_SHARELINK = "sharelink";
    public static final String MSG_TYPE_STATUS = "status";
    public static final String MSG_TYPE_ADDFRIEND = "addfriend";
    public static final String MSG_TYPE_CONFIRMFRIEND = "confirmfriend";
    public static final String MSG_TYPE_DELETEFRIEND = "deletefriend";
    public static final String MSG_TYPE_INSERT_COMMENT = "insert_comment";
    public static final String MSG_TYPE_DELETE_COMMENT = "delete_comment";
    public static final String MSG_TYPE_RECORDSTREAM = "recordstream";

    public static final String MSG_TYPE_STREAMPLAY = "streamplay";
    public static final String MSG_TYPE_STREAMOFF = "streampause";
    public static final String MSG_TYPE_ADDFRIENDREFLY = "addfriendreply";

    public static final String MSG_TYPE_ACTIVITY_READ = "activityProc";
    public static final String MSG_TYPE_COMMENT_LIST = "commentList";

    public static final String CALLER_ACTIVITY = "callerActivity";

    public static final String ACTION = "action";
    public static final String ACTION_SIGNUP = "signup";
    public static final String ACTION_SIGNIN = "signin"; // user_mode : signin
    public static final String ACTION_SIGNOUT = "signout"; // user_mode : signout
    public static final String ACTION_COMMENT_LIST  = "commentList";
    public static final String ACTION_FRIEDNDS = "friendList";
    public static final String ACTION_ACTIVITY = "activityList";
    public static final String ACTION_RECORD_STREAM = "recordstream";
    public static final String ACTION_ACTIVITY_READ = "activityProc";


    public static final String ACTION_ROOM_PUBLIC = "public"; // user_mode : public, push_type : publish
    public static final String ACTION_ROOM_PRIVATE = "private"; // user_mode : private, push_type : publish
    public static final String ACTION_ROOM_WATCH = "watch"; // user_mode : watch
    public static final String ACTION_ROOM_LEAVE = "leave"; // user_mode : signin
    public static final String ACTION_SHARE_OLINK = "sharelink"; // user_mode : signin
    public static final String ACTION_ROOM_CLOSE = "close"; // user_mode : signin

    public static final String ACTION_PULL = "pull";
    public static final String ACTION_KNOCK = "knock";
    public static final String ACTION_INVITE = "invite";
    public static final String ACTION_STATUS = "status";
    public static final String ACTION_REGISTRATION = "registation";
    public static final String ACTION_ADD_FRIEND = "addfriend";
    public static final String ACTION_ADD_FACEBOOK_FRIEND = "addfacebookfriend";
    public static final String ACTION_ADD_FACE_BOOK_FRIEND_COUNT = "addfacebookfriendcount";
    public static final String ACTION_ADD_FRIEND_COUNT = "addfriendcount";
    public static final String ACTION_COMMENT      = "comment";
    public static final String ACTION_CONFIRM_FRIEND = "confirmfriend";
    public static final String ACTION_DELETE_FRIEND  = "deletefriend";
    public static final String ACTION_STREAM_LIST     = "streamlist";
    public static final String ACTION_SCREEN_ON     = "screenOn";

    public static final String ACTION_APP_OUT = "appout";

    // server, push message parameter
    public static final String PARAM_PULL_FROM = "pull_from";
    public static final String PARAM_ALLOW_LIST = "allow_list";
    public static final String PARAM_RECEIVER_LIST = "receiver_list";
    public static final String RECEIVER_NAME = "receiver_name";

    public static final String PARAM_ANONYMOUS = "anonymous";
    // intent parameter
    public static final String INTENT_IS_MY_APP = "isMyApp";
    public static final String INTENT_CLASS_NM = "className";

    public static final String SHARE_MODE_PUBLIC = "public"; // 공개방송
    public static final String SHARE_MODE_PRIVATE = "private"; // 비공개방송

    public static final String PULL_MODE_ANONYMOUSLY = "A"; // 익명
    public static final String PULL_MODE_USENAME = "N"; // 실명


    ////////// not use
    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWD = "user_pw";
    public static final String USER_MODE = "mode";
    public static final String USER_GCM_ID = "device_reg_id";

    public static final String USER_FROM_NAME  = "user_from_name";
    public static final String USER_TO_NAME   = "user_to_name";
    public static final String USER_OBJECT_ID = "objectId";
    public static final String USER_PUSH_ID   = "push_id";

    public static final String SEARCH_WORD = "";


    public static final String PREF_IS_FIRST = "IS_FIRST";
    public static final String PREF_INIT = "INIT";
    public static final String PREF_SIGNIN = "SIGNIN";
    public static final String PREF_SIGNOUT = "SIGNOUT";

    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_FAIL = 0;


    public static final String FRIEND_ID = "friend_id";
    public static final String DB_FRIEND_ID = "friendId";


    public static final String MSG_PULL_FROM = "pull_from";
//    public static final String MSG_SENDER_GCM_ID = "sender_reg_id";

    public static final String MAIN_ACTIVITY = "MainActivity";
    public static final String BROAD_ACTIVITY = "BroadcastActivity";
    public static final String WATCH_ACTIVITY = "WatchActivity";

    public static final String USER_ACTIVE_YES = "y";
    public static final String USER_ACTIVE_NO = "n";

//    public static final String MSTPCD_SIGNIN = "SI";
//    public static final String MSTPCD_SIGNOUT = "SO";
//    public static final String MSTPCD_PULL_ONE_ANYMS = "POA";
//    public static final String MSTPCD_PULL_EVERY_ANYMS = "PEA";
//    public static final String MSTPCD_PULL_ONE_USNM = "PON";
//    public static final String MSTPCD_PULL_EVERY_USNM = "PEN";
//    public static final String MSTPCD_KNOCK_LOCKED = "KL";
//    public static final String MSTPCD_SHARE_PUBLIC = "SOP";
//    public static final String MSTPCD_SHARE_PRIVATE = "SCL";
//    public static final String MSTPCD_CLOSE_BORADCAST = "CB";
//    public static final String MSTPCD_USER_STATUS = "US";


}

