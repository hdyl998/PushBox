package com.hdyl.pushbox.base;

import android.app.Activity;

public interface IMyHelper {
	public void startNewActivity(Class<? extends BaseFragment> clazz, String... strings);

	public void startNewActivityForRestlt(Class<? extends BaseFragment> clazz, int requestCode, String... strings);

	public void startActivity(Class<? extends Activity> clazz);
	public void startActivityForRestlt(Class<? extends Activity> clazz, int requestCode);
	public void showLoadingDialog();

	public void showLoadingDialog(String message);

	public void hideLoadingDialog();

}
