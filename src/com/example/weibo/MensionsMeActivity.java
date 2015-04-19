package com.example.weibo;

import org.json.JSONArray;
import com.example.adapter.MyAdapter;
import com.example.adapter.WeiBoListAdapter;
import com.example.api.AccessTokenKeeper;
import com.example.weibo.LoginActivity.UserCurrent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.api.WeiboAPI.SRC_FILTER;

import android.app.Activity;
import android.os.Bundle;

public class MensionsMeActivity extends BaseActivity {
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
		if (!isFormoreData) {
			mStatuses.mentions(0l, 0l, maxItemPerPage, 1,
					WeiboAPI.AUTHOR_FILTER.ALL, SRC_FILTER.WEIBO.ALL,
					WeiboAPI.TYPE_FILTER.ALL, false, new MyRequestListener(
							false));
		} else {
			mStatuses.mentions(0l, 0l, maxItemPerPage, mPageCount,
					WeiboAPI.AUTHOR_FILTER.ALL, SRC_FILTER.WEIBO.ALL,
					WeiboAPI.TYPE_FILTER.ALL, false,
					new MyRequestListener(true));
		}
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
		return "favorites";
	}
}
