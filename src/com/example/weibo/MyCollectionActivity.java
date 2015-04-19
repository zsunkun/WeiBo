package com.example.weibo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

	class MyCollectionAdapter extends WeiBoListAdapter {

		public MyCollectionAdapter(Activity context, JSONArray jsonArray,
				StatusesAPI statuses) {
			super(context, jsonArray, statuses);
		}

		@Override
		public JSONObject getItem(int position) {
			try {
				return ((JSONObject) mJsonArray.opt(position))
						.getJSONObject("status");
			} catch (JSONException e) {
			}
			return null;
		}
	}

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
		if (!isFormoreData) {
			mFavoritesAPI.favorites(maxItemPerPage, 1, new MyRequestListener(
					false));
		} else {
			mFavoritesAPI.favorites(maxItemPerPage, mPageCount,
					new MyRequestListener(true));
		}
	}

	@Override
	protected MyAdapter getAdapter(JSONArray weibo_array) {
		return new MyCollectionAdapter(this, weibo_array, mStatuses);
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
