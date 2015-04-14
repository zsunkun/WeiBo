package com.example.ui;

import com.example.weibo.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LoadingDialog extends Dialog {

	private ImageView mSpaceshipImage;
	private Animation mLoadingAnim;

	public LoadingDialog(Context context) {
		super(context, R.style.Mydialog);
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
		mSpaceshipImage = (ImageView) v.findViewById(R.id.img);
		mLoadingAnim = AnimationUtils
				.loadAnimation(context, R.anim.anim_rotate);
		setContentView(layout);// 设置布局
	}

	@Override
	public void show() {
		mSpaceshipImage.startAnimation(mLoadingAnim);
		super.show();
	}
}
