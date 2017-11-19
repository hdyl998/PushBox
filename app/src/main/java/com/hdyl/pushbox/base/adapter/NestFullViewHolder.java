package com.hdyl.pushbox.base.adapter;
//package com.example.superadatertest.Adapter.viewGroup;
//
//import android.content.Context;
//import android.util.SparseArray;
//import android.view.View;
//import android.widget.TextView;
//
//public class NestFullViewHolder {
//	private SparseArray<View> mViews;
//	private View mConvertView;
//	private Context mContext;
//
//	public NestFullViewHolder(Context context, View view) {
//		mContext = context;
//		this.mViews = new SparseArray<View>();
//		mConvertView = view;
//	}
//
//	/**
//	 * 通过viewId获取控件
//	 * 
//	 * @param viewId
//	 * @return
//	 */
//	public <T extends View> T getView(int viewId) {
//		View view = mViews.get(viewId);
//		if (view == null) {
//			view = mConvertView.findViewById(viewId);
//			mViews.put(viewId, view);
//		}
//		return (T) view;
//	}
//
//	public View getConvertView() {
//		return mConvertView;
//	}
//
//	public NestFullViewHolder setSelected(int viewId, boolean flag) {
//		View v = getView(viewId);
//		v.setSelected(flag);
//		return this;
//	}
//
//	/**
//	 * 设置TextView的值
//	 * 
//	 * @param viewId
//	 * @param text
//	 * @return
//	 */
//	public NestFullViewHolder setText(int viewId, String text) {
//		TextView tv = getView(viewId);
//		tv.setText(text);
//		return this;
//	}
//}
