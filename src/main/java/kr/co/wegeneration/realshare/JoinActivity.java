package kr.co.wegeneration.realshare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.NetController.ParseNetController;
import kr.co.wegeneration.realshare.app.MyApplication;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.gcm.GCMRegister;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JoinActivity extends AppCompatActivity {
    private static final String LogTag = "JoinActivity";

    static Context thisContext;

    EditText edtUserNmLast, edtId, edtPasswd, edtUserNmFirst;
    TextView txtGoLogin;
    Button btnSignUp;

    String email = "";
    String user_nm = "";
    String passwd = "";

    String userFirstName = "";
    String userLastName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        thisContext = this;

        setLayoutInit();
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
        return super.onOptionsItemSelected(item);
    }

    private void setLayoutInit() {
        edtUserNmLast = (EditText) findViewById(R.id.edtUserNmLast);
        edtUserNmFirst = (EditText) findViewById(R.id.edtUserNmFirst);
        edtId = (EditText) findViewById(R.id.edtId);
        edtPasswd = (EditText) findViewById(R.id.edtPasswd);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        txtGoLogin = (TextView) findViewById(R.id.txtGoLogin);


        txtGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetController.moveToLogin(thisContext);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtId.getText().toString();
                try {
                    userFirstName = URLEncoder.encode(edtUserNmFirst.getText().toString(), "utf-8");
                    userLastName = URLEncoder.encode(edtUserNmLast.getText().toString(), "utf-8");

                    user_nm = URLEncoder.encode(edtUserNmFirst.getText().toString(), "utf-8") + " " + URLEncoder.encode(edtUserNmLast.getText().toString(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                passwd = edtPasswd.getText().toString();

                if (email == null || email.isEmpty()) {
                    Toast.makeText(thisContext.getApplicationContext(), "Id/email을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.length() > 60) {
                    Toast.makeText(thisContext.getApplicationContext(), "Id/email 60자를 넘습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isEmailValid(email)) {
                    Toast.makeText(thisContext.getApplicationContext(), "Id/email이 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (user_nm == null || user_nm.isEmpty()) {
                    Toast.makeText(thisContext.getApplicationContext(), "사용자명을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (user_nm.length() > 30) {
                    Toast.makeText(thisContext.getApplicationContext(), "사용자명이 30자를 넘습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (passwd == null || passwd.isEmpty()) {
                    Toast.makeText(thisContext.getApplicationContext(), "password를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }


                try {


                    Map<String, String> param = new HashMap<>();
                    param.put(Define.ACTION             , Define.ACTION_SIGNUP);
                    param.put(Define.DB_EMAIL           , email);
                    param.put(Define.DB_PASSWD          , passwd);
                    param.put(Define.DB_USER_FIRST_NM  , userFirstName);
                    param.put(Define.DB_USER_LAST_NM   , userLastName);
                    param.put(Define.DB_USER_NM         , user_nm);


                    ParseNetController.SignUp(thisContext, param);




                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;

    }

}
