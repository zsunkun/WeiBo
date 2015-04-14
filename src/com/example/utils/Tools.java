package com.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Tools {

	public static String formatDate(String date_str) {
		Date date = new Date(date_str);
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
		return formatter.format(date);
	}

	/**
	 * ͨ��url ��ö�Ӧ��Drawable��Դ
	 * 
	 * @param flag
	 *            �Ƿ�ѹ�����
	 * @param url
	 *            ͼƬ��ַ
	 * @return
	 */
	public static Bitmap getBitmapFromUrl(int flag, String url) {
		try {
			URLConnection urlc = new URL(url).openConnection();
			URLConnection urlc2 = new URL(url).openConnection();
			if (flag == 0) {
				return Tools.decodeSampledBitmapFromStream(
						urlc.getInputStream(), urlc2.getInputStream(), 50, 50);
			} else if (flag == 1) {
				return Tools
						.decodeSampledBitmapFromStream(urlc.getInputStream(),
								urlc2.getInputStream(), 150, 150);

			} else if (flag == 2) {
				return Tools
						.decodeSampledBitmapFromStream(urlc.getInputStream(),
								urlc2.getInputStream(), 500, 500);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap decodeSampledBitmapFromStream(InputStream is,
			InputStream is2, int reqWidth, int reqHeight) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(is2, null, options);

	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio <= widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
}
