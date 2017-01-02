package kr.co.wegeneration.realshare;

import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.NetController.ParseNetController;
import kr.co.wegeneration.realshare.adapter.TabsPagerAdapter;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;
import kr.co.wegeneration.realshare.util.FeedImageView;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.support.v4.view.MenuItemCompat;

import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.view.View;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import kr.co.wegeneration.realshare.widget.BadgeView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private static final String LogTag = "MainActivity";

    Button btnShare, btnPull;
    static ListView lstFriend;
    TextView nameTextView, profileTextView;
    RelativeLayout mainLayout;

    ImageView imgbtnUploadPendingPods;

    String email = "";
    String passwd = "";
    String gcm_reg_id = "";
    TextView badgeCountNum;
    ImageView badgeCountImg;
    static Handler refreshFriendHandler;
    static String msgType = "";
    static String object_id = "";
    static String senderNm = "";
    static String senderId = "";
    static String senderStatus = "";
    public static String dbstatus = "";
    static TextView notif ;
    static Context thisContext;
    static Bundle thissavedInstanceState;
    static String user_id = "";
    static SharedPreferences pref;
    DialogInterface profileChangePopup;
    FrameLayout badgeCount;
    ArrayList<Drawable> icons;
    ArrayList<Drawable> iconsHilighted;
    TabLayout tabLayout = null;

    private static MainActivity mInstance = null;

    public static MainActivity getInstance(){
        if(mInstance == null){
            mInstance = new MainActivity();
        }
        return mInstance;
    }

    public void setTabTitlesToIcons() {

        icons              = new ArrayList<Drawable>();
        iconsHilighted   = new ArrayList<Drawable>();


        Drawable icon1 = getBaseContext().getResources().getDrawable(R.drawable.tap_friendlist_off);
        Drawable icon2 = getBaseContext().getResources().getDrawable(R.drawable.tap_timeline_off);
        Drawable icon3 = getBaseContext().getResources().getDrawable(R.drawable.tap_not_off);
        Drawable icon1Hilighted = getBaseContext().getResources().getDrawable(R.drawable.tap_friendlist_on);
        Drawable icon2Hilighted = getBaseContext().getResources().getDrawable(R.drawable.tap_timeline_on);
        Drawable icon3Hilighted = getBaseContext().getResources().getDrawable(R.drawable.tap_noti_on);

        icons.add(icon1);
        icons.add(icon2);
        icons.add(icon3);
        iconsHilighted.add(icon1Hilighted);
        iconsHilighted.add(icon2Hilighted);
        iconsHilighted.add(icon3Hilighted);

        for(int i = 0; i < icons.size(); i++) {
            if(i == 0) {
                //noinspection ConstantConditions
                tabLayout.getTabAt(i).setIcon(iconsHilighted.get(i));
            }
            else {
                //noinspection ConstantConditions
                tabLayout.getTabAt(i).setIcon(icons.get(i));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.tab_activity);


        pref = RSPreference.getPreference(this);
        thisContext = this;
        user_id = pref.getString(Define.USER_ID, "");

        Intent intent = getIntent();
        if( intent != null ) {


            msgType = intent.getStringExtra(Define.MSG_TYPE);
            senderId = intent.getStringExtra(Define.MSG_SENDER_ID);
            senderNm = intent.getStringExtra(Define.MSG_SENDER_NM);
            senderStatus = intent.getStringExtra(Define.MSG_SENDER_STATUS);
            dbstatus = intent.getStringExtra(Define.DB_STATUS);

            SharedPreferences.Editor editor = pref.edit();
            editor.putString(Define.MSG_TYPE, msgType);
            editor.putString(Define.MSG_SENDER_ID, senderId);
            editor.putString(Define.MSG_SENDER_NM, senderNm);
            editor.putString(Define.MSG_SENDER_STATUS, senderStatus);
            editor.putString(Define.NOTIF_YN, "0");
            editor.apply();
        }


        mainLayout                        = (RelativeLayout)findViewById(R.id.main_layout);
    /*    badgeCount                        = (FrameLayout)findViewById(R.id.badge_count);
        badgeCountImg                       = (ImageView)findViewById(R.id.badge_count_img);
        badgeCountNum                       = (TextView)findViewById(R.id.badge_count_num);

        int badge = ParseUser.getCurrentUser().getInt("badge_count");
        if(badge > 0){
            badgeCount.setVisibility(View.VISIBLE);
            badgeCountNum.setText(String.valueOf(badge));
            badgeCountNum.setVisibility(View.VISIBLE);
            badgeCountImg.setVisibility(View.VISIBLE);
            badgeCountNum.bringToFront();
            badgeCountImg.bringToFront();
        }
        else{
            badgeCount.setVisibility(View.INVISIBLE);
            badgeCountNum.setVisibility(View.INVISIBLE);
            badgeCountImg.setVisibility(View.INVISIBLE);
        }*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //View view = getLayoutInflater().inflate(R.layout.main_custom_menu, null);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.main_custom_menu);

        //getActionBar().setBackgroundDrawable(R.layout.main_custom_menu);
        //setTitle(R.drawable.menu_logo);


        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.tap_friendlist_on));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.tap_timeline_on));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.tap_noti_on));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setDrawingCacheEnabled(true);
        tabLayout.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        setTabTitlesToIcons();
        //프레그먼트 메니져 작동
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final TabsPagerAdapter adapter = new TabsPagerAdapter
                (fm, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setDrawingCacheEnabled(true);
        viewPager.setDrawingCacheQuality(TabLayout.DRAWING_CACHE_QUALITY_HIGH);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        ParseObject user = ParseUser.getCurrentUser().getParseObject(Define.UserInfo);
        if( user.getInt("badge_count") > 0 ) {

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);


            View target = findViewById(R.id.tab_layout);
            BadgeView badge = new BadgeView(this, target);

            badge.setBadgeMargin(metrics.widthPixels/4-20, metrics.heightPixels/65);
            badge.setText(String.valueOf(user.getInt("badge_count")));
            badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT_MARGIN);
            badge.show();
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                FriendFragment.isPrivate = false;


                tabLayout.getTabAt(0).setIcon(R.drawable.tap_friendlist_off);
                tabLayout.getTabAt(1).setIcon(R.drawable.tap_timeline_off);
                tabLayout.getTabAt(2).setIcon(R.drawable.tap_not_off);

                tabLayout.getTabAt(tab.getPosition()).setIcon(iconsHilighted.get(tab.getPosition()));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if(msgType!=null && msgType.equals(Define.MSG_TYPE_INSERT_COMMENT))
        {
            if(viewPager!=null)
            viewPager.setCurrentItem(0);
        }

    }


    public static TextView tv;
    public static ImageView tv_img;
    boolean isSeen = false;

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            // signin 정보 재확인 후 main으로 이동
            SharedPreferences pref = RSPreference.getPreference(thisContext);


            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(Define.ADD_FRIEND_SEEN, false);
            editor.apply();
            tv.setText("");

            String user_status = pref.getString(Define.DB_STATUS,"");
            NetController.moveToAddFriends(thisContext, user_status);

        }
    };
/*
    public void onBackPressed() {
        this.finish();
       AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.dismiss();     //닫기
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                }).setMessage(R.string.exitMain);


        alert.show();
    }
*/

    public static String getAppVersion(Context callerContext) {
        try {
            PackageInfo packageInfo = callerContext.getPackageManager()
                    .getPackageInfo(callerContext.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item         = menu.findItem(R.id.action_prev);
        MenuItem item_version = menu.findItem(R.id.action_version);


        MenuItemCompat.setActionView(item, R.layout.menu_item_cart);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
        item_version.setTitle("v"+ "("+ String.valueOf(Build.VERSION.SDK_INT)  +")"+getString(R.string.appVersion)+"(" + Build.MODEL+")");


        tv = (TextView) notifCount.findViewById(R.id.cartCountTextView);
        tv_img = (ImageView) notifCount.findViewById(R.id.cartCountImageView);
        tv_img.setVisibility(View.GONE);
        SharedPreferences pref =  RSPreference.getPreference(thisContext);

        boolean isSeen =  pref.getBoolean(Define.ADD_FRIEND_SEEN, false);

        Log.d("AddFriendSeen", String.valueOf(isSeen));

        /*    refreshFriendHandler = new Handler() {
                public void handleMessage(Message msg) {
                    AddFriendActivity.callAddFriendListCnt();

                    refreshFriendHandler.sendEmptyMessageDelayed(10, 40000);
                }
            };
            refreshFriendHandler.sendEmptyMessage(10);*/

        menu.findItem(R.id.action_prev).getActionView().setOnClickListener(mClickListener);

        return true;
    }

    @Override
    public void onPause() {
        super.onPause();


        ParseNetController.ActiveOnOff(thisContext, "n");
        /*NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.ActiveOnOff(thisContext, user_id, "n"));*/

    }

    @Override
    public void onResume() {
        super.onResume();

        ParseNetController.ActiveOnOff(thisContext, "y");
/*        NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.ActiveOnOff(thisContext, user_id, "y"));*/
    }

    public void callBadgeCount(){
        Intent intents = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        // 패키지 네임과 클래스 네임 설정
        intents.putExtra("badge_count_package_name", MainActivity.thisContext.getPackageName());
        intents.putExtra("badge_count_class_name", MainActivity.thisContext.getClass().getName());
        // 업데이트 카운트
        intents.putExtra("badge_count", ParseUser.getCurrentUser().getInt("badge_count"));
        MainActivity.thisContext.sendBroadcast(intents);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /*inflater.inflate(R.menu.badge, menu);

        RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.badge).getActionView();
        TextView tv = (TextView) badgeLayout.findViewById(R.id.actionbar_notifcation_textview);
        tv.setText("12");
        */
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            ParseNetController.SignOut(thisContext);

            return true;
        }

        if (id == R.id.action_alarm) {

            NetController.moveToNotificationSetting(thisContext, user_id);
            return true;
        }

        if(id == R.id.action_invite){

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "안녕! 친구끼리 쓰는 실시간 영상 공유 SNS, LIVEO 테스트 버전 한 번 써보고 있는데 같이\n" +
                    "써보자. 여기에서 다운로드 가능! http://liveo.me/dl_beta";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Liveo-공유해보세요");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivityForResult(Intent.createChooser(sharingIntent, "Share via"),1000);

        }

        if(id == R.id.action_prev) {

            // signin 정보 재확인 후 main으로 이동

            String user_status = pref.getString(Define.DB_STATUS,"");
            ParseNetController.moveToAddFriends(thisContext, user_status);
        }

        /*if (id == R.id.action_settings) {
                    final Intent intent = getIntent();

                    final String str[] = {"알람 설정", "1시간 동안 알림 끄기", "4시간 동안 알림 끄기", "8시간 동안 알림 끄기"};
                    View rootView = getWindow().getDecorView().getRootView();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
                    builder.setTitle(R.string.title_alarm);
                    builder.setItems(str, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SharedPreferences.Editor editor = pref.edit();
                            if (which == 0) editor.putString(Define.NOTIF_YN, "0");
                            if (which == 1) editor.putString(Define.NOTIF_YN, "1");
                            if (which == 4) editor.putString(Define.NOTIF_YN, "4");
                            if (which == 8) editor.putString(Define.NOTIF_YN, "8");
                            editor.apply();

                            Toast.makeText(getApplicationContext(), str[which], Toast.LENGTH_SHORT).show();

                            profileChangePopup.dismiss();
                        }
                    });

                    profileChangePopup = builder.show();


            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }
}