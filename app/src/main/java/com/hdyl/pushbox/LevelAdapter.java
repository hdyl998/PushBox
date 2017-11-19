package com.hdyl.pushbox;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hdyl.pushbox.base.ConstData;
import com.hdyl.pushbox.tools.DatabaseHelper;
import com.hdyl.pushbox.tools.LevelInfo;

public class LevelAdapter extends BaseAdapter {

	Context context;
	int[] arrPoint = { R.drawable.point0, R.drawable.point1, R.drawable.point2, R.drawable.point3 };
	int[] arrNum = { R.drawable.num_0, R.drawable.num_1, R.drawable.num_2, R.drawable.num_3, R.drawable.num_4, R.drawable.num_5, R.drawable.num_6, R.drawable.num_7, R.drawable.num_8, R.drawable.num_9 };

	List<LevelInfo> levelInfos;

	int indexType = 0;

	public LevelAdapter(Context context, int type) {
		this.indexType = type;
		this.context = context;
		levelInfos = new DatabaseHelper(context).selectAllInfos(type);
		calcMaxUnCompletedCount();
		// currentCount = ConstData.getCurrentLevel(context);
		// UserInfo userInfo = MyApplication.getMyApplicationInstance()
		// .getCurUserInfo();
		// this.context = context;// 上下文
		// this.userLevel = userInfo.getLevel();// 当前关级别，表示可玩多少关
		// DBHelper dbHelper = new DBHelper(context);
		// list = dbHelper.getAllScoreOfUser(userInfo.getUid());// 得到一个用户所有关卡数据
		// this.totalCount = dbHelper.getLevelCount();// 关卡总数
		// // list.add(new ScoreInfo(1, 0));
	}

	@Override
	public void notifyDataSetChanged() {
		levelInfos = new DatabaseHelper(context).selectAllInfos(indexType);
		calcMaxUnCompletedCount();
		super.notifyDataSetChanged();
	}

	public void calcMaxUnCompletedCount() {
		boolean isFirst = false;
		int count = 0;
		int maxLeveL = 0;

		// 倒序查看
		for (int i = levelInfos.size() - 1; i >= 0; i--) {
			LevelInfo info = levelInfos.get(i);
			if (isFirst == false) {
				if (info.isPass == true) {
					maxLeveL = info.level;
					isFirst = true;
				}
			} else {
				if (info.isPass == false) {
					info.canOpen = true;
					count++;
				}
			}
		}

		if (indexType == 1) {
			int add = ConstData.MAX_NOT_PLAY_STAGE - count;
//			Log.e("lgdxadd", (add) + "");
			maxLeveL -= 100;
			if (maxLeveL < 0) {
				maxLeveL = 0;
			}
			for (int i = 0; i < add; i++) {
//				Log.e("lgdx", (i + maxLeveL) + "");
				if (i + maxLeveL < levelInfos.size() && i + maxLeveL >= 0)
					levelInfos.get(i + maxLeveL).canOpen = true;
			}
		} else {
			int add = ConstData.MAX_NOT_PLAY_STAGE - count;
			for (int i = 0; i < add; i++) {
				if (i + maxLeveL < levelInfos.size() && i + maxLeveL >= 0)
					levelInfos.get(i + maxLeveL).canOpen = true;
			}
		}

	}

	@Override
	public int getCount() {
		int count = levelInfos == null ? 0 : levelInfos.size();
//		Log.e("aa", count + "");
		return count;
	}

	@Override
	public LevelInfo getItem(int arg0) {
		return levelInfos.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_stage, null);
			holder = new ViewHolder();
			holder.ivPoint = (ImageView) convertView.findViewById(R.id.iv_item_point);
			holder.ivNum0 = (ImageView) convertView.findViewById(R.id.iv_item_num0);
			holder.ivNum1 = (ImageView) convertView.findViewById(R.id.iv_item_num1);
			holder.ivNum2 = (ImageView) convertView.findViewById(R.id.iv_item_num2);
			holder.number = convertView.findViewById(R.id.ll_number);
			holder.lvAll = convertView.findViewById(R.id.ll_item_all);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		LevelInfo levelInfo = levelInfos.get(position);

		if (levelInfo.isPass || levelInfo.canOpen) {// 解锁标志
			holder.number.setVisibility(View.VISIBLE);
			holder.ivPoint.setVisibility(View.VISIBLE);
			holder.lvAll.setBackgroundResource(R.drawable.pass_bg);

			holder.ivNum1.setVisibility(View.VISIBLE);

			int num = levelInfo.level;//
			int bai = (num / 100) % 10;
			int shi = (num / 10) % 10;
			if (bai == 0) {
				holder.ivNum0.setVisibility(View.GONE);
				if (shi == 0) {
					holder.ivNum1.setVisibility(View.GONE);
				}
			} else {
				holder.ivNum0.setVisibility(View.VISIBLE);
				holder.ivNum0.setImageResource(arrNum[bai]);
			}
			holder.ivNum1.setImageResource(arrNum[shi]);
			holder.ivNum2.setImageResource(arrNum[num % 10]);
			if (levelInfo.isPass) {
				holder.ivPoint.setImageResource(arrPoint[3]);// 设置得分
			} else {
				holder.ivPoint.setImageResource(arrPoint[0]);// 设置得分
			}

		} else {// 锁标志
			holder.number.setVisibility(View.GONE);
			holder.ivPoint.setVisibility(View.GONE);
			holder.lvAll.setBackgroundResource(R.drawable.lock);
		}

		return convertView;
	}

	class ViewHolder {
		ImageView ivPoint, ivNum0, ivNum1, ivNum2;// 点,数字1，数字2
		View number;
		View lvAll;// 全部
	}

}
