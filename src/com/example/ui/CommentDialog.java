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
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class CommentDialog extends Dialog implements
		android.view.View.OnClickListener {

	class MyRequestListener implements RequestListener {

		private boolean mIsLoadMoreData = false;

		private MyRequestListener(boolean isLoadMoreData) {
			mIsLoadMoreData = isLoadMoreData;
		}

		@Override
		public void onComplete(String arg0) {
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
							mCommentsAdapter.notifyDataSetChanged();
						}
					}
				});
			} catch (JSONException e) {
			}
		}

		@Override
		public void onError(WeiboException arg0) {
			Log.i("WeiboActivity", "onError :" + arg0.getMessage());
		}

		@Override
		public void onIOException(IOException arg0) {

		}

	}

	private LineEditText mEditText;
	private ImageButton mSentButton;
	private ListView mCommentList;
	private CommentsAdapter mCommentsAdapter;
	private CommentsAPI mCommentsAPI;
	private Activity mContext;
	private long mWeiboID;

	private final int MAX_COMMENT_COUNT = 20;

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
		mSentButton = (ImageButton) findViewById(R.id.comment_send);
		mSentButton.setOnClickListener(this);
		initListView();
	}

	private void initListView() {
		mCommentList = (ListView) findViewById(R.id.list_comment);
		Oauth2AccessToken o2at = AccessTokenKeeper.readAccessToken(mContext,
				UserCurrent.currentUser.getUser_id());
		mCommentsAPI = new CommentsAPI(o2at);
		mCommentsAPI.show(mWeiboID, 0l, 0l, MAX_COMMENT_COUNT, 1,
				WeiboAPI.AUTHOR_FILTER.ALL, new MyRequestListener(false));
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
			Toast.makeText(mContext, "«Î ‰»Îƒ⁄»›", Toast.LENGTH_SHORT).show();
			return;
		}
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				mCommentsAPI.create(content, mWeiboID, false,
						new MyRequestListener(false));
				return null;
			}
		}.execute();
	}
}
