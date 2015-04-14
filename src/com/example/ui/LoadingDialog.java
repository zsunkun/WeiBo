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
import android.widget.TextView;

public class LoadingDialog {

	public static Dialog createLoadingDialog(Context context) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_loading, null);// �õ�����view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// ���ز���
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// ��ʾ����
		// ���ض���
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.anim_rotate);
		// ʹ��ImageView��ʾ����
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// �����Զ�����ʽdialog

		loadingDialog.setCancelable(false);// �������á����ؼ���ȡ��
		loadingDialog.setContentView(layout);// ���ò���
		return loadingDialog;
	}
}
