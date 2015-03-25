package com.example.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/** utils for screen display */
public class DisplayUtils {

	private static float mCachedDensity = -1f;

	public static int getPixel(Context context, float dp) {
		float density = getDensity(context);
		return (int) (dp * density);
	}

	public static float getDensity(Context context) {
		if (mCachedDensity > 0)
			return mCachedDensity;
		DisplayMetrics metrics = getMetric(context);
		if (metrics != null)
			mCachedDensity = metrics.density;
		return mCachedDensity < 0 ? 1.5f : mCachedDensity;
	}

	public static int getScreenHeight(Context context) {
		DisplayMetrics metrics = getMetric(context);
		if (metrics != null)
			return metrics.heightPixels;
		else
			return 801;
	}

	public static int getScreenWidth(Context context) {
		DisplayMetrics metrics = getMetric(context);
		if (metrics != null)
			return metrics.widthPixels;
		else
			return 481;
	}

	public static int getScreenResolution(Context context) {
		DisplayMetrics metrics = getMetric(context);
		if (metrics != null)
			return metrics.widthPixels * metrics.heightPixels;
		else
			return 480 * 800;
	}

	public static DisplayMetrics getMetric(Context context) {
		try {
			DisplayMetrics metrics = new DisplayMetrics();
			WindowManager winMgr = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			winMgr.getDefaultDisplay().getMetrics(metrics);
			return metrics;
		} catch (Exception e) {
		}
		return null;
	}
}
