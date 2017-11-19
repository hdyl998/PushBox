package com.hdyl.pushbox.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;

public class DialogCreator {

	public static void create(Context mContext, String title, String message, String positiveString, String negativeString, final OnClickListener onClickListener) {

		AlertDialog.Builder bulder = new AlertDialog.Builder(mContext);
		if (!TextUtils.isEmpty(title)) {
			bulder.setTitle(title);
		}
		bulder.setIcon(android.R.drawable.ic_dialog_info);
		bulder.setMessage(message);

		if (!TextUtils.isEmpty(positiveString)) {
			bulder.setPositiveButton(positiveString, new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if (onClickListener != null) {
						onClickListener.onClick(arg0, 0);
					}
				}
			});
		}
		if (!TextUtils.isEmpty(negativeString)) {
			bulder.setNegativeButton(negativeString, new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if (onClickListener != null)
						onClickListener.onClick(arg0, 1);
				}
			});
		}
		bulder.show();
	}

	public static void create(Context mContext, String message, String positiveString, String negativeString, final OnClickListener onClickListener) {
		create(mContext, null, message, positiveString, negativeString, onClickListener);
	}

	public static void create(Context mContext, String message) {
		create(mContext, "提示", message);
	}

	public static void create(Context mContext, String title, String message) {
		new AlertDialog.Builder(mContext).setIcon(android.R.drawable.ic_dialog_info).setTitle(title).setMessage(message).setNegativeButton("确定", null).show();
	}

}
