package kr.co.wegeneration.realshare;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SlidingDrawer;
import android.widget.Toast;
import android.os.Build;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.content.IntentFilter;
import android.view.WindowManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import cz.msebera.android.httpclient.Header;
import kr.co.wegeneration.realshare.NetController.ParseNetController;
import kr.co.wegeneration.realshare.adapter.ChatFriendListParseAdapter;
import kr.co.wegeneration.realshare.adapter.FriendMainListParseAdapter;
import kr.co.wegeneration.realshare.chat.Chat;
import kr.co.wegeneration.realshare.chat.ChatListAdapter;
import kr.co.wegeneration.realshare.models.Room;
import kr.co.wegeneration.realshare.util.BackPressEditText;
import kr.co.wegeneration.realshare.util.URLShortener;
import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspClient;
import net.majorkernelpanic.streaming.video.VideoQuality;

//import org.apache.http.params.client;
//import org.apache.http.Header;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class BroadcastActivity extends ListActivity implements RtspClient.Callback, Session.Callback, SurfaceHolder.Callback, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {


    private static final String LogTag = "BroadcastActivity";

    // TODO: change this to your own Firebase URL
    private static final String FIREBASE_URL = "https://liveo-27.firebaseio.com";
    private boolean recording = false;
    private PopupWindow popWindow;
    private ImageView chatTotal;
    private TextView chatTotalNumber;
    private Button handle, clickMe;
    private TextView text1;
    private Context context;
    private View chatLine;
    private String mUsername;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private ValueEventListener mMessageListener;
    private ValueEventListener mChatNumberListener;
    static android.os.Handler refreshFriendHandler;
    private ImageView btn_exit;
    private ImageView btn_exit_end;
    private String thumbnailUrl="";
    private CheckBox timeLineYn;
    private ChatListAdapter mChatListAdapter;
    private android.media.MediaPlayer mPlayer = null;
    private Firebase mFirebaseChatInOut;
    private Firebase mFirebasePeopleRef;
    private boolean voiceplaying=false;
    private static final String ABS_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String REAL_SHARE_PATH = "real_share";
    private static final String RECORD_FILE_SURFIX = ".jpg";
    private static final String RECORD_FILE_PREFIX = "thumbnail_";
    private boolean broadOut =false;
    static Context thisContext;
    static String filename = "";
    static String callerActivity = "";
    static String room_name = "";
    static int angle = 90;
    static String status_nm="";
    static String str_chat_server_url="";
    static String user_id = "";
    static String user_nm = "";
    static String str_voiceUploadURL="";
    private boolean switch_content  = false;
    private boolean exit_content    = false;
    public static boolean onAir;
    private WebView chatView;
    private TextView chatViewLoadding;
    private LinearLayout messageBox;
    private LinearLayout screenView;
    private BackPressEditText messageInput;
    private Button btnShares, btnShareExit;
    private TextView stream_ended;
    private TextView chatNumber;
    private EditText broadcast_title;
    private ImageButton mButtonCamera;
    private ImageButton mButtonExit;
    private FrameLayout layoutExit = null;
    private static SurfaceView mSurfaceView;
    private String thumbnailPathSave="";
    // Rtsp session
    private Session mSession;
    private static RtspClient mClient;
    private GestureDetector gestureScanner;
    private long startRecordTime = 0;
    private long endRecordTime   = 0;

    @Override
    public boolean onTouchEvent(MotionEvent me)
    {
        return gestureScanner.onTouchEvent(me);
    }

    @Override
    public boolean onDown(MotionEvent e)
    {

        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e)
    {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {

        return true;
    }

    @Override
    public void onShowPress(MotionEvent e)
    {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e)
    {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d("test" , " double tab");
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.d("test" , " double tabevent");
        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Log.d(LogTag, "onCreate");

        super.onCreate(savedInstanceState);

        gestureScanner = new GestureDetector(this);
        //R.layout.activity_broadcast
        //R.layout.popup_layout_broadcast
        setContentView(R.layout.activity_broadcast);
        thisContext = this;


        final FrameLayout layoutTop, layoutBottom;


        layoutTop       = (FrameLayout) findViewById(R.id.broadcast_content);
        layoutBottom    = (FrameLayout) findViewById(R.id.broadcast_intro);
        layoutExit     = (FrameLayout) findViewById(R.id.broadcast_exit);

        layoutBottom.bringToFront();
        layoutBottom.invalidate();


        btnShares           = (Button) findViewById(R.id.btnShares);
        btn_exit            =(ImageView) findViewById(R.id.btn_exit);
        btn_exit_end       =(ImageView) findViewById(R.id.exit_btn_end);
        btnShareExit       =(Button) findViewById(R.id.btnShareExit);
        broadcast_title    =(EditText) findViewById(R.id.broadcast_title);
        stream_ended       =(TextView)findViewById(R.id.stream_ended);
        timeLineYn          =(CheckBox)findViewById(R.id.checkBox_broadcast);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "BRI293.TTF");
        stream_ended.setTypeface(typeface);

        SharedPreferences pref = RSPreference.getPreference(thisContext);

        try {
            user_id = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_USER_ID);
            user_nm = ParseUser.getCurrentUser().getParseObject(Define.UserInfo).getString(Define.DB_USER_NM);
            status_nm = pref.getString(Define.DB_STATUS, "");
            str_voiceUploadURL = pref.getString(Define.VOICE_UPLOAD_URL, "");
            str_chat_server_url = pref.getString(Define.CHAT_SERVER_URL, "");

        }catch(Exception e ){e.printStackTrace(); }

        Intent intent = getIntent();
        if (intent != null) {
            callerActivity = intent.getStringExtra(Define.CALLER_ACTIVITY);
            room_name      = intent.getStringExtra(Define.DB_ROOM);
        }


        setupLayout();
        setupListener();
        //setupChatView();
        LayoutOut();

        initRtspClient_BeforeStart();;

        btnShares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                switch_content = true;
                layoutTop.bringToFront();
                layoutTop.invalidate();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(messageInput.getWindowToken(), 0);

                LayoutOn();
                setChatLayout();
                startFirebase();
                initRtspClient_AfterStart();
                toggleStreaming(true);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // dpmin : 화면 자동꺼짐 끔
                onAir = true;
                ModuleAutoRecordBasic();


                RecordStream();


                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                intentFilter.addAction(Intent.ACTION_USER_PRESENT);
                intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
                intentFilter.addAction(Intent.ACTION_SCREEN_ON);
                intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                //NetworkReceiver receiver = new NetworkReceiver();
                //registerReceiver(receiver, intentFilter);
                LocalBroadcastManager.getInstance(thisContext).registerReceiver(receiver, new IntentFilter("MSG"));

            }
        });


        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(BroadcastActivity.this)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                onAir = false;
                                finishRoom();
                                dialog.dismiss();
                                finish();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                            }
                        }).setMessage(R.string.exitBroadcast);

                alert.show();


            }
        });


        btnShareExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*AlertDialog.Builder alert = new AlertDialog.Builder(BroadcastActivity.this)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                onAir = false;
                                finishRoom();
                                dialog.dismiss();
                                finish();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                            }
                        }).setMessage(R.string.exitBroadcast);

                alert.show();*/

                onAir = false;
                if (!timeLineYn.isChecked()) EndTimeLine("own");
                finishRoom();
                finish();

            }
        });


        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchCameraClick();
            }
        });

        btn_exit_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(BroadcastActivity.this)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                onAir = false;
                                if (!timeLineYn.isChecked()) EndTimeLine("own");
                                finishRoom();
                                dialog.dismiss();
                                finish();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                            }
                        }).setMessage(R.string.exitBroadcast);

                alert.show();


            }
        });

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        image.deleteOnExit();
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }



    private void uploadfILEToS3(String uploadType){

        Map<String, String> param = new HashMap<>();

        param.put(Define.SESSION_ID  , user_id);
        param.put(Define.UPLOAD_TYPE , uploadType);

        NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.beforeFileUpload(this, param));


    }



    private void StartTimeLine(String recordUrl, String thumbnailPath ) {

        SharedPreferences pref    = RSPreference.getPreference(context);
        Map<String, String> param = new HashMap<>();
        param.put(Define.MSG_TYPE                  , pref.getString(Define.MSG_TYPE, ""));
        param.put(Define.CALLER_ACTIVITY         , pref.getString(Define.CALLER_ACTIVITY, ""));
        param.put(Define.PARAM_PULL_FROM         , pref.getString(Define.PARAM_PULL_FROM, ""));
        param.put(Define.PARAM_RECEIVER_LIST    , pref.getString(Define.PARAM_RECEIVER_LIST, ""));
        param.put(Define.OWNER_ID                 , ParseUser.getCurrentUser().getString(Define.DB_USER_ID));
        param.put(Define.TITLE                    , broadcast_title.getText().toString());
        param.put(Define.DB_ROOM                , room_name);
        param.put(Define.BROAD_STATUS           , "live");
        param.put(Define.THUMBNAILPATH               , thumbnailPath);
        param.put(Define.MSG_ROOM                 , Define.MSG_ROOM_START);
        param.put(Define.MSG_RECORD_URL           , recordUrl);

        ParseNetController.TimeLine(context, param);
    }

    private void EndTimeLine(String end) {

        Map<String, String> param = new HashMap<>();

        param.put(Define.TITLE                    , broadcast_title.getText().toString());
        param.put(Define.DB_ROOM                , room_name);
        param.put(Define.BROAD_STATUS           , end);
        param.put(Define.MSG_ROOM               , Define.MSG_ROOM_END);
        param.put(Define.THUMBNAILPATH           , thumbnailPathSave);
        param.put(Define.RECORDTIME           ,  String.valueOf(endRecordTime-startRecordTime));

        ParseNetController.TimeLine(context, param);
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Bundle bundle = intent.getExtras();
            Log.d(LogTag, "BroadCastReceiver");
            switch (bundle.getString(Define.MSG_TYPE, "")){
                case Define.MSG_TYPE_PULL:
                    break;
                case Define.MSG_TYPE_PUBLISH:
                    // AlertDialog.Builder publishBuilder = new AlertDialog.Builder(BroadcastActivity.this);
                    AlertDialog.Builder publishBuilder = new AlertDialog.Builder(thisContext);
                    publishBuilder.setTitle(bundle.getString(Define.MSG_TITLE, ""));
                    publishBuilder.setMessage(bundle.getString(Define.MSG_CONTENTS, ""));
                    publishBuilder.setCancelable(true);
                    publishBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    publishBuilder.show();
                    break;
                case Define.MSG_TYPE_KNOCK:
                    //AlertDialog.Builder knockBuilder = new AlertDialog.Builder(BroadcastActivity.this);
                    Log.d(LogTag, "case Define.MSG_TYPE_KNOCK");
                    AlertDialog.Builder knockBuilder = new AlertDialog.Builder(thisContext);
                    knockBuilder.setTitle(bundle.getString(Define.MSG_TITLE, ""));
                    knockBuilder.setMessage(bundle.getString(Define.MSG_CONTENTS, ""));
                    knockBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Map<String, String> param = new HashMap<>();
                            param.put(Define.ACTION, Define.ACTION_INVITE);
                            param.put(Define.MSG_TYPE, Define.MSG_TYPE_INVITE);
                            param.put(Define.MSG_SENDER_ID, user_id);
                            param.put(Define.PARAM_RECEIVER_LIST, "[" + bundle.getString(Define.MSG_SENDER_ID, "") + "]");

                            NetController.getInstance(thisContext)
                                    .getRequestQueue()
                                    .add(NetController.Push(thisContext, param));
                            dialog.dismiss();
                        }
                    });
                    knockBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    knockBuilder.show();
                    break;
                case Define.MSG_TYPE_INVITE:
                    break;
                case Define.MSG_TYPE_STATUS:
                    //AlertDialog.Builder statusBuilder = new AlertDialog.Builder(BroadcastActivity.this);
                    AlertDialog.Builder statusBuilder = new AlertDialog.Builder(thisContext);
                    statusBuilder.setTitle(bundle.getString(Define.MSG_TITLE, ""));
                    statusBuilder.setMessage(bundle.getString(Define.MSG_CONTENTS, ""));
                    statusBuilder.setCancelable(true);
                    statusBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    statusBuilder.show();
                    break;
                default:
                    break;
            }
        }
    };


    private void ModuleAutoRecordBasic() {
        Log.d(LogTag, "ModuleAutoRecordBasic");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


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
                voiceplaying= false;
                deletePlayer();
            }

        } catch (IOException e) {
            return false;
        }

        return true;
    }


    public void onPlayWeb(String url) {
        deletePlayer();
//        String url = "http://wisjoy.com/2005/Soundomusic/EinsamerHirte-GheorgheZamfir.mp3";

        LoadMedia(url);
        Log.d("MediaPlayer", "Started_test");
        // mTextMessage.setText("'Web' is Selected");
    }


    private void startFirebase(){

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
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

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
                    Toast.makeText(BroadcastActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BroadcastActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();

    }

    // 흔들기가 끝나면 호출되는 함수
    public void onStop()
    {
        Log.i("kmsTest", "onStop()");
        super.onStop();

        /*if(switch_content) {
            DisconnectFirebase();
        }*/


    }



    private void sendMessage(String msg, String MessageType, String voicePath) {
        String input = msg;
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            Chat chat = new Chat(input, user_nm, System.currentTimeMillis(), MessageType, voicePath);
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

    private void setupUsername() {
        SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
        mUsername = prefs.getString("username", null);
        if (mUsername == null) {
            Random r = new Random();
            // Assign a random user name if we don't have one saved.
            mUsername = "JavaUser" + r.nextInt(100000);
            prefs.edit().putString("username", mUsername).commit();
        }
    }



    @Override
    public void onPause() {
        Log.d(LogTag, "onPause");
        super.onPause();
        ParseNetController.ActiveOnOff(thisContext, "n");
//        onAir=false;
        /*NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.ActiveOnOff(thisContext, user_id, "n"));*/


        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        if(switch_content==true) {
            toggleStreaming(false);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // dpmin : 화면 자동꺼짐 켬
        }

    }

    @Override
    protected void onResume() {
        Log.d(LogTag, "onResume");
        super.onResume();

        ParseNetController.ActiveOnOff(thisContext, "y");
        /*NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.ActiveOnOff(thisContext, user_id, "y"));*/


        if(switch_content==true){
                toggleStreaming(true);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // dpmin : 화면 자동꺼짐 끔
                onAir = true;
        }
    }


    @Override
    public void onDestroy() {
        Log.d(LogTag, "onDestroy");
        super.onDestroy();


        if(switch_content) {
            mClient.release();
        }
            deletePlayer();
            mSession.release();
            mSurfaceView.getHolder().removeCallback(this);
            onAir = false;

            if (chatView != null) {
                ViewGroup parent = (ViewGroup) chatView.getParent();
                if (parent != null) {
                    parent.removeView(chatView);
                }
                chatView.removeAllViews();
                chatView.destroy();
            }
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void initRtspClient_BeforeStart() {
        Log.d(LogTag, "initRtspClient_BeforeStart");

        mSurfaceView.setAspectRatioMode(SurfaceView.ASPECT_RATIO_PREVIEW);
        // Configures the SessionBuilder
        mSession = SessionBuilder.getInstance()
                .setContext(getApplicationContext())
                .setAudioEncoder(SessionBuilder.AUDIO_AAC)
                .setAudioQuality(AudioQuality.DEFAULT_AUDIO_QUALITY.clone())
                .setVideoEncoder(SessionBuilder.VIDEO_H264)
                .setSurfaceView(mSurfaceView)
                .setCallback(this)
                .setPreviewOrientation(angle)
                        //.setVideoQuality(VideoQuality.SD_VIDEO_QUALITY.clone())
                .setVideoQuality(VideoQuality.SD_VIDEO_QUALITY.clone()).build();

    }

    private void initRtspClient_AfterStart() {
        Log.d(LogTag, "initRtspClient_AfterStart");


        // Configures the RTSP clientS
        mClient = new RtspClient();
        mClient.setSession(mSession);

        String ip, port, path;

        // We parse the URI written in the Editext
        Pattern uri = Pattern.compile("rtsp://(.+):(\\d+)/(.+)");
        //Pattern uri = Pattern.compile("rtsp://(.+)/(.+)");
        Matcher mtch = uri.matcher(getString(R.string.wowza_server_url) + "ch" + user_id); // login_email
        mtch.find();
        ip = mtch.group(1);
        port = mtch.group(2);
        path = mtch.group(3);

        //Log.d("iptest", ip+":"+ path);


        mClient.setCredentials(getString(R.string.wowza_publisher_username), getString(R.string.wowza_publisher_password));
        mClient.setServerAddress(ip, Integer.parseInt(port));
        mClient.setStreamPath("/" + path);
        mClient.setCallback(this);


    }

    private void toggleStreaming(Boolean toggle) {
        Log.d(LogTag, "toggleStreaming " + toggle);

        if (toggle) {
            if (!mClient.isStreaming()) {
                // Start camera preview

                mSession.setSurfaceView(mSurfaceView);
                mSession.setPreviewOrientation(angle);
                mSession.startPreview();


                if(switch_content==true) {
           /*         Map<String, String> param = new HashMap<>();
                    param.put(Define.ACTION, Define.STREAM_PLAY);
                    param.put(Define.MSG_TYPE, Define.MSG_TYPE_STREAMPLAY);
                    param.put(Define.MSG_ROOM_NM, room_name);
                    param.put(Define.MSG_SENDER_ID, user_id);
                    param.put(Define.PARAM_RECEIVER_LIST, "[" + user_id + "]");

                    NetController.getInstance(thisContext)
                            .getRequestQueue()
                            .add(NetController.Push(thisContext, param));*/

                    /*NetController.getInstance(thisContext)
                            .getRequestQueue()
                            .add(NetController.SCREENON(thisContext, room_name, "screenon"));*/
                    mFirebaseChatInOut.setValue("ScreenOn");
                }

                // Start video stream
                mClient.startStream();
            }
        } else {
            if (mClient.isStreaming()) {
                // already streaming, stop streaming
                // stop camera preview
            /*    Map<String, String> param = new HashMap<>();
                param.put(Define.ACTION, Define.STREAM_PAUSE);
                param.put(Define.MSG_TYPE, Define.MSG_TYPE_STREAMOFF);
                param.put(Define.MSG_ROOM_NM, room_name);
                param.put(Define.MSG_SENDER_ID, user_id);
                param.put(Define.PARAM_RECEIVER_LIST, "[" + user_id + "]");

                NetController.getInstance(thisContext)
                        .getRequestQueue()
                        .add(NetController.Push(thisContext, param));
        */
                if(switch_content==true) {
                    /*NetController.getInstance(thisContext)
                            .getRequestQueue()
                            .add(NetController.SCREENON(thisContext, room_name, "screenoff"));*/
                    mFirebaseChatInOut.setValue("ScreenOff");
                }

                mSession.stopPreview();

                // stop streaming
                mClient.stopStream();
            }
        }
    }

    @Override
    public void onSessionError(int reason, int streamType, Exception e) {
        switch (reason) {
            case Session.ERROR_CAMERA_ALREADY_IN_USE:
                break;
            case Session.ERROR_CAMERA_HAS_NO_FLASH:
           //     mButtonFlash.setImageResource(R.drawable.ic_flash_on_holo_light);
          //      mButtonFlash.setTag("off");
                break;
            case Session.ERROR_INVALID_SURFACE:
                break;
            case Session.ERROR_STORAGE_NOT_READY:
                break;
            case Session.ERROR_CONFIGURATION_NOT_SUPPORTED:
                break;
            case Session.ERROR_OTHER:
                break;
        }

        if (e != null) {
            alertError(e.getMessage());
            e.printStackTrace();
        }
    }

    private void alertError(final String msg) {
        final String error = (msg == null) ? "Unknown error: " : msg;
        AlertDialog.Builder builder = new AlertDialog.Builder(BroadcastActivity.this);
        builder.setMessage(error).setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onRtspUpdate(int message, Exception exception) {
        switch (message) {
            case RtspClient.ERROR_CONNECTION_FAILED:
                Toast.makeText(thisContext.getApplicationContext(), "Connection Lost " , Toast.LENGTH_SHORT).show();
                break;
            case RtspClient.ERROR_WRONG_CREDENTIALS:
                alertError(exception.getMessage());
                exception.printStackTrace();
                break;
        }
    }

    @Override
    public void onPreviewStarted() {

   /*     Log.d(LogTag, "onPreviewStarted");
       if (mSession.getCamera() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mButtonFlash.setEnabled(false);
                mButtonFlash.setTag("off");
                mButtonFlash.setImageResource(R.drawable.ic_flash_on_holo_light);
        }
        else {
              mButtonFlash.setEnabled(true);
        }*/
    }

    private void enableUI() {
        //mButtonStart.setEnabled(true);
        //mButtonCamera.setEnabled(true);
    }

    @Override
    public void onSessionConfigured() {
        Log.d(LogTag, "onSessionConfigured");
    }

    @Override
    public void onSessionStarted() {
        enableUI();
        //    mButtonStart.setImageResource(R.drawable.ic_switch_video_active);
        Log.d(LogTag, "onSessionStarted");
    }

    @Override
    public void onSessionStopped() {
        Log.d(LogTag, "onSessionStopped");
        enableUI();
        //mButtonStart.setImageResource(R.drawable.ic_switch_video);

    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.d(LogTag, "surfaceChanged");


        //RtspReset();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(LogTag, "surfaceCreated");

        //mClient.startStream();
//        mSurfaceView.getHolder().addCallback(this);
        //RtspReset();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(LogTag, "surfaceDestroyed");
    }

    @Override
    public void onBitrareUpdate(long bitrate) {
        //Log.d(LogTag, "onBitrareUpdate");
    }

    ProgressDialog mProgressDialog;

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {


            AlertDialog.Builder alert = new AlertDialog.Builder(BroadcastActivity.this)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(switch_content==true) {
                                if (exit_content == true) {

                                    onAir = false;
                                    if (!timeLineYn.isChecked()) EndTimeLine("own");
                                    //else                          EndTimeLine("end");
                                    finishRoom();
                                    dialog.dismiss();     //닫기
                                    finish();
                                } else {
                                    exit_content = true;

                                    LayoutOut();
                                    DisconnectFirebase();
                                    getListView().setVisibility(View.INVISIBLE);
                                    mFirebaseChatInOut.setValue("Exit");
                                    RecordStream();
                                    layoutExit.bringToFront();
                                    layoutExit.invalidate();
                                }
                            }
                            else{
                                finishRoom();
                                dialog.dismiss();     //닫기
                                finish();
                            }
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                        }
                    }).setMessage(R.string.exitBroadcast);

            alert.show();

            return false;
        }

        return super.onKeyDown(keyCode, event);
    }
    //@Override
    /*   public void onBackPressed() {
               AlertDialog.Builder alert = new AlertDialog.Builder(BroadcastActivity.this)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        finishRoom();

                                               dialog.dismiss();     //닫기
                                    }
                            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                       dialog.dismiss();     //닫기
                                    }
                            }).setMessage(R.string.exitBroadcast);


                alert.show();
    }

*/



    private String sendThumbnailMessage() {

        String thumbnailUrl  = "http://";
        thumbnailUrl = thumbnailUrl.concat(getString(R.string.wowza_publisher_username));
        thumbnailUrl = thumbnailUrl.concat(":");
        thumbnailUrl = thumbnailUrl.concat(getString(R.string.wowza_publisher_password));
        thumbnailUrl = thumbnailUrl.concat("@");
        thumbnailUrl = thumbnailUrl.concat(getString(R.string.wowza_server_record_url));
        thumbnailUrl = thumbnailUrl.concat("transcoderthumbnail?application=live");
        thumbnailUrl = thumbnailUrl.concat("&streamname=ch");
        thumbnailUrl = thumbnailUrl.concat(user_id);
        thumbnailUrl = thumbnailUrl.concat("&format=png");
        thumbnailUrl = thumbnailUrl.concat("&size=1280x720");

        RequestParams params = new RequestParams();

        try {
           /* params.put("user", "kidaehong");
            params.put("pass", "realshare99");
            params.put("encoding", "binary");*/
            Log.d(LogTag,       thumbnailUrl);
            Log.d(LogTag,       params.toString());
        } catch (Exception e){
            e.printStackTrace();
        }

        oAsyncHttpClient.getHttpClient().getParams().setParameter("http.protocol.allow-circular-redirects", true);


        oAsyncHttpClient.get(thumbnailUrl, params, new FileAsyncHttpResponseHandler(this) {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                /*for (int i = 0; i < headers.length; i++) {
                    Log.d(LogTag, "httpget onFailure : " + statusCode + " : " + headers[i].getValue());
                }*/
                Log.d(LogTag, "httpget onFailure : " + statusCode );
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                Log.d(LogTag, "httpget onSuccess");
                thumbnailPathSave = ParseNetController.uploadFileToS3(context, file);
            }

            @Override
            public void onStart() {
                // called before request is started
                Log.d(LogTag, "httpget onStart");
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Log.d(LogTag, "httpget onRetry");
            }
        });
        return thumbnailUrl;
    }

    private void finishRoom() {
        //Map<String, String> param = new HashMap<String, String>();
        Map<String, String> param = new HashMap<>();
        param.put(Define.ACTION, Define.ACTION_ROOM_CLOSE);
        param.put(Define.CALLER_ACTIVITY, callerActivity);
        param.put(Define.DB_USER_ID, user_id);


        ParseNetController.Room(thisContext, param);

        if(refreshFriendHandler!=null)
            refreshFriendHandler.removeMessages(10);
        /*NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.Room(thisContext, param));*/

    }


    private void setupLayout() {

        messageInput      = (BackPressEditText)findViewById(R.id.inputMsg);
         chatTotal         = (ImageView)findViewById(R.id.chat_total);
        chatTotalNumber  = (TextView)findViewById(R.id.chat_total_number);
        chatView          = (WebView)findViewById(R.id.chatView);
        //mButtonCamera     = (ImageButton) findViewById(R.id.camera);

        screenView        = (LinearLayout)findViewById(R.id.screenView);
        mButtonExit       = (ImageButton) findViewById(R.id.exit);
        mSurfaceView        = (SurfaceView) findViewById(R.id.surface);
        chatLine           = (View) findViewById(R.id.chatLine);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "BRI293.TTF");
        chatTotalNumber.setTypeface(typeface);
        chatTotal.bringToFront();
        chatTotalNumber.bringToFront();
    }


    private void LayoutOn(){

        mButtonExit.setVisibility(View.VISIBLE);
        //mButtonCamera.setVisibility(View.VISIBLE);
        chatTotal.setVisibility(View.VISIBLE);
        chatTotalNumber.setVisibility(View.VISIBLE);
        messageInput.setVisibility(View.VISIBLE);
        chatLine.setVisibility(View.VISIBLE);


    }

    private void DisconnectFirebase(){

        sendMessage(" left.", "Event", "");
        mChatListAdapter.notifyDataSetChanged();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mFirebaseRef.removeEventListener(mMessageListener);
        mFirebasePeopleRef.removeEventListener(mChatNumberListener);
        mChatListAdapter.cleanup();


    }

    private void LayoutOut(){

        mButtonExit.setVisibility(View.INVISIBLE);
        //mButtonCamera.setVisibility(View.INVISIBLE);
        chatTotalNumber.setVisibility(View.INVISIBLE);
        chatTotal.setVisibility(View.INVISIBLE);
        messageInput.setVisibility(View.INVISIBLE);
        chatLine.setVisibility(View.INVISIBLE);
    }

    private BackPressEditText.OnBackPressListener onBackPressListener = new BackPressEditText.OnBackPressListener() {
        @Override
        public void onBackPress()
        {
            ViewGroup.MarginLayoutParams params1=(ViewGroup.MarginLayoutParams) messageInput.getLayoutParams();
            //ViewGroup.MarginLayoutParams params2=(ViewGroup.MarginLayoutParams) chatViewId.getLayoutParams();
            //ViewGroup.MarginLayoutParams params3=(ViewGroup.MarginLayoutParams) btnSendChat.getLayoutParams();

            params1.bottomMargin=0;
            //params2.bottomMargin=0;
            //params3.bottomMargin=0;

//            btnSendChat.setLayoutParams(params3);
//            chatViewId.setLayoutParams(params2);
            messageInput.setLayoutParams(params1);

        }
    };



    private void setupListener() {
        messageInput.setImeOptions(EditorInfo.IME_ACTION_SEND);
        messageInput.setOnEditorActionListener(actionListener);
        messageInput.setOnFocusChangeListener(focusListenter);
        messageInput.setOnBackPressListener(onBackPressListener);
        messageInput.setOnClickListener(btnMessageInputListener);
        //mButtonCamera.setOnClickListener(btnSwitchCameraListener);
        mButtonExit.setOnClickListener(btnExitListener);
        chatTotal.setOnClickListener(btnChatTotal);


        mSurfaceView.getHolder().addCallback(this);
    }

    private View.OnClickListener btnMessageInputListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if ( v == messageInput )
            {
                ViewGroup.MarginLayoutParams params1=(ViewGroup.MarginLayoutParams) messageInput.getLayoutParams();
                //ViewGroup.MarginLayoutParams params2=(ViewGroup.MarginLayoutParams) chatViewId.getLayoutParams();
                //ViewGroup.MarginLayoutParams params3=(ViewGroup.MarginLayoutParams) btnSendChat.getLayoutParams();

                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);



                Log.d("fuck", String.valueOf(screenView.getHeight()/metrics.density));
                Log.d("fuck2", String.valueOf(metrics.heightPixels/metrics.density));

                Rect r1 = new Rect();
                Rect r2 = new Rect();

                Window win = getWindow();
                win.getDecorView().getWindowVisibleDisplayFrame(r1);
                win.getDecorView().getGlobalVisibleRect(r2);



                if(Build.MODEL.equals("SM-N900K")) { params1.bottomMargin=880;}
                else if(!Build.MODEL.equals("SHV-E210K")) { params1.bottomMargin=1000;}
                else                             {    params1.bottomMargin=450; }
                //params2.bottomMargin=850;
                //params3.bottomMargin=850;

                //btnSendChat.setLayoutParams(params3);
//                chatViewId.setLayoutParams(params2);
                messageInput.setLayoutParams(params1);
            }
        }
    };

    private View.OnClickListener btnChatTotal = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if ( v == chatTotal )
            {
                onShowPopup(v);
            }
        }
    };

    public static void setSimpleList(ListView listView){

        ArrayList<String> contactsList = new ArrayList<String>();

        for (int index = 0; index < 10; index++) {
            contactsList.add("  I am @ index " + index + " today " + Calendar.getInstance().getTime().toString());
        }

        //   commentListAdapter =  new CommentListAdapter(thisContext, R.layout.fb_comments_list_item, new ArrayList<CommentLog>(), false);

        listView.setAdapter(new ArrayAdapter<String>(thisContext,
                R.layout.fb_comments_list_item, android.R.id.text1, contactsList));
    }

    public void onShowPopup(View v){

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView chatTotalNumberPopup;
        // inflate the custom popup layout
        final View inflatedView = layoutInflater.inflate(R.layout.chat_friend_popup, (ViewGroup)getWindow().getDecorView()  ,false);
        // find the ListView in the popup layout
        chatNumber              = (TextView)inflatedView.findViewById(R.id.chatNumber);
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





    private TextView.OnEditorActionListener actionListener = new TextView.OnEditorActionListener() {


        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            if ( actionId == EditorInfo.IME_ACTION_SEND ) {
                if ( messageInput.getText().toString().equals("") ) return true;

                String msg;
                try {
                    //msg = URLEncoder.encode(messageInput.getText().toString(), "utf-8").replace("+", "%20");
                    msg = messageInput.getText().toString();
                    Log.d("msg1:", messageInput.getText().toString());
                } catch (Exception e) {
                    msg = "";
                    e.printStackTrace();
                }

                String url = "javascript:document.getElementById('message').value = '" + msg + "'; document.getElementById('mSubmit').click();";
                chatView.loadUrl(url);

                if (messageInput.length() > 0) {
                    messageInput.getText().clear();
                }
                //messageInput.setText("");
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

            if (hasFocus) {

                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);

                ViewGroup.MarginLayoutParams params1=(ViewGroup.MarginLayoutParams) messageInput.getLayoutParams();
//                ViewGroup.MarginLayoutParams params2=(ViewGroup.MarginLayoutParams) chatViewId.getLayoutParams();
//                ViewGroup.MarginLayoutParams params3=(ViewGroup.MarginLayoutParams) btnSendChat.getLayoutParams();


                Rect r1 = new Rect();
                Rect r2 = new Rect();

                Window win = getWindow();
                getWindow().getDecorView().setFitsSystemWindows(true);
                win.getDecorView().getWindowVisibleDisplayFrame(r1);
                win.getDecorView().getGlobalVisibleRect(r2);
                int screenHeight = getWindow().getDecorView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r1.bottom;

                Log.d("fuck height: ",  String.valueOf(r1.top) + " : " + r1.bottom + " : " + screenHeight + " : " + r2.top + " : " + r2.bottom);

                //params1.bottomMargin=r1.top*10;
                if(Build.MODEL.equals("SM-N900K")) { params1.bottomMargin=880;}
                else if(!Build.MODEL.equals("SHV-E210K")) { params1.bottomMargin=1000;}
                else                             {    params1.bottomMargin=450; }


  //              chatViewId.setLayoutParams(params2);
                messageInput.setLayoutParams(params1);
                messageInput.bringToFront();
                messageInput.invalidate();

            }
            else {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                ViewGroup.MarginLayoutParams params1=(ViewGroup.MarginLayoutParams) messageInput.getLayoutParams();
                //ViewGroup.MarginLayoutParams params2=(ViewGroup.MarginLayoutParams) chatViewId.getLayoutParams();
                //ViewGroup.MarginLayoutParams params3=(ViewGroup.MarginLayoutParams) btnSendChat.getLayoutParams();

                params1.bottomMargin=0;
                //params3.bottomMargin=0;

                //ChatLine.setLayoutParams(params3);
                messageInput.setLayoutParams(params1);
            }
        }
    };


    private void RecordStream()
    {
        if (recording) {
            recordStream("stopRecording");
            endRecordTime = System.currentTimeMillis();
            EndTimeLine("end");


        } else {

            recording = true;
            startRecordTime = System.currentTimeMillis();
            final String recordUrl = recordStream("startRecording");

            android.os.Handler clickhandler = new android.os.Handler();
            /*clickhandler.postDelayed(new Runnable() {
                @Override
                public void run() {*/
            uploadfILEToS3("photo");
            thumbnailUrl = sendThumbnailMessage();

            StartTimeLine(recordUrl, thumbnailUrl);
            refreshFriendHandler = new Handler() {
                public void handleMessage(Message msg) {

                    sendThumbnailMessage();

                    if(!thumbnailPathSave.equals("")) {
                        Log.d("path", thumbnailPathSave);
                        Toast.makeText(thisContext, "Upload Sucess", Toast.LENGTH_SHORT).show();
                        refreshFriendHandler.removeMessages(10);
                    }
                    else
                        refreshFriendHandler.sendEmptyMessageDelayed(10, 2000);
                }
            };
            if(thumbnailPathSave.equals(""))
                refreshFriendHandler.sendEmptyMessage(10);

            /*    }
            }, 7000);*/


        }

    }

    private View.OnClickListener btnShareListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //shareIt();
        }
    };


/*    private void shareIt() {
        //sharing implementation here
        String parameter    = "realshare?user_id="+user_id + "&room_name="+ room_name + "&user_nm=" + user_nm;

        //YfOMsJ
        URLShortener u = new URLShortener(5, getString(R.string.chat_server_url)+"realshare?key=");
        final String hash_url    = u.shortenURL(parameter);

        Map<String, String> param = new HashMap<>();
        param.put(Define.ACTION, Define.ACTION_SHARE_OLINK);
        param.put(Define.MSG_TYPE, Define.MSG_TYPE_SHARELINK);
        param.put(Define.MSG_HASH_URL, hash_url);
        param.put(Define.MSG_SHARE_URL, parameter);

        NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.ShareLink(thisContext, param));

        sendBroadcast(new Intent("kr.co.wegeneration.realshare.BroadcastActivity.share.start"));

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Here is the share content body";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "RealShare-공유해보세요");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, hash_url );
        startActivityForResult(Intent.createChooser(sharingIntent, "Share via"),1000);


    }*/



    private View.OnClickListener btnSetFlashListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
/*            if (mButtonFlash.getTag().equals("on")) {
                mButtonFlash.setTag("off");
                mButtonFlash.setImageResource(R.drawable.ic_flash_on_holo_light);
            } else {
                mButtonFlash.setImageResource(R.drawable.ic_flash_off_holo_light);
                mButtonFlash.setTag("on");
            }
            mSession.toggleFlash();*/
        }
    };

    private View.OnClickListener btnExitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if ( v == mButtonExit ) {
                AlertDialog.Builder alert = new AlertDialog.Builder(BroadcastActivity.this)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                              if(switch_content==true) {
                                  if (exit_content == true) {
                                      onAir = false;
                                      if (!timeLineYn.isChecked()) EndTimeLine("own");
                                      //else                          EndTimeLine("end");
                                      finishRoom();
                                      dialog.dismiss();     //닫기
                                      finish();
                                  } else {

                                      exit_content = true;
                                      LayoutOut();
                                      getListView().setVisibility(View.INVISIBLE);
                                      RecordStream();
                                      mFirebaseChatInOut.setValue("Exit");
                                      DisconnectFirebase();
                                      layoutExit.bringToFront();
                                      layoutExit.invalidate();
                                  }
                              }
                              else{
                                  finishRoom();
                                  dialog.dismiss();     //닫기
                                  finish();
                              }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                            }
                        }).setMessage(R.string.exitBroadcast);

                alert.show();
            }

        }
    };

    private View.OnClickListener btnSwitchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //if ( v == mButtonCamera ) switchCameraClick();
        }
    };


    private View.OnClickListener btnRefreshListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(LogTag, "btnRefreshListener");

//            // Configures the RTSP client
//            if( mClient == null ) {
//                mClient = new RtspClient();
//                mClient.setSession(mSession);
//                mSurfaceView.setAspectRatioMode(SurfaceView.ASPECT_RATIO_PREVIEW);
//            }
//
//
//            String ip, port, path;
//
//            // We parse the URI written in the Editext
//            Pattern uri = Pattern.compile("rtsp://(.+):(\\d+)/(.+)");
//            Matcher mtch   = uri.matcher( getString(R.string.wowza_server_url) + login_email );
//            mtch.find();
//            ip      = mtch.group(1);
//            port    = mtch.group(2);
//            path    = mtch.group(3);
//
//            mClient.setCredentials(getString(R.string.wowza_publisher_username), getString(R.string.wowza_publisher_password));
//            mClient.setServerAddress(ip, Integer.parseInt(port));
//            mClient.setStreamPath("/" + path);
            mClient.setCallback(BroadcastActivity.this);
        }
    };

    private void setChatLayout(){


        Firebase.setAndroidContext(this);
        mFirebaseRef         = new Firebase(FIREBASE_URL).child("chat").child(room_name).child("messageList");
        mFirebaseChatInOut  = new Firebase(FIREBASE_URL).child("chat").child(room_name).child("ScreenInOut");
        mFirebasePeopleRef  = new Firebase(FIREBASE_URL).child("chat").child(room_name).child("PeopleList");
        mFirebasePeopleRef.child(user_id).push().setValue(user_id);
        mFirebaseChatInOut.setValue("ScreenOn");

        sendMessage(" joined.", "Event", "");
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

    }

    private String chatServerURL;
    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private void setupChatView() {
        chatServerURL = getString(R.string.chat_server_url);
        //chatServerURL= str_chat_server_url;
        // dpmin : 배경 투명 처리


        chatView.setBackgroundColor(0x00000000);
        //if (Build.VERSION.SDK_INT >= 11) chatView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        if (Build.VERSION.SDK_INT >= 11)
        {
            chatView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            chatView.setVisibility(View.VISIBLE);
        }
        else if (Build.VERSION.SDK_INT <= 10) {
            //   chatViewLoadding.setVisibility(View.GONE);
            chatView.setVisibility(View.VISIBLE);
        }

        //((TextView)findViewById(R.id.userIDlable)).setText(user_nm); //

        // 웹뷰에서 자바스크립트실행가능
        chatView.getSettings().setJavaScriptEnabled(true);


        // 웹뷰에서 오디오 자동재생 가능하도록
        chatView.getSettings().setMediaPlaybackRequiresUserGesture(false);


        String url = chatServerURL + "?user_id=" + user_id;  // "?uid=" + user_nm + "&room=" + room; // user_id
        Log.d(LogTag, url);
        chatView.loadUrl(url);
        chatView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.setBackgroundColor(0x00000000);
                //if (Build.VERSION.SDK_INT >= 11)
//                    view.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
                if (Build.VERSION.SDK_INT >= 19) {
                    view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    //chatView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

                } else {
                    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }

                //  chatViewLoadding.setVisibility(View.GONE);
                Log.d(LogTag, "View:Gone");
                chatView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //  chatViewLoadding.setText("채팅 서버 접속 실패.");
            }
        });
    }


    //    private String voiceUploadURL;
    private AsyncHttpClient oAsyncHttpClient = new AsyncHttpClient();

    private String recordStream(String state)
    {
        RequestParams params = new RequestParams();

        filename = "ch"+ user_id + "_" +System.currentTimeMillis();
        // https://s3-us-west-1.amazonaws.com/s3.liveo.me/wowza/파일명.mp4

        //String VideoPath = getString(R.string.wowza_server_vod_url)+ filename+".mp4";
        String VideoPath = getString(R.string.wowza_server_vod_url_s3)+ filename+".mp4";

        String RecordUploadURL  = "http://";
        RecordUploadURL = RecordUploadURL.concat(getString(R.string.wowza_publisher_username));
        RecordUploadURL = RecordUploadURL.concat(":");
        RecordUploadURL = RecordUploadURL.concat(getString(R.string.wowza_publisher_password));
        RecordUploadURL = RecordUploadURL.concat("@");
        RecordUploadURL = RecordUploadURL.concat(getString(R.string.wowza_server_record_url));
        RecordUploadURL = RecordUploadURL.concat("livestreamrecord?app=live");
        RecordUploadURL = RecordUploadURL.concat("&streamname=ch");
        RecordUploadURL = RecordUploadURL.concat(user_id);
        RecordUploadURL = RecordUploadURL.concat("&action=");
        RecordUploadURL = RecordUploadURL.concat(state);
        RecordUploadURL = RecordUploadURL.concat("&outputFile=");
        RecordUploadURL = RecordUploadURL.concat(filename + ".mp4");
//        RecordUploadURL = RecordUploadURL.concat("&outputPath=/home/content");


        try {

            params.put("user_id", user_id);
            params.put("state", state);

            Log.d(LogTag, RecordUploadURL);
        } catch (Exception e){

            e.printStackTrace();
        }

        oAsyncHttpClient.getHttpClient().getParams().setParameter("http.protocol.allow-circular-redirects", true);
        oAsyncHttpClient.post(RecordUploadURL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                Log.d(LogTag, "httpget onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(LogTag, "httpget onSuccess " + Arrays.toString(responseBody));
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
        return VideoPath;
    }

    private void switchCameraClick() {
        if (mClient.isStreaming() ) {
            // dpmin : switch camera
            Log.d(LogTag, "switch camera!");
            mSession.switchCamera();
        } else {
            // dpmin : do nothing
        }
    }


}
