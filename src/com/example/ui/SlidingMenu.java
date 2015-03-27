package com.example.ui;

import com.example.utils.DisplayUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class SlidingMenu extends FrameLayout {

	private int mScreenWidth;
	private int mMenuRightPadding = 50;
	private int mSafeWidth = 10;
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
			// ½«²Ëµ¥Òþ²Ø
			// this.scrollTo(mMenuWidth, 0);
			mMenuParams.leftMargin = -mMenuWidth;
			mIsMenuOpen = false;
			mFirst = false;
		}
	}

	// @Override
	// public boolean onTouchEvent(MotionEvent ev) {
	// int action = ev.getAction();
	// switch (action) {
	// case MotionEvent.ACTION_UP:
	// int scrollX = getScrollX();
	// if (scrollX > mHalfMenuWidth) {
	// this.smoothScrollTo(mMenuWidth, 0);
	// mIsMenuOpen = false;
	// } else {
	// this.smoothScrollTo(0, 0);
	// mIsMenuOpen = true;
	// }
	// return true;
	// case MotionEvent.ACTION_MOVE:
	// endX = ev.getX();
	// if (Math.abs(endX - startX) > mSafeWidth) {
	// //TODO
	// }
	// break;
	// case MotionEvent.ACTION_DOWN:
	// startX = ev.getX();
	// break;
	// }
	// return super.onTouchEvent(ev);
	// }

	public void toggleMenu() {
		if (mIsMenuOpen) {
			// this.scrollTo(mMenuWidth, 0);
			new MoveTask().execute(-mMenuWidth);
			mIsMenuOpen = false;
		} else {
			// this.scrollTo(0, 0);
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
