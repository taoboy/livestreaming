package kr.co.wegeneration.realshare.gcm;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONObject;

import java.util.List;

import kr.co.wegeneration.realshare.BroadcastActivity;
import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.SplashActivity;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;

/**
 * Created by SSH on 15. 7. 30..
 */
public class BroadCastReceiver extends BroadcastReceiver {

    private static final String LogTag = "BroadcastReceiver";

    //TODO Change Request Code
    private static final int CHECK_IT_ID = 880218;

    String msgType = "";
    String title = "";
    String contents = "";
    String senderId = "";
    String senderNm = "";
    String senderStatus = "";
    String notif_YN = "";

    Context context;
    /**
     * Called when message is received.
     *
     * @param ctx SenderID of the sender.
     * @param intent Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onReceive(Context ctx, Intent intent) {

        try {

            context = ctx;
            String action = intent.getAction();
            String channel = intent.getExtras().getString("com.parse.Channel");
            JSONObject data = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            Log.d("onMessageReceived", data.toString());
            try {
                msgType = data.getString(Define.MSG_TYPE);
                title = data.getString(Define.MSG_TITLE);
                contents = data.getString(Define.MSG_CONTENTS);
                senderId = data.getString(Define.MSG_SENDER_ID);
                senderNm = data.getString(Define.MSG_SENDER_NM);
                senderStatus = data.getString(Define.MSG_SENDER_STATUS);

            }catch(Exception e ) {e.printStackTrace();}
            Log.d(LogTag, "Message : " + msgType + "/" + title + "/" + contents + "/" );
            Log.d(LogTag, "Message : " + senderId + "/" + senderNm + "/" );

            /**
             * Production applications would usually process the message here.
             * Eg: - Syncing with server.
             *     - Store message in local database.
             *     - Update UI.
             */

            /**
             * In some cases it may be useful to show a notification indicating to the user
             * that a message was received.
             */
/*            Intent startIntent = new Intent(this, MyService.class);
            this.startService(startIntent);
            Toast.makeText(this.getApplicationContext(), "test",Toast.LENGTH_SHORT).show();
*/
            if (msgType == null) return;

            sendNotification();
        }catch (Exception e){
//            e.printStackTrace();

        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     */
    private void sendNotification() {

        Intent startIntent = new Intent(context, SplashActivity.class);
        //Intent mainIntent = new Intent(this, MainActivity.class);
        SharedPreferences pref = RSPreference.getPreference(context);

        //notif_YN= pref.getString(Define.NOTIF_YN, "");
        startIntent.setAction(Intent.ACTION_MAIN);

        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, CHECK_IT_ID, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        if( ! msgType.equals( Define.MSTPCD_SIGNIN ) || msgType.equals( Define.MSTPCD_SIGNOUT ) ) {
           long SystemCurrentTime = System.currentTimeMillis();// + Integer.parseInt((notif_YN==null || notif_YN=="" ) ? "0" : notif_YN) * 1000 * 60 * 60;
            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(contents)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setWhen(SystemCurrentTime)
                    ;

            NotificationManager notificationManager =
                    (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            // ID of notification
            notificationManager.notify(0 , notificationBuilder.build());


        /////
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runList = am.getRunningTasks(10);

            ComponentName name = runList.get(0).topActivity;
            String className = name.getClassName();

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = pm.isScreenOn();

            boolean isMyApp = className.contains(context.getPackageName());

            PushWakeLock.acquireCpuWakeLock(context);


        //Intent msgIntent = new Intent();
        Intent msgIntent = new Intent("MSG");
        Intent streamIntent = new Intent("Stream");
        Intent snackBarIntent = new Intent("SnackBar");
        Intent serviceIntent = new Intent(context, MyService.class);

        Bundle msgBundle = new Bundle();
        msgBundle.putString(Define.MSG_TYPE, msgType);
        msgBundle.putString(Define.MSG_TITLE, title);
        msgBundle.putString(Define.MSG_CONTENTS, contents);
        msgBundle.putString(Define.MSG_SENDER_ID, senderId);
        msgBundle.putString(Define.MSG_SENDER_NM, senderNm);
        msgBundle.putString(Define.MSG_SENDER_STATUS, senderStatus);
        msgBundle.putBoolean(Define.INTENT_IS_MY_APP, isMyApp);
        msgBundle.putString(Define.INTENT_CLASS_NM, className);

        msgIntent.putExtras(msgBundle);
        streamIntent.putExtras(msgBundle);
        snackBarIntent.putExtras(msgBundle);
        //msgIntent.setComponent(new ComponentName(this.getPackageName(), this.getPackageName() + ".util.ShowMsg"));
        msgIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);

        //this.startActivity(msgIntent);

        if(msgType.equals(Define.MSG_TYPE_STREAMPLAY) || msgType.equals(Define.MSG_TYPE_STREAMOFF)) {
                    Log.d(LogTag, msgType);
                    manager.sendBroadcast(streamIntent);
        }
        else if(BroadcastActivity.onAir){

                   manager.sendBroadcast(msgIntent);
        } else {

                msgIntent.setComponent(new ComponentName(context.getPackageName(), context.getPackageName() + ".util.ShowMsg"));

                if(isMyApp) {
                    context.startActivity(msgIntent);
                }
              else {

                }
        }

        Log.i(LogTag, "Completed work @ " + SystemClock.elapsedRealtime());
        }catch(Exception e) {e.printStackTrace();}

    }

}
