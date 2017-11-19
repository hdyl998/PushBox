package com.hdyl.pushbox.view;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;


public class MyPagerAdapter extends PagerAdapter { // PagerView的适配器
	List<View> viewList;

	List<String> listTitles;

	public MyPagerAdapter(List<View> viewList) {
		this.viewList = viewList;
	}

	public MyPagerAdapter(List<View> viewList, List<String> listTitles) {
		this(viewList);
		this.listTitles = listTitles;
	}

	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;// 官方提示这样写
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(viewList.get(position));// 删除页卡
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(viewList.get(position));// 添加页卡
		return viewList.get(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		if (listTitles != null)
			return listTitles.get(position);
		return "";
	}
}
