package com.example.weibo;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.adapter.WeiBoListAdapter;
import com.example.api.AccessTokenKeeper;
import com.example.ui.LoadingDialog;
import com.example.ui.SlidingMenu;
import com.example.weibo.LoginActivity.UserCurrent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.net.RequestListener;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainPage extends Activity implements OnClickListener,
		SwipeRefreshLayout.OnRefreshListener {

	class MyRequestListener implements RequestListener {

		private boolean mIsLoadMoreData = false;

		private MyRequestListener(boolean isLoadMoreData) {
			mIsLoadMoreData = isLoadMoreData;
		}

		@Override
		public void onComplete(String arg0) {
			if (!mIsLoadMoreData)
				refresh(arg0);
			else
				refreshForMoreData(arg0);

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
	private Dialog mLoadingDialog;
	private WeiBoListAdapter mAdapter;
	private JSONArray weibo_array;
	private Handler handler;
	private StatusesAPI mStatuses;
	private TextView mTopUserName;
	private int mPageCount = 1;
	private final int maxItemPerPage = 20;

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
		mLoadingDialog = LoadingDialog.createLoadingDialog(this);
		mLoadingDialog.show();
		handler = new Handler();
		initListView();
	}

	private void initListView() {
		mListView = (ListView) findViewById(R.id.id_listview);
		mListView.setOnScrollListener(new OnScrollListenerImpl());
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);

		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		Oauth2AccessToken o2at = AccessTokenKeeper.readAccessToken(this,
				UserCurrent.currentUser.getUser_id());

		mStatuses = new StatusesAPI(o2at);
		mStatuses.friendsTimeline(0l, 0l, maxItemPerPage, 1, false,
				WeiboAPI.FEATURE.ALL, false, new MyRequestListener(false));

	}

	// SwipeRefreshLayout
	public void onRefresh() {
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(1000);
					mStatuses.friendsTimeline(0l, 0l, maxItemPerPage, 1, false,
							WeiboAPI.FEATURE.ALL, false, new MyRequestListener(
									false));
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

	private void loadMoreData() {
		mPageCount++;
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(1000);
					mStatuses.friendsTimeline(0l, 0l, maxItemPerPage,
							mPageCount, false, WeiboAPI.FEATURE.ALL, false,
							new MyRequestListener(true));
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
			if (mAdapter != null) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mAdapter.updateData(weibo_array);
						mAdapter.notifyDataSetChanged();
					}
				});
			} else {
				handler.post(new Runnable() {
					@Override
					public void run() {
						mAdapter = new WeiBoListAdapter(MainPage.this,
								weibo_array);
						mListView.setAdapter(mAdapter);
						if (mLoadingDialog.isShowing())
							mLoadingDialog.dismiss();
					}
				});
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void refreshForMoreData(String arg0) {
		JSONObject weibo_json;
		try {
			weibo_json = new JSONObject(arg0);
			weibo_array = weibo_json.getJSONArray("statuses");
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mAdapter != null) {
						mAdapter.updateForMoreData(weibo_array);
						mAdapter.notifyDataSetChanged();
					}
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private class OnScrollListenerImpl implements OnScrollListener {
		@Override
		public void onScroll(AbsListView listView, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}

		@Override
		public void onScrollStateChanged(AbsListView listview, int scrollState) {
			// 当不滚动时
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				// 判断是否滚动到底部
				if (listview.getLastVisiblePosition() == listview.getCount() - 1) {
					// 加载更多功能的代码
					loadMoreData();
				}
			}
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
