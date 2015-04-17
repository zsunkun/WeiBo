package com.example.weibo;

import org.json.JSONArray;

import com.example.adapter.MyAdapter;
import com.example.adapter.WeiBoListAdapter;
import com.example.api.AccessTokenKeeper;
import com.example.utils.DisplayUtils;
import com.example.weibo.LoginActivity.UserCurrent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;

public class MainPage extends BaseActivity implements OnClickListener,
		SwipeRefreshLayout.OnRefreshListener {

	private long mLastBackClickTime = 0;
	private PopupWindow mBtnAddWeiboWindow;
	private View mBtnAddWeiboView;
	private StatusesAPI mStatuses;
	private Oauth2AccessToken o2at;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		o2at = AccessTokenKeeper.readAccessToken(this,
				UserCurrent.currentUser.getUser_id());
		mStatuses = new StatusesAPI(o2at);
		super.onCreate(savedInstanceState);
		initAddWeiboPopupWindow();
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

	private void StartActivity(Class<?> cls) {
		Intent intent = new Intent(this, cls);
		startActivity(intent);
	}

	@Override
	protected MyAdapter getAdapter(JSONArray weibo_array) {
		return new WeiBoListAdapter(this, weibo_array, mStatuses);
	}

	@Override
	protected void getData(boolean isFormoreData) {
		if (!isFormoreData)
			mStatuses.friendsTimeline(0l, 0l, maxItemPerPage, 1, false,
					WeiboAPI.FEATURE.ALL, false, new MyRequestListener(false));
		else {
			mStatuses.friendsTimeline(0l, 0l, maxItemPerPage, mPageCount,
					false, WeiboAPI.FEATURE.ALL, false, new MyRequestListener(
							true));
		}
	}

	@Override
	protected Activity getActivity() {
		return this;
	}
}
