package com.example.weibo;

import com.example.utils.Constants;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private Weibo mWeibo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mWeibo = Weibo.getInstance(Constants.APP_KEY, Constants.REDIRECT_URL);
		mWeibo.authorize(LoginActivity.this, new AuthDialogListener());
	}

	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			String uid = values.getString("uid");
			Log.v("uid", uid);
			Oauth2AccessToken accessToken = new Oauth2AccessToken(token,
					expires_in);
			accessToken.setExpiresIn(expires_in);
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, MainPage.class);
			startActivity(intent);
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

		@Override
		public void onError(WeiboDialogError arg0) {
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + arg0.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

	}
}
