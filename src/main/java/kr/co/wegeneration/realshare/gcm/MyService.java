package kr.co.wegeneration.realshare.gcm;

/**
 * Created by User on 2015-10-18.
 */
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.view.WindowManager;
import android.widget.Toast;

import kr.co.wegeneration.realshare.MainActivity;
import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.common.Define;

import android.util.Log;
public class MyService extends Service {
    //private ImageView mView;
    private View mView;
    private WindowManager mManager;
    private WindowManager.LayoutParams mParams;
    private Intent msgIntent;
    private float mTouchX, mTouchY;
    private int mViewX, mViewY;

    private boolean isMove = false;

    private OnTouchListener mViewTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isMove = false;

                    mTouchX = event.getRawX();
                    mTouchY = event.getRawY();
                    mViewX = mParams.x;
                    mViewY = mParams.y;

                    break;

                case MotionEvent.ACTION_UP:
                    if (!isMove) {
                        Toast.makeText(getApplicationContext(), "Go LIVEO",
                                Toast.LENGTH_SHORT).show();

                        msgIntent.putExtra(Define.INTENT_IS_MY_APP, true);
                        msgIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        msgIntent.setComponent(new ComponentName(getApplicationContext().getPackageName(), getApplicationContext().getPackageName() + ".util.ShowMsg"));
                        startActivity(msgIntent);
                        mManager.removeViewImmediate(mView);
                    }

                    break;

                case MotionEvent.ACTION_MOVE:
                    isMove = true;

                    int x = (int) (event.getRawX() - mTouchX);
                    int y = (int) (event.getRawY() - mTouchY);

                    final int num = 5;
                    if ((x > -num && x < num) && (y > -num && y < num)) {
                        isMove = false;
                        break;
                    }

                    mParams.x = mViewX + x;
                    mParams.y = mViewY + y;

                    mManager.updateViewLayout(mView, mParams);

                    break;
            }

            return true;
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        Log.d("OnStartCommand", String.valueOf(intent.getData()));
        msgIntent = intent;

        //mManager.addView(mView, mParams);
        mManager.notify();
        return super.onStartCommand(intent, flags, startId);
    }




    @Override
    public void onCreate() {
        super.onCreate();

        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = mInflater.inflate(R.layout.toast_layout, null);

        //mView = new ImageView(this);
        //mView.setImageResource(R.mipmap.liveo_logo);
        mView.setOnTouchListener(mViewTouchListener);

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.TOP | Gravity.LEFT;

        mManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mManager.addView(mView, mParams);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView != null) {
            mManager.removeView(mView);
            mView = null;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        msgIntent= arg0;
        return null;
    }

}
