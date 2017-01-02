package kr.co.wegeneration.realshare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;

/*import com.pedrogomez.renderers.AdapteeCollection;
import com.pedrogomez.renderers.ListAdapteeCollection;
import com.pedrogomez.renderers.RendererAdapter;
import com.pedrogomez.renderers.sample.model.RandomVideoCollectionGenerator;
import com.pedrogomez.renderers.sample.model.Video;
import com.pedrogomez.renderers.sample.module.OnVideoClickedListener;
import com.pedrogomez.renderers.sample.ui.builder.VideoRendererBuilder;
*/
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.firebase.client.Firebase;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.wegeneration.realshare.NetController.NetController;
import kr.co.wegeneration.realshare.adapter.CommentListAdapter;
import kr.co.wegeneration.realshare.adapter.FeedListAdapter;
import kr.co.wegeneration.realshare.adapter.TImeLineParseAdapter;
import kr.co.wegeneration.realshare.app.MyApplication;
import kr.co.wegeneration.realshare.models.CommentLog;
import kr.co.wegeneration.realshare.models.Friend;
import kr.co.wegeneration.realshare.models.Room;
import kr.co.wegeneration.realshare.models.UserInfo;
import kr.co.wegeneration.realshare.util.AppController;
import kr.co.wegeneration.realshare.common.Define;
import kr.co.wegeneration.realshare.common.RSPreference;
import kr.co.wegeneration.realshare.models.FeedItem;


public class TimeLineFragment extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String LogTag = "TimeLineFragment";
    private String URL_FEED = "http://api.androidhive.info/feed/feed.json";

    private TImeLineParseAdapter tImeLineParseAdapter;
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout comment_timeline;
    private CommentListAdapter commentListAdapter;
    private static final int VIDEO_COUNT = 100;
    //public static List<Video> videos = new LinkedList<Video>();
    static Handler refreshFriendHandler;
    static String user_id = "";
    static String user_nm = "";
    static String user_status = "";
    static Context thisContext;



//    public static RendererAdapter<Video> adapter;



    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        super.onCreate(savedInstanceState);

        thisContext = getContext();

        swipeRefreshLayout  = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        SharedPreferences pref = RSPreference.getPreference(getActivity());

        user_id = pref.getString(Define.USER_ID, "");
        user_nm = pref.getString(Define.DB_USER_NM, "");


        listView = (ListView) rootView.findViewById(R.id.list);


        //feedItems = new ArrayList<FeedItem>();
        //listAdapter = new FeedListAdapter(getActivity(), feedItems);
        Firebase firebase = new Firebase(getString(R.string.firebaseUrl));



        tImeLineParseAdapter = new TImeLineParseAdapter(thisContext, getActivity().getAssets(), firebase);
        listView.setAdapter(tImeLineParseAdapter);
        //loadFromParse();
        //tImeLineParseAdapter.loadObjects();
        //tImeLineParseAdapter.notifyDataSetChanged();


        ParseQuery<Room> query_back = Room.getQuery();
        query_back.fromPin("room");
        query_back.findInBackground(new FindCallback<Room>() {
            @Override
            public void done(List<Room> list, com.parse.ParseException e) {
                ParseObject.saveAllInBackground(list);
            }
        });

        /*refreshFriendHandler = new Handler() {
            public void handleMessage(Message msg) {
                tImeLineParseAdapter.loadObjects();
                tImeLineParseAdapter.notifyDataSetChanged();

                refreshFriendHandler.sendEmptyMessageDelayed(7, 4000);
            }
        };
        refreshFriendHandler.sendEmptyMessage(7);*/

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
/*        Cache cache = MyApplication.getInstance().getRequestQueue().getCache();
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

        /*listView = (ListView)rootView.findViewById(R.id.lv_renderers);
        initListView(inflater, rootView, container);

            refreshFriendHandler = new Handler() {
            public void handleMessage(Message msg) {

                callRecordUrlList();
                refreshFriendHandler.sendEmptyMessageDelayed(3, 60000);
            }
        };

        refreshFriendHandler.sendEmptyMessage(3);

        callRecordUrlList();

        adapter.notifyDataSetChanged();
    */


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


    private void loadFromParse() {
        ParseQuery<Room> query = Room.getQuery();
        //query.whereEqualTo("author", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Room>() {
            public void done(List<Room> todos, com.parse.ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground((List<Room>) todos,
                            new SaveCallback() {
                                public void done(com.parse.ParseException e) {
                                    if (e == null) {
                                        tImeLineParseAdapter.loadObjects();
                                        /*if (!getActivity().isFinishing()) {

                                        }*/
                                    } else {
                                        Log.i("TodoListActivity",
                                                "Error pinning todos: "
                                                        + e.getMessage());
                                    }
                                }
                            });
                } else {
                    Log.i("TodoListActivity",
                            "loadFromParse: Error finding pinned todos: "
                                    + e.getMessage());
                }
            }
        });
    }

    public static void callRecordUrlList() {
        Map<String, String> param = new HashMap<String, String>();
        param.put(Define.ACTION, Define.ACTION_STREAM_LIST);
        param.put(Define.DB_USER_ID, user_id);
//            param.put(Define.DB_EMAIL, email);
        Log.i("rstest", "recordListList calling");
        NetController.getInstance(thisContext)
                .getRequestQueue()
                .add(NetController.RecordStreamList(thisContext, user_id, "recordUrlList", param));
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(LogTag, "activity resume ");

       ParseQuery<Room> query = Room.getQuery();


        query.findInBackground(new FindCallback<Room>() {

            @Override
            public void done(final List<Room> list, com.parse.ParseException e) {
                ParseObject.unpinAllInBackground(list, new DeleteCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        //ParseObject.fetchAllIfNeededInBackground(list);
                        ParseObject.pinAllInBackground("room", list);
                    }
                });

            }
        });
    }
    /**
     * Initialize ListVideo with our RendererAdapter.
     */
    private void initListView(LayoutInflater layoutInflater , View rootView, ViewGroup container) {

        //OnVideoClickedListener test = new OnVideoClickedListener(getContext());
        //VideoRenderer.OnVideoClicked test = VideoRenderer.OnVideoClicked();
        //RandomVideoCollectionGenerator randomVideoCollectionGenerator = new RandomVideoCollectionGenerator();
        //AdapteeCollection<Video> videoCollection = 	getVideoAdapteeCollection(randomVideoCollectionGenerator);

        //callRecordUrlList();

        /*AdapteeCollection<Video> videoCollection = 	new ListAdapteeCollection<Video>(videos);
        VideoRendererBuilder rendererBuilder = new VideoRendererBuilder(getContext(), test);
        //RendererAdapter<Video>
        adapter =
                new RendererAdapter<Video>(layoutInflater, rendererBuilder, videoCollection);
*/
        //listView.setAdapter(adapter);
    }


    /*private AdapteeCollection<Video> getVideoAdapteeCollection(
            RandomVideoCollectionGenerator randomVideoCollectionGenerator) {
        return randomVideoCollectionGenerator.generateListAdapteeVideoCollection(100);
    }*/

/*
    public static void RecordStreamListEvent(JSONObject resJSON){

        Log.d(LogTag, " : RecordStreamListEvent ");

        //adapter.clear();
        if( resJSON == null ) return;
        if( !resJSON.has("recordUrlList") ) return;

        try {
            JSONArray users = resJSON.getJSONArray("recordUrlList");
            if( users == null || users.length() == 0 ) return;

            for(int i = 0; i < users.length(); i++){
                JSONObject temp = users.getJSONObject(i);

                String upload_image_yn = temp.getString("upload_no");
               Video video = new Video();
                video.setTitle(temp.getString("status_msg"));
                if(upload_image_yn.equals("y"))
                    video.setThumbnail("http://thetvdb.com/banners/_cache/posters/79168-4.jpg");
                else
                    video.setThumbnail("http://1.234.83.232:3333/download-image/" + temp.getString("record_url") + ".jpg");
                video.setVideoUrl("rtmp://1.234.83.232:1935/vod/" + temp.getString("record_url") + ".mp4");
                video.setLive(true);
                video.setLiked(true);
                video.setFavorite(true);

                //videos.add(video);
                //	Log.d("Title and Thumbnail", video.getTitle() + " : " + video.getThumbnail() + " : " + video.getVideoUrl());
                adapter.add(video);

            }
            listView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    */



}
