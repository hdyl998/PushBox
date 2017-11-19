package com.hdyl.pushbox.soko;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hdyl.pushbox.R;
import com.hdyl.pushbox.base.BaseActivity;
import com.hdyl.pushbox.base.ConstData;
import com.hdyl.pushbox.base.DialogCreator;
import com.hdyl.pushbox.base.adapter.BaseViewHolder;
import com.hdyl.pushbox.base.adapter.SuperAdapter;
import com.hdyl.pushbox.soko.LevelChooseItem.LevelInfo;
import com.hdyl.pushbox.soko.LevelChooseItem.MyLevel;
import com.hdyl.pushbox.soko.tool.ClipboardTools;
import com.hdyl.pushbox.soko.tool.SaveDataUtls;
import com.hdyl.pushbox.soko.tool.ShareUtils;
import com.hdyl.pushbox.tools.LogUtils;
import com.hdyl.pushbox.tools.MySharepreferences;
import com.hdyl.pushbox.tools.ToastUtils;
import com.hdyl.pushbox.tools.Tools;
import com.hdyl.pushbox.view.ShapeCornerBgView;

public class SokoLevelListActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener {

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.tv_export:// 导出
			export2File();
			break;
		case R.id.tv_view_type:
			isList = !isList;
			setType(isList);
			MySharepreferences.putBoolean(mContext, "aa", "isList", isList);
			break;
		case R.id.back:
			finish();
			break;
		case R.id.tvIntroduce:
			// String sss = "作者：" + levelChooseItem.auther + "\n邮箱：" +
			// levelChooseItem.email + "\n描述：" + levelChooseItem.description;
			DialogCreator.create(mContext, ((TextView) arg0).getText().toString(), levelChooseItem.getLevelsString());
			break;

		}
	}

	public final String HandongApplication = "寒冬娱乐";
	public final String PushBox = "推箱子";

	/**
	 * 方法描述：createFile方法
	 * 
	 * @param String
	 *            app_name
	 * @return
	 * @see FileUtil
	 */
	public String getSavePath() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return Environment.getExternalStorageDirectory() + "/" + HandongApplication + "/" + PushBox + "/" + "导出关卡" + "/";
		} else {
			return null;
		}
	}

	// 导出到文件
	private void export2File() {
		try {
			String pathString = getSavePath();
			if (pathString == null) {
				ToastUtils.makeTextAndShow("不支持存储卡");
				return;
			}
			File destDir = new File(pathString);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}

			File file = new File(pathString, keyString + ".xsb");
			// if (file.exists()) {
			// ToastUtils.makeTextAndShow("文件已经存在！");
			// }
			file.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
			bWriter.append(levelItem2String());
			bWriter.flush();
			bWriter.close();
			fileOutputStream.close();
			DialogCreator.create(mContext, "导出成功!\n位置：" + file.getAbsolutePath());
		} catch (Exception e) {

			LogUtils.Print(e.toString());
		}

	}

	// 关卡转String
	private String levelItem2String() {
		StringBuilder sbBuilder = new StringBuilder();
		appendNotNullText(sbBuilder, levelChooseItem.title);
		appendNotNullText(sbBuilder, levelChooseItem.auther);
		appendNotNullText(sbBuilder, levelChooseItem.email);
		appendNotNullText(sbBuilder, levelChooseItem.description);
		int len = levelChooseItem.listLev.size();
		for (int i = 0; i < len; i++) {
			MyLevel myLevel = levelChooseItem.listLev.get(i);
			sbBuilder.append("\r\n");
			sbBuilder.append(";" + myLevel.ID);
			sbBuilder.append("\r\n");
			for (String sss : myLevel.levDatas) {
				sbBuilder.append(sss);
				sbBuilder.append("\r\n");
			}
		}
		sbBuilder.append("\r\n");
		return sbBuilder.substring(0);
	}

	private void appendNotNullText(StringBuilder sBuilder, String text) {
		if (!TextUtils.isEmpty(text)) {
			sBuilder.append(text);
			sBuilder.append("\r\n");
		}
	}

	public void setType(boolean isList) {
		if (isList) {
			registerForContextMenu(listView2);
			listView2.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			tvType.setText("视图");
		} else {
			registerForContextMenu(listView);
			listView.setVisibility(View.VISIBLE);
			listView2.setVisibility(View.GONE);
			tvType.setText("列表");
		}
	}

	boolean isList = false;

	LevelChooseItem levelChooseItem;

	String keyString;
	TextView tvType;
	ListView listView2;
	GridView listView;
	SuperAdapter levelAdapter;
	SuperAdapter gridAdapter;

	boolean isUserList = false;

	@Override
	public int[] setClickID() {
		return new int[] { R.id.tv_view_type, R.id.tv_export };
	}

	@Override
	protected void initData() {
		TextView textView = (TextView) findViewById(R.id.back);

		textView.setOnClickListener(this);

		keyString = getIntent().getStringExtra(EXTRA1);

		LogUtils.Print("keyString" + keyString);

		String sssString = getIntent().getStringExtra(EXTRA2);

		String showName = getIntent().getStringExtra(EXTRA3);

		isUserList = getIntent().getBooleanExtra(EXTRA4, isUserList);

		levelChooseItem = JSON.parseObject(sssString, LevelChooseItem.class);

		if (!TextUtils.isEmpty(showName)) {
			textView.setText(showName.toUpperCase());
		} else {
			textView.setText((levelChooseItem.title + "").toUpperCase());
		}

		if (levelChooseItem.auther == null) {
			levelChooseItem.auther = "";
		}

		if (levelChooseItem.email == null) {
			levelChooseItem.email = "";
		}

		listView = (GridView) findViewById(R.id.gv_select_level);
		// listView.addView(View.inflate(mContext, R.layout., null),0);
		//
		((TextView) findViewById(R.id.textViewAuthor)).append(levelChooseItem.auther + "");

		if (levelChooseItem.description == null)
			levelChooseItem.description = "";

		((TextView) findViewById(R.id.textViewStage)).setText("进度:" + levelChooseItem.getFinishedString());

		listView2 = (ListView) findViewById(R.id.listView1);

		// gridAdapter = new GridViewLevelAdapter(mContext,
		// levelChooseItem.listLev);

		listView.setAdapter(gridAdapter = new SuperAdapter<MyLevel>(mContext, levelChooseItem.listLev, R.layout.item_levelchoose_grideview) {

			@Override
			protected void onBind(BaseViewHolder holder, MyLevel level, int position) {
				holder.setText(R.id.textView1, position + 1 + "");
				SokoView sokoView = holder.getView(R.id.sokoView1);
				sokoView.setArrDatas(level.levDatas);
				holder.setVisibility(R.id.ivCompleted, level.isFinished ? View.VISIBLE : View.GONE);
			}
		});
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);

		listView2.setOnItemLongClickListener(this);

		// ListViewLevelAdapter
		listView2.setAdapter(levelAdapter = new SuperAdapter<MyLevel>(mContext, levelChooseItem.listLev, R.layout.item_levelchoose) {

			@Override
			protected void onBind(BaseViewHolder holder, MyLevel level, int position) {

				holder.setText(R.id.textView1, position + 1 + "");
				SokoView sokuView = holder.getView(R.id.sokoView1);
				sokuView.setArrDatas(level.levDatas);
				holder.setVisibility(R.id.ivCompleted, level.isFinished ? View.VISIBLE : View.GONE);

				holder.setText(R.id.textView2, level.ID);

				if (level.isSolution) {
					holder.setVisibility(R.id.tv_tongguan, View.VISIBLE);
				} else {
					holder.setVisibility(R.id.tv_tongguan, View.GONE);
				}
				ShapeCornerBgView bgView = holder.getView(R.id.tv_passdate);
				if (level.isFinished && !TextUtils.isEmpty(level.passDate)) {
					bgView.setVisibility(View.VISIBLE);
					bgView.setText(Tools.getDistenceTime(level.passDate));
					bgView.setColor(Color.GRAY);
				} else {
					bgView.setVisibility(View.GONE);
				}
			}
		});
		listView2.setOnItemClickListener(this);

		tvType = (TextView) findViewById(R.id.tv_view_type);
		tvType.setOnClickListener(this);

		findViewById(R.id.tvIntroduce).setOnClickListener(this);

		isList = MySharepreferences.getBoolean(mContext, "aa", "isList", true);
		setType(isList);

	}

	@Override
	protected int setViewId() {
		return R.layout.activity_soko_list;
	}

	@Override
	protected String getPageName() {
		return null;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		SokoGameActivity.lunch(mContext, arg2, levelChooseItem.listLev, REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE) {

			if (resultCode == Activity.RESULT_OK) {

				@SuppressWarnings("unchecked")
				HashSet<Integer> hashSetPassLevel = (HashSet<Integer>) data.getSerializableExtra(EXTRA1);
				if (hashSetPassLevel.size() != 0) {
					// 所有通过的关卡
					for (Integer integer : hashSetPassLevel) {
						MyLevel myLevel = levelChooseItem.listLev.get(integer);
						String ID_MD5 = LevelChooseItem.getLevelMD5(myLevel);
						LevelInfo levelInfo = SaveDataUtls.getLevelInfoData(ID_MD5);
						if (levelInfo != null) {
							myLevel.isFinished = levelInfo.isFinished;
							myLevel.passDate = levelInfo.passDate;
							myLevel.isSolution = true;
						}
						LogUtils.Print("存档", levelInfo);
					}
					((TextView) findViewById(R.id.textViewStage)).setText("进度:" + levelChooseItem.getFinishedString());
					levelAdapter.notifyDataSetChanged();
					gridAdapter.notifyDataSetChanged();
					MySharepreferences.putString(this, keyString, keyString, JSON.toJSONString(levelChooseItem));
				}
				LogUtils.Print("更新" + hashSetPassLevel.size() + "关");

			}
			// else if (resultCode == Activity.RESULT_FIRST_USER) {
			// // 统计所有的关卡
			//
			// new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			// for (MyLevel myLevel : levelChooseItem.listLev) {
			// String ID_MD5 = LevelChooseItem.getLevelMD5(myLevel);
			// LevelInfo levelInfo = SaveDataUtls.getLevelInfoData(ID_MD5);
			// if (levelInfo != null) {
			// myLevel.isSolution = !TextUtils.isEmpty(levelInfo.solution);
			// }
			//
			// }
			// MySharepreferences.putString(mContext, keyString, keyString,
			// JSON.toJSONString(levelChooseItem));
			// listView.post(new Runnable() {
			//
			// @Override
			// public void run() {
			// levelAdapter.notifyDataSetChanged();
			// gridAdapter.notifyDataSetChanged();
			//
			// }
			// });
			//
			// }
			// }).start();
			//
			// }
		}
	}

	private static final int ITEM1 = Menu.FIRST;
	private static final int ITEM2 = Menu.FIRST + 1;
	private static final int ITEM3 = Menu.FIRST + 2;
	private static final int ITEM4 = Menu.FIRST + 3;
	private static final int ITEM5 = Menu.FIRST + 4;
	private static final int ITEM6 = Menu.FIRST + 5;

	MenuItem menuItem;
	int currentIndex;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		menu.add(0, ITEM1, 0, "复制方案");
		menu.add(0, ITEM2, 0, "复制关卡");

		menu.add(0, ITEM5, 0, "分享关卡");
		menu.add(0, ITEM6, 0, "删除");
		menu.add(0, ITEM3, 0, "关于");
		menu.add(0, ITEM4, 0, "取消");

		MyLevel myLevel = levelChooseItem.listLev.get(currentIndex);

		menuItem = menu.findItem(ITEM1).setVisible(myLevel.isSolution);
		menu.findItem(ITEM6).setVisible(isUserList);
	}

	// 菜单单击响应
	@SuppressLint("NewApi")
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		MyLevel myLevel = levelChooseItem.listLev.get(currentIndex);

		switch (item.getItemId()) {
		case ITEM1:
			String ID_MD5 = LevelChooseItem.getLevelMD5(myLevel);
			LevelInfo levelInfo = SaveDataUtls.getLevelInfoData(ID_MD5);
			ClipboardTools.copy2Clipboard(mContext, levelInfo.solution);
			break;
		case ITEM2:
			ClipboardTools.copy2Clipboard(mContext, LevelChooseItem.getLevelString(myLevel));
			break;
		case ITEM3:
			String sss = "关卡名：" + notNull(myLevel.ID) + "\n作者：" + notNull(myLevel.author) + "\n描述：" + notNull(myLevel.comment);
			DialogCreator.create(mContext, item.getTitle().toString(), sss);

			break;
		case ITEM4:

			break;

		case ITEM5:
			ShareUtils.shareInfo(mContext, myLevel);
			break;
		case ITEM6:
			DialogCreator.create(mContext, "提示", "确认要删除第" + (currentIndex + 1) + "关", "确认", "取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {

					switch (arg1) {
					case 0:
						levelChooseItem.listLev.remove(currentIndex);
						String data = JSON.toJSONString(levelChooseItem);
						MySharepreferences.putString(mContext, SokoCollectionListActivity.USER_LIST, SokoCollectionListActivity.USER_LIST, data);
						levelAdapter.notifyDataSetChanged();
						gridAdapter.notifyDataSetChanged();
						break;

					default:
						break;
					}
				}
			});

			break;
		}
		return true;
	}

	public String notNull(String sss) {
		if (TextUtils.isEmpty(sss)) {
			return "暂无数据";
		}
		return sss;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {

		currentIndex = arg2;

		if (menuItem != null) {
			if (!menuItem.isVisible()) {
				MyLevel myLevel = levelChooseItem.listLev.get(currentIndex);
				menuItem.setVisible(myLevel.isSolution);
			}
		}

		return false;
	}

	public final static int REQUEST_CODE = 888;
}
