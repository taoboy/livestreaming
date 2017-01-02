package kr.co.wegeneration.realshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.NetController.ParseNetController;
import kr.co.wegeneration.realshare.app.MyApplication;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;
import kr.co.wegeneration.realshare.gcm.GCMRegister;

public class LoginActivity extends AppCompatActivity {
    private static final String LogTag = "LoginActivity";

    static Context thisContext;

    EditText edtId, edtPasswd;
    Button btnLogin;
    TextView btnJoin;

    static String email = "";
    static String passwd = "";
    static String gcm_reg_id = "";
    static String object_id = "";
    static String installation_id="";

    static String msgType = "";
    static String senderId = "";
    static String senderNm = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        thisContext = this;

        Intent intent = getIntent();
        if( intent != null ) {
            msgType = intent.getStringExtra(Define.MSG_TYPE);
            senderId = intent.getStringExtra(Define.MSG_SENDER_ID);
            senderNm = intent.getStringExtra(Define.MSG_SENDER_NM);
        }

        setLayoutInit();


        try {

            object_id = ParseInstallation.getCurrentInstallation().getObjectId();
            //gcm_reg_id = GCMRegister.register(this);
            installation_id = ParseInstallation.getCurrentInstallation().getInstallationId();

        }catch(Exception e ) {e.printStackTrace();}

        Log.d(LogTag, "object_id : " + object_id);
        Log.d(LogTag, "gcm_reg_id : " + gcm_reg_id);
        Log.d(LogTag, "installation_id : " + installation_id);
    }

    @Override
    public void onResume() {
        Log.d(LogTag, "onResume !!");
        super.onResume();

    }

    @Override
    public void onPause() {
        Log.d(LogTag, "onPause");
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /*private View.OnFocusChangeListener focusListenter = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

            if (hasFocus) {
                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);

                ViewGroup.MarginLayoutParams params1=(ViewGroup.MarginLayoutParams) edtId.getLayoutParams();
                ViewGroup.MarginLayoutParams params2=(ViewGroup.MarginLayoutParams) edtPasswd.getLayoutParams();
                ViewGroup.MarginLayoutParams params3=(ViewGroup.MarginLayoutParams) btnLogin.getLayoutParams();

                params1.bottomMargin=850;
                params2.bottomMargin=850;
                params3.bottomMargin=850;


                edtId.setLayoutParams(params1);
                edtPasswd.setLayoutParams(params2);
                btnLogin.setLayoutParams(params3);


            }
            else {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                ViewGroup.MarginLayoutParams params1=(ViewGroup.MarginLayoutParams) edtId.getLayoutParams();
                ViewGroup.MarginLayoutParams params2=(ViewGroup.MarginLayoutParams) edtPasswd.getLayoutParams();
                ViewGroup.MarginLayoutParams params3=(ViewGroup.MarginLayoutParams) btnLogin.getLayoutParams();

                params1.bottomMargin=0;
                params2.bottomMargin=0;
                params3.bottomMargin=0;


                edtId.setLayoutParams(params1);
                edtPasswd.setLayoutParams(params2);
                btnLogin.setLayoutParams(params3);

            }
        }
    };*/

    private void setLayoutInit() {
        edtId = (EditText) findViewById(R.id.edtId);
        edtPasswd = (EditText) findViewById(R.id.edtPasswd);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnJoin = (TextView) findViewById(R.id.btnJoin);

        edtId.clearFocus();

        final SpannableStringBuilder sp = new SpannableStringBuilder(getString(R.string.join));
        sp.setSpan(new ForegroundColorSpan(Color.RED), 14, 21, Spannable.SPAN_POINT_MARK);
        sp.setSpan(new UnderlineSpan(), 14, 21, Spannable.SPAN_POINT_MARK);
        btnJoin.append(sp);
        //edtId.setImeOptions(EditorInfo.IME_ACTION_SEND);
        //edtId.setOnEditorActionListener(actionListener);
        //edtId.setOnFocusChangeListener(focusListenter);

        //edtPasswd.setImeOptions(EditorInfo.IME_ACTION_SEND);
        //edtPasswd.setOnEditorActionListener(actionListener);
        //edtPasswd.setOnFocusChangeListener(focusListenter);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtId.getText().toString();
                passwd = edtPasswd.getText().toString();

                if( email == null || email.isEmpty() ) {
                    Toast.makeText(thisContext.getApplicationContext(), "Id를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if( passwd == null || passwd.isEmpty() ) {
                    Toast.makeText(thisContext.getApplicationContext(), "password를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {

                    final SharedPreferences prefs = RSPreference.getPreference(getApplicationContext());

                    Map<String, String> param = new HashMap<String, String>();
                    param.put(Define.ACTION, Define.ACTION_SIGNIN);
                    param.put(Define.DB_EMAIL, email);
                    param.put(Define.DB_PASSWD, passwd);

                    /*
                    NetController.getInstance(thisContext)
                            .getRequestQueue()
                            .add(NetController.SignIn(thisContext, param));*/

                    ParseNetController.SignIn(thisContext, param);

                }catch(Exception e) {e.printStackTrace();}
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent joinIntent = new Intent( getApplicationContext(), JoinActivity.class);
                startActivity(joinIntent);
                finish();
                // resume 으로 돌아올 수 있다.
            }
        });
    }


}
;