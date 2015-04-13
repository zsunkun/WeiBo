package com.example.adapter;

import java.util.HashMap;
import java.util.List;

import com.example.weibo.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 自定义Adapter，用于spinner显示图片和文字
 */
public class LoginSpinnerAdapter extends BaseAdapter {

	private List<HashMap<String, Object>> userList;
	private Activity mContext;

	public LoginSpinnerAdapter(Activity context,
			List<HashMap<String, Object>> userList) {
		this.userList = userList;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return userList.size();
	}

	@Override
	public HashMap<String, Object> getItem(int position) {
		return userList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mContext.getLayoutInflater().inflate(
					R.layout.login_user_item, null);
		}
		ImageView image = (ImageView) convertView
				.findViewById(R.id.spinner_userhead);
		TextView text = (TextView) convertView
				.findViewById(R.id.spinner_username);

		image.setImageDrawable((Drawable) (getItem(position)).get("head"));
		text.setText((String) (getItem(position)).get("name"));

		return convertView;
	}
}
