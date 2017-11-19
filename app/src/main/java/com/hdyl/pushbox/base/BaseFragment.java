package com.hdyl.pushbox.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment implements OnClickListener, Consts {

	protected BaseActivity mContext;

	private View viewRoot;

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (setViewId() == 0) {
			return viewRoot = setView(container.getContext());
		}
		return viewRoot = inflater.inflate(setViewId(), container, false);
	}

	@Override
	public final void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = (BaseActivity) getActivity();
		findView();
		initData();
		initListener();
	}

	@SuppressWarnings("unchecked")
	protected <T extends View> T findViewById(int id) {
		return (T) viewRoot.findViewById(id);
	}

	protected abstract void initData();

	// 全部调用activity的方法
	public void startNewActivity(Class<? extends BaseFragment> clazz) {
		mContext.startNewActivity(clazz);
	}

	public void startNewActivityForRestlt(Class<? extends BaseFragment> clazz, int requestCode) {
		mContext.startNewActivityForRestlt(clazz, requestCode);
	}

	// public String download(String urlStr) {
	// return mContext.download(urlStr);
	// }

	protected void findView() {

	}

	public int[] setClickId() {
		return null;
	}

	private void initListener() {
		int ids[] = setClickId();
		if (ids != null) {
			for (int i : ids) {
				findViewById(i).setOnClickListener(this);
			}
		}
	}

	public void onClick(View view) {
	}

	protected int setViewId() {
		return 0;
	}

	protected View setView(Context mContext) {
		return viewRoot;
	}

	public void onActivityResult(int requestCode, Intent data) {

	}

	public void startActivityForResult(Intent intent, int requestCode) {
		mContext.startActivityForResult(intent, requestCode);
	}

}
