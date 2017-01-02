package kr.co.wegeneration.realshare.gcm;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
public class GCMIntentService_BACKUP extends GcmListenerService {

    private static final String LogTag = "GCMIntentService";

    //TODO Change Request Code
    private static final int CHECK_IT_ID = 880218;

    String msgType = "";
    String title = "";
    String contents = "";
    String senderId = "";
    String senderNm = "";
    String senderStatus = "";
    String notif_YN = "";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param msg Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle msg) {

        try {

            JSONObject data = new JSONObject(msg.getString("data"));

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

        Intent startIntent = new Intent(this, SplashActivity.class);
        //Intent mainIntent = new Intent(this, MainActivity.class);
        SharedPreferences pref = RSPreference.getPreference(this);

        //notif_YN= pref.getString(Define.NOTIF_YN, "");
        startIntent.setAction(Intent.ACTION_MAIN);

        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, CHECK_IT_ID, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        if( ! msgType.equals( Define.MSTPCD_SIGNIN ) || msgType.equals( Define.MSTPCD_SIGNOUT ) ) {
           long SystemCurrentTime = System.currentTimeMillis();// + Integer.parseInt((notif_YN==null || notif_YN=="" ) ? "0" : notif_YN) * 1000 * 60 * 60;
            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(contents)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setWhen(SystemCurrentTime)
                    ;

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // ID of notification
            notificationManager.notify(0 , notificationBuilder.build());


        /////
        try {
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runList = am.getRunningTasks(10);

            ComponentName name = runList.get(0).topActivity;
            String className = name.getClassName();

            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = pm.isScreenOn();

            boolean isMyApp = className.contains(this.getPackageName());

            PushWakeLock.acquireCpuWakeLock(this);


        //Intent msgIntent = new Intent();
        Intent msgIntent = new Intent("MSG");
        Intent streamIntent = new Intent("Stream");
        Intent snackBarIntent = new Intent("SnackBar");
        Intent serviceIntent = new Intent(this, MyService.class);

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


        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());

        //this.startActivity(msgIntent);

        if(msgType.equals(Define.MSG_TYPE_STREAMPLAY) || msgType.equals(Define.MSG_TYPE_STREAMOFF)) {
                    Log.d(LogTag, msgType);
                    manager.sendBroadcast(streamIntent);
        }
        else if(BroadcastActivity.onAir){

                   manager.sendBroadcast(msgIntent);
        } else {

                msgIntent.setComponent(new ComponentName(this.getPackageName(), this.getPackageName() + ".util.ShowMsg"));

                if(isMyApp) {
                    this.startActivity(msgIntent);
                }
              else {
                    //this.startService(serviceIntent);
                    Message myMessage = new Message();
                    myMessage.setData(msgBundle);

                    Handler handler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {

                            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                            final View rootView = inflater.inflate(R.layout.toast_layout, null, false);
                            View layout = inflater.inflate(R.layout.toast_layout,
                                    (ViewGroup)rootView.findViewById(R.id.toast_layout_root));

                            TextView text = (TextView) layout.findViewById(R.id.text);
                            text.setText(msg.getData().getString(Define.MSG_CONTENTS))  ;

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();

                        }
                    };
                    handler.sendMessage(myMessage);
                }
        }

        Log.i(LogTag, "Completed work @ " + SystemClock.elapsedRealtime());
        }catch(Exception e) {e.printStackTrace();}

    }

}
