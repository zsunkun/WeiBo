package com.example.weibo;

import org.json.JSONArray;

import android.app.Activity;
import android.os.Bundle;

import com.example.adapter.MyAdapter;
import com.example.adapter.WeiBoListAdapter;
import com.example.api.AccessTokenKeeper;
import com.example.weibo.LoginActivity.UserCurrent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI;

public class MyWeiboActivity extends BaseActivity {

	private StatusesAPI mStatuses;
	private Oauth2AccessToken o2at;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		o2at = AccessTokenKeeper.readAccessToken(this,
				UserCurrent.currentUser.getUser_id());
		mStatuses = new StatusesAPI(o2at);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void getData(boolean isFormoreData) {
		if (!isFormoreData)
			mStatuses.userTimeline(0l, 0l, maxItemPerPage, 1, false,
					WeiboAPI.FEATURE.ALL, false, new MyRequestListener(false));
		else
			mStatuses.userTimeline(0l, 0l, maxItemPerPage, mPageCount, false,
					WeiboAPI.FEATURE.ALL, false, new MyRequestListener(true));
	}

	@Override
	protected MyAdapter getAdapter(JSONArray weibo_array) {
		return new WeiBoListAdapter(this, weibo_array, mStatuses);
	}

	@Override
	protected Activity getActivity() {
		return this;
	}

	@Override
	protected String getJSONName() {
		return "statuses";
	}

}
