package kr.co.wegeneration.realshare;

//import butterknife.Bind;
import kr.co.wegeneration.realshare.NetController.NetController;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/*import com.pedrogomez.renderers.AdapteeCollection;
import com.pedrogomez.renderers.RendererAdapter;
import com.pedrogomez.renderers.sample.model.RandomVideoCollectionGenerator;
import com.pedrogomez.renderers.sample.model.Video;
import com.pedrogomez.renderers.sample.module.OnVideoClickedListener;
import com.pedrogomez.renderers.sample.ui.builder.VideoRendererBuilder;

import javax.inject.Inject;
*/
import kr.co.wegeneration.realshare.adapter.ActivityListAdapter;
//import kr.co.wegeneration.realshare.adapter.VideoListAdapter;
import kr.co.wegeneration.realshare.adapter.NotificationListAdapter;
import kr.co.wegeneration.realshare.adapter.NotificationParseAdapter;
import kr.co.wegeneration.realshare.adapter.TImeLineParseAdapter;
import kr.co.wegeneration.realshare.app.MyApplication;
import kr.co.wegeneration.realshare.util.AppController;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;
import kr.co.wegeneration.realshare.models.ActivityLog;
import kr.co.wegeneration.realshare.models.FeedItem;
//import kr.co.wegeneration.realshare.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
//import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseUser;

public class ActivityFragment extends Fragment {


	private static final String TAG = MainActivity.class.getSimpleName();
	private String URL_FEED = "http://api.androidhive.info/feed/feed.json";

	private ListView listView;
	private NotificationListAdapter listAdapter;
	private List<FeedItem> feedItems;

	private static final String LogTag = "ActivityFragment";
	public static String user_id = "";
	public static String user_nm = "";
	static String user_status = "";
	static Context thisContext;
	static ActivityListAdapter activityListAdapter;
	static ListView lstActivity;
	static Handler refreshFriendHandler;
	private SwipeRefreshLayout swipeRefreshLayout;
//	@Inject
//	RendererAdapter<Video> adapter;
	public View.OnClickListener mOnClickListener;
//	@Bind(R.id.lv_renderers)
	ImageView iv_thumbnail;
	private static ActivityFragment mInstance = null;



	public static ActivityFragment getInstance(){
		if(mInstance == null){
			mInstance = new ActivityFragment();
		}
		return mInstance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.fragment_activity, container, false);

		thisContext = getContext();


		SharedPreferences pref = RSPreference.getPreference(getActivity());


		//user_id = pref.getString(Define.USER_ID, "");
		//user_nm = pref.getString(Define.DB_USER_NM, "");
		user_id = ParseUser.getCurrentUser().getObjectId();
		user_nm = ParseUser.getCurrentUser().getUsername();

		swipeRefreshLayout  = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
		listView = (ListView) rootView.findViewById(R.id.list);

		/*feedItems = new ArrayList<FeedItem>();

		listAdapter = new NotificationListAdapter(getActivity(), feedItems);
		listView.setAdapter(listAdapter);*/

		final NotificationParseAdapter tImeLineParseAdapter = new NotificationParseAdapter(thisContext,getActivity().getAssets(), getActivity());
		tImeLineParseAdapter.setPaginationEnabled(true);
		listView.setAdapter(tImeLineParseAdapter);
		//tImeLineParseAdapter.loadObjects();
		//tImeLineParseAdapter.notifyDataSetChanged();

		/*refreshFriendHandler = new Handler() {
			public void handleMessage(Message msg) {
				tImeLineParseAdapter.loadObjects();
				tImeLineParseAdapter.notifyDataSetChanged();

				refreshFriendHandler.sendEmptyMessageDelayed(6, 4000);
			}
		};
		refreshFriendHandler.sendEmptyMessage(6);*/

		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {

						tImeLineParseAdapter.notifyDataSetChanged();
						tImeLineParseAdapter.loadObjects();
						swipeRefreshLayout.setRefreshing(false);
					}
				}, 2000);
			}
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//Toast.makeText(thisContext.getApplicationContext(), "The message has read " + position + " / " + id, Toast.LENGTH_SHORT).show();

			}
		});


		final View viewPos = rootView.findViewById(R.id.myCoordinatorLayout);

		Snackbar snackbar = Snackbar
				.make(rootView, "Change the alarm settings", Snackbar.LENGTH_LONG)
				.setAction("Go", new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						NetController.moveToNotificationSetting(thisContext, user_id);
						//Snackbar snackbar1 = Snackbar.make(rootView, "Message is restored!", Snackbar.LENGTH_SHORT);
						//snackbar1.show();
					}
				});
		snackbar.setActionTextColor(Color.RED);
		View snackbarView = snackbar.getView();
		snackbarView.setBackgroundColor(Color.DKGRAY);
		TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
		textView.setTextColor(Color.YELLOW);
		snackbar.show();

		/*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(thisContext.getApplicationContext(), "read", Toast.LENGTH_SHORT).show();


			}
		});*/


		// We first check for cached request
		/*Cache cache = MyApplication.getInstance().getRequestQueue().getCache();
		Cache.Entry entry = cache.get(URL_FEED);
		if (entry != null) {
			// fetch the data from cache
			try {
				String data = new String(entry.data, "UTF-8");
				try {
					parseJsonFeed(new JSONObject(data));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		} else {
			// making fresh volley request and getting json
			JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
					URL_FEED, null, new Response.Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject response) {
					VolleyLog.d(TAG, "Response: " + response.toString());
					if (response != null) {
						parseJsonFeed(response);
					}
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					VolleyLog.d(TAG, "Error: " + error.getMessage());
				}
			});

			// Adding request to volley request queue
			MyApplication.getInstance().addToRequestQueue(jsonReq);
		}
*/

		//lstActivity = (ListView)rootView.findViewById(R.id.lstActivity);


		/*activityListAdapter =  new ActivityListAdapter(thisContext, R.layout.row_activity, new ArrayList<ActivityLog>(), false);


		refreshFriendHandler = new Handler() {
			public void handleMessage(Message msg) {
				callActivityList();
				activityListAdapter.notifyDataSetChanged();
				refreshFriendHandler.sendEmptyMessageDelayed(2, 60000);
			}
		};


		lstActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(thisContext.getApplicationContext(), "The message has read " + position + " / " + id, Toast.LENGTH_SHORT).show();

				final Map<String, String> param = new HashMap<>();
				param.put(Define.ACTION, Define.ACTION_ACTIVITY_READ);
				param.put(Define.MSG_LOG_ID, activityListAdapter.getItem(position).getLog_id()) ;

				NetController.getInstance(thisContext)
						.getRequestQueue()
						.add(NetController.ActivityRead(thisContext, param));


				callActivityList();
				activityListAdapter.notifyDataSetChanged();
				//    callStatusPopup(String.valueOf(friendListAdapter.getItem(position).getUserId()), view);
			}
		});

		callActivityList();
		refreshFriendHandler.sendEmptyMessage(2);

		lstActivity.setAdapter(activityListAdapter);*/

		return rootView;
	}

	/**
	 * Parsing json reponse and passing the data to feed view list adapter
	 * */
	private void parseJsonFeed(JSONObject response) {
		try {
			JSONArray feedArray = response.getJSONArray("feed");

			for (int i = 0; i < feedArray.length(); i++) {
				JSONObject feedObj = (JSONObject) feedArray.get(i);

				FeedItem item = new FeedItem();
				item.setId(feedObj.getInt("id"));
				item.setName(feedObj.getString("name"));

				// Image might be null sometimes
				String image = feedObj.isNull("image") ? null : feedObj
						.getString("image");
				item.setImge(image);
				item.setStatus(feedObj.getString("status"));
				item.setProfilePic(feedObj.getString("profilePic"));
				item.setTimeStamp(feedObj.getString("timeStamp"));

				// url might be null sometimes
				String feedUrl = feedObj.isNull("url") ? null : feedObj
						.getString("url");
				item.setUrl(feedUrl);

				feedItems.add(item);
			}

			// notify data changes to list adapater
			listAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	public static void callActivityList() {
		Map<String, String> param = new HashMap<String, String>();
		param.put(Define.ACTION, Define.ACTION_ACTIVITY);
		param.put(Define.DB_USER_ID, user_id);
//            param.put(Define.DB_EMAIL, email);
		Log.i("rstest", "activityList calling");
		NetController.getInstance(thisContext)
				.getRequestQueue()
				.add(NetController.ActivityList(thisContext, user_id, "activityfriend",  param));
	}

	public static void ActivityListEvent(JSONObject resJSON){

		Log.d(LogTag, " : ActivityListEvent ");

		activityListAdapter.clear();

		if( resJSON == null ) return;
		if( !resJSON.has("activityList") ) return;

		try {
			JSONArray users = resJSON.getJSONArray("activityList");
			if( users == null || users.length() == 0 ) return;

			for(int i = 0; i < users.length(); i++){
				JSONObject temp = users.getJSONObject(i);
				ActivityLog activity = new ActivityLog(
						temp.getString("log_id"),
						temp.getString("push_msg"),
						temp.getLong("push_date"),
						temp.getString("status").equals("received") ? true :false,
						temp.getString("read_yn"),
						temp.getString("user_sent")
				);
				activityListAdapter.add(activity);
			}

			MainActivity.notif.setText(String.valueOf(users.length()));


		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
