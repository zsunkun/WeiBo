package com.example.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.ui.LoadingDialog;
import com.example.weibo.R;
import com.weibo.sdk.android.api.StatusesAPI;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class MyAdapter extends BaseAdapter {

	protected Activity mContext;
	protected JSONArray mJsonArray;
	protected Dialog mOriginalPicDialog;
	protected View mOriginalPicView;
	protected Dialog mLoadingDialog;
	protected StatusesAPI mStatuses;

	public MyAdapter(Activity context, JSONArray jsonArray, StatusesAPI statuses) {
		mContext = context;
		mJsonArray = jsonArray;
		mStatuses = statuses;
		mOriginalPicDialog = new AlertDialog.Builder(mContext).create();
		mOriginalPicView = LayoutInflater.from(mContext).inflate(
				R.layout.view_weibo_original_pic, null);
		mLoadingDialog = new LoadingDialog(mContext);
	}

	@Override
	public int getCount() {
		return mJsonArray.length();
	}

	@Override
	public JSONObject getItem(int position) {
		return (JSONObject) mJsonArray.opt(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void updateData(JSONArray jsonArray) {
		mJsonArray = jsonArray;
	}

	public void updateForMoreData(JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			mJsonArray.put(jsonArray.optJSONObject(i));
		}
	}

	@Override
	abstract public View getView(final int position, View convertView,
			ViewGroup parent);

}
