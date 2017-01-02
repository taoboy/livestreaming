package kr.co.wegeneration.realshare.adapter;

import android.animation.Animator;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.app.MyApplication;
import kr.co.wegeneration.realshare.util.AppController;
import kr.co.wegeneration.realshare.models.FeedItem;
import kr.co.wegeneration.realshare.util.FeedImageView;

public class FeedListAdapter extends BaseAdapter {
	private static Activity activity;
	private LayoutInflater inflater;
	private List<FeedItem> feedItems;
	ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
	private PopupWindow popWindow;


	public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
		this.activity = activity;
		this.feedItems = feedItems;
	}

	@Override
	public int getCount() {
		return feedItems.size();
	}

	@Override
	public Object getItem(int location) {
		return feedItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.feed_item, null);

		if (imageLoader == null)
			imageLoader = MyApplication.getInstance().getImageLoader();

		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView timestamp = (TextView) convertView
				.findViewById(R.id.timestamp);
		TextView statusMsg = (TextView) convertView
				.findViewById(R.id.txtStatusMsg);
		NetworkImageView profilePic = (NetworkImageView) convertView
				.findViewById(R.id.profilePic);
		FeedImageView feedImageView = (FeedImageView) convertView
				.findViewById(R.id.feedImage1);

		FeedItem item = feedItems.get(position);

		name.setText(item.getName());

		// Converting timestamp into x ago format
		CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
				Long.parseLong(item.getTimeStamp()),
				System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
		timestamp.setText(timeAgo);

		// Chcek for empty status message
		if (!TextUtils.isEmpty(item.getStatus())) {
			statusMsg.setText(item.getStatus());
			statusMsg.setVisibility(View.VISIBLE);
		} else {
			// status is empty, remove from view
			statusMsg.setVisibility(View.GONE);
		}

		// Checking for null feed url
		/*if (item.getUrl() != null) {
			url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
					+ item.getUrl() + "</a> "));

			// Making url clickable
			url.setMovementMethod(LinkMovementMethod.getInstance());
			url.setVisibility(View.VISIBLE);
		} else {
			// url is null, remove from the view
			url.setVisibility(View.GONE);
		}*/

		// user profile pic
		profilePic.setImageUrl(item.getProfilePic(), imageLoader);

		// Feed image
		if (item.getImge() != null) {
			feedImageView.setImageUrl(item.getImge(), imageLoader);
			feedImageView.setVisibility(View.VISIBLE);
			feedImageView
					.setResponseObserver(new FeedImageView.ResponseObserver() {
						@Override
						public void onError() {
						}

						@Override
						public void onSuccess() {
						}
					});
		} else {
			feedImageView.setVisibility(View.GONE);
		}

		LinearLayout comment_timeline = (LinearLayout)convertView.findViewById(R.id.comment_timeline);
		comment_timeline.setOnClickListener(new View.OnClickListener() {
			@Override

			public void onClick(View v) {

				//onShowPopup(v);
				F1.newInstance().show(activity.getFragmentManager(), null);

			}
		});




		return convertView;
	}


	// call this method when required to show popup
	public void onShowPopup(View v){

		LayoutInflater layoutInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// inflate the custom popup layout
		final View inflatedView = layoutInflater.inflate(R.layout.fb_popup_layout, null,false);
		// find the ListView in the popup layout
		ListView listView = (ListView)inflatedView.findViewById(R.id.commentsListView);

		// get device size
		Display display = activity.getWindowManager().getDefaultDisplay();
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
		popWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.fb_popup_bg));
		// make it focusable to show the keyboard to enter in `EditText`
		popWindow.setFocusable(true);
		// make it outside touchable to dismiss the popup window
		popWindow.setOutsideTouchable(true);
		// ....other code, whatever you want to do with your popupWindow (named dialog in our case here)
		popWindow.setAnimationStyle(R.style.animationName);

		//popWindow.setBackgroundDrawable(d);

		// show the popup at bottom of the screen and set some margin at bottom ie,
		popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 200);

		popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				Drawable d = new ColorDrawable(Color.WHITE);
			//	onDismiss();
			}
		});
		//activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

	}


	public static void setSimpleList(ListView listView){

		ArrayList<String> contactsList = new ArrayList<String>();

		for (int index = 0; index < 10; index++) {
			contactsList.add("  I am @ index " + index + " today " + Calendar.getInstance().getTime().toString());
		}

		//   commentListAdapter =  new CommentListAdapter(thisContext, R.layout.fb_comments_list_item, new ArrayList<CommentLog>(), false);

		listView.setAdapter(new ArrayAdapter<String>(activity,
				R.layout.fb_comments_list_item, android.R.id.text1, contactsList));
	}


	public static class F1 extends DialogFragment {

		public static F1 newInstance() {
			F1 f1 = new F1();

			f1.setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_DeviceDefault_Wallpaper);
			return f1;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			// Remove the default background
			getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

			// Inflate the new view with margins and background
			View v = inflater.inflate(R.layout.popup_layout, container, false);

			// find the ListView in the popup layout
			ListView listView = (ListView)v.findViewById(R.id.commentsListView);
			setSimpleList(listView);
			// Set up a click listener to dismiss the popup if they click outside
			// of the background view
			v.findViewById(R.id.popup_root).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});

			return v;
		}
	}


}
