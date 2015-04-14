package com.example.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.ui.LoadingDialog;
import com.example.utils.AsyncImageLoader;
import com.example.utils.AsyncImageLoader.ImageCallback;
import com.example.utils.NetworkUtils;
import com.example.utils.Tools;
import com.example.weibo.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeiBoListAdapter extends BaseAdapter {

	private ViewHolder mViewHolder;
	private Context mContext;
	private JSONArray mJsonArray;
	private String textImage;
	private Dialog mOriginalPicDialog;
	private View mOriginalPicView;
	private Dialog mLoadingDialog;

	public WeiBoListAdapter(Context context, JSONArray jsonArray) {
		mContext = context;
		mJsonArray = jsonArray;
		mOriginalPicDialog = new AlertDialog.Builder(mContext).create();
		mOriginalPicView = LayoutInflater.from(mContext).inflate(
				R.layout.view_weibo_original_pic, null);
		mLoadingDialog = new LoadingDialog(mContext);
	}

	@Override
	public int getCount() {
		return mJsonArray.length();
	}

	@Override
	public JSONObject getItem(int position) {
		return (JSONObject) mJsonArray.opt(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void updateData(JSONArray jsonArray) {
		mJsonArray = jsonArray;
	}

	public void updateForMoreData(JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			mJsonArray.put(jsonArray.optJSONObject(i));
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		mViewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.weibo_list_item_content, null);

			mViewHolder = new ViewHolder();
			mViewHolder.image_head = (ImageView) convertView
					.findViewById(R.id.weibo_item_headimage);

			mViewHolder.tv_name = (TextView) convertView
					.findViewById(R.id.weibo_item_name);
			mViewHolder.tv_text = (TextView) convertView
					.findViewById(R.id.weibo_item_text);

			mViewHolder.image_textImage = (ImageView) convertView
					.findViewById(R.id.weibo_item_textImage);

			mViewHolder.tv_retweeted_status_texts = (TextView) convertView
					.findViewById(R.id.weibo_item_retweeted_status_texts);

			mViewHolder.tv_time = (TextView) convertView
					.findViewById(R.id.weibo_item_time);
			mViewHolder.tv_repost = (TextView) convertView
					.findViewById(R.id.weibo_item_repost);
			mViewHolder.tv_comment = (TextView) convertView
					.findViewById(R.id.weibo_item_comment);

			mViewHolder.image_original_pic = (ImageView) mOriginalPicView
					.findViewById(R.id.iv_original_pic);

			convertView.setTag(mViewHolder);

		} else {
			mViewHolder = (ViewHolder) convertView.getTag(); //
			ViewHolder.resetViewHolder(mViewHolder);
		}

		try {
			final JSONObject itemJson = getItem(position);
			mViewHolder.tv_time.setText(Tools.formatDate(itemJson
					.getString("created_at")));
			mViewHolder.tv_name.setText(new JSONObject(itemJson
					.getString("user")).getString("name"));
			mViewHolder.tv_text.setText(itemJson.getString("text"));
			mViewHolder.tv_repost.setText(String.valueOf(itemJson
					.getInt("reposts_count")));
			mViewHolder.tv_comment.setText(String.valueOf(itemJson
					.getInt("comments_count")));

			// 点击小图片显示原始大小图片
			mViewHolder.image_textImage
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							ImageOnClick(itemJson, position);
							if (!mLoadingDialog.isShowing())
								mLoadingDialog.show();
						}
					});

			// 微博原文
			if (itemJson.has("retweeted_status")) {
				/* holder.tv_retweeted_status_texts */
				mViewHolder.tv_retweeted_status_texts.setText(itemJson
						.getJSONObject("retweeted_status")
						.getJSONObject("user").getString("name")
						+ ":"
						+ itemJson.getJSONObject("retweeted_status").getString(
								"text"));
				LinearLayout layout = (LinearLayout) convertView
						.findViewById(R.id.weibo_item_ll_retweeted_status);
				layout.setVisibility(View.VISIBLE);
			} else {
				// holder.tv_retweeted_status_texts.setVisibility(View.GONE);
				LinearLayout layout = (LinearLayout) convertView
						.findViewById(R.id.weibo_item_ll_retweeted_status);
				layout.setVisibility(View.GONE);
			}

			// 头像图片
			String image_head_url = new JSONObject(itemJson.getString("user"))
					.getString("profile_image_url");
			mViewHolder.image_head.setTag(image_head_url);
			Bitmap head_image = AsyncImageLoader.loadBitmap(0, image_head_url,
					mViewHolder.image_head, position, new ImageCallback() {
						@Override
						public void imageSet(Bitmap bitmap, ImageView iv) {
							iv.setImageBitmap(bitmap);
						}
					});

			if (head_image != null) {
				mViewHolder.image_head.setImageBitmap(head_image);
			}

			// 内容中图片 如果是wifi用中等缩略图，如果是gprs用小缩略图
			if (NetworkUtils.getNetworkState(mContext) == NetworkUtils.WIFI) {
				textImage = "bmiddle_pic";
			} else if (NetworkUtils.getNetworkState(mContext) == NetworkUtils.MOBILE) {
				textImage = "thumbnail_pic";
			}

			if (itemJson.has(textImage)) {// thumbnail_pic
				mViewHolder.image_textImage.setVisibility(View.VISIBLE);
				String image_textImage_url = itemJson.getString(textImage);
				mViewHolder.image_textImage.setTag(image_textImage_url);
				Bitmap image_text = AsyncImageLoader.loadBitmap(1,
						(itemJson.getString(textImage)),
						mViewHolder.image_textImage, position,
						new ImageCallback() {
							@Override
							public void imageSet(Bitmap drawable, ImageView iv) {
								iv.setImageBitmap(drawable);
							}
						});

				if (image_text != null) {
					mViewHolder.image_textImage.setImageBitmap(image_text);
					mViewHolder.image_textImage.setVisibility(View.VISIBLE);
				}
			} else {
				mViewHolder.image_textImage.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			Log.i("Exception", "Try Exception:" + e.getMessage());
		}
		return convertView;
	}

	private void ImageOnClick(JSONObject itemJson, int position) {

		if (itemJson.has("original_pic")) {
			try {
				String iv_original_pic_url = itemJson.getString("original_pic");
				mViewHolder.image_original_pic.setTag(iv_original_pic_url);
				Bitmap xxxx = AsyncImageLoader.loadBitmap(2,
						(itemJson.getString("original_pic")),
						mViewHolder.image_original_pic, position,
						new ImageCallback() {
							@Override
							public void imageSet(Bitmap bitmap, ImageView iv) {
								mViewHolder.image_original_pic
										.setImageBitmap(bitmap);
								mLoadingDialog.dismiss();
								mOriginalPicDialog.show();
								mOriginalPicDialog
										.setContentView(mOriginalPicView);
							}
						});
				if (xxxx != null) {
					mViewHolder.image_original_pic.setImageBitmap(xxxx);
					mLoadingDialog.dismiss();
					mOriginalPicDialog.show();
					mOriginalPicDialog.setContentView(mOriginalPicView);
				}

			} catch (JSONException e) {
				// block
				e.printStackTrace();
				Toast.makeText(mContext, "未获取到原始图片，请稍后再试", Toast.LENGTH_LONG)
						.show();
			}

		} else {
			mViewHolder.image_original_pic.setImageBitmap(null);
			Toast.makeText(mContext, "没有大图", Toast.LENGTH_LONG).show();
		}

	}

	static class ViewHolder {
		public ImageView image_head, image_textImage, image_original_pic;
		public TextView tv_name, tv_text, tv_time;
		public TextView tv_repost, tv_comment;
		public TextView tv_retweeted_status_texts;

		public static void resetViewHolder(ViewHolder viewHolder) {
			viewHolder.tv_name.setText(null);
			viewHolder.tv_text.setText(null);
			viewHolder.tv_time.setText(null);
			viewHolder.tv_repost.setText(null);
			viewHolder.tv_comment.setText(null);
			if (viewHolder.tv_retweeted_status_texts != null) {
				viewHolder.tv_retweeted_status_texts.setText(null);
			}

			viewHolder.image_head.setImageBitmap(null);
			if (viewHolder.image_textImage != null) {
				viewHolder.image_textImage.setImageBitmap(null);
			}

			if (viewHolder.image_original_pic != null) {
				viewHolder.image_original_pic.setImageBitmap(null);
			}

		}
	}
}
