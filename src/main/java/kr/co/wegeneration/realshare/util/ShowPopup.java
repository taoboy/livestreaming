package kr.co.wegeneration.realshare.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import kr.co.wegeneration.realshare.FriendFragment;
import kr.co.wegeneration.realshare.MainActivity;
import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.SplashActivity;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;
import kr.co.wegeneration.realshare.gcm.PushWakeLock;

/**
 * Created by admin on 2015-08-17.
 */
public class ShowPopup extends AppCompatActivity {
    private static final String LogTag = "ShowPopup";

    static Context thisContext;

    private PopupWindow popWindow;

    static String user_id = "";
    static String user_nm = "";
    static String senderStatus="";
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_layout);
        onShowPopup();

    }


    // call this method when required to show popup
    public void onShowPopup(){

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        final View inflatedView = layoutInflater.inflate(R.layout.fb_popup_layout, null,false);
        // find the ListView in the popup layout
        ListView listView = (ListView)inflatedView.findViewById(R.id.commentsListView);

        // get device size
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
//        mDeviceHeight = size.y;

        // fill the data to the list items
        setSimpleList(listView);

        Drawable d = new ColorDrawable(Color.WHITE);
        d.setAlpha(130);

        // set height depends on the device size
        popWindow = new PopupWindow(inflatedView, size.x,size.y-200, true );
        // set a background drawable with rounders corners
        popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.fb_popup_bg));
        // make it focusable to show the keyboard to enter in `EditText`
        popWindow.setFocusable(true);
        // make it outside touchable to dismiss the popup window
        popWindow.setOutsideTouchable(true);
        // ....other code, whatever you want to do with your popupWindow (named dialog in our case here)
        popWindow.setAnimationStyle(R.style.animationName);

        //popWindow.setBackgroundDrawable(d);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(ShowPopup.this.getParent().getWindow().getDecorView(), Gravity.BOTTOM, 0, 200);

        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Drawable d = new ColorDrawable(Color.WHITE);
                finish();
            }
        });
        //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

    }


    void setSimpleList(ListView listView){

        ArrayList<String> contactsList = new ArrayList<String>();

        for (int index = 0; index < 10; index++) {
            contactsList.add("  I am @ index " + index + " today " + Calendar.getInstance().getTime().toString());
        }

        //   commentListAdapter =  new CommentListAdapter(thisContext, R.layout.fb_comments_list_item, new ArrayList<CommentLog>(), false);

        listView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.fb_comments_list_item, android.R.id.text1, contactsList));
    }

}
