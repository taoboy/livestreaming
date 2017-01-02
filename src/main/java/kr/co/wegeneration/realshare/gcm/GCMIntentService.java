package kr.co.wegeneration.realshare.gcm;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.util.TypedValue;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.gcm.GcmListenerService;
import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.SplashActivity;
import kr.co.wegeneration.realshare.MainActivity;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.BroadcastActivity;
import kr.co.wegeneration.realshare.common.RSPreference;
import java.util.List;
import android.app.Notification;
import android.widget.LinearLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by SSH on 15. 7. 30..
 */
public class GCMIntentService  extends ParsePushBroadcastReceiver {

    private static final String LogTag = CustomPushReceiver.class.getSimpleName();
    private static final String TAG = "GCMIntentService";

    Bundle msgBundle = new Bundle();
    //TODO Change Request Code
    private static final int CHECK_IT_ID = 880218;

    public GCMIntentService() {
        super();
    }

    String msgType = "";
    String title = "";
    String contents = "";
    String senderId = "";
    String senderNm = "";
    String thumbnailPath="";
    String path="";
    String senderStatus = "";
    /**
     * Method checks if the app is in background or not
     *
     * @param context
     * @return
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        }
        else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


    public void callBadgeCount(Context context){
        Intent intents = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        // 패키지 네임과 클래스 네임 설정
        intents.putExtra("badge_count_package_name", context.getPackageName());
        intents.putExtra("badge_count_class_name", context.getClass().getName());
        // 업데이트 카운트
        intents.putExtra("badge_count", ParseUser.getCurrentUser().getInt("badge_count"));
        context.sendBroadcast(intents);
    }
    @Override
    protected void onPushReceive(final Context context, Intent intent) {
        super.onPushReceive(context, intent);

        if (intent == null)
            return;

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            Log.e(TAG, "Push received: " + json);


            Log.d("onMessageReceived", json.toString());
            try {
                msgType = json.getString(Define.MSG_TYPE);
                title = json.getString(Define.MSG_TITLE);
                contents = json.getString(Define.MSG_CONTENTS);
                senderId = json.getString(Define.MSG_SENDER_ID);
                senderNm = json.getString(Define.MSG_SENDER_NM);
                //thumbnailPath  = json.getString(Define.THUMBNAILPATH);

                //senderStatus = json.getString(Define.MSG_SENDER_STATUS);

            }catch(Exception e) {e.printStackTrace();}
            Log.d(LogTag, "Message : " + msgType + "/" + title + "/" + contents + "/" );
            Log.d(LogTag, "Message : " + senderId + "/" + senderNm + "/");



            if (msgType == null) return;

            try {

                ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded();

                kr.co.wegeneration.realshare.models.Notification noti = new kr.co.wegeneration.realshare.models.Notification();
                noti.put("type", msgType);
                noti.put("userId", senderId);
                noti.put("userNm", senderNm);
                noti.put("friendId", ParseUser.getCurrentUser().getString(Define.DB_USER_ID));
                noti.put("content", contents);
                noti.put("thumbnailPath", path);
                noti.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("test", "notification");
                            try {
                                ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded();
                                user.increment("badge_count");
                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) callBadgeCount(context);
                                        else {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }catch(Exception e1) { e1.printStackTrace(); }
                        } else e.printStackTrace();
                    }
                });

                if(user.getBoolean(Define.CommentStatus)==false && msgType.equals(Define.MSG_TYPE_INSERT_COMMENT)) return;
                if(user.getBoolean(Define.CommentPost)==false   && msgType.equals(Define.MSG_TYPE_TIMELINE_INSERT_COMMENT)) return;
                if(user.getBoolean(Define.FriendStatus)==false  && msgType.equals(Define.MSG_TYPE_STATUS)) return;

            }catch (Exception e1) {e1.printStackTrace();}


            sendNotification(context, json);

        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }

    }

    public void doSendTest (View v, Context context) {

        Intent startIntent = new Intent(context, SplashActivity.class);

        msgBundle.putBoolean(Define.INTENT_IS_MY_APP, true);
        msgBundle.putString(Define.INTENT_CLASS_NM, "MainActivity");

        startIntent.setAction(Intent.ACTION_MAIN);
        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startIntent.putExtras(msgBundle);
        startIntent.setComponent(new ComponentName(context.getPackageName(), context.getPackageName() + ".util.ShowMsg"));


        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        startIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );



        Intent intent = new Intent();
        intent.setClass(context, OverlayServiceCommon.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.putExtra("packageName", context.getPackageName());
        intent.putExtra("title", title);
        intent.putExtra("text", contents);
        intent.putExtra("action", pendingIntent);

        if (Build.VERSION.SDK_INT >= 11) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.liveo_logo_small);
            intent.putExtra("iconLarge", bitmap);
        }
        intent.putExtra("icon", -1);
        intent.putExtra("color", context.getResources().getColor(R.color.primary_dark_material_dark));

        /*intent.putExtra("actionCount", 2);
        intent.putExtra("action2title", context.getString(R.string.action_settings));
        intent.putExtra("action2icon", R.drawable.ic_action_settings);
        intent.putExtra("action2intent", PendingIntent.getActivity(context, 0,
                new Intent(context, SplashActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT));
        intent.putExtra("action1title", "test");
        intent.putExtra("action1icon", R.drawable.liveo_logo);
        intent.putExtra("action1intent", PendingIntent.getActivity(context, 0,
                new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("http://simen.codes/donate/#heads-up"))
                , PendingIntent.FLAG_UPDATE_CURRENT));
    */
        context.startService(intent);

//        Mlog.v(logTag, "open");
    }



    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }


    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

    AlphaAnimation fade_in;
    AlphaAnimation fade_out;
    Intent msgIntent = new Intent("MSG");
    Intent streamIntent = new Intent("Stream");
    Context thisContext;
    Boolean isExit =false;
    View mView = null;
    WindowManager mManager=null;
    WindowManager.LayoutParams mParams;


    View.OnTouchListener mViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_UP:
                    Toast.makeText(thisContext, "Go LIVEO",
                            Toast.LENGTH_SHORT).show();

                    msgIntent.putExtra(Define.INTENT_IS_MY_APP, true);
                    msgIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    msgIntent.setComponent(new ComponentName(thisContext.getPackageName(), thisContext.getPackageName() + ".util.ShowMsg"));
                    thisContext.startActivity(msgIntent);
                    mManager.removeViewImmediate(mView);
                    isExit = true;
                    break;

                case MotionEvent.ACTION_MOVE:
                    break;
            }

            return true;
        }
    };

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     */
    private void sendNotification(final Context context, JSONObject json) {

        Intent startIntent = new Intent(context, SplashActivity.class);
        //Intent mainIntent = new Intent(this, MainActivity.class);
        SharedPreferences pref = RSPreference.getPreference(context);

        //notif_YN= pref.getString(Define.NOTIF_YN, "");
        startIntent.setAction(Intent.ACTION_MAIN);

        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent =
                PendingIntent.getService(
                        context,
                        0,
                        startIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, CHECK_IT_ID, startIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        if (TextUtils.isEmpty(contents))
            return;

         boolean isAppIsInBackgrounds =isAppIsInBackground(context);
//        boolean isAppIsInBackgrounds = false;

        thisContext = context;
        msgBundle.putString(Define.MSG_TYPE, msgType);
        msgBundle.putString(Define.MSG_TITLE, title);
        msgBundle.putString(Define.MSG_CONTENTS, contents);
        msgBundle.putString(Define.MSG_SENDER_ID, senderId);
        msgBundle.putString(Define.MSG_SENDER_NM, senderNm);
        msgBundle.putString(Define.MSG_SENDER_STATUS, senderStatus);
        msgBundle.putBoolean(Define.INTENT_IS_MY_APP, isAppIsInBackgrounds == true ? false : true);
        msgBundle.putString(Define.INTENT_CLASS_NM, "MainActivity");

        msgIntent.putExtras(msgBundle);
        streamIntent.putExtras(msgBundle);
        msgIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        PushWakeLock.acquireCpuWakeLock(context);
        if (isAppIsInBackgrounds) {

            int icon = R.mipmap.liveo_logo;

            int smallIcon = R.drawable.liveo_logo;

            int mNotificationId = 100;

            doSendTest(null, context);

         /*   PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            startIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    );

            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.liveo_logo)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVibrate(new long[] {1, 1, 1})
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setContentTitle("Simple Heads-Up Notification")
                    .setContentText("This is a heads-up notification.");

            nm.notify(2, notificationBuilder.build());
*/
/*
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, startIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder mCompatBuilder = new NotificationCompat.Builder(context);
            mCompatBuilder.setSmallIcon(R.mipmap.liveo_logo);
            mCompatBuilder.setTicker(title);
            mCompatBuilder.setWhen(System.currentTimeMillis());
            mCompatBuilder.setContentTitle(title);
            mCompatBuilder.setContentText(contents);
            //mCompatBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            mCompatBuilder.setPriority(Notification.PRIORITY_MAX);
            mCompatBuilder.setContentIntent(pendingIntent2);
            mCompatBuilder.setAutoCancel(true);

            nm.notify(222, mCompatBuilder.build());*/
            // notification icon
           /* int icon = R.mipmap.liveo_logo;

            int smallIcon = R.drawable.liveo_logo;

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            long SystemCurrentTime = System.currentTimeMillis();// + Integer.parseInt((notif_YN==null || notif_YN=="" ) ? "0" : notif_YN) * 1000 * 60 * 60;
            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(contents)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setSmallIcon(icon)
                    .setContentIntent(pendingIntent)
                    .setWhen(SystemCurrentTime)
                    .setStyle(inboxStyle)
                    ;

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // ID of notification
            notificationManager.notify(0, notificationBuilder.build());

*/
/*            Intent startService = new Intent(context, MyService.class);
            startService.putExtras(msgBundle);
            context.startService(startService);
*/


/*                    LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    final View rootView = inflater.inflate(R.layout.toast_layout, null, false);
                    //View layout = inflater.inflate(R.layout.toast_layout,
                    //         (ViewGroup)rootView.findViewById(R.id.toast_layout_root));

                    TextView text = (TextView) rootView.findViewById(R.id.text);
                    text.setText(msg.getData().getString(Define.MSG_CONTENTS))  ;

                    Toast toast = new Toast(context.getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(rootView);
                    toast.show();*/

/*            Message myMessage = new Message();
            myMessage.setData(msgBundle);





            final Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {

                    mManager=  (WindowManager) context.getSystemService(context.WINDOW_SERVICE);;
                    LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    mView = mInflater.inflate(R.layout.toast_layout, null);

                    TextView text = (TextView) mView.findViewById(R.id.text);
                    text.setText(msg.getData().getString(Define.MSG_CONTENTS));


                    fade_in = new AlphaAnimation(1, 0);
                    fade_in.setDuration(2000);

                    fade_out = new AlphaAnimation(1, 1);
                    fade_out.setDuration(1);
                    mView.setAnimation(fade_in);
                    //mView = new ImageView(this);
                    //mView.setImageResource(R.mipmap.liveo_logo);
                    mView.setOnTouchListener(mViewTouchListener);

                    mParams = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.TYPE_PHONE,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSLUCENT);
                    mParams.gravity = Gravity.TOP;

                    mManager.addView(mView, mParams);

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(isExit==false) {
                                mView.setAnimation(fade_in);
                                mManager.removeView(mView);
                            }
                        }
                    }, 5000);


                }
            };



            handler.sendMessage(myMessage);*/

/*                      Snackbar snackbar = Snackbar
                            .make(mPopupView, "Had a snack at Snackbar", Snackbar.LENGTH_LONG);
                  snackbar.setActionTextColor(Color.RED);
                   View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.DKGRAY);
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
*/
            PushWakeLock.releaseCpuLock();

        } else {

            try{

                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context.getApplicationContext());


                //this.startActivity(msgIntent);

                if(msgType.equals(Define.MSG_TYPE_STREAMPLAY) || msgType.equals(Define.MSG_TYPE_STREAMOFF)) {
                    Log.d(LogTag, msgType);
                    manager.sendBroadcast(streamIntent);
                }
                else if(BroadcastActivity.onAir){

                    manager.sendBroadcast(msgIntent);
                } else {

                    msgIntent.setComponent(new ComponentName(context.getPackageName(), context.getPackageName() + ".util.ShowMsg"));
                    context.startActivity(msgIntent);

                }
                //}

                Log.i(LogTag, "Completed work @ " + SystemClock.elapsedRealtime());
            }catch(Exception e) {e.printStackTrace();}
        }

    }

}
