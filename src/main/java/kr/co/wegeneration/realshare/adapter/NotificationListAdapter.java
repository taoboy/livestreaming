package kr.co.wegeneration.realshare.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import kr.co.wegeneration.realshare.R;
import kr.co.wegeneration.realshare.app.MyApplication;
import kr.co.wegeneration.realshare.util.AppController;
import kr.co.wegeneration.realshare.models.FeedItem;
import kr.co.wegeneration.realshare.widget.CircularNetworkImageView;
import kr.co.wegeneration.realshare.widget.MLRoundedImageView;
import kr.co.wegeneration.realshare.widget.RoundedAvatarDrawable;

public class NotificationListAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private List<FeedItem> feedItems;
	ImageLoader imageLoader ;

	public NotificationListAdapter(Activity activity, List<FeedItem> feedItems) {
		this.activity = activity;
		this.feedItems = feedItems;
		this.imageLoader = MyApplication.getInstance().getImageLoader();
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
			convertView = inflater.inflate(R.layout.activity_item, null);

		if (imageLoader == null)
			imageLoader = MyApplication.getInstance().getImageLoader();

		TextView name = (TextView) convertView.findViewById(R.id.name_activity);
		TextView timestamp = (TextView) convertView
				.findViewById(R.id.timestamp_activity);
		TextView statusMsg = (TextView) convertView
				.findViewById(R.id.txtStatusMsg_activity);

		NetworkImageView profilePic = (NetworkImageView) convertView
				.findViewById(R.id.profilePic_activity);


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

		// user profile pic

		//RoundedAvatarDrawable tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(비트맵명);
		profilePic.setImageUrl(item.getProfilePic(), imageLoader);
		//profilePic.setBackground(tmpRoundedAvatarDrawable);
		//profilePic.setBackgroundResource(R.drawable.circle_feed);


		return convertView;
	}

}
