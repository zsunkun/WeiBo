package com.example.ui;

import com.example.utils.DisplayUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ScaleImage extends ImageView {

	private Context mContext;

	public ScaleImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		float widthSize = getMeasuredWidth();
		float heightSize = getMeasuredHeight();
		if (widthSize == 0 || heightSize == 0) {
			setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
					MeasureSpec.getSize(heightMeasureSpec));
			return;
		}
		float screenWidth = DisplayUtils.getScreenWidth(mContext);
		int width = (int) screenWidth;
		int height = (int) ((heightSize / widthSize) * width);
		setMeasuredDimension(width, height);
	}
}
