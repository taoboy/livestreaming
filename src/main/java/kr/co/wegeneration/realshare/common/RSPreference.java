package kr.co.wegeneration.realshare.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ytlim on 2015-08-07.
 */
public class RSPreference {
    public static final int MODE_PRIVATE = 0;

    Context context;
    static SharedPreferences pref;

    public RSPreference(Context ctx)
    {
        this.context = ctx;
        if( pref == null ) {
            pref = ctx.getSharedPreferences(ctx.getPackageName(), MODE_PRIVATE);
        }
    }

    public static RSPreference newInstance(Context ctx) {
        return new RSPreference(ctx);
    }

    public static SharedPreferences getPreference(Context ctx) {
        if( pref == null ) {
            pref = ctx.getSharedPreferences(ctx.getPackageName(), MODE_PRIVATE);
        }
        return pref;
    }

    public static String getString(String key)
    {
        return pref.getString(key, "");
    }

    public static Boolean setString(String key, String value)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static int getInt(String key)
    {
        return pref.getInt(key, 0);
    }

    public static Boolean setInt(String key, int value)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static Boolean getBoolean(String key)
    {
        return pref.getBoolean(key, false);
    }

    public static Boolean setBoolean(String key, Boolean value)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public Boolean checkFirst() {
        if( pref.getString(Define.USER_ID, "").isEmpty() ) return true;
        else if( pref.getString(Define.USER_EMAIL, "").isEmpty() ) return true;
        else if (pref.getString(Define.USER_PASSWD, "").isEmpty() ) return true;
        else if (pref.getString(Define.USER_GCM_ID, "").isEmpty() ) return true;
        return false;
    }
}
