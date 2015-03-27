package com.example.weibo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.ui.SlidingMenu;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class MainPage extends Activity implements OnClickListener,
		SwipeRefreshLayout.OnRefreshListener {

	private long mLastBackClickTime = 0;
	private SlidingMenu mMenu;
	private ImageButton mSwitchMenuButton;

	private static final int REFRESH_COMPLETE = 0X110;
	private SwipeRefreshLayout mSwipeLayout;
	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	private List<String> mDatas = new ArrayList<String>(Arrays.asList("Java",
			"Javascript", "C++", "Ruby", "Json", "HTML"));

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_COMPLETE:
				mDatas.addAll(Arrays.asList("Lucene", "Canvas", "Bitmap"));
				mAdapter.notifyDataSetChanged();
				mSwipeLayout.setRefreshing(false);
				break;

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_page);
		mMenu = (SlidingMenu) findViewById(R.id.menu);
		mSwitchMenuButton = (ImageButton) findViewById(R.id.button_menu_switch);
		mSwitchMenuButton.setOnClickListener(this);

		mListView = (ListView) findViewById(R.id.id_listview);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);

		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mDatas);
		mListView.setAdapter(mAdapter);
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

	public void onRefresh() {
		mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);

	}
}
