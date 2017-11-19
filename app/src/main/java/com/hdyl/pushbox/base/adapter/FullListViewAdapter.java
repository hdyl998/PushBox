package com.hdyl.pushbox.base.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;


/***
 * 用于向linearlayout里面动态添加布局
 * 
 * @author Administrator
 * 
 * @param <T>
 */
public abstract class FullListViewAdapter<T> {
	private int mItemLayoutId;// 看名字
	private List<T> mDatas;// 数据源

	private List<BaseViewHolder> mVHCahces;// 缓存ViewHolder,按照add的顺序缓存，
	private LayoutInflater mInflater;

	private LinearLayout mLayout;

	public FullListViewAdapter(Context mContext, List<T> mDatas, int mItemLayoutId) {
		this.mItemLayoutId = mItemLayoutId;
		this.mDatas = mDatas;
		this.mInflater = LayoutInflater.from(mContext);
		mVHCahces = new ArrayList<BaseViewHolder>();
	}

	public FullListViewAdapter(LinearLayout mLayout, List<T> mDatas, int mItemLayoutId) {
		this(mLayout.getContext(), mDatas, mItemLayoutId);
		bindLinearLayout(mLayout);
	}

	public int getItemLayoutId() {
		return mItemLayoutId;
	}

	public void setItemLayoutId(int mItemLayoutId) {
		this.mItemLayoutId = mItemLayoutId;
	}

	public List<T> getDatas() {
		return mDatas;
	}

	public void setDatas(List<T> mDatas) {
		this.mDatas = mDatas;
	}

	/***
	 * 绑定linearlayout
	 * 
	 * @param mLayout
	 *            线性布局
	 */
	public void bindLinearLayout(LinearLayout mLayout) {
		this.mLayout = mLayout;
		this.mLayout.setOrientation(LinearLayout.VERTICAL);
	}

	/***
	 * 刷新数据 方法名同一般适配器的方法一致方便调用
	 */
	public void notifyDataSetChanged() {
		if (null != mLayout) {
			if (null != mDatas && !mDatas.isEmpty()) {
				int childCount = mLayout.getChildCount();
				int dataSize = mDatas.size();
				// 数据源有数据
				if (dataSize > childCount) {// 数据源大于现有子View不清空

				} else if (dataSize < childCount) {// 数据源小于现有子View，删除后面多的
					mLayout.removeViews(dataSize, childCount - dataSize);
					// 删除View也清缓存
					while (mVHCahces.size() > dataSize) {
						mVHCahces.remove(mVHCahces.size() - 1);
					}
				}
				for (int i = 0; i < dataSize; i++) {
					BaseViewHolder holder;
					if (mVHCahces.size() - 1 >= i) {// 说明有缓存，不用inflate，否则inflate
						holder = mVHCahces.get(i);
					} else {
						holder = BaseViewHolder.get(mInflater, null, mLayout, getItemLayoutId());
						mVHCahces.add(holder);// inflate 出来后 add进来缓存
					}
					onBind(i, holder);
					// 如果View没有父控件 添加
					if (null == holder.getItemView().getParent()) {
						mLayout.addView(holder.getItemView(
								));
					}
				}
			} else {
				mLayout.removeAllViews();// 数据源没数据 清空视图
			}
		} else {
			mLayout.removeAllViews();// 适配器为空 清空视图
		}
	}

	/**
	 * 被FullListView调用
	 * 
	 * @param i
	 * @param holder
	 */
	public void onBind(int i, BaseViewHolder holder) {
		// 回调bind方法，多传一个data过去
		onBind(holder, mDatas.get(i), i);
	}

	/**
	 * 数据绑定方法
	 * 
	 * @param pos
	 *            位置
	 * @param t
	 *            数据
	 * @param holder
	 *            ItemView的ViewHolder
	 */
	public abstract void onBind(BaseViewHolder holder, T item, int position);

}
