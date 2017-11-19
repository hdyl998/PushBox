package com.hdyl.pushbox.tuijian;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.hdyl.pushbox.R;
import com.hdyl.pushbox.WebActivity;
import com.hdyl.pushbox.base.BaseActivity;

public class TuijianAcitivity extends BaseActivity implements OnClickListener {

	List<AppItem> listMatchItems = new ArrayList<AppItem>();

	private OnItemClickListener onItemLongClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (arg2 >= 0 && arg2 < listMatchItems.size()) {
				Intent intent = new Intent(TuijianAcitivity.this, WebActivity.class);
				String url = "http://openbox.mobilem.360.cn/qcms/view/t/detail?sid=";
				intent.putExtra("title", listMatchItems.get(arg2).name);
				intent.putExtra("url", url + listMatchItems.get(arg2).url);
				startActivity(intent);
			}
		}
	};

	@Override
	public void onClick(View arg0) {
		finish();
	}

	@Override
	protected void initData() {
		findViewById(R.id.back).setOnClickListener(this);

		ListView listView = (ListView) findViewById(R.id.listview);
		AppAdapter adapter = new AppAdapter(this);

		listView.setAdapter(adapter);

		TextView textView = new TextView(this);
		textView.setText("\n\n以上应用完全免费，请放心下载使用!\n\n寒冬已至 2016\n\n");
		textView.setTextColor(getResources().getColor(R.color.green));
		listView.addFooterView(textView);

		AppItem appItem = new AppItem();
		appItem.name = "经典扫雷";
		appItem.icon = R.drawable.app1;
		appItem.url = "3281879";
		listMatchItems.add(appItem);

		appItem = new AppItem();
		appItem.name = "连连看";
		appItem.icon = R.drawable.app2;
		appItem.url = "3203033";
		listMatchItems.add(appItem);

		appItem = new AppItem();
		appItem.name = "推箱子";
		appItem.icon = R.drawable.ic_launcher;
		appItem.url = "3387918";
		listMatchItems.add(appItem);

		appItem = new AppItem();
		appItem.name = "趣味连萌";
		appItem.icon = R.drawable.app6;
		appItem.url = "3502028";
		listMatchItems.add(appItem);

		appItem = new AppItem();
		appItem.name = "15拼图";
		appItem.icon = R.drawable.app4;
		appItem.url = "3118746";
		listMatchItems.add(appItem);

		appItem = new AppItem();
		appItem.name = "坚持一下";
		appItem.icon = R.drawable.app5;
		appItem.introduce = "实用工具";
		appItem.url = "3401593";
		listMatchItems.add(appItem);

		adapter.setData(listMatchItems);

		listView.setOnItemClickListener(onItemLongClickListener);
	}

	@Override
	protected int setViewId() {

		return R.layout.activity_tuijian;
	}

	@Override
	protected String getPageName() {
		// TODO Auto-generated method stub
		return null;
	}

}
