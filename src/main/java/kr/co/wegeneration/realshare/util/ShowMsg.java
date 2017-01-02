package kr.co.wegeneration.realshare.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.EditText;

import kr.co.wegeneration.realshare.FriendFragment;
import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.NetController.ParseNetController;
import kr.co.wegeneration.realshare.SplashActivity;
import kr.co.wegeneration.realshare.MainActivity;
import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;
import kr.co.wegeneration.realshare.gcm.PushWakeLock;

import java.util.HashMap;
import java.util.Map;

import com.parse.ParseUser;

/**
 * Created by admin on 2015-08-17.
 */
public class ShowMsg extends AppCompatActivity {
    private static final String LogTag = "ShowMsg";

    static Context thisContext;

    ProgressDialog mPDialog;

    AlertDialog.Builder dialogBuilder =null ;

    static String user_id = "";
    static String user_nm = "";
    static String senderStatus="";
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_layout);


        thisContext = this;

        SharedPreferences pref = RSPreference.getPreference(thisContext);
        //user_id = pref.getString(Define.USER_ID, "");
        //user_nm = pref.getString(Define.USER_NM, "");

        try {

            user_id = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_USER_ID);
            user_nm = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_USER_NM);

        }catch(Exception e) { e.printStackTrace(); }

        Bundle bundle = getIntent().getExtras();

        final Boolean isMyApp = bundle.getBoolean(Define.INTENT_IS_MY_APP);
        final String className = bundle.getString(Define.INTENT_CLASS_NM);

        final String msgType = bundle.getString(Define.MSG_TYPE);
        final String title = bundle.getString(Define.MSG_TITLE);
        final String contents = bundle.getString(Define.MSG_CONTENTS);
        final String senderId = bundle.getString(Define.MSG_SENDER_ID);
        final String senderNm = bundle.getString(Define.MSG_SENDER_NM);
        senderStatus = bundle.getString(Define.MSG_SENDER_STATUS);

        if (msgType == null || msgType.isEmpty()) return;
        if (contents == null || contents.isEmpty()) return;

        Log.d( LogTag, "msgType : " + msgType + " / senderId : " + senderId + " / senderNm : " + senderNm + " / senderStatus : " + senderStatus + " classname: "+ className + " ismyapp: "+ isMyApp) ;



        String txtPositiveButton = "OK";
        String txtNegativeButton = "CLOSE";
        String txtMoreButton = "Private";



        if( msgType.equals( Define.MSG_TYPE_PULL ) ) {
            if( senderId != null && !senderId.isEmpty() ) {
                txtMoreButton = "Private";
            }
            txtPositiveButton = "Public Share";
            txtNegativeButton = "Ignore";

        } else if( msgType.equals( Define.MSG_TYPE_PUBLISH ) ) {
            txtPositiveButton = "Go";

        } else if( msgType.equals( Define.MSG_TYPE_KNOCK ) ) {
            txtPositiveButton = "Share now!";
            txtNegativeButton = "Reject";

        } else if( msgType.equals( Define.MSG_TYPE_INVITE ) ) {
            txtPositiveButton = "Go";

        } else if( msgType.equals( Define.MSG_TYPE_STATUS ) ) {
            txtPositiveButton = "Pull Now";
            txtNegativeButton = "Close";
        }
        else if( msgType.equals( Define.MSG_TYPE_INSERT_COMMENT ) ) {
            txtPositiveButton = "Comment";
            txtNegativeButton = "Close";
        } else if( msgType.equals( Define.MSG_TYPE_ADDFRIEND ) ) {
            txtPositiveButton = "Accept";
            txtNegativeButton = "Close";
        } else if( msgType.equals( Define.MSG_TYPE_ADDFRIENDREFLY ) ) {
            txtPositiveButton = "OK";
            txtNegativeButton = "";
        }else if( msgType.equals( Define.MSG_TYPE_TIMELINE_INSERT_COMMENT ) ) {
            txtPositiveButton = "Comment";
            txtNegativeButton = "Close";
        }

        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(title).setMessage(contents);
        dialogBuilder.setCancelable(true);
        // 닫기 버튼
        dialogBuilder.setNegativeButton(txtNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PushWakeLock.releaseCpuLock();


                if( isMyApp && className.contains( "MainActivity" ) ) {
                    // TODO :: publish 메시지 수신 후 송신자 버튼 LIVE 활성화.. server friendList 에 위임
                }
                if( msgType.equals( Define.MSG_TYPE_STATUS ) ) {
                    if (senderId != null && !senderId.isEmpty()) {
                        finish();
                   /*     Log.d("Comment", " Comment Dialog");
                        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(thisContext);
                        dialogBuilder.setTitle("Comment " + senderNm + "'s Status");

                        final LinearLayout layout = (LinearLayout) View.inflate(getApplicationContext(), R.layout.dialog_comment,null);
                        dialogBuilder.setView(layout);

                        dialogBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText edit = (EditText) layout.findViewById(R.id.commentText);
                                //edit.setBackgroundResource(R.color.white);
                                //edit.setTextColor(Color.parseColor("#FFFFFF"));
                                String s = edit.getText().toString();
//
                                final Map<String, String> param = new HashMap<>();
                                param.put(Define.ACTION, Define.ACTION_COMMENT);
                                param.put(Define.MSG_TYPE, Define.MSG_TYPE_INSERT_COMMENT);
                                param.put(Define.MSG_SENDER_ID, user_id);
                                param.put(Define.MSG_OWNER_ID, senderId);
                                param.put(Define.MSG, s);
                                param.put(Define.MSG_LOG_ID, "");

                                NetController.getInstance(thisContext)
                                        .getRequestQueue()
                                        .add(NetController.Comment(thisContext, param));
                                     // Toast.makeText(this, "edittext:" + s + " checkbox:" + b, Toast.LENGTH_SHORT).show();
                                //dialogBuilder.show().dismiss();
                                //finish();
                            }
                        });
                        dialogBuilder.show();*/
                    }
                }

                if( msgType.equals( Define.MSG_TYPE_INSERT_COMMENT ) ) {
                    if (senderId != null && !senderId.isEmpty()) {
                        finish();//moveToMain(msgType, senderId, senderNm);
                    }
                }

                if( msgType.equals( Define.MSG_TYPE_TIMELINE_INSERT_COMMENT ) ) {
                    if (senderId != null && !senderId.isEmpty()) {
                        //moveToMain(msgType, senderId, senderNm);
                        finish();
                    }
                }

                if( msgType.equals( Define.MSG_TYPE_ADDFRIEND ) ) {
                    if (senderId != null && !senderId.isEmpty()) {
                        //moveToMain( msgType, senderId, senderNm );
                        //dialogBuilder.show().dismiss();
                        finish();
                    }
                }

                if( msgType.equals( Define.MSG_TYPE_PUBLISH ) ) {
                    if (senderId != null && !senderId.isEmpty()) {
                        //moveToMain( msgType, senderId, senderNm );
                        //dialogBuilder.show().dismiss();
                        finish();
                    }
                }
                if( msgType.equals( Define.MSG_TYPE_PULL ) ) {
                    if (senderId != null && !senderId.isEmpty()) {
                        //moveToMain( msgType, senderId, senderNm );
                        //dialogBuilder.show().dismiss();
                        finish();
                    }
                }
                if( msgType.equals( Define.MSG_TYPE_INVITE ) ) {
                    if (senderId != null && !senderId.isEmpty()) {
                        //moveToMain( msgType, senderId, senderNm );
                        //dialogBuilder.show().dismiss();
                        finish();
                    }
                }



            }
        });

        if( msgType.equals( Define.MSG_TYPE_PULL ) ) {

            if( senderId != null && !senderId.isEmpty() ) {
                // 비공개방송
                dialogBuilder.setNeutralButton(txtMoreButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PushWakeLock.releaseCpuLock();

                        if( isMyApp ) {
                            // pull 메시지 수신 후, private 친구 선택
                            // TODO :: watch mode 확인 창?? private 친구 선택
                            // Main : private 친구 선택
                            // Broadcast : 방송 중이라 반응할 필요가 없다.
                            // Watch?
                            if( className.contains( "MainActivity" ) ) {
                                /*Map<String, String> param = new HashMap<String, String>();
                                param.put(Define.ACTION, Define.ACTION_ROOM_PRIVATE);
                                param.put(Define.DB_USER_ID, user_id);
                                param.put(Define.PARAM_PULL_FROM, senderNm);
                                //param.put(Define.CALLER_ACTIVITY, "MainActivity");

                                NetController.getInstance(thisContext)
                                        .getRequestQueue()
                                        .add(NetController.Room(thisContext, param));*/
                                FriendFragment.isPrivate = true;
                                moveToMain(msgType, senderId, senderNm);
                            }

//                            moveToMain( msgType, senderId, senderNm );
                        } else {
                            moveToLSplash( msgType, senderId, senderNm );
                        }
                    }
                });
            }
        }

        dialogBuilder.setPositiveButton(txtPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PushWakeLock.releaseCpuLock();

                Log.d(LogTag, "isMyApp " + isMyApp);
                Log.d(LogTag, "className " + className);

             /*   if (!isMyApp && !className.contains("LoginActivity") && !className.contains("JoinActivity")) {
                    if (msgType.equals(Define.MSG_TYPE_PUBLISH)) {
                        // 다른 사용자의 publish 메시지 수신 후, watch 이동

                        // Main : 이동
                        // Broadcast : 방송 중이라 닫고 이동하면 안된다.
                        // Watch?
                        if (className.contains("MainActivity") || className.contains("Launcher")) {

                            Map<String, String> param = new HashMap<String, String>();
                            param.put(Define.ACTION, Define.USER_MODE_WATCH);
                            param.put(Define.DB_USER_ID, user_id);
                            param.put(Define.OWNER_ID, senderId);
                            param.put(Define.IS_MY_APP, String.valueOf(isMyApp));
                            //param.put(Define.CALLER_ACTIVITY, "MainActivity");

                            param.put(Define.DB_USER_MODE, Define.USER_MODE_WATCH);

                            NetController.getInstance(thisContext)
                                    .getRequestQueue()
                                    .add(NetController.Room(thisContext, param));
                        }
                    }
                }*/
                if (isMyApp && !className.contains("LoginActivity") && !className.contains("JoinActivity")) {
                    if (msgType.equals(Define.MSG_TYPE_PULL)) {
                        // pull 메시지 수신 후, public publish

                        // Main : 이동
                        // Broadcast : 방송 중이라 반응할 필요가 없다.
                        // Watch?

                        if (className.contains("MainActivity")) {
                            Map<String, String> param = new HashMap<String, String>();
                            param.put(Define.ACTION, Define.ACTION_ROOM_PUBLIC);
                            param.put(Define.DB_USER_ID, user_id);
                            param.put(Define.PARAM_PULL_FROM, senderNm);
                            param.put(Define.MSG_TYPE,  msgType);
                            //param.put(Define.CALLER_ACTIVITY, "MainActivity");

                            /*NetController.getInstance(thisContext)
                                    .getRequestQueue()
                                    .add(NetController.Room(thisContext, param));*/

                            ParseNetController.Room(thisContext, param);

                        }

                    } else if (msgType.equals(Define.MSG_TYPE_PUBLISH)) {
                        // 다른 사용자의 publish 메시지 수신 후, watch 이동

                        // Main : 이동
                        // Broadcast : 방송 중이라 닫고 이동하면 안된다.
                        // Watch?
                        if (className.contains("MainActivity")) {



                            /*NetController.getInstance(getContext())
                                    .getRequestQueue()
                                    .add(NetController.Room(getContext(), param));*/

                            Map<String, String> param = new HashMap<String, String>();
                            param.put(Define.ACTION, Define.USER_MODE_WATCH);
                            param.put(Define.DB_USER_ID  , user_id);
                            param.put(Define.OWNER_ID    , senderId);
                            //param.put(Define.IS_MY_APP, String.valueOf(isMyApp));
                            param.put(Define.CALLER_ACTIVITY, "ShowMsg");
                            param.put(Define.MSG_TYPE,  msgType);
                            param.put(Define.DB_USER_MODE, Define.USER_MODE_WATCH);

                            /*NetController.getInstance(thisContext)
                                    .getRequestQueue()
                                    .add(NetController.Room(thisContext, param));*/

                            ParseNetController.Room(thisContext, param);
                        }
                        //dialogBuilder.show().dismiss();

                    } else if (msgType.equals(Define.MSG_TYPE_KNOCK)) {
                        // 다른 사용자가 나의 비공개방 참여 요청 knock 메시지 수신 후, 허용

                        // Main : 방송을 끝냈기 때문에 무시
                        // Broadcast : 방송 중이라 invite 메시지만 날림
                        // Watch : 방송을 끝냈기 때문에 무시

                        if (className.contains("BroadcastActivity")) {
                            Map<String, String> param = new HashMap<>();
                            param.put(Define.ACTION, Define.ACTION_INVITE);
                            param.put(Define.MSG_TYPE, Define.MSG_TYPE_INVITE);
                            param.put(Define.MSG_SENDER_ID, user_id);

                            param.put(Define.PARAM_RECEIVER_LIST, "[" + senderId + "]");

                            NetController.getInstance(thisContext)
                                    .getRequestQueue()
                                    .add(NetController.Push(thisContext, param));
                        }

                    } else if (msgType.equals(Define.MSG_TYPE_INVITE)) {
                        // 내가 knock 보낸 사용자로부터 허용 invite 메지시 수신 후, 이동
                        // Main : 이동
                        // Broadcast : 방송 중이라 닫고 이동하면 안된다.
                        // Watch?
                        if (className.contains("MainActivity")) {
                            Map<String, String> param = new HashMap<String, String>();

                            param.put(Define.ACTION         , Define.USER_MODE_WATCH);
                            param.put(Define.DB_USER_ID     , user_id);
                            param.put(Define.OWNER_ID       , senderId);
                            param.put(Define.DB_USER_MODE   , Define.USER_MODE_WATCH);
                            param.put(Define.MSG_TYPE,  msgType);
                            //param.put(Define.CALLER_ACTIVITY, "MainActivity");

                            /*NetController.getInstance(thisContext)
                                    .getRequestQueue()
                                    .add(NetController.Room(thisContext, param));*/

                            ParseNetController.Room(thisContext, param);
                        }

                    } else if (msgType.equals(Define.MSG_TYPE_STATUS)) {


                        pullRequest(senderId, senderNm);
                        //dialogBuilder.show().dismiss();
                        finish();

                    }else if (msgType.equals(Define.MSG_TYPE_ADDFRIENDREFLY)) {
                        moveToMain(msgType, senderId, senderNm);
                        //dialogBuilder.show().dismiss();
                        //finish();

                    } else if (msgType.equals(Define.MSG_TYPE_INSERT_COMMENT)) {
                        if (senderId != null && !senderId.isEmpty()) {
                            Log.d("Comment", " Comment Dialog");
                            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(thisContext);
                            dialogBuilder.setTitle("Comment " + senderNm + "'s Status");

                            final LinearLayout layout = (LinearLayout) View.inflate(getApplicationContext(), R.layout.dialog_comment, null);
                            dialogBuilder.setView(layout);

                            dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    //dialogBuilder.show().dismiss();
                                    ((Activity) thisContext).finish();
                                }
                            });
                            dialogBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText edit = (EditText) layout.findViewById(R.id.commentText);
                                    //edit.setBackgroundResource(R.color.white);
                                    //edit.setTextColor(Color.parseColor("#FFFFFF"));
                                    String s = edit.getText().toString();
//
                                    final Map<String, String> param = new HashMap<>();
                                    param.put(Define.ACTION         , Define.ACTION_COMMENT);
                                    param.put(Define.MSG_TYPE       , Define.MSG_TYPE_INSERT_COMMENT);
                                    param.put(Define.MSG_SENDER_ID , user_id);
                                    param.put(Define.MSG_OWNER_ID  , senderId);
                                    param.put(Define.DB_USER_NM     , user_nm);
                                    param.put(Define.MSG              , s);

                                    ParseNetController.Comment(thisContext, param);

                                    /*final Map<String, String> param = new HashMap<>();
                                    param.put(Define.ACTION, Define.ACTION_COMMENT);
                                    param.put(Define.MSG_TYPE, Define.MSG_TYPE_INSERT_COMMENT);
                                    param.put(Define.MSG_SENDER_ID, user_id);
                                    param.put(Define.MSG_OWNER_ID, senderId);
                                    param.put(Define.MSG, s);
                                    param.put(Define.MSG_LOG_ID, "");

                                    NetController.getInstance(thisContext)
                                            .getRequestQueue()
                                            .add(NetController.Comment(thisContext, param, false));*/
                                    // Toast.makeText(this, "edittext:" + s + " checkbox:" + b, Toast.LENGTH_SHORT).show();
                                    //dialogBuilder.show().dismiss();
                                    ((Activity) thisContext).finish();
                                }

                            });

                            dialogBuilder.show();
                        }
                    }
                    else if (msgType.equals(Define.MSG_TYPE_ADDFRIEND)) {
                        Map<String, String> param = new HashMap<String, String>();

                        param.put(Define.ACTION, Define.ACTION_CONFIRM_FRIEND);
                        param.put(Define.MSG_TYPE, Define.MSG_TYPE_CONFIRMFRIEND);
                        param.put(Define.USER_ID, senderId);
                        param.put(Define.FRIEND_ID, user_id);

                        ParseNetController.AddFriend(thisContext, param);

                        moveToMain(msgType, senderId, senderNm);
                        //dialogBuilder.show().dismiss();
                        finish();

                    } else if (msgType.equals(Define.MSG_TYPE_TIMELINE_INSERT_COMMENT)) {
                        if (senderId != null && !senderId.isEmpty()) {
                            Log.d("Comment", " Comment Dialog");
                            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(thisContext);
                            dialogBuilder.setTitle("Comment " + senderNm + "'s Status");

                            final LinearLayout layout = (LinearLayout) View.inflate(getApplicationContext(), R.layout.dialog_comment, null);
                            dialogBuilder.setView(layout);

                            dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    //dialogBuilder.show().dismiss();
                                    ((Activity) thisContext).finish();
                                }
                            });
                            dialogBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText edit = (EditText) layout.findViewById(R.id.commentText);
                                    //edit.setBackgroundResource(R.color.white);
                                    //edit.setTextColor(Color.parseColor("#FFFFFF"));
                                    String s = edit.getText().toString();
//
                                    final Map<String, String> param = new HashMap<>();
                                    param.put(Define.ACTION         , Define.ACTION_COMMENT);
                                    param.put(Define.MSG_TYPE       , Define.MSG_TYPE_INSERT_COMMENT);
                                    param.put(Define.MSG_SENDER_ID , user_id);
                                    param.put(Define.MSG_OWNER_ID  , senderId);
                                    param.put(Define.DB_USER_NM     , user_nm);
                                    param.put(Define.MSG              , s);

                                    ParseNetController.RoomComment(thisContext, param);

                                    ((Activity) thisContext).finish();
                                }

                            });

                            dialogBuilder.show();
                        }
                    }


//                    moveToMain( msgType, senderId, senderNm );

//                    if( msgType.equals( Define.MSTPCD_PULL_ONE_ANYMS ) ) {
//                        // 공개방송
//
//
//
//                    } else if( msgType.equals( Define.MSTPCD_PULL_EVERY_ANYMS ) ) {
//                        // 공개방송
//                        moveToBroadcast( room, Define.SHARE_MODE_PUBLIC);
//                        // TODO : (Pulled by someone)
//
//                    } else if( msgType.equals( Define.MSTPCD_PULL_ONE_USNM ) ) {
//                        // 공개방송
//                        moveToBroadcast( room, Define.SHARE_MODE_PUBLIC);
//                        // TODO : (Pulled by ~~~)
//
//                    } else if( msgType.equals( Define.MSTPCD_PULL_EVERY_USNM ) ) {
//                        // 공개방송
//                        moveToBroadcast( room, Define.SHARE_MODE_PUBLIC);
//                        // TODO : (Pulled by ~~~)
//
//                    } else if( msgType.equals( Define.MSTPCD_KNOCK_LOCKED ) ) {
//                        // 비공개방송 허용
//                        List<String> rcvRegIds = new ArrayList<>();
//                        rcvRegIds.add(senderGCMId);
//
//                        Map msgInfo = new HashMap();
//                        msgInfo.put(Define.MSG_TYPE, Define.MSTPCD_SHARE_PRIVATE); // TODO :: 추가메시지 정의
//                        msgInfo.put(Define.MSG_CONTENTS, user_nm + "has accepted your request");
//                        msgInfo.put(Define.MSG_SENDER_ID, user_id); // String.valueOf(user_id));
//                        msgInfo.put(Define.MSG_SENDER_NM, user_nm);
//                        msgInfo.put(Define.MSG_SENDER_GCM_ID, gcm_reg_id);
//                        msgInfo.put(Define.ROOM, room);
//                        new GCMSendTask(rcvRegIds, msgInfo);
//
//                    } else if( msgType.equals( Define.MSTPCD_SHARE_PUBLIC ) ) {
//                        // 공개방송 참여
//
//                    } else if( msgType.equals( Define.MSTPCD_SHARE_PRIVATE ) ) {
//                        // 비공개방송 참여
//                        moveToWatch( room );
//                    } else if( msgType.equals( Define.MSTPCD_CLOSE_BORADCAST ) ) {
//                        // 확인
//                        finish();
//                    } else if( msgType.equals( Define.MSTPCD_USER_STATUS ) ) {
//                        // 확인
//                        finish();
//                    }


                } else

                {
                    Log.d(LogTag, "It's not my app.. moveToLSplash");
                    moveToLSplash(msgType, senderId, senderNm);
                }
            }
        });

        //TODO -*ssh*- add OncancelListener
        dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialogBuilder.show().dismiss();
                ((Activity) thisContext).finish();
            }
        });

        //TODO -*ssh*- show dialog
        dialogBuilder.show();

    }

    private void moveToLSplash( String msgType, String senderId, String senderNm ) {
        Intent splashIntent = new Intent( getApplicationContext(), SplashActivity.class );
        splashIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if( msgType != null && !msgType.isEmpty() ) {
            splashIntent.putExtra(Define.MSG_TYPE, msgType);
            splashIntent.putExtra(Define.MSG_SENDER_ID, senderId);
            splashIntent.putExtra(Define.MSG_SENDER_NM, senderNm);
            splashIntent.putExtra(Define.MSG_SENDER_STATUS, senderStatus);

        }

        startActivity(splashIntent);

        //dialogBuilder.show().dismiss();
        finish();
    }

    private void moveToMain( String msgType, String senderId, String senderNm ) {
        Intent mainIntent = new Intent( getApplicationContext(), MainActivity.class );
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if( msgType != null && !msgType.isEmpty() ) {
            mainIntent.putExtra(Define.MSG_TYPE, msgType);
            mainIntent.putExtra(Define.MSG_SENDER_ID, senderId);
            mainIntent.putExtra(Define.MSG_SENDER_NM, senderNm);
            mainIntent.putExtra(Define.MSG_SENDER_STATUS, senderStatus);
        }
//        intent.putExtra(Define.DB_STATUS, user_status);

        startActivity(mainIntent);
        //dialogBuilder.show().dismiss();
        finish();
    }
    public void pullRequest(String senderId, String senderNm ){


        Map<String, String> param = new HashMap<String, String>();

        param.put(Define.ACTION, Define.ACTION_PULL);
        param.put(Define.MSG_TYPE, Define.MSG_TYPE_PULL);
        param.put(Define.DB_USER_ID, ParseUser.getCurrentUser().getObjectId());
        param.put(Define.PARAM_RECEIVER_LIST, "[" + senderId + "]");
        param.put(Define.RECEIVER_NAME, senderNm);

        /*NetController.getInstance(getContext())
                .getRequestQueue()
                .add(NetController.Push(getContext(), param));*/
        ParseNetController.PushSend(thisContext, param);

    }


    /*public void pullRequest(String sender_id, boolean which, String senderNm){

        Map<String, String> param = new HashMap<String, String>();

        param.put(Define.ACTION, Define.ACTION_PULL);
        param.put(Define.MSG_TYPE, Define.MSG_TYPE_PULL);
        param.put(Define.MSG_SENDER_ID, user_id);
        param.put(Define.PARAM_RECEIVER_LIST, "[" + sender_id + "]");
        param.put(Define.PARAM_ANONYMOUS, (which) ? "y" : "n");
        param.put(Define.RECEIVER_NAME, senderNm);

        NetController.getInstance(getApplicationContext())
                .getRequestQueue()
                .add(NetController.Push(getApplicationContext(), param));

    }
*/
//    private void moveToBroadcast( String mode ) {
//        Intent intent = new Intent( thisContext.getApplicationContext(), BroadcastActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        if( msgType != null && !msgType.isEmpty() ) {
//            intent.putExtra(Define.MSG_TYPE, msgType);
//            intent.putExtra(Define.MSG_SENDER_ID, senderId);
//        }
//        intent.putExtra(Define.USER_MODE, mode);
//
//        startActivity(intent);
//        finish();
//    }
//
//    private void moveToWatch( ) {
//        Intent intent = new Intent( thisContext.getApplicationContext(), WatchActivity.class );
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        if( msgType != null && !msgType.isEmpty() ) {
//            intent.putExtra(Define.MSG_TYPE, msgType);
//            intent.putExtra(Define.MSG_SENDER_ID, senderId);
//        }
//
//        startActivity(intent);
//        finish();
//    }
}
