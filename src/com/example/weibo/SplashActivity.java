package com.example.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

	private Runnable mRunnable = new Runnable() {
		public void run() {
			startActivity(new Intent(SplashActivity.this, MainPage.class));
			finish();
		}
	};

	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_page);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out_fast);
		mHandler.postDelayed(mRunnable, 1500);

	}
}
