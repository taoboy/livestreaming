package kr.co.wegeneration.realshare.common;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by systran on 2015-08-07.
 */
public class RSToast {
    static String[] msgs = {
            "OK", "FAIL", "ERROR"
    };
    //TODO -*ssh*- showing Toast
    public static void showToast(Context ctx, String text) {
        Toast toast = Toast.makeText(ctx.getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showToast(Context ctx, Integer key) {
        Toast toast = Toast.makeText(ctx.getApplicationContext(), msgs[key], Toast.LENGTH_SHORT);
        toast.show();
    }
}
