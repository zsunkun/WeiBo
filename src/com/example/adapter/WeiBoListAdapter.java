package com.example.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.ui.CommentDialog;
import com.example.ui.GridViewInList;
import com.example.utils.AsyncImageLoader;
import com.example.utils.AsyncImageLoader.ImageCallback;
import com.example.utils.Tools;
import com.example.weibo.R;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI.COMMENTS_TYPE;
import com.weibo.sdk.android.net.RequestListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class WeiBoListAdapter extends MyAdapter {

	private ViewHolder mViewHolder;
	private String mImageSmall = "thumbnail";
	private String mImageMiddle = "bmiddle";
	private String mImageLarge = "large";

	public WeiBoListAdapter(Activity context, JSONArray jsonArray,
			StatusesAPI statuses) {
		super(context, jsonArray, statuses);
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
			mViewHolder.my_grid_view = (GridViewInList) convertView
					.findViewById(R.id.gridView_self);
			mViewHolder.repost_grid_view = (GridViewInList) convertView
					.findViewById(R.id.gridView_repost);
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
			if (itemJson == null) {
				return null;
			}
			mViewHolder.tv_time.setText(Tools.formatDate(itemJson
					.getString("created_at")));
			mViewHolder.tv_name.setText(new JSONObject(itemJson
					.getString("user")).getString("name"));
			mViewHolder.tv_text.setText(itemJson.getString("text"));
			mViewHolder.tv_repost.setText(String.valueOf(itemJson
					.getInt("reposts_count")));
			mViewHolder.tv_comment.setText(String.valueOf(itemJson
					.getInt("comments_count")));

			mViewHolder.my_grid_view
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							if (!mLoadingDialog.isShowing())
								mLoadingDialog.show();
							ImageOnClick(itemJson, position);
						}
					});

			mViewHolder.repost_grid_view
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							try {
								ImageOnClick(itemJson
										.getJSONObject("retweeted_status"),
										position);
							} catch (JSONException e) {
							}
							if (!mLoadingDialog.isShowing())
								mLoadingDialog.show();
						}
					});

			// 转发微博
			mViewHolder.tv_repost.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					repostWeibo(position);
					if (!mLoadingDialog.isShowing())
						mLoadingDialog.show();
				}
			});
			// 评论微博
			mViewHolder.tv_comment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 得到要转发的微博ID
					try {
						long weiBoID = itemJson.getLong("mid");
						new CommentDialog(mContext, weiBoID).show();
					} catch (JSONException e) {
					}
				}
			});

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

						@Override
						public void imageLoadDone(Bitmap bitmap) {
						}
					});

			if (head_image != null) {
				mViewHolder.image_head.setImageBitmap(head_image);
			}

			// 微博原文
			if (itemJson.has("retweeted_status")) {
				/* holder.tv_retweeted_status_texts */
				mViewHolder.tv_retweeted_status_texts.setText(itemJson
						.getJSONObject("retweeted_status")
						.getJSONObject("user").getString("name")
						+ ":"
						+ itemJson.getJSONObject("retweeted_status").getString(
								"text"));
				if (itemJson.getJSONObject("retweeted_status").has("pic_urls")) {
					getImage(itemJson.getJSONObject("retweeted_status"),
							mViewHolder.repost_grid_view, position);
				}
				LinearLayout layout = (LinearLayout) convertView
						.findViewById(R.id.weibo_item_ll_retweeted_status);
				layout.setVisibility(View.VISIBLE);
			} else {
				// holder.tv_retweeted_status_texts.setVisibility(View.GONE);
				LinearLayout layout = (LinearLayout) convertView
						.findViewById(R.id.weibo_item_ll_retweeted_status);
				layout.setVisibility(View.GONE);
			}

			// 微博图片
			if (itemJson.has("pic_urls")) {
				getImage(itemJson, mViewHolder.my_grid_view, position);
			} else {
				mViewHolder.my_grid_view.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			Log.i("Exception", "Try Exception:" + e.getMessage());
		}
		return convertView;
	}

	private void getImage(JSONObject itemJson, GridViewInList gridView,
			int position) throws JSONException {
		final ArrayList<HashMap<String, Object>> gridViewData = new ArrayList<HashMap<String, Object>>();
		final SimpleAdapter gridViewAdapter = new SimpleAdapter(mContext,
				gridViewData, // 数据源
				R.layout.layout_image_item, // xml实现
				new String[] { "image" }, // 对应map的Key
				new int[] { R.id.weibo_item_textImage }); // 对应R的Id
		gridViewAdapter.setViewBinder(new ViewBinder() {

			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				// 判断是否为我们要处理的对象
				if (view instanceof ImageView && data instanceof Bitmap) {
					ImageView iv = (ImageView) view;
					iv.setImageBitmap((Bitmap) data);
					return true;
				} else
					return false;
			}

		});
		gridView.setVisibility(View.VISIBLE);
		gridView.setAdapter(gridViewAdapter);
		JSONArray picIds = itemJson.getJSONArray("pic_urls");
		for (int i = 0; i < picIds.length(); i++) {
			String url = picIds.optJSONObject(i).getString("thumbnail_pic");
			url = url.replaceAll("\\\\", "");
			if (itemJson.has("bmiddle_pic")) {
				url = url.replace(mImageSmall, mImageMiddle);
			}
			final Bitmap image_text = AsyncImageLoader.loadBitmap(1, url, null,
					position, new ImageCallback() {
						@Override
						public void imageSet(Bitmap drawable, ImageView iv) {
						}

						@Override
						public void imageLoadDone(Bitmap bitmap) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("image", bitmap);
							gridViewData.add(map);
							gridViewAdapter.notifyDataSetChanged();
						}
					});

			if (image_text != null) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("image", image_text);
				gridViewData.add(map);
				gridViewAdapter.notifyDataSetChanged();
			}
		}
	}

	private void ImageOnClick(JSONObject itemJson, int position) {
		if (itemJson.has("original_pic")) {
			try {
				JSONArray array = itemJson.getJSONArray("pic_urls");
				String iv_original_pic_url = array.optJSONObject(position)
						.getString("thumbnail_pic");
				iv_original_pic_url = iv_original_pic_url
						.replaceAll("\\\\", "");
				iv_original_pic_url = iv_original_pic_url.replace(mImageSmall,
						mImageLarge);
				mViewHolder.image_original_pic.setTag(iv_original_pic_url);
				Bitmap xxxx = AsyncImageLoader.loadBitmap(2,
						iv_original_pic_url, mViewHolder.image_original_pic,
						position, new ImageCallback() {
							@Override
							public void imageSet(Bitmap bitmap, ImageView iv) {
								mViewHolder.image_original_pic
										.setImageBitmap(bitmap);
								mLoadingDialog.dismiss();
								if (bitmap != null) {
									mOriginalPicDialog.show();
									mOriginalPicDialog
											.setContentView(mOriginalPicView);
								}
							}

							@Override
							public void imageLoadDone(Bitmap bitmap) {
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

	private void repostWeibo(int position) {
		try {
			JSONObject repostWeibo = (JSONObject) mJsonArray.get(position);
			// 得到要转发的微博ID
			long repost_id = repostWeibo.getLong("mid");
			String text = repostWeibo.getString("text");
			mStatuses.repost(repost_id, null, COMMENTS_TYPE.NONE,
					new RequestListener() {

						@Override
						public void onIOException(IOException arg0) {
							mLoadingDialog.dismiss();
							Looper.prepare();
							Toast.makeText(mContext, "转发失败~",
									Toast.LENGTH_SHORT).show();
							Looper.loop();
						}

						@Override
						public void onError(WeiboException arg0) {
							mLoadingDialog.dismiss();
							Looper.prepare();
							Toast.makeText(mContext, "转发失败~",
									Toast.LENGTH_SHORT).show();
							Looper.loop();
						}

						@Override
						public void onComplete(String arg0) {
							mLoadingDialog.dismiss();
							Looper.prepare();
							Toast.makeText(mContext, "转发成功~",
									Toast.LENGTH_SHORT).show();
							Looper.loop();
						}
					});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static class ViewHolder {
		public ImageView image_head, image_original_pic;
		public TextView tv_name, tv_text, tv_time;
		public TextView tv_repost, tv_comment;
		public TextView tv_retweeted_status_texts;
		public GridViewInList my_grid_view, repost_grid_view;

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

			if (viewHolder.image_original_pic != null) {
				viewHolder.image_original_pic.setImageBitmap(null);
			}

		}
	}
}
