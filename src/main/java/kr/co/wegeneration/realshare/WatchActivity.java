package kr.co.wegeneration.realshare;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parse.ParseUser;


import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.VitamioLicense;
import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.NetController.ParseNetController;
import kr.co.wegeneration.realshare.adapter.ChatFriendListParseAdapter;
import kr.co.wegeneration.realshare.chat.Chat;
import kr.co.wegeneration.realshare.chat.ChatListAdapter;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;

//import org.apache.http.Header;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import kr.co.wegeneration.realshare.util.BackPressEditText;

import android.widget.ImageView;


public class WatchActivity extends ListActivity implements OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback {

    private static final String LogTag = "WatchActivity";
    private static final String FIREBASE_URL = "https://liveo-27.firebaseio.com";

    public static boolean onAir = true;
    static Context thisContext;

    static String callerActivity = "";

    private PopupWindow popWindow;
    private TextView chatNumber;

    static String user_id = "";
    static String user_nm = "";
    static String owner_id = "";
    static String roomname = "";
    static String str_chat_server_url = "";

    private static final String ABS_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String REAL_SHARE_PATH = "real_share";
    private static final String RECORD_FILE_SURFIX = ".mp4";
    private static final String RECORD_FILE_PREFIX = "voice_";

    private static final int    voiceMethod = 2; // dpmin : voice message방식 선택 (1.누른후 확인버튼, 2.누르는 동안)

    private WebView chatView;
    private TextView chatViewLoadding;
    private BackPressEditText messageInput;
    private Button mVoiceBtn;
    private ImageView chatTotal;
    private TextView chatTotalNumber;
    private boolean voiceplaying= false;
    private ValueEventListener mChatNumberListener;
    private Firebase mFirebaseRef;
    private Firebase mFirebaseChatInOut;
    private Firebase mFirebasePeopleRef;
    private ValueEventListener mConnectedListener;
    private ValueEventListener mScreenInOutListener;
    private ValueEventListener mMessageListener;
    private ChatListAdapter mChatListAdapter;


//    Button btnRefresh;

    /**
     * TODO: Set the path variable to a streaming video URL or a local media file
     * path.
     */
    private static final String TAG = "Stream wowza";

    private android.media.MediaPlayer mPlayer = null;
    static public int mVideoWidth;
    static public int mVideoHeight;
    static public MediaPlayer mMediaPlayer;
    static public MediaPlayer mMediaPlayer_pause;
    static public SurfaceView mVideoView;
    static public SurfaceHolder holder;
    static public TextView     stopView;
    static public LinearLayout     inputWrapper;
    static public ProgressBar     stopProgressView;
    static public String str_voice_upload_url="";
    private SharedPreferences pref;
    private Bundle extras;
    private static final String MEDIA = "media";
    private static final int LOCAL_AUDIO = 1;
    private static final int STREAM_AUDIO = 2;
    private static final int RESOURCES_AUDIO = 3;
    private static final int LOCAL_VIDEO = 4;
    private static final int STREAM_VIDEO = 5;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;

    public MediaPlayer getMediaPlayer ()
    {
        return mMediaPlayer;
    }
    public SurfaceView getSurfaceView ()
    {
        return mVideoView;
    }

    public SurfaceHolder getSurfaceHolder ()
    {
        return holder;
    }

    public TextView getStopView ()
    {
        return stopView;
    }

    private static WatchActivity mInstance = null;

    public static WatchActivity getInstance(){
        if(mInstance == null){
            mInstance = new WatchActivity();
        }
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        thisContext = this;

        pref = RSPreference.getPreference(this);
        try {
            user_id = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_USER_ID);
            user_nm = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).fetchIfNeeded().getString(Define.DB_USER_NM);
            str_voice_upload_url = pref.getString(Define.VOICE_UPLOAD_URL, "");
            str_chat_server_url = pref.getString(Define.CHAT_SERVER_URL, "");
        }catch(Exception e) { e.printStackTrace(); }

        Intent intent = getIntent();
        if( intent != null ) {
            callerActivity  = intent.getStringExtra(Define.CALLER_ACTIVITY);
            owner_id        = intent.getStringExtra(Define.OWNER_ID);
            roomname        = intent.getStringExtra(Define.DB_ROOM);
        }

        Log.d("ownerId", roomname);

        setupLayout();
        setupListener();
        //setupChatView();
        WatchTimeLine();
        setChatLayout();

        Log.d("fuck3", String.valueOf(messageInput.hasFocus()))  ;
        setupVideoStream();
        playVideo();
        Log.d("fuck4", String.valueOf(messageInput.hasFocus()))  ;
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("Stream"));

        Log.d("fuck5", String.valueOf(messageInput.hasFocus()))  ;
    }

    private void WatchTimeLine() {

        Map<String, String> param = new HashMap<>();

        param.put(Define.DB_ROOM, roomname);
        param.put(Define.BROAD_STATUS, "live");
        param.put(Define.MSG_ROOM, Define.MSG_WATCH_ROOM);

        ParseNetController.TimeLine(thisContext, param);
    }



    private void sendMessage(String msg ,String MessageType, String voicePath) {
        String input = msg;
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            Chat chat = new Chat(input, user_nm, System.currentTimeMillis(), MessageType,  voicePath);
            // Create a new, auto-generated child of that chat location, and save our chat data there

            mFirebaseRef.push().setValue(chat);
        }
    }

    private void sendMessage(String MessageType, String voicePath) {
        BackPressEditText inputText = (BackPressEditText) findViewById(R.id.inputMsg);
        String input = inputText.getText().toString();

        if (!input.equals("")) {
            // Create our 'model', a Chat object
            Chat chat = new Chat(input, user_nm, System.currentTimeMillis(), MessageType, voicePath);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(chat);
            inputText.setText("");
        }
    }

    private void setChatLayout(){
        // Make sure we have a mUsername
        //setupUsername();

        //setTitle("Chatting as " + mUsername);

        Firebase.setAndroidContext(this);
        // Setup our Firebase mFirebaseRef
        mFirebaseRef        = new Firebase(FIREBASE_URL).child("chat").child(roomname).child("messageList");
        mFirebaseChatInOut     = new Firebase(FIREBASE_URL).child("chat").child(roomname).child("ScreenInOut");
        mFirebasePeopleRef  = new Firebase(FIREBASE_URL).child("chat").child(roomname).child("PeopleList");
        mFirebasePeopleRef.child(user_id).push().setValue(user_id);

        sendMessage("joined.", "Event", "");
        // Setup our input methods. Enter key on the keyboard or pushing the send button
        BackPressEditText inputText = (BackPressEditText) findViewById(R.id.inputMsg);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage("Text", "");
                }
                return true;
            }
        });

        /*findViewById(R.id.btnSendChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });*/

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Bundle bundle = intent.getExtras();
            Log.d(LogTag, "BroadCastReceiver" + " : " + bundle.getString(Define.MSG_TYPE, ""));
            switch (bundle.getString(Define.MSG_TYPE, "")){
                case Define.MSG_TYPE_STREAMOFF:
                    Log.d(LogTag, Define.STREAM_PAUSE);
                    stopView.setVisibility(View.VISIBLE);
                    stopProgressView.setVisibility(View.VISIBLE);
                    //stopView.bringToFront();
                    //mMediaPlayer.pause();
                    break;
                case Define.MSG_TYPE_STREAMPLAY:
                    Log.d(LogTag, Define.STREAM_PLAY);
                    stopView.setVisibility(View.INVISIBLE);
                    stopProgressView.setVisibility(View.INVISIBLE);
                    //startVideoPlayback();
                    //mMediaPlayer.start();
                    break;
                default:
                    break;
            }
        }
    };


    public void deletePlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    android.media.MediaPlayer.OnCompletionListener mCompleteListener =
            new android.media.MediaPlayer.OnCompletionListener() {
                public void onCompletion(android.media.MediaPlayer arg0) {
                    Log.d("MediaPlayer", "Ended");
                    deletePlayer();
                }
            };


    public boolean LoadMedia(String filePath) {
        try {
            if(voiceplaying==false) {
                mPlayer = new android.media.MediaPlayer();
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setDataSource(filePath);
                mPlayer.setOnCompletionListener(mCompleteListener);
                mPlayer.prepare();
                mPlayer.start();
                voiceplaying = true;
            }
            else{
                voiceplaying = false;
                deletePlayer();
            }

        } catch (IOException e) {
            return false;
        }

        return true;
    }


    public void onPlayWeb(String url) {
        deletePlayer();
        LoadMedia(url);
        Log.d("MediaPlayer", "Started_test");
    }


    @Override
    public void onStart() {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 50 messages at a time
        mChatListAdapter = new ChatListAdapter(mFirebaseRef.limit(50), this, R.layout.chat_message, user_nm, getAssets());
        listView.setSelector(R.drawable.list_selector);
        //listView.setClickable(false);
        listView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);

            }
        });

        final Animation animation = AnimationUtils.loadAnimation(this,
                R.anim.slide_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //listView.removeViewAt(0);
                mChatListAdapter.notifyDataSetChanged();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //view.startAnimation(animation);

            }

        });

        mScreenInOutListener = mFirebaseChatInOut.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    if (dataSnapshot.getValue().equals("ScreenOn")) {
                        stopView.setVisibility(View.INVISIBLE);
                        stopProgressView.setVisibility(View.INVISIBLE);
                    } else if (dataSnapshot.getValue().equals("ScreenOff")) {
                        Typeface typeface = Typeface.createFromAsset(getAssets(), "BRI293.TTF");
                        stopView.setTypeface(typeface);
                        stopView.setVisibility(View.VISIBLE);
                        stopProgressView.setVisibility(View.VISIBLE);
                        stopView.bringToFront();

                        /*AnimationDrawable frameAnimation = (AnimationDrawable)imgView.getBackground();
                        frameAnimation.start();
                        frameAnimation.stop();*/
                    } else if (dataSnapshot.getValue().equals("Exit")) {
                        onAir = false;
                        AlertDialog.Builder alert = new AlertDialog.Builder(WatchActivity.this)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finishRoom();
                                        //  releaseMediaPlayer();
                                        dialog.dismiss(); //닫기
                                        finish();
                                    }
                                }).setMessage(getString(R.string.roomEnded));
                        alert.show();
                    }
                } catch(Exception e ){ e.printStackTrace(); }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mChatNumberListener = mFirebasePeopleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatTotalNumber.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mMessageListener = mFirebaseRef.limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    Chat chat = messageSnapshot.getValue(Chat.class);
                    if(chat.getMessageType().equals("Voice")) {
                        if (chat.getVoicePath() != null)
                            onPlayWeb(chat.getVoicePath());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });

        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();

                if (connected) {
                    Toast.makeText(WatchActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WatchActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }


    // 흔들기가 끝나면 호출되는 함수
    public void onStop()
    {
        super.onStop();

        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mFirebaseRef.removeEventListener(mMessageListener);
        mFirebasePeopleRef.removeEventListener(mChatNumberListener);
        mChatListAdapter.cleanup();

        releaseMediaPlayer();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        Log.d(LogTag, "onPause");
        super.onPause();
        ParseNetController.ActiveOnOff(thisContext, "nw");
        /*NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.ActiveOnOff(thisContext, user_id, "n"));*/

        if ( mProgressDialog != null ) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        releaseMediaPlayer();
        doCleanUp();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // dpmin : 화면 자동꺼짐 켬
    }

    @Override
    protected void onResume() {
        Log.d(LogTag, "onResume");
        super.onResume();
        onAir=true;

        messageInput.clearFocus();
        ParseNetController.ActiveOnOff(thisContext, "y");
        /*NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.ActiveOnOff(thisContext, user_id, "y"));*/


        if (onAir == true) {
            setupVideoStream();
            playVideo();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // dpmin : 화면 자동꺼짐 끔
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    @Override
    protected void onDestroy() {

        if (chatView != null) {
            ViewGroup parent = (ViewGroup) chatView.getParent();
            if (parent != null) {
                parent.removeView(chatView);
            }
            chatView.removeAllViews();
            chatView.destroy();
        }
        super.onDestroy();
        Log.d(LogTag, "onDestroy");
        super.onDestroy();

        releaseMediaPlayer();
        doCleanUp();


    }

    ProgressDialog mProgressDialog;
    /*@Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder alert = new AlertDialog.Builder(WatchActivity.this)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishRoom();

                            dialog.dismiss();     //닫기

                            mProgressDialog = ProgressDialog.show(WatchActivity.this, "", "종료 중입니다.", true);
                        }
                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                        }
                    }).setMessage("방송에서 나가시겠습니까?");

            alert.show();

            return false;
        }

        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(WatchActivity.this)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        finishRoom();
                        mProgressDialog = ProgressDialog.show(WatchActivity.this, "", getString(R.string.leaveRoom), true);
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                }).setMessage(getString(R.string.askLeaveRoom));

        alert.show();
    }

    private void finishRoom() {

        sendMessage(" left.", "Event", "");
        mFirebasePeopleRef.child(user_id).removeValue();

        Map<String, String> param = new HashMap<>();
        param.put(Define.ACTION, Define.ACTION_ROOM_LEAVE);
        param.put(Define.CALLER_ACTIVITY, callerActivity);
        param.put(Define.DB_USER_ID,  user_id);
        param.put(Define.IS_MY_APP, "true");

        ParseNetController.Room(thisContext, param);

        /*NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.Room(thisContext, param));*/

    }

    private void setupLayout() {

        chatTotal           = (ImageView)findViewById(R.id.chat_total);
        chatTotalNumber    = (TextView)findViewById(R.id.chat_total_number);
        chatView            = (WebView)findViewById(R.id.chatView);
        messageInput       = (BackPressEditText)findViewById(R.id.inputMsg);
        mVoiceBtn           = (Button)findViewById(R.id.voiceBtn);
        mVideoView           = (SurfaceView) findViewById(R.id.surface);
        stopView             = (TextView)findViewById(R.id.userStopView);
        stopProgressView     = (ProgressBar)findViewById(R.id.loadingPanel);
        inputWrapper         = (LinearLayout)findViewById(R.id.inputWrapper);

        messageInput.clearFocus();
        inputWrapper.clearFocus();

        /*ViewGroup.MarginLayoutParams params1=(ViewGroup.MarginLayoutParams) inputWrapper.getLayoutParams();
        params1.bottomMargin=0;
        inputWrapper.setLayoutParams(params1);*/

        stopView.setVisibility(View.INVISIBLE);
        stopProgressView.setVisibility(View.INVISIBLE);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "BRI293.TTF");
        chatTotalNumber.setTypeface(typeface);

        chatTotal.bringToFront();
        chatTotalNumber.bringToFront();
    }

    public void onShowPopup(View v){

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView chatTotalNumberPopup;
        // inflate the custom popup layout
        final View inflatedView = layoutInflater.inflate(R.layout.chat_friend_popup, (ViewGroup)getWindow().getDecorView()  ,false);
        // find the ListView in the popup layout
        chatNumber                = (TextView)inflatedView.findViewById(R.id.chatNumber);
        chatTotalNumberPopup      = (TextView)inflatedView.findViewById(R.id.chatTotalNumber);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "BRI293.TTF");

        chatNumber.setTypeface(typeface);
        chatTotalNumberPopup.setTypeface(typeface);
        chatTotalNumberPopup.setText(chatTotalNumber.getText());

        ListView listView = (ListView)inflatedView.findViewById(R.id.chatfriendlist);
        ChatFriendListParseAdapter chatFriendListParseAdapter = new ChatFriendListParseAdapter(this ,getAssets());
        listView.setAdapter(chatFriendListParseAdapter);
        chatFriendListParseAdapter.notifyDataSetChanged();
        chatFriendListParseAdapter.loadObjects();
        // get device size
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);


        Drawable d = new ColorDrawable(Color.WHITE);
        d.setAlpha(50);

        // set height depends on the device size
        popWindow = new PopupWindow(inflatedView, size.x/3*2, size.y/2, true );
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
        popWindow.showAtLocation(v, Gravity.RIGHT, 100, 100);

        //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

    }
    private View.OnClickListener btnChatTotal = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if ( v == chatTotal )
            {
                onShowPopup(v);
            }
        }
    };


    private void setupListener() {
        messageInput.setImeOptions(EditorInfo.IME_ACTION_SEND);
        messageInput.setOnEditorActionListener(actionListener);
        messageInput.setOnFocusChangeListener(focusListenter);
        messageInput.setOnBackPressListener(onBackPressListener);
        messageInput.setOnClickListener(btnMessageInputListener);

        messageInput.clearFocus();
        chatTotal.setOnClickListener(btnChatTotal);

        if ( voiceMethod == 1 ) mVoiceBtn.setOnClickListener(btnListener);
        else if ( voiceMethod == 2 ) mVoiceBtn.setOnTouchListener(touchListner);


    }

    private BackPressEditText.OnBackPressListener onBackPressListener = new BackPressEditText.OnBackPressListener() {
        @Override
        public void onBackPress()
        {
            ViewGroup.MarginLayoutParams params1=(ViewGroup.MarginLayoutParams) inputWrapper.getLayoutParams();
            params1.bottomMargin=0;
            inputWrapper.setLayoutParams(params1);

        }
    };



    private View.OnClickListener btnMessageInputListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if ( v == messageInput )
            {
                ViewGroup.MarginLayoutParams params1=(ViewGroup.MarginLayoutParams) inputWrapper.getLayoutParams();

                if(Build.MODEL.equals("SM-N900K")) { params1.bottomMargin=880;}
                else if(!Build.MODEL.equals("SHV-E210K")) { params1.bottomMargin=1000;}
                else                             {    params1.bottomMargin=450; }
                inputWrapper.setLayoutParams(params1);
            }
        }
    };



    private TextView.OnEditorActionListener actionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            if ( actionId == EditorInfo.IME_ACTION_SEND ) {
                if ( messageInput.getText().toString().equals("") ) return true;

                String msg;
                try {
                    //    msg = URLEncoder.encode(messageInput.getText().toString(), "utf-8").replace("+", "%20");
                    msg = messageInput.getText().toString();
                } catch (Exception e) {
                    msg = "";
                    e.printStackTrace();
                }

                String url = "javascript:document.getElementById('message').value = '" + msg + "'; document.getElementById('mSubmit').click();";
                chatView.loadUrl(url);

                messageInput.setText("");
                return true;
            }

            return false;
        }
    };

    private View.OnFocusChangeListener focusListenter = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

            Log.d("focus", "change");
            if (hasFocus) {
                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                ViewGroup.MarginLayoutParams params1=(ViewGroup.MarginLayoutParams) inputWrapper.getLayoutParams();
                if(Build.MODEL.equals("SM-N900K")) { params1.bottomMargin=880;}
                else if(!Build.MODEL.equals("SHV-E210K")) { params1.bottomMargin=1000;}
                else                             {    params1.bottomMargin=450; }
                inputWrapper.setLayoutParams(params1);

            }
            else {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                ViewGroup.MarginLayoutParams params1=(ViewGroup.MarginLayoutParams) inputWrapper.getLayoutParams();
                params1.bottomMargin=0;
                inputWrapper.setLayoutParams(params1);
            }
        }
    };

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if ( v == mVoiceBtn ) startVoiceRecordingMethod1();
        }
    };

    private View.OnTouchListener touchListner = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if ( v == mVoiceBtn ) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    Log.d(LogTag, "Voice btn down");
                    startVoiceRecordingMethod2();
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    Log.d(LogTag, "Voice btn up");

                    uploadVoiceToS3("voice");
                    Handler clickhandler = new Handler();
                    clickhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            stopVoiceRecording();
                            sendVoiceMessage();
                        }
                    }, 1000);

                }
            }

            return false;
        }
    };

    private View.OnClickListener btnRefreshListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(LogTag, "btnRefreshListener");

            startVideoPlayback();
        }
    };

    //    private String chatServerURL;
    private void setupChatView() {
        String chatServerURL  = getString(R.string.chat_server_url);
        //String chatServerURL  = str_chat_server_url;

        // dpmin : 배경 투명 처리
        chatView.setBackgroundColor(0x00000000);
        if (Build.VERSION.SDK_INT >= 11)
        {
            chatView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            chatView.setVisibility(View.VISIBLE);
        }
        else if (Build.VERSION.SDK_INT <= 10) {
            //   chatViewLoadding.setVisibility(View.GONE);
            chatView.setVisibility(View.VISIBLE);
        }

        //((TextView)findViewById(R.id.userIDlable)).setText(user_nm); // login_email

        // 웹뷰에서 자바스크립트실행가능
        chatView.getSettings().setJavaScriptEnabled(true);

        String url = chatServerURL + "?user_id=" + user_id; //"?uid=" + userName + "&user_nm=" + userName + "&room=" + room;  // user_id
        Log.d(LogTag, url);

        chatView.loadUrl(url);
        chatView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(LogTag, "onPageFinished");
                view.setBackgroundColor(0x00000000);
                if (Build.VERSION.SDK_INT >= 11)
                    view.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

                //  chatViewLoadding.setVisibility(View.GONE);
                chatView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.d(LogTag, "onReceivedError");
                // chatViewLoadding.setText("채팅 서버 접속 실패.");
                chatViewLoadding.setText(getString(R.string.chatAccessFail));
            }
        });

        // 웹뷰에서 자바스크립트 이벤트를 받기 위해
        chatView.addJavascriptInterface(new AndroidBridge(), "android");
    }
    public void goFinish()
    {
        finish();
    }

    private final Handler javascriptHandler = new Handler();
    private class AndroidBridge {
        @JavascriptInterface
        public void setMessage(final String arg) {
            Log.d(LogTag, "AndroidBridge: " + arg);
            if ( arg.equals("finish") ) {
                onAir = false;
                javascriptHandler.post(new Runnable() {
                    public void run() {
                        onAir = false;
                        AlertDialog.Builder alert = new AlertDialog.Builder(WatchActivity.this)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finishRoom();
                                        //  releaseMediaPlayer();
                                        dialog.dismiss(); //닫기
                                        finish();
                                    }
                                }).setMessage(getString(R.string.roomEnded));
                        alert.show();
                    }
                });
            } else if( arg.equals("screenoff")){
                javascriptHandler.post(new Runnable() {
                    public void run() {
                        Log.d(LogTag, Define.STREAM_PAUSE);

                        stopView.setVisibility(View.VISIBLE);
                        stopProgressView.setVisibility(View.VISIBLE);
                        stopView.bringToFront();
                        //onPause();
                        //releaseMediaPlayer();
                    }
                });
            }
            else if( arg.equals("screenon")){
                javascriptHandler.post(new Runnable() {
                    public void run() {

                        Handler clickhandler = new Handler();
                        clickhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(LogTag, Define.STREAM_PLAY);
                                stopView.setVisibility(View.INVISIBLE);
                                stopProgressView.setVisibility(View.INVISIBLE);
                            }
                        }, 5000);
                        // onResume();
                        //        setupVideoStream();
                        //        mVideoView.postInvalidate();
                        //mMediaPlayer.start();
                        //playVideo(extras.getInt(MEDIA), true);
                    }
                });
            }
        }
    }

    private MediaRecorder mRecorder = null;
    private void startVoiceRecordingMethod1() {
        makeNewFolder(ABS_PATH, REAL_SHARE_PATH);

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        mRecorder.setOutputFile(ABS_PATH + "/" + REAL_SHARE_PATH + "/" + RECORD_FILE_PREFIX + "temp" + RECORD_FILE_SURFIX);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
            mRecorder.start();

            mVoiceBtn.setBackgroundResource(R.drawable.voicetalk_on);
            //mVoiceBtn.setTextColor(Color.RED);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("음성 메세지를 녹음중입니다.\n완료 후 전송버튼을 눌러주십시오.")
                    .setCancelable(false) // 뒤로 버튼 클릭시 취소 가능 설정
                    .setPositiveButton("전송", new DialogInterface.OnClickListener() {
                        // 전송 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton) {
                            stopVoiceRecording();
                            //sendVoiceMessage();
                            dialog.dismiss();
                            //removeVoiceTempFile();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        // 취소 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                            stopVoiceRecording();
                            //removeVoiceTempFile();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();

            Toast.makeText(this, "음성 메세지를 전송 할 수 없습니다.\n다시 시도해 주세요.", Toast.LENGTH_SHORT).show();

            mRecorder.release();
            mRecorder = null;
        }
    }
    private void writeVoice()
    {

        //       String url = "javascript:document.getElementById('message').value = '" + msg + "'; document.getElementById('mSubmit').click();";
        //       chatView.loadUrl(url);
        //       messageInput.setText("");d
        //       messageInput.setText("");d
    }

    private void startVoiceRecordingMethod2() {
        makeNewFolder(ABS_PATH, REAL_SHARE_PATH);

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        mRecorder.setOutputFile(ABS_PATH + "/" + REAL_SHARE_PATH + "/" + RECORD_FILE_PREFIX + "temp" + RECORD_FILE_SURFIX);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
            mRecorder.start();

            mVoiceBtn.setBackgroundResource(R.drawable.voicetalk_on);
            findViewById(R.id.voiceHelpMsg).setVisibility(View.VISIBLE);
            //mVoiceBtn.setTextColor(Color.RED);
        } catch (IOException e) {
            e.printStackTrace();

            Toast.makeText(this, "음성 메세지를 전송 할 수 없습니다.\n다시 시도해 주세요.", Toast.LENGTH_SHORT).show();

            mRecorder.release();
            mRecorder = null;
        }
    }

    private void makeNewFolder(String path, String newFolder) {

        File aFile = new File(path + "/" + newFolder);
//        if ( aFile.exists() == false ) aFile.mkdir();
        // -*ssh*- change condition sentence easy and handle return value
        if ( !aFile.exists() ){
            if(aFile.mkdir()){
                Log.d(LogTag,"make directory success");
            }else{
                Log.d(LogTag,"make directory fail");
            }
        }

    }

    private void stopVoiceRecording() {

        if( mRecorder != null ) {

            try {
                mRecorder.stop();
            }catch(RuntimeException e) { e.printStackTrace();}
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
        findViewById(R.id.voiceHelpMsg).setVisibility(View.INVISIBLE);
        //mVoiceBtn.setTextColor(Color.BLACK);
        mVoiceBtn.setBackgroundResource(R.drawable.voicetalk_off);
    }

    //    private String voiceUploadURL;
    private AsyncHttpClient oAsyncHttpClient = new AsyncHttpClient();

    private void uploadVoiceToS3(String uploadType){

        Map<String, String> param = new HashMap<>();

        param.put(Define.SESSION_ID  , user_id);
        param.put(Define.UPLOAD_TYPE , uploadType);

        NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.beforeFileUpload(this, param));


    }

    public void sendVoiceMessage() {


        String voiceUploadURL  = "https://s3-us-west-1.amazonaws.com/s3.liveo.me/";
        File vMessage = new File(ABS_PATH + "/" + REAL_SHARE_PATH + "/" + RECORD_FILE_PREFIX + "temp" + RECORD_FILE_SURFIX);
        RequestParams params = new RequestParams();


        try {


            SharedPreferences pref = RSPreference.getPreference(thisContext);

            String key         = pref.getString(Define.KEY, "");
            String policy      = pref.getString(Define.POLICY, "");
            String signature   = pref.getString(Define.SIGNATURE, "");

            params.put("AWSAccessKeyId"             , "AKIAJN6SHS4ZX33YWPFA");
            params.put("success_action_status"     , "201");
            params.put("Content-Type"                , "image/png");

            params.put(Define.ACL        , "private");
            params.put(Define.KEY        , key);
            params.put(Define.POLICY     , policy);
            params.put(Define.SIGNATURE, signature);
            params.put(Define.FILE, vMessage);

            Log.d("file" , String.valueOf(vMessage.exists()) + " : " + vMessage.getName());
            Log.d("param", params.toString());
            String url = voiceUploadURL.concat(key);

            sendMessage(voiceUploadURL, "Voice", url);

            Log.d(LogTag, ABS_PATH + "/" + REAL_SHARE_PATH + "/" + RECORD_FILE_PREFIX + "temp" + RECORD_FILE_SURFIX);
            Log.d(LogTag, voiceUploadURL + "/" + vMessage.getAbsolutePath());
        } catch (Exception e){
            // -*ssh*- FileNotFoundException is subcase of Exception
            e.printStackTrace();
        }

        oAsyncHttpClient.post(voiceUploadURL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                Log.d(LogTag, "httpget onStart");
            }


            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                // -*ssh*- avoid to call toString on array
                Log.d(LogTag, "httpget onSuccess " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d(LogTag, "httpget onFailure " + statusCode);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Log.d(LogTag, "httpget onRetry");
            }
        });
    }

    private void removeVoiceTempFile() {
        File aFile = new File(ABS_PATH + "/" + REAL_SHARE_PATH + "/" + RECORD_FILE_PREFIX + "temp" + RECORD_FILE_SURFIX);
        //TODO Change condition sentence
//        if ( aFile.exists() == true ) aFile.delete();
        if ( aFile.exists()) {
            if(aFile.delete()){
                Log.d(LogTag, "File Delete Success");
            }else{
                Log.d(LogTag, "File Delete Fail");
            }
        }
    }

    private void setupVideoStream() {

//        Vitamio.initialize(this);
        if (!LibsChecker.checkVitamioLibs(this))
            return;
        mVideoView = (SurfaceView) findViewById(R.id.surface);
        holder = mVideoView.getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.RGBA_8888);

        extras = getIntent().getExtras();
    }
    private void playVideo() {
        doCleanUp();

        Integer Media=0;
        String path = getString(R.string.wowza_client_url) + "ch"+owner_id;

        Log.d(TAG, "rtmp path : " + path);

        try {
            switch (Media) {
                case STREAM_VIDEO:
                    /*
                     * TODO: Set path variable to progressive streamable mp4 or
                     * 3gpp format URL. Http protocol should be used.
                     * Mediaplayer can only play "progressive streamable
                     * contents" which basically means: 1. the movie atom has to
                     * precede all the media data atoms. 2. The clip has to be
                     * reasonably interleaved.
                     *
                     */
                    // -*ssh*- Change condition sentence
                    if(path.isEmpty()){
//                    if (path == "") {
                        // Tell the user to provide a media file URL.
                        Toast.makeText(WatchActivity.this, "Please edit MediaPlayerDemo_Video Activity," + " and set the path variable to your media file URL.", Toast.LENGTH_LONG).show();
                        return;
                    }
            }
            releaseMediaPlayer();
            // Create a new media player and set the listeners
            mMediaPlayer = new MediaPlayer(this);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setDisplay(holder);
            mMediaPlayer.prepareAsync();

            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);

            mMediaPlayer.setOnVideoSizeChangedListener(this);
            setVolumeControlStream(AudioManager.STREAM_MUSIC);

        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
    }

    private void playVideo_pause(Integer Media, boolean isValid) {
        doCleanUp();

        String path = getString(R.string.wowza_client_url) + "ch"+owner_id;

        Log.d(TAG, "rtmp path : " + path);

        try {
            switch (Media) {
                case STREAM_VIDEO:
                    /*
                     * TODO: Set path variable to progressive streamable mp4 or
                     * 3gpp format URL. Http protocol should be used.
                     * Mediaplayer can only play "progressive streamable
                     * contents" which basically means: 1. the movie atom has to
                     * precede all the media data atoms. 2. The clip has to be
                     * reasonably interleaved.
                     *
                     */
                    // -*ssh*- Change condition sentence
                    if(path.isEmpty()){
//                    if (path == "") {
                        // Tell the user to provide a media file URL.
                        Toast.makeText(WatchActivity.this, "Please edit MediaPlayerDemo_Video Activity," + " and set the path variable to your media file URL.", Toast.LENGTH_LONG).show();
                        return;
                    }
            }
            // Create a new media player and set the listeners
            //mMediaPlayer_pause = new MediaPlayer(this);
            releaseMediaPlayer();

            mMediaPlayer = new MediaPlayer(this);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setDisplay(holder);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);

            mMediaPlayer.setOnVideoSizeChangedListener(this);
            setVolumeControlStream(AudioManager.STREAM_MUSIC);

            //mMediaPlayer = mMediaPlayer_pause;

        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
    }

    public void onBufferingUpdate(MediaPlayer arg0, int percent) {
        //     Log.d(TAG, "onBufferingUpdate percent:" + percent);
    }

    public void onCompletion(MediaPlayer arg0) {
        if(mMediaPlayer!=null) {
            Log.d(TAG, "onCompletion called" + ": " + onAir + " : " + mMediaPlayer.isPlaying());
            if (onAir == true && !mMediaPlayer.isPlaying()) {
                setupVideoStream();
                playVideo_pause(extras.getInt(MEDIA), true);
            }
        }
    }

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.v(TAG, "onVideoSizeChanged called");
        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    public void onPrepared(MediaPlayer mediaplayer) {
        Log.d(TAG, "onPrepared called");
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        Log.d(TAG, "surfaceChanged called");
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        Log.d(TAG, "surfaceDestroyed called");

    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated called");

        //playVideo(extras.getInt(MEDIA), false);

    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            if ( mMediaPlayer.isPlaying() ) mMediaPlayer.stop();
            mMediaPlayer.reset();
            try{
                mMediaPlayer.release();}
            catch(Exception e) { e.printStackTrace();}
            mMediaPlayer = null;
        }
    }

    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }

    static public void startVideoPlayback() {
        Log.v(TAG, "startVideoPlayback");
        holder.setFixedSize(mVideoWidth, mVideoHeight);
        if ( !mMediaPlayer.isPlaying() ) mMediaPlayer.start();
    }


}