package com.hdyl.pushbox.tuijian;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdyl.pushbox.R;

public class AppAdapter extends BaseAdapter {

	Context context;
	int[] arrPoint = { R.drawable.point0, R.drawable.point1, R.drawable.point2, R.drawable.point3 };
	int[] arrNum = { R.drawable.num_0, R.drawable.num_1, R.drawable.num_2, R.drawable.num_3, R.drawable.num_4, R.drawable.num_5, R.drawable.num_6, R.drawable.num_7, R.drawable.num_8, R.drawable.num_9 };

	List<AppItem> list;

	public AppAdapter(Context context) {

		this.context = context;

		// UserInfo userInfo = MyApplication.getMyApplicationInstance()
		// .getCurUserInfo();
		// this.context = context;// 上下文
		// this.userLevel = userInfo.getLevel();// 当前关级别，表示可玩多少关
		// DBHelper dbHelper = new DBHelper(context);
		// list = dbHelper.getAllScoreOfUser(userInfo.getUid());// 得到一个用户所有关卡数据
		// this.totalCount = dbHelper.getLevelCount();// 关卡总数
		// // list.add(new ScoreInfo(1, 0));
	}

	public void setData(List<AppItem> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_app_adv, null);
			holder = new ViewHolder();
			holder.ivNum0 = (ImageView) convertView.findViewById(R.id.iv_item_num0);
			holder.ivNum1 = (ImageView) convertView.findViewById(R.id.iv_item_num1);
			holder.ivNum2 = (ImageView) convertView.findViewById(R.id.iv_item_num2);
			holder.number = convertView.findViewById(R.id.ll_number);

			holder.title = (TextView) convertView.findViewById(R.id.textViewTitle);

			holder.imageView = (ImageView) convertView.findViewById(R.id.imageView1);

			holder.tvIntroduce=(TextView) convertView.findViewById(R.id.TextView01);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

//		holder.ivPoint.setImageResource(arrPoint[arrPoint.length - 1]);
		holder.ivNum1.setVisibility(View.VISIBLE);
		int num = position + 1;//
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

		AppItem item=list.get(position);
		
		holder.title.setText(item.name);
		if(item.icon!=0){
			holder.imageView.setImageResource(item.icon);
		}
		else {
			holder.imageView.setImageBitmap(null);
		}
		holder.tvIntroduce.setText(item.introduce);
		return convertView;
	}

	class ViewHolder {
		ImageView ivNum0, ivNum1, ivNum2;// 点,数字1，数字2
		View number;
		View lvAll;// 全部

		TextView title,tvIntroduce;
		ImageView imageView;
	}

}
