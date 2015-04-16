package com.example.weibo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.adapter.LoginSpinnerAdapter;
import com.example.api.AccessTokenKeeper;
import com.example.api.User;
import com.example.api.UserInfoHandler;
import com.example.db.UserHandler;
import com.example.utils.Constants;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.UsersAPI;
import com.weibo.sdk.android.net.RequestListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {

	private Weibo mWeibo;
	private Button mLoginButton;
	private Button mAuthButton;
	private Spinner mUserSpinner;
	private List<User> mUserData;
	private User mCurrentUser;
	private TextView mAuthRemind;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mLoginButton = (Button) findViewById(R.id.login_submit);
		mAuthButton = (Button) findViewById(R.id.login_authorize);
		mUserSpinner = (Spinner) findViewById(R.id.login_spinner);
		mAuthRemind = (TextView) findViewById(R.id.login_auth_remind);
		mLoginButton.setOnClickListener(this);
		mAuthButton.setOnClickListener(this);
		UserHandler user = new UserHandler(this);
		mUserData = user.findAllUsers();

		if (mUserData == null || mUserData.isEmpty()) {
			mLoginButton.setVisibility(View.GONE);
			mUserSpinner.setVisibility(View.GONE);
			mAuthRemind.setVisibility(View.VISIBLE);
			startAuth();
		} else {
			List<HashMap<String, Object>> userList = new ArrayList<HashMap<String, Object>>();
			for (User u : mUserData) {
				HashMap<String, Object> userMap = new HashMap<String, Object>();
				userMap.put("name", u.getUser_name());
				userMap.put("head", u.getUser_head());
				userList.add(userMap);
			}
			mUserSpinner.setAdapter(new LoginSpinnerAdapter(this, userList));
			mUserSpinner.setDropDownVerticalOffset(0);// 调整下拉框位置
			mUserSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							mCurrentUser = mUserData.get(position);
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
						}
					});
		}
	}

	private void startAuth() {
		mWeibo = Weibo.getInstance(Constants.APP_KEY, Constants.REDIRECT_URL);
		mWeibo.authorize(LoginActivity.this, new AuthDialogListener());
	}

	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			String uid = values.getString("uid");
			final Oauth2AccessToken accessToken = new Oauth2AccessToken(token,
					expires_in);
			accessToken.setExpiresIn(expires_in);
			UsersAPI users = new UsersAPI(accessToken);

			users.show(Long.parseLong(uid), new RequestListener() {

				@Override
				public void onIOException(IOException arg0) {
					Toast.makeText(LoginActivity.this, "授权失败",
							Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onError(WeiboException arg0) {
					Toast.makeText(LoginActivity.this, "授权失败",
							Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onComplete(String arg0) {
					JSONObject user_json;
					try {
						user_json = new JSONObject(arg0);
						UserCurrent.currentUser = UserInfoHandler
								.updateUserInfoFromJson(LoginActivity.this,
										arg0);
						;
						AccessTokenKeeper.keepAccessToken(LoginActivity.this,
								user_json.getString("idstr"), accessToken);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, MainPage.class);
					startActivity(intent);
					LoginActivity.this.finish();
				}
			});

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

	public static class UserCurrent {
		public static User currentUser;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_submit:
			// 保存当前登录的用户
			UserCurrent.currentUser = mCurrentUser;
			Intent intent = new Intent(LoginActivity.this, MainPage.class);
			startActivity(intent);
			LoginActivity.this.finish();
			Log.i("user", UserCurrent.currentUser.getUser_name());
			break;
		case R.id.login_authorize:
			startAuth();
			break;
		default:
			break;
		}
	}
}
