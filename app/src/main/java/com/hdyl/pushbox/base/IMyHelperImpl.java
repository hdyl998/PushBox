package com.hdyl.pushbox.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

public class IMyHelperImpl implements IMyHelper{

	public Activity mContext;
	public IMyHelperImpl(Activity mContext){
		this.mContext=mContext;
	}
	
	public void startNewActivity(Class<? extends BaseFragment> clazz, String... strings) {
		startNewActivityForRestlt(clazz, -1, strings);
	}

	public void startNewActivityForRestlt(Class<? extends BaseFragment> clazz, int requestCode, String... strings) {
		Intent intent = new Intent(mContext, FragmentHolderActivity.class);
		intent.putExtra(Consts.ID_MAIN, clazz);
		if (strings != null && strings.length > 0) {
			for (int i = 0; i < strings.length; i++) {
				intent.putExtra("" + (i + 1), strings[i]);
			}
		}
		mContext.startActivityForResult(intent, requestCode);
	}

	public void startActivity(Class<? extends Activity> clazz) {
		startActivityForRestlt(clazz, -1);
	}

	public void startActivityForRestlt(Class<? extends Activity> clazz, int requestCode) {
		mContext.startActivityForResult(new Intent(mContext, clazz), requestCode);
	}

	ProgressDialog dialog;

	public void showLoadingDialog() {
		showLoadingDialog("加载中...");
	}

	public void showLoadingDialog(String message) {
		if (dialog == null) {
			dialog = new ProgressDialog(mContext);
			dialog.setCanceledOnTouchOutside(false);
		}
		dialog.setMessage(message);
		dialog.show();
	}

	public void hideLoadingDialog() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

}
