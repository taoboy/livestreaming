package kr.co.wegeneration.realshare.app;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
//import android.support.multidex.MultiDex;
//import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
///import com.facebook.FacebookSdk;
import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;
import com.parse.ParseACL;
//import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;

import io.vov.vitamio.Vitamio;
import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.models.RoomAllowed;
import kr.co.wegeneration.realshare.models.Friend;
import kr.co.wegeneration.realshare.models.Notification;
import kr.co.wegeneration.realshare.models.Room;
import kr.co.wegeneration.realshare.models.RoomComment;
import kr.co.wegeneration.realshare.models.StatusComment;
import kr.co.wegeneration.realshare.models.TimeLine;
import kr.co.wegeneration.realshare.models.UserInfo;
import kr.co.wegeneration.realshare.util.LruBitmapCache;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Field;

public class MyApplication extends Application { // android.support.multidex.MultiDexApplication {

    private static MyApplication sInstance;

    public static final String TAG = MyApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;


    /**
     * @return ApplicationController singleton instance
     */
    public static synchronized MyApplication getInstance() {
        return sInstance;
    }



    public static void setDefaultFont(Context ctx,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(ctx.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        try {
            final Field StaticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            StaticField.setAccessible(true);
            StaticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private void setupParse(Context context) {

        //ParseUser.enableAutomaticUser();

        ParseObject.registerSubclass(UserInfo.class);
        ParseObject.registerSubclass(Notification.class);
        ParseObject.registerSubclass(Friend.class);
        ParseObject.registerSubclass(RoomAllowed.class);
        ParseObject.registerSubclass(StatusComment.class);
        ParseObject.registerSubclass(Room.class);
        ParseObject.registerSubclass(RoomComment.class);
        ParseObject.registerSubclass(TimeLine.class);

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
        Parse.enableLocalDatastore(getApplicationContext());

        Parse.initialize(this, getResources().getString(R.string.applicationid), getResources().getString(R.string.clientkey));
        ParseUser.enableRevocableSessionInBackground();
        //PushService.startServiceIfRequired(context);
        ParseInstallation.getCurrentInstallation().put("uniqueId", getWifiMacAddress(context));
        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully save  the current installation.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

    }

    private String getWifiMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && wifiManager.getConnectionInfo() != null) {
            return wifiManager.getConnectionInfo().getMacAddress();
        }

        return "";
    }

    /*@Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }*/


    @Override
    public void onCreate() {

        //MultiDex.install(this);
        //   ParseFacebookUtils.initialize(getApplicationContext());
        super.onCreate();

        setDefaultFont(this, "DEFAULT", "BRI293.TTF");

        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(getApplicationContext());

        Vitamio.initialize(this);
        setupParse(getApplicationContext());

        sInstance = this;

    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


}
