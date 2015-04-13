package com.example.weibo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.adapter.WeiBoListAdapter;
import com.example.api.AccessTokenKeeper;
import com.example.ui.SlidingMenu;
import com.example.weibo.LoginActivity.UserCurrent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.net.RequestListener;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainPage extends Activity implements OnClickListener,
		SwipeRefreshLayout.OnRefreshListener {

	class MyRequestListener implements RequestListener {

		@Override
		public void onComplete(String arg0) {
			refresh(arg0);

		}

		@Override
		public void onError(WeiboException arg0) {
			Log.i("WeiboActivity", "onError :" + arg0.getMessage());
		}

		@Override
		public void onIOException(IOException arg0) {

		}

	}

	private long mLastBackClickTime = 0;
	private SlidingMenu mMenu;
	private ImageButton mSwitchMenuButton;

	private SwipeRefreshLayout mSwipeLayout;
	private ListView mListView;
	private WeiBoListAdapter mAdapter;
	private JSONArray weibo_array;
	private Handler handler;
	private StatusesAPI statuses;
	private TextView mTopUserName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_page);
		mMenu = (SlidingMenu) findViewById(R.id.menu);
		mSwitchMenuButton = (ImageButton) findViewById(R.id.button_menu_switch);
		mTopUserName = (TextView) findViewById(R.id.text_user_name);
		String userName = UserCurrent.currentUser.getUser_name();
		if (userName != null)
			mTopUserName.setText(userName);
		mSwitchMenuButton.setOnClickListener(this);
		handler = new Handler();
		initListView();
	}

	private void initListView() {
		mListView = (ListView) findViewById(R.id.id_listview);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);

		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		Oauth2AccessToken o2at = AccessTokenKeeper.readAccessToken(this,
				UserCurrent.currentUser.getUser_id());

		statuses = new StatusesAPI(o2at);
		statuses.friendsTimeline(0l, 0l, 20, 1, false, WeiboAPI.FEATURE.ALL,
				false, new MyRequestListener());

	}

	public void onRefresh() {
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(1000);
					statuses.friendsTimeline(0l, 0l, 20, 1, false,
							WeiboAPI.FEATURE.ALL, false,
							new MyRequestListener());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

				mAdapter.notifyDataSetChanged();
				mSwipeLayout.setRefreshing(false);
			}

		}.execute();
	}

	private void refresh(String arg0) {
		JSONObject weibo_json;
		try {
			weibo_json = new JSONObject(arg0);
			weibo_array = weibo_json.getJSONArray("statuses");
			if (mAdapter != null)
				mAdapter.updateData(weibo_array);
			else {
				mAdapter = new WeiBoListAdapter(this, weibo_array);
				handler.post(new Runnable() {

					@Override
					public void run() {
						mListView.setAdapter(mAdapter);
					}
				});
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		long now = System.currentTimeMillis();
		if (now - mLastBackClickTime < 1000) {
			super.onBackPressed();
			return;
		}
		mLastBackClickTime = now;
		Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_menu_switch:
			mMenu.toggleMenu();
			break;

		}
	}
}
