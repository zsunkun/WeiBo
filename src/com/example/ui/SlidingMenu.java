package com.example.ui;

import com.example.utils.DisplayUtils;
import com.example.weibo.R;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class SlidingMenu extends FrameLayout {

	private int mScreenWidth;
	private int mMenuRightPadding = 20;
	private int mSafeWidth = 30;
	private int mMenuWidth;
	private int mHalfMenuWidth;
	private boolean mIsMenuOpen;
	private ViewGroup mMenu;
	private ViewGroup mContent;
	private LinearLayout.LayoutParams mMenuParams;

	private boolean mFirst = true;

	public SlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScreenWidth = DisplayUtils.getScreenWidth(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mFirst) {
			LinearLayout wrapper = (LinearLayout) getChildAt(0);
			findViewById(R.id.id_listview).setOnTouchListener(listener);
			mMenu = (ViewGroup) wrapper.getChildAt(0);
			mContent = (ViewGroup) wrapper.getChildAt(1);
			// dp to px
			mMenuRightPadding = DisplayUtils.getPixel(getContext(),
					mMenuRightPadding);
			mMenuWidth = mScreenWidth - mMenuRightPadding;
			mSafeWidth = DisplayUtils.getPixel(getContext(), mSafeWidth);
			mHalfMenuWidth = mMenuWidth / 2;
			mMenu.getLayoutParams().width = mMenuWidth;
			mContent.getLayoutParams().width = mScreenWidth;
			mMenuParams = (LinearLayout.LayoutParams) mMenu.getLayoutParams();

		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			mMenuParams.leftMargin = -mMenuWidth;
			mIsMenuOpen = false;
			mFirst = false;
		}
	}

	private OnTouchListener listener = new OnTouchListener() {
		private float startX = 0;
		private float endX = 0;

		@Override
		public boolean onTouch(View v, MotionEvent ev) {
			int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_UP:
				endX = ev.getRawX();
				float finalDelta = endX - startX;
				if (finalDelta > mHalfMenuWidth && !mIsMenuOpen) {
					mMenuParams.leftMargin = 0;
					mMenu.setLayoutParams(mMenuParams);
					mIsMenuOpen = true;
				}
				if (finalDelta < -mHalfMenuWidth && mIsMenuOpen) {
					mMenuParams.leftMargin = -mMenuWidth;
					mMenu.setLayoutParams(mMenuParams);
					mIsMenuOpen = false;
				}
				if (finalDelta < mHalfMenuWidth && finalDelta > 0
						&& !mIsMenuOpen) {
					mMenuParams.leftMargin = -mMenuWidth;
					mMenu.setLayoutParams(mMenuParams);
				}
				if (finalDelta < 0 && finalDelta > (-mHalfMenuWidth)
						&& mIsMenuOpen) {
					mMenuParams.leftMargin = 0;
					mMenu.setLayoutParams(mMenuParams);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				endX = ev.getRawX();
				float delta = endX - startX;
				if (delta > mSafeWidth && !mIsMenuOpen) {
					mMenuParams.leftMargin = (int) (-mMenuWidth + delta);
					mMenu.setLayoutParams(mMenuParams);
				}
				if (delta < (-mSafeWidth) && mIsMenuOpen) {
					mMenuParams.leftMargin = (int) (0 + delta);
					mMenu.setLayoutParams(mMenuParams);
				}
				break;
			case MotionEvent.ACTION_DOWN:
				startX = ev.getRawX();
				break;
			}
			return false;
		}
	};

	public void toggleMenu() {
		if (mIsMenuOpen) {
			new MoveTask().execute(-mMenuWidth);
			mIsMenuOpen = false;
		} else {
			new MoveTask().execute(0);
			mIsMenuOpen = true;
		}
	}

	class MoveTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			int width = params[0];
			if (width < 0) {
				for (int i = 0; i > -mMenuWidth - 6;) {
					mMenuParams.leftMargin = i;
					i = i - 5;
					publishProgress(null);
					SystemClock.sleep(1);
				}
			} else {
				for (int i = -mMenuWidth; i < 6;) {
					mMenuParams.leftMargin = i;
					i = i + 5;
					publishProgress(null);
					SystemClock.sleep(1);

				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			mMenu.setLayoutParams(mMenuParams);
		}
	}
}
