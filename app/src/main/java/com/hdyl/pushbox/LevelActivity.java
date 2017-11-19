package com.hdyl.pushbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.hdyl.pushbox.base.BaseActivity;
import com.hdyl.pushbox.base.ConstData;
import com.hdyl.pushbox.base.DialogCreator;
import com.hdyl.pushbox.soko.SokoCollectionListActivity;
import com.hdyl.pushbox.tools.LevelInfo;
import com.hdyl.pushbox.view.MyPagerAdapter;
import com.hdyl.pushbox.view.PagerSlidingTabStrip;

public class LevelActivity extends BaseActivity implements OnClickListener {

	// GridView listView[] = new GridView[2];
	LevelAdapter levelAdapter[] = new LevelAdapter[2];
	public static boolean isChange = false;

	int index = 0;
	View view;
	TextView tvFinishTextView;

	@Override
	protected void onResume() {
		if (isChange) {
			levelAdapter[index].notifyDataSetChanged();
			isChange = false;
		}
		super.onResume();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.tv_other_level:// 其它关卡
			startActivity(new Intent(mContext, SokoCollectionListActivity.class));
			break;
		}
	}

	@Override
	protected void initData() {
		isChange = false;

		ViewPager viewPager = (ViewPager) findViewById(R.id.vp_team);
		List<View> list = new ArrayList<View>();

		for (int i = 0; i < 2; i++) {

			View view = View.inflate(this, R.layout.view_level, null);

			GridView listView = (GridView) view.findViewById(R.id.gv_select_level);
			list.add(view);
			listView.setAdapter(levelAdapter[i] = new LevelAdapter(this, i));
			listView.setOnItemClickListener(itemClickListener);
		}

		String titles[] = "1-60关,新 101-200关".split(",");
		viewPager.setAdapter(new MyPagerAdapter(list, Arrays.asList(titles)));
		PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.pagerSlidingTabStrip1);
		tabStrip.setViewPager(viewPager);

		view = findViewById(R.id.ll);

		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.tv_other_level).setOnClickListener(this);

		tabStrip.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				index = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			LevelInfo levelInfo = levelAdapter[index].getItem(arg2);
			if (levelInfo.canOpenOrPass()) {//
				Intent intent = new Intent(LevelActivity.this, MainActivity.class);
				intent.putExtra("level", levelInfo.level);
				// intent.putExtra("type", index);
				startActivity(intent);
			} else {
				String messageString = "第$关还没有解锁呢！别着急啊~~".replace("$", levelInfo.level + "");
				DialogCreator.create(mContext, messageString);
			}
		}
	};

	@Override
	protected int setViewId() {
		return R.layout.activity_level;
	}

	@Override
	protected String getPageName() {
		return "关卡浏览";
	}

}
