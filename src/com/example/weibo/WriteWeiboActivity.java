package com.example.weibo;

import java.io.IOException;

import com.example.api.AccessTokenKeeper;
import com.example.utils.StringUtils;
import com.example.weibo.LoginActivity.UserCurrent;
import com.king.photo.activity.AlbumActivity;
import com.king.photo.util.Bimp;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WriteWeiboActivity extends Activity implements OnClickListener,
		TextWatcher {

	class MyReListener implements RequestListener {

		@Override
		public void onComplete(String arg0) {
			Looper.prepare();
			Toast.makeText(WriteWeiboActivity.this, "发表成功", Toast.LENGTH_SHORT)
					.show();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mContent.setText("");
					WriteWeiboActivity.this.finish();
				}
			});
			Looper.loop();
		}

		@Override
		public void onError(WeiboException arg0) {
			Looper.prepare();
			Toast.makeText(WriteWeiboActivity.this, "发表失败", Toast.LENGTH_SHORT)
					.show();
			Looper.loop();
		}

		@Override
		public void onIOException(IOException arg0) {
			Looper.prepare();
			Toast.makeText(WriteWeiboActivity.this, "发表失败", Toast.LENGTH_SHORT)
					.show();
			Looper.loop();
		}
	}

	class sendWeiboThread extends Thread {
		@Override
		public void run() {
			try {
				Oauth2AccessToken o2at = AccessTokenKeeper.readAccessToken(
						WriteWeiboActivity.this,
						UserCurrent.currentUser.getUser_id());

				final StatusesAPI statuses = new StatusesAPI(o2at);
				String imagePath = null;
				if (Bimp.tempSelectBitmap.size() > 0)
					imagePath = Bimp.tempSelectBitmap.get(0).imagePath;
				if (StringUtils.isNotEmpty(imagePath)) {
					statuses.upload(mContent.getText().toString(), imagePath,
							null, null, new MyReListener());
				} else {
					statuses.update(mContent.getText().toString(), null, null,
							new MyReListener());
				}
				Bimp.tempSelectBitmap.clear();
			} catch (Exception e) {
			}
		}
	}

	private LinearLayout mBtnSubmit;
	private LinearLayout mBtnSelectPhoto;
	private EditText mContent;
	private TextView mTextCount;
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_weibo);
		mBtnSubmit = (LinearLayout) findViewById(R.id.iv_submit);
		mBtnSelectPhoto = (LinearLayout) findViewById(R.id.iv_camera);
		mContent = (EditText) findViewById(R.id.et_comment);
		mContent.addTextChangedListener(this);
		mTextCount = (TextView) findViewById(R.id.tv_count);
		mBtnSubmit.setOnClickListener(this);
		mBtnSelectPhoto.setOnClickListener(this);
		mImageView = (ImageView) findViewById(R.id.image_select_photo);
	}

	@Override
	protected void onResume() {
		if(!Bimp.tempSelectBitmap.isEmpty()){
			Bitmap bitmap =Bimp.tempSelectBitmap.get(0).getBitmap();
			mImageView.setImageBitmap(bitmap);
		}
		super.onResume();
	}
	private void setTextCount() {
		int textCount = 0;
		String comment = mContent.getText().toString();
		if (StringUtils.isNotEmpty(comment)) {
			textCount = comment.length();
		}
		int remainWordsCount = 140 - textCount;
		if (remainWordsCount < 0)
			mTextCount.setTextColor(Color.RED);
		mTextCount.setText("还可以输入" + remainWordsCount + "个字");
	}

	private boolean checkTextValid() {
		String comment = mContent.getText().toString();
		if (StringUtils.isEmpty(comment)) {
			Toast.makeText(this, "请输入内容后发送", Toast.LENGTH_SHORT).show();
			return false;
		}
		int count = comment.length();
		if (count > 140 || count <= 0) {
			Toast.makeText(this, "输入不合法", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void sendWeibo() {
		if (!checkTextValid())
			return;
		new sendWeiboThread().start();
	}

	private void selectPhoto() {
		Intent intent = new Intent(this, AlbumActivity.class);
		startActivity(intent);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		setTextCount();
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_submit:
			sendWeibo();
			break;
		case R.id.iv_camera:
			selectPhoto();
			break;
		default:
			break;
		}
	}
}
