package com.example.weibo;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.adapter.WeiBoListAdapter;
import com.example.api.AccessTokenKeeper;
import com.example.ui.LoadingDialog;
import com.example.utils.DisplayUtils;
import com.example.weibo.LoginActivity.UserCurrent;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.net.RequestListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
			isRefreshing = false;
			if (!mIsLoadMoreData)
				refresh(arg0);
			else
				refreshForMoreData(arg0);
		}

		@Override
		public void onError(WeiboException arg0) {
		}

		@Override
		public void onIOException(IOException arg0) {
		}

	}

	private long mLastBackClickTime = 0;
	private ImageButton mSwitchMenuButton;

	private SwipeRefreshLayout mSwipeLayout;
	private ListView mListView;
	private Dialog mLoadingDialog;
	private WeiBoListAdapter mAdapter;
	private JSONArray weibo_array;
	private Handler mHandler;
	private StatusesAPI mStatuses;
	private TextView mTopUserName;
	private PopupWindow mBtnAddWeiboWindow;
	private SlidingMenu mSlidingMenu;
	private View mBtnAddWeiboView;
	private ImageView mMoreUserImage;
	private TextView mMoreUserName;
	private LinearLayout mItemHome;
	private LinearLayout mItemCollection;
	private LinearLayout mItemMessage;
	private LinearLayout mItemAt;
	private LinearLayout mItemComment;
	private LinearLayout mItemAboutUs;
	private TextView mWeibo;
	private TextView mAttention;
	private TextView mFans;
	private int mPageCount = 1;
	private final int maxItemPerPage = 20;
	private boolean isRefreshing = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_page);
		initMenu();
		initElements();
		mLoadingDialog = new LoadingDialog(this);
		mLoadingDialog.show();
		mHandler = new Handler();
		initUserInfo();
		initAddWeiboPopupWindow();
		initListView();
	}

	private void initElements() {
		mSwitchMenuButton = (ImageButton) findViewById(R.id.button_menu_switch);
		mSwitchMenuButton.setOnClickListener(this);
		mMoreUserImage = (ImageView) findViewById(R.id.image_head);
		mMoreUserName = (TextView) findViewById(R.id.text_more_user_name);
		mItemAboutUs = (LinearLayout) findViewById(R.id.item_about_us);
		mItemAboutUs.setOnClickListener(this);
		mItemAt = (LinearLayout) findViewById(R.id.item_at_me);
		mItemAt.setOnClickListener(this);
		mItemCollection = (LinearLayout) findViewById(R.id.item_collect);
		mItemCollection.setOnClickListener(this);
		mItemComment = (LinearLayout) findViewById(R.id.item_comment);
		mItemComment.setOnClickListener(this);
		mItemHome = (LinearLayout) findViewById(R.id.item_home);
		mItemHome.setOnClickListener(this);
		mItemMessage = (LinearLayout) findViewById(R.id.item_msg);
		mItemMessage.setOnClickListener(this);
		mTopUserName = (TextView) findViewById(R.id.text_user_name);
		mWeibo = (TextView) findViewById(R.id.text_my_weibo);
		mWeibo.setOnClickListener(this);
		mAttention = (TextView) findViewById(R.id.text_my_attention);
		mAttention.setOnClickListener(this);
		mFans = (TextView) findViewById(R.id.text_my_fans);
		mFans.setOnClickListener(this);
	}

	private void initMenu() {
		// 实例化滑动菜单对象
		mSlidingMenu = new SlidingMenu(this);
		// 设置为左滑菜单
		mSlidingMenu.setMode(SlidingMenu.LEFT);
		// 设置触摸屏幕的模式
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		// 设置滑动阴影的宽度
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		// 设置滑动阴影的图像资源
		mSlidingMenu.setShadowDrawable(R.drawable.shape_slide_menu_shadow);
		// 设置滑动菜单划出时主页面显示的剩余宽度
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置渐入渐出效果的值
		mSlidingMenu.setFadeDegree(0.35f);
		// 附加在Activity上
		mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		// 设置滑动菜单的布局
		mSlidingMenu.setMenu(R.layout.layout_menu);
	}

	private void initUserInfo() {
		String userName = UserCurrent.currentUser.getUser_name();
		if (userName != null) {
			mTopUserName.setText(userName);
			mMoreUserName.setText(userName);
		}
		Drawable userHead = UserCurrent.currentUser.getUser_head();
		mMoreUserImage.setBackgroundDrawable(userHead);
	}

	private void initAddWeiboPopupWindow() {
		mBtnAddWeiboView = getLayoutInflater().inflate(
				R.layout.view_popup_add_weibo, null);
		mBtnAddWeiboWindow = new PopupWindow(mBtnAddWeiboView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mBtnAddWeiboView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StartActivity(WriteWeiboActivity.class);
			}
		});
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		showBtnAddWeibo();
		super.onWindowFocusChanged(hasFocus);
	}

	private void showBtnAddWeibo() {
		int screenHeight = DisplayUtils.getScreenHeight(this);
		int screeWidth = DisplayUtils.getScreenWidth(this);
		mBtnAddWeiboWindow.showAtLocation(mSwipeLayout, Gravity.NO_GRAVITY,
				screeWidth - 200, screenHeight - 200);
	}

	private void dismissBtnAddWeibo() {
		if (mBtnAddWeiboWindow != null && mBtnAddWeiboWindow.isShowing())
			mBtnAddWeiboWindow.dismiss();
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
		isRefreshing = true;// 请求前记为true
		mStatuses.friendsTimeline(0l, 0l, maxItemPerPage, 1, false,
				WeiboAPI.FEATURE.ALL, false, new MyRequestListener(false));

	}

	// SwipeRefreshLayout
	public void onRefresh() {
		if (isRefreshing) {
			return;
		}
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				try {
					isRefreshing = true;
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
		if (isRefreshing)
			return;
		mPageCount++;
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				try {
					isRefreshing = true;
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
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mAdapter = new WeiBoListAdapter(MainPage.this,
								weibo_array, mStatuses);
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
	protected void onDestroy() {
		dismissBtnAddWeibo();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_menu_switch:
			mSlidingMenu.toggle();
			break;
		case R.id.item_home:
			mSlidingMenu.toggle();
			mListView.smoothScrollToPosition(0);
			mSwipeLayout.setRefreshing(true);
			onRefresh();
			break;

		}
	}

	private void StartActivity(Class<?> cls) {
		Intent intent = new Intent(this, cls);
		startActivity(intent);
	}
}
