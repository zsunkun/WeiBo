package com.example.weibo;

import org.json.JSONArray;

import com.example.adapter.MyAdapter;
import com.example.adapter.WeiBoListAdapter;
import com.example.api.AccessTokenKeeper;
import com.example.weibo.LoginActivity.UserCurrent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.api.FavoritesAPI;
import com.weibo.sdk.android.api.StatusesAPI;

import android.app.Activity;
import android.os.Bundle;

public class MyCollectionActivity extends BaseActivity {
	private FavoritesAPI mFavoritesAPI;
	private StatusesAPI mStatuses;
	private Oauth2AccessToken o2at;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		o2at = AccessTokenKeeper.readAccessToken(this,
				UserCurrent.currentUser.getUser_id());
		mFavoritesAPI = new FavoritesAPI(o2at);
		mStatuses = new StatusesAPI(o2at);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void getData(boolean isFormoreData) {
		if (!isFormoreData)
			mFavoritesAPI.favorites(maxItemPerPage, 1, new MyRequestListener(
					false));
		else {
			mFavoritesAPI.favorites(maxItemPerPage, mPageCount,
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
}
