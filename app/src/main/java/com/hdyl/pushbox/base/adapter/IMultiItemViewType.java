package com.hdyl.pushbox.base.adapter;

public interface IMultiItemViewType<T> {
	  int getViewTypeCount();
	  int getItemViewType(int position, T t);
	  int getLayoutId(int viewType);
}
