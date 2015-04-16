package com.example.ui;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.adapter.CommentsAdapter;
import com.example.api.AccessTokenKeeper;
import com.example.utils.StringUtils;
import com.example.weibo.LoginActivity.UserCurrent;
import com.example.weibo.R;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.CommentsAPI;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.net.RequestListener;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class CommentDialog extends Dialog implements
		android.view.View.OnClickListener {

	class MyRequestListener implements RequestListener {

		private boolean mIsLoadMoreData = false;

		private MyRequestListener(boolean isLoadMoreData) {
			mIsLoadMoreData = isLoadMoreData;
		}

		@Override
		public void onComplete(String arg0) {
			isRefreshing = false;
			final JSONArray jsonArray;
			try {
				JSONObject commentJson = new JSONObject(arg0);
				jsonArray = commentJson.getJSONArray("comments");
				mContext.runOnUiThread(new Runnable() {
					public void run() {
						if (mCommentsAdapter == null) {
							mCommentsAdapter = new CommentsAdapter(mContext,
									jsonArray);
							mCommentList.setAdapter(mCommentsAdapter);
						} else {
							if (mIsLoadMoreData)
								mCommentsAdapter.updateForMoreData(jsonArray);
							else
								mCommentsAdapter.updateData(jsonArray);
							mCommentsAdapter.notifyDataSetChanged();
						}
						dismissLoading();
					}
				});
			} catch (JSONException e) {
			}
		}

		@Override
		public void onError(WeiboException arg0) {
		}

		@Override
		public void onIOException(IOException arg0) {
		}

	}

	private LineEditText mEditText;
	private LinearLayout mSentButton;
	private ListView mCommentList;
	private CommentsAdapter mCommentsAdapter;
	private CommentsAPI mCommentsAPI;
	private Activity mContext;
	private ImageView mLoadingImage;
	private long mWeiboID;
	private int mPageCount = 1;
	private boolean isRefreshing = false;

	private final int MAX_COMMENT_COUNT = 20;
	private Animation mLoadingAnim;

	public CommentDialog(Activity context, long id) {
		super(context, R.style.Mydialog);
		mWeiboID = id;
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_comment);
		mEditText = (LineEditText) findViewById(R.id.edit_comment);
		mSentButton = (LinearLayout) findViewById(R.id.comment_send);
		mLoadingImage = (ImageView) findViewById(R.id.img_loading);
		mLoadingAnim = AnimationUtils.loadAnimation(mContext,
				R.anim.anim_rotate);
		mSentButton.setOnClickListener(this);
		initListView();
		showLoading();
	}

	private void initListView() {
		mCommentList = (ListView) findViewById(R.id.list_comment);
		mCommentList.setOnScrollListener(new OnScrollListenerImpl());
		Oauth2AccessToken o2at = AccessTokenKeeper.readAccessToken(mContext,
				UserCurrent.currentUser.getUser_id());
		mCommentsAPI = new CommentsAPI(o2at);
		mCommentsAPI.show(mWeiboID, 0l, 0l, MAX_COMMENT_COUNT, 1,
				WeiboAPI.AUTHOR_FILTER.ALL, new MyRequestListener(false));
		isRefreshing = true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.comment_send:
			sendComment();
			break;
		}
	}

	private void sendComment() {
		final String content = mEditText.getText().toString();
		if (StringUtils.isEmpty(content)) {
			Toast.makeText(mContext, "请输入内容", Toast.LENGTH_SHORT).show();
			return;
		}
		mEditText.setText("");
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				isRefreshing = true;
				mCommentsAPI.create(content, mWeiboID, false,
						new MyRequestListener(false));
				return null;
			}

			protected void onPostExecute(Void result) {
				Toast.makeText(mContext, "评论成功", Toast.LENGTH_SHORT);
			};
		}.execute();
	}

	private class OnScrollListenerImpl implements OnScrollListener {
		@Override
		public void onScroll(AbsListView listView, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}

		@Override
		public void onScrollStateChanged(AbsListView listview, int scrollState) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				if (listview.getLastVisiblePosition() == listview.getCount() - 1) {
					loadMoreData();
				}
			}
		}
	}

	private void loadMoreData() {
		if (isRefreshing) {
			return;
		}
		mPageCount++;
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				mCommentsAPI.show(mWeiboID, 0l, 0l, MAX_COMMENT_COUNT,
						mPageCount, WeiboAPI.AUTHOR_FILTER.ALL,
						new MyRequestListener(true));
				return null;
			}

		}.execute();
	}

	private void showLoading() {
		mLoadingImage.setVisibility(View.VISIBLE);
		mLoadingImage.startAnimation(mLoadingAnim);
		mCommentList.setVisibility(View.GONE);
	}

	private void dismissLoading() {
		mCommentList.setVisibility(View.VISIBLE);
		mLoadingImage.setVisibility(View.GONE);
	}
}
