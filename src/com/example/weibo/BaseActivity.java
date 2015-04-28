package com.example.weibo;

import java.io.IOException;
import java.text.BreakIterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.adapter.MyAdapter;
import com.example.api.User;
import com.example.ui.LoadingDialog;
import com.example.weibo.LoginActivity.UserCurrent;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/*
 * when you extends this Activity, you must get Weibo API before invoke super.OnCreate
 */
public abstract class BaseActivity extends FragmentActivity implements
		OnClickListener, SwipeRefreshLayout.OnRefreshListener {

	class MyRequestListener implements RequestListener {

		private boolean mIsLoadMoreData = false;

		MyRequestListener(boolean isLoadMoreData) {
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

	private ImageButton mSwitchMenuButton;
	private Dialog mLoadingDialog;
	private MyAdapter mAdapter;
	private JSONArray weibo_array;
	private Handler mHandler;
	private TextView mTopUserName;
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
	private boolean isRefreshing = false;

	protected ListView mListView;
	protected SlidingMenu mSlidingMenu;
	protected SwipeRefreshLayout mSwipeLayout;
	protected int mPageCount = 1;
	protected final int maxItemPerPage = 20;

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
		mSlidingMenu.attachToActivity(getActivity(),
				SlidingMenu.SLIDING_CONTENT);
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
		mWeibo.setText("微博" + UserCurrent.currentUser.getStatuses_count());
		mAttention.setText("关注" + UserCurrent.currentUser.getFriends_count());
		mFans.setText("粉丝" + UserCurrent.currentUser.getFollowers_count());
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
		isRefreshing = true;// 请求前记为true
		getData(false);
	}

	abstract protected void getData(boolean isFormoreData);

	abstract protected MyAdapter getAdapter(JSONArray weibo_array);

	abstract protected Activity getActivity();

	abstract protected String getJSONName();

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
					getData(false);
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
					getData(true);
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
			weibo_array = weibo_json.getJSONArray(getJSONName());
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
						mAdapter = getAdapter(weibo_array);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_menu_switch:
			mSlidingMenu.toggle();
			break;
		case R.id.item_home:
			if (!(getActivity().getComponentName().toString()
					.contains("MainPage"))) {
				StartActivity(MainPage.class);
			} else {
				mSlidingMenu.toggle();
				mListView.smoothScrollToPosition(0);
				mSwipeLayout.setRefreshing(true);
				onRefresh();
			}
			break;
		case R.id.item_collect:
			StartActivity(MyCollectionActivity.class);
			break;
		case R.id.item_at_me:
			StartActivity(MensionsMeActivity.class);
			break;
		case R.id.item_comment:
			StartActivity(CommentsActivity.class);
			break;
		case R.id.text_my_weibo:
			StartActivity(MyWeiboActivity.class);
			break;
		case R.id.text_my_attention:
			break;
		case R.id.text_my_fans:
			break;
		}
	}

	private void StartActivity(Class<?> cls) {
		Intent intent = new Intent(getActivity(), cls);
		startActivity(intent);
		if (mSlidingMenu.isMenuShowing())
			mSlidingMenu.toggle();
	}

	@Override
	public void onBackPressed() {
		StartActivity(MainPage.class);
	}
}
