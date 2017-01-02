package kr.co.wegeneration.realshare.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.parse.ParseInstallation;

import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.app.MyApplication;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.GetCallback;
import java.text.ParseException;

/**
 * Created by admin on 2015-08-12.
 */
public class GCMRegister  {
    private static final String LogTag = "GCMRegister";
    private static final String SENDER_ID = "586276884901"; // "586276884901"; 138519417205
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static Context callerContext;

    private static String _regId;

    public static String register(Context context) {
        callerContext = context;
        if (checkPlayServices()) {
            _regId = getRegistrationId();

           /* try {
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(callerContext.getApplicationContext());
                gcm.unregister();
            }catch(IOException e){ e.printStackTrace(); }
            */

            if (_regId.isEmpty()) {
                registerInBackground();
            }
            Log.d(LogTag, "regId : " + _regId);
        } else {
            Log.i(LogTag, "No valid Google Play Services APK found.");
            _regId = "";
        }
        return _regId;
    }

    public static  GCMRegister newInstance(Context context) {
        callerContext = context;
        return new GCMRegister();
    }

    // google play service가 사용가능한가
    public static boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(callerContext);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity)callerContext,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Log.i(LogTag, "MainActivity.java | checkPlayService | This device is not supported.|");
            }
            return false;
        }
        return true;
    }

    public static String getRegistrationId() {
        final SharedPreferences prefs = RSPreference.getPreference(callerContext.getApplicationContext());
        String registrationId = prefs.getString(Define.GCM_REG_ID, null);
        if (registrationId == null) {
            Log.i(LogTag, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(Define.GCM_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.i(LogTag, "App version changed.");
            return "";
        }
        return registrationId;
    }

    public static int getAppVersion() {
        try {
            PackageInfo packageInfo = callerContext.getPackageManager()
                    .getPackageInfo(callerContext.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static void registerInBackground() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {

                    String scope = "GCM"; // e.g. communicating using GCM, but you can use any
                    // URL-safe characters up to a maximum of 1000, or
                    // you can also leave it blank.
                    //String token = InstanceID.getInstance(callerContext).getToken(SENDER_ID,scope);

                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(callerContext.getApplicationContext());
                    _regId = gcm.register(SENDER_ID);
                    //_regId = token;

                    msg = "Device registered, registration ID=" + _regId;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(_regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(LogTag, "GCM ID is "+ msg);
            }
        }.execute(null, null, null);
    }

    private static void sendRegistrationIdToBackend() {
        // Your implementation here.
        //TODO if want to change

        /*Map<String, String> param = new HashMap<String, String>();
        final SharedPreferences prefs = RSPreference.getPreference(callerContext.getApplicationContext());
        param.put(Define.ACTION, Define.ACTION_REGISTRATION);
        param.put(Define.DB_OBJECT_ID, MyApplication.object_id);
        param.put(Define.DB_GCM_ID,    getRegistrationId());
        param.put(Define.DB_USER_ID , prefs.getString(Define.DB_USER_ID, ""));
        param.put(Define.GCM_APP_VERSION, String.valueOf(getAppVersion()));

        NetController.getInstance(callerContext)
                .getRequestQueue()
                .add(NetController.ReigisterToBackEnd(callerContext, param));
*/
  /*      ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("deviceToken", getRegistrationId());
        installation.saveInBackground();*/


    }

    private static void storeRegistrationId( String regId) {
        final SharedPreferences prefs = RSPreference.getPreference(callerContext.getApplicationContext());
        int appVersion = getAppVersion();
        Log.i("storeRegistrationId", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Define.GCM_REG_ID, regId);
        editor.putInt(Define.GCM_APP_VERSION, appVersion);

        editor.commit();
    }

}

