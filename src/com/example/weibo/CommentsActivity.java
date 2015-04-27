package com.example.weibo;

import org.json.JSONArray;

import android.app.Activity;
import android.os.Bundle;

import com.example.adapter.MyAdapter;
import com.example.adapter.MyCommentsWeiBoAdapter;
import com.example.api.AccessTokenKeeper;
import com.example.weibo.LoginActivity.UserCurrent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.api.CommentsAPI;
import com.weibo.sdk.android.api.StatusesAPI;

public class CommentsActivity extends BaseActivity {

	private StatusesAPI mStatuses;
	private Oauth2AccessToken o2at;
	private CommentsAPI mCommentsApi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		o2at = AccessTokenKeeper.readAccessToken(this,
				UserCurrent.currentUser.getUser_id());
		mCommentsApi = new CommentsAPI(o2at);
		mStatuses = new StatusesAPI(o2at);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void getData(boolean isFormoreData) {
		if (!isFormoreData)
			mCommentsApi.timeline(0, 0, maxItemPerPage, 1, false,
					new MyRequestListener(false));
		else
			mCommentsApi.timeline(0, 0, maxItemPerPage, mPageCount, false,
					new MyRequestListener(true));
	}

	@Override
	protected MyAdapter getAdapter(JSONArray weibo_array) {
		return new MyCommentsWeiBoAdapter(this, weibo_array, mStatuses);
	}

	@Override
	protected Activity getActivity() {
		return this;
	}

	@Override
	protected String getJSONName() {
		return "comments";
	}

}
