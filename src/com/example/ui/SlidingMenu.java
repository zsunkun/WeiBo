package com.example.ui;

import com.example.utils.DisplayUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SlidingMenu extends HorizontalScrollView {

	private int mScreenWidth;
	private int mMenuRightPadding = 50;
	private int mMenuWidth;
	private int mHalfMenuWidth;
	private boolean mIsMenuOpen;

	private boolean mFirst = true;

	public SlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScreenWidth = DisplayUtils.getScreenWidth(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mFirst) {
			LinearLayout wrapper = (LinearLayout) getChildAt(0);
			ViewGroup menu = (ViewGroup) wrapper.getChildAt(0);
			ViewGroup content = (ViewGroup) wrapper.getChildAt(1);
			// dp to px
			mMenuRightPadding = DisplayUtils.getPixel(getContext(),
					mMenuRightPadding);
			mMenuWidth = mScreenWidth - mMenuRightPadding;
			mHalfMenuWidth = mMenuWidth / 2;
			menu.getLayoutParams().width = mMenuWidth;
			content.getLayoutParams().width = mScreenWidth;

		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			// 将菜单隐藏
			this.scrollTo(mMenuWidth, 0);
			mFirst = false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		// Up时，进行判断，如果显示区域大于菜单宽度一半则完全显示，否则隐藏
		case MotionEvent.ACTION_UP:
			int scrollX = getScrollX();
			if (scrollX > mHalfMenuWidth) {
				this.smoothScrollTo(mMenuWidth, 0);
				mIsMenuOpen = false;
			} else {
				this.smoothScrollTo(0, 0);
				mIsMenuOpen = true;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	public void toggleMenu() {
		if (mIsMenuOpen) {
			this.smoothScrollTo(mMenuWidth, 0);
			mIsMenuOpen = false;
		} else {
			this.smoothScrollTo(0, 0);
			mIsMenuOpen = true;
		}

	}
}
