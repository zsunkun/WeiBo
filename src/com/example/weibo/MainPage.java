package com.example.weibo;

import com.example.ui.SlidingMenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainPage extends Activity implements OnClickListener {

	private long mLastBackClickTime = 0;
	private SlidingMenu mMenu;
	private ImageButton mSwitchMenuButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_page);
		mMenu = (SlidingMenu) findViewById(R.id.menu);
		mSwitchMenuButton = (ImageButton) findViewById(R.id.button_menu_switch);
		mSwitchMenuButton.setOnClickListener(this);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_menu_switch:
			mMenu.toggleMenu();
			break;

		}
	}
}
