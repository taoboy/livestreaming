package kr.co.wegeneration.realshare.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import kr.co.wegeneration.realshare.R;

import kr.co.wegeneration.realshare.FriendFragment;
import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by admin on 2015-08-14.
 */

public class AccountAdapter extends BaseAdapter { //  RecyclerView.Adapter<AccountViewHolder> {
    private static final String LogTag = "AccountAdapter";

    private Context callerContext;

    private List<Map<String, String>> friendInfoList;

    private List<Button> buttonList;


    static String pullMode;
    static String shareMode;

    static String user_id = "";
    static String user_nm = "";

    public AccountAdapter(Context ctx) {
        new AccountAdapter(ctx, null);
    }

    public AccountAdapter(Context ctx, List<Map<String, String>> list) {
        this.callerContext = ctx;

        if( list != null ) {
            this.friendInfoList = list;
        } else {
            this.friendInfoList = new ArrayList<>();
        }

        buttonList = new ArrayList<Button>();

        SharedPreferences pref = RSPreference.getPreference(callerContext);
        user_id = pref.getString(Define.USER_ID, "");
        user_nm = pref.getString(Define.USER_NM, "");

//        pullMode = ((MainActivity) callerContext).getPullMode();
        shareMode = FriendFragment.getShareMode();
        if( pullMode == null ) pullMode = Define.PULL_MODE_USENAME;
        if( shareMode == null ) shareMode = Define.SHARE_MODE_PUBLIC;

    }

//    @Override
//    public AccountViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//        View view
//                = LayoutInflater.from(viewGroup.getContext())
//                .inflate(R.layout.account_row, viewGroup, false);
//        return new AccountViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(AccountViewHolder viewHolder, int position) {
////        String text = list.get(position);
////        viewHolder.title.setText(text);
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }

    @Override
    public int getCount() {
        return friendInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setButtonClickMode( Button button, Boolean mode ) {
        if( button != null ) button.setClickable(mode);
    }

    public void setButtonClickMode( int positin, Boolean mode ) {
        if( positin < buttonList.size() ) {
            Button button = buttonList.get(positin);
            if( button != null ) button.setClickable(mode);
        }
    }

    public View getView(final int postion, View convertView, ViewGroup parent ) {
        ViewHolder holder; // AccountViewHolder
        if( convertView == null ) {
            LayoutInflater inflater = (LayoutInflater) callerContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.user_row, parent, false);

            holder = new ViewHolder();
            holder.btnAction = (Button) convertView.findViewById(R.id.btnAction);
            holder.txtUserNm = (TextView) convertView.findViewById(R.id.txtUserName);
            holder.txtStatus = (TextView) convertView.findViewById(R.id.txtUserStat);
            holder.chkUser  = (CheckBox) convertView.findViewById(R.id.chkUser);

            convertView.setTag(holder);

            buttonList.add( holder.btnAction );
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if( friendInfoList != null && friendInfoList.get(postion) != null ) {
            final Map<String, String> friendInfo = friendInfoList.get(postion);
            final String friend_id = friendInfo.get(Define.DB_USER_ID);
            final String friend_nm = friendInfo.get(Define.DB_USER_NM);
            final String friend_mode = ( friendInfo.get(Define.DB_USER_MODE) == null ) ? Define.USER_MODE_SIGNIN : friendInfo.get(Define.DB_USER_MODE);
            final String friend_status = friendInfo.get(Define.DB_STATUS);

            Log.d(LogTag, "friend_mode : " + friend_mode);
            Log.d(LogTag, "shareMode : " + shareMode);
            Log.d(LogTag, "pullMode : " + pullMode);

            holder.txtUserNm.setText(friend_nm);

            if( friend_mode.equals( Define.USER_MODE_PUBLIC )) {
                holder.btnAction.setVisibility(View.VISIBLE);
                holder.chkUser.setVisibility(View.INVISIBLE);
                holder.btnAction.setText("LIVE");

            } else if( friend_mode.equals( Define.USER_MODE_PRIVATE )) {
                holder.btnAction.setVisibility(View.VISIBLE);
                holder.chkUser.setVisibility(View.INVISIBLE);
                holder.btnAction.setText("KNOCK");

            } else if( friend_mode.equals( Define.USER_MODE_SIGNIN ) || friend_mode.equals( Define.USER_MODE_WATCH ) || friend_mode.equals( Define.USER_MODE_SIGNOUT ) ) {
                if( shareMode.equals(Define.SHARE_MODE_PUBLIC) ) {
                    holder.btnAction.setVisibility(View.VISIBLE);
                    holder.chkUser.setVisibility(View.INVISIBLE);
                    holder.btnAction.setText("PULL");

                } else {
                    holder.btnAction.setVisibility(View.INVISIBLE);
                    holder.chkUser.setVisibility(View.VISIBLE);
                }
            }
            holder.txtStatus.setText(friend_status);

            holder.btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Button button = (Button) v;
                    String buttonMode = button.getText().toString();

                    if ("PULL".equals(buttonMode)) {
                        Map<String, String> param = new HashMap<String, String>();

                        param.put(Define.ACTION, Define.ACTION_PULL);
                        param.put(Define.MSG_TYPE, Define.MSG_TYPE_PULL);
                        param.put(Define.MSG_SENDER_ID, user_id);
                        param.put(Define.PARAM_RECEIVER_LIST, "[" + friend_id + "]");
                        if (pullMode.equals(Define.PULL_MODE_ANONYMOUSLY)) {
                            param.put(Define.PARAM_ANONYMOUS, "y");
                        } else {
                            param.put(Define.PARAM_ANONYMOUS, "n");
                        }

                        NetController.getInstance(callerContext)
                                .getRequestQueue()
                                .add(NetController.Push(callerContext, param));

                        button.setClickable(false);

                        Handler clickHandler = new Handler();
                        clickHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                button.setClickable(true);
                            }
                        }, 60000);

                    } else if ("LIVE".equals(buttonMode)) {
                        Map<String, String> param = new HashMap<String, String>();

                        param.put(Define.ACTION, Define.USER_MODE_WATCH);
                        param.put(Define.CALLER_ACTIVITY, "MainActivity");
                        param.put(Define.DB_USER_ID, user_id);
                        param.put(Define.OWNER_ID, friend_id);
                        param.put(Define.DB_USER_MODE, Define.USER_MODE_WATCH);

                        NetController.getInstance(callerContext)
                                .getRequestQueue()
                                .add(NetController.Room(callerContext, param));

                    } else if ("KNOCK".equals(buttonMode)) {
                        Map<String, String> param = new HashMap<String, String>();

                        param.put(Define.ACTION, Define.ACTION_KNOCK);
                        param.put(Define.MSG_TYPE, Define.MSG_TYPE_KNOCK);
                        param.put(Define.MSG_SENDER_ID, user_id);
                        param.put(Define.PARAM_RECEIVER_LIST, "[" + friend_id + "]");
                        if( pullMode.equals(Define.PULL_MODE_ANONYMOUSLY)) {
                            param.put(Define.PARAM_ANONYMOUS, "y");
                        } else {
                            param.put(Define.PARAM_ANONYMOUS, "n");
                        }

                        NetController.getInstance(callerContext)
                                .getRequestQueue()
                                .add(NetController.Push(callerContext, param));

                        button.setClickable(false);

                        Handler clickHandler = new Handler();
                        clickHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                button.setClickable(true);
                            }
                        }, 60000);

                    }

                }
            });

            holder.chkUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    friendInfo.put(Define.USER_SELECTED, (isChecked ? "checked" : ""));
                }
            });
        }

        return convertView;
    }

    private class BtnClickHandler extends Handler {
        Button target;
        public BtnClickHandler( Button btn ) {
            target = btn;
        }
        public void setBtnMode( int time ) {
            this.postDelayed(setClickTrue, time);
        }
        private Runnable setClickTrue = new Runnable() {
            public void run() {
                if( target != null ) {
                    target.setClickable(true);
                }
            }
        };
    }

//    Handler clickhandler = new Handler();
//    clickhandler.postDelayed(new Runnable() {
//        @Override
//        public void run() {
//            holder.btnAction.setClickable(true);
//        }
//    }, 60000);

    // 외부에서 아이템 추가 요청 시 사용
    public void add(Map<String, String> friend) {
        // Log.d("name : ", name);
        friendInfoList.add(friend);
    }

    // 외부에서 아이템 삭제 요청 시 사용
    public void remove(int position) {
        friendInfoList.remove(position);
    }

    public List getItems() {
        return friendInfoList;
    }

    public List getCheckedItems() {
        List list = new ArrayList();
        for( int lp0 = 0 ; lp0 < friendInfoList.size() ; lp0++ ) {
            Map<String, String> friendInfo = friendInfoList.get(lp0);
            String checked = friendInfo.get(Define.USER_SELECTED);
            if( checked != null && checked.equals("checked") ) {
                list.add(friendInfo);
            }
        }
        return  list;
    }

    public List getCheckedIds() {
        List list = new ArrayList();
        for( int lp0 = 0 ; lp0 < friendInfoList.size() ; lp0++ ) {
            Map<String, String> friendInfo = friendInfoList.get(lp0);
            String checked = friendInfo.get(Define.USER_SELECTED);
            if( checked != null && checked.equals("checked") ) {
                list.add(friendInfo.get(Define.USER_ID));
            }
        }
        return  list;
    }

    public String getAllIdStr() {
        String ids = "";
        for( int lp0 = 0 ; lp0 < friendInfoList.size() ; lp0++ ) {
            Map<String, String> friendInfo = friendInfoList.get(lp0);
            ids = ids.concat(",").concat(friendInfo.get(Define.USER_ID));
        }
        if( !ids.isEmpty() ) {
            ids = ids.substring(1);
        }
        return "["+ids+"]";
    }

    public String getCheckedIdStr() {
        String ids = "";
        List list = new ArrayList();

        for( int lp0 = 0 ; lp0 < friendInfoList.size() ; lp0++ ) {
            Map<String, String> friendInfo = friendInfoList.get(lp0);
            String checked = friendInfo.get(Define.USER_SELECTED);
            if( checked != null && checked.equals("checked") ) {
                ids = ids.concat(",").concat(friendInfo.get(Define.USER_ID));
            }
        }
        Log.d(LogTag, "AccountAdapter : test");
        if( !ids.isEmpty() ) {
            ids = ids.substring(1);
        }
        return "["+ids+"]";
    }

    private static class ViewHolder {
        public TextView txtUserNm;
        public TextView txtStatus;
        public Button btnAction;
        public CheckBox chkUser;
    }
}
