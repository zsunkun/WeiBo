package com.example.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class SplashActivity extends Activity {

	private ImageView mSplashLogo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_page);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out_fast);
		mSplashLogo = (ImageView) findViewById(R.id.image_splash_logo);
		AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(3000);
		mSplashLogo.setAnimation(animation);

		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				startActivity(new Intent(SplashActivity.this, MainPage.class));
				finish();
			}

		});
	}
	
}
