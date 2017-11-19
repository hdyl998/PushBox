package com.hdyl.pushbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.content.Intent;
import android.content.res.AssetManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hdyl.pushbox.base.BaseActivity;
import com.hdyl.pushbox.base.ConstData;
import com.hdyl.pushbox.db.DBHelper;
import com.hdyl.pushbox.db.User;
import com.hdyl.pushbox.setting.SettingActivity;
import com.hdyl.pushbox.soko.SokoCollectionListActivity;
import com.hdyl.pushbox.tools.DatabaseHelper;
import com.hdyl.pushbox.tools.LevelInfo;
import com.hdyl.pushbox.tools.MySharepreferences;
import com.hdyl.pushbox.tools.ShareCacheUtil;
import com.hdyl.pushbox.tools.Tools;
import com.hdyl.pushbox.tuijian.TuijianAcitivity;

public class StartActivity extends BaseActivity implements OnClickListener {

	ImageView imageView;
	boolean isSwitchOn;

	TextView tvVersion;

	private void setUI() {
		if (isSwitchOn) {
			imageView.setImageResource(R.drawable.m1);
		} else {
			imageView.setImageResource(R.drawable.m5);
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_music:
			isSwitchOn = !isSwitchOn;

			MySharepreferences.putBoolean(this, "aa", "music", isSwitchOn);

			setUI();

			Intent intent = new Intent(this, SoundService.class);
			intent.putExtra("playing", isSwitchOn);
			startService(intent);

			break;
		case R.id.iv_set:
			startActivity(new Intent(this, TuijianAcitivity.class));
			break;
		case R.id.about:
		case R.id.iv_about:
			ConstData.showAbout(this);
			break;
		case R.id.iv_old_level:// 其它关卡
			startActivity(new Intent(this, LevelActivity.class));
			break;
		case R.id.iv_setting:// 设置
			startActivity(new Intent(this, SettingActivity.class));
			break;
		default:
			startActivity(new Intent(mContext, SokoCollectionListActivity.class));
			break;
		}
	}

	@Override
	protected void onDestroy() {
		Intent intent = new Intent(this, SoundService.class);
		stopService(intent);
		super.onDestroy();
	}

	int maxX, maxY;

	public String book2Array(Book book) {
		int arr[][] = new int[18][17];
		setPointVar(book.actor, GameView.ID_MAN, arr);
		setPointVar(book.wall, GameView.ID_WALL, arr);
		setPointVar(book.box, GameView.ID_BOX, arr);
		setPointVar(book.target, GameView.ID_POINT, arr);

		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("new,");
		maxX++;
		maxY++;
		sBuilder.append(maxX + ",");
		sBuilder.append(maxY + ",");
		// int map[][] = new int[maxX + 1][maxY + 1];
		for (int i = 0; i < maxX; i++) {
			for (int j = 0; j < maxY; j++) {
				sBuilder.append(arr[i][j]);
			}
		}

		maxX = 0;
		maxY = 0;
		return sBuilder.substring(0);
		// for (int i = 0; i < 18; i++) {
		// for (int j = 0; j < 18; j++) {
		// System.out.print(arr[i][j]);
		// }
		// System.out.println();
		// }

	}

	public void setPointVar(String string, int VAR, int map[][]) {
		String arr[] = string.split(";");
		for (int i = 0; i < arr.length; i++) {
			String sssString[] = arr[i].split(",");
			int x = Integer.parseInt(sssString[0]);
			int y = Integer.parseInt(sssString[1]);
			if (maxX < x) {
				maxX = x;
			}
			if (maxY < y) {
				maxY = y;
			}
			if (VAR == GameView.ID_POINT && map[x][y] == GameView.ID_BOX) {
				map[x][y] = GameView.ID_BOX_IN;
			} else {
				map[x][y] = VAR;
			}

		}
	}

	@Override
	public int[] setClickID() {
		return new int[] {};
	}

	@Override
	protected void initData() {
		
//		DBHelper<User> dbHelper=new DBHelper<User>(mContext, User.class);
//		dbHelper.insert(new User(12,"aa",true));
//		dbHelper.insert(new User(13,"aaffd",true));
//		dbHelper.insert(new User(15,"fff",false));
//		dbHelper.insert(new User(16,"ggggg",true));
//		
//		Object object=dbHelper.querry();
//		
//		System.out.println(JSON.toJSONString(object));
//		
//		dbHelper.delete("age=?",new String[]{"12"});
//		
//		object=dbHelper.querry();
//		
//		System.out.println(JSON.toJSONString(object));

		findViewById(R.id.textView2).setOnClickListener(this);
		findViewById(R.id.iv_set).setOnClickListener(this);

		findViewById(R.id.about).setOnClickListener(this);
		findViewById(R.id.iv_old_level).setOnClickListener(this);
		findViewById(R.id.iv_about).setOnClickListener(this);
		findViewById(R.id.iv_setting).setOnClickListener(this);

		tvVersion = (TextView) findViewById(R.id.tvVersion);

		tvVersion.setText(tvVersion.getText().toString().replace("$", Tools.getVerName(mContext)));
		boolean isOn = MySharepreferences.getBoolean(this, "aa", "music", false);
		Intent intent = new Intent(this, SoundService.class);
		intent.putExtra("playing", isOn);
		startService(intent);
		isSwitchOn = isOn;

		imageView = (ImageView) findViewById(R.id.iv_music);
		imageView.setOnClickListener(this);
		setUI();
		// 旧关卡
		String isinit = ShareCacheUtil.getString(this, "isInit");
		if (isinit == null) {
			ShareCacheUtil.putString(this, "isInit", "aa");
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						initMaps();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		// 创建新关卡2
		String isinit2 = ShareCacheUtil.getString(this, "isInit2");
		if (isinit2 == null) {
			ShareCacheUtil.putString(this, "isInit2", "aa");
			new Thread(new Runnable() {

				@Override
				public void run() {
					paraseXXX();
				}
			}).start();
		}

	}

	private void initMaps() throws IOException {
		// String isinit = ShareCacheUtil.getString(this, "isInit");
		// if (isinit == null) {
		// ShareCacheUtil.putString(this, "isInit", "aa");
		AssetManager aManager = getAssets();
		InputStream input = aManager.open("mymap.txt");
		BufferedReader in = new BufferedReader(new InputStreamReader(input, "gbk"));
		String line = null;
		DatabaseHelper dHelper = new DatabaseHelper(this);
		while ((line = in.readLine()) != null) {
			if (line.length() > 0) {
				LevelInfo levelInfo = new LevelInfo();
				levelInfo.level = Integer.parseInt(line.substring(1, 4));
				levelInfo.levelString = line.substring(5);
				dHelper.insert(levelInfo);
			}
		}
		input.close();
		// }
	}

	// 插入新关卡
	private void paraseXXX() {
		try {
			InputStream is = getAssets().open("levels.xml");
			PullBookParser parser = new PullBookParser();
			List<Book> books = parser.parse(is);
			DatabaseHelper dHelper = new DatabaseHelper(this);
			int i = 101;
			for (Book book : books) {
				String sss = book2Array(book);
				LevelInfo levelInfo = new LevelInfo();
				levelInfo.level = i++;
				levelInfo.levelString = sss;
				dHelper.insert(levelInfo);
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected int setViewId() {
		return R.layout.acvivity_main;
	}

	@Override
	protected String getPageName() {
		return "欢迎页";
	}

}
