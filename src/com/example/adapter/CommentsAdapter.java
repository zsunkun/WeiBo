package com.example.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.utils.AsyncImageLoader;
import com.example.utils.Tools;
import com.example.utils.AsyncImageLoader.ImageCallback;
import com.example.weibo.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentsAdapter extends BaseAdapter {

	private JSONArray mDataJsonArray;
	private ViewHolder mViewHolder;
	private Context mContext;

	public CommentsAdapter(Context context, JSONArray array) {
		mDataJsonArray = array;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mDataJsonArray.length();
	}

	@Override
	public JSONObject getItem(int position) {
		return mDataJsonArray.optJSONObject(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void updateData(JSONArray jsonArray) {
		mDataJsonArray = jsonArray;
	}

	public void updateForMoreData(JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			mDataJsonArray.put(jsonArray.optJSONObject(i));
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		mViewHolder = null;
		if (convertView == null) {
			mViewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_comment_list, null);
			mViewHolder.commentContent = (TextView) convertView
					.findViewById(R.id.comment_content);
			mViewHolder.commentTime = (TextView) convertView
					.findViewById(R.id.comment_time);
			mViewHolder.userImage = (ImageView) convertView
					.findViewById(R.id.comment_user_image);
			mViewHolder.userName = (TextView) convertView
					.findViewById(R.id.comment_user_name);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		try {
			final JSONObject itemJson = getItem(position);
			String image_head_url = new JSONObject(itemJson.getString("user"))
					.getString("profile_image_url");
			mViewHolder.userImage.setTag(image_head_url);
			Bitmap head_image = AsyncImageLoader.loadBitmap(0, image_head_url,
					mViewHolder.userImage, position, new ImageCallback() {
						@Override
						public void imageSet(Bitmap bitmap, ImageView iv) {
							iv.setImageBitmap(bitmap);
						}

						@Override
						public void imageLoadDone(Bitmap bitmap) {
						}
					});

			if (head_image != null) {
				mViewHolder.userImage.setImageBitmap(head_image);
			}
			mViewHolder.userName.setText(new JSONObject(itemJson
					.getString("user")).getString("name"));
			mViewHolder.commentTime.setText(Tools.formatDate(itemJson
					.getString("created_at")));
			mViewHolder.commentContent.setText(itemJson.getString("text"));
		} catch (Exception e) {

		}
		return convertView;
	}

	static class ViewHolder {
		public ImageView userImage;
		public TextView userName;
		public TextView commentContent;
		public TextView commentTime;
	}
}
