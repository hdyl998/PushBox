package com.hdyl.pushbox.soko;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hdyl.pushbox.R;
import com.hdyl.pushbox.base.BaseActivity;
import com.hdyl.pushbox.base.ConstData;
import com.hdyl.pushbox.base.DialogCreator;
import com.hdyl.pushbox.base.adapter.BaseViewHolder;
import com.hdyl.pushbox.base.adapter.SuperAdapter;
import com.hdyl.pushbox.file.MyFileListActivity;
import com.hdyl.pushbox.soko.LevelChooseItem.LevelInfo;
import com.hdyl.pushbox.soko.LevelChooseItem.MyLevel;
import com.hdyl.pushbox.soko.tool.ClipboardTools;
import com.hdyl.pushbox.soko.tool.ParaseLevelTools;
import com.hdyl.pushbox.soko.tool.SaveDataUtls;
import com.hdyl.pushbox.tools.LogUtils;
import com.hdyl.pushbox.tools.MySharepreferences;
import com.hdyl.pushbox.tools.ToastUtils;

public class SokoCollectionListActivity extends BaseActivity implements OnItemClickListener {

	final static String FOLDER = "sokolevels";

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.tv_sd_level:// 扩展

			startActivity(MyFileListActivity.class);
			break;
		case R.id.tv_export:// 输入
			inputSolution();
			break;
		default:
			finish();
			break;
		}
	}

	@SuppressLint("NewApi")
	private void inputSolution() {

		final EditText editText = new EditText(this);

		CharSequence charSequence = ClipboardTools.getStringFromClipboard(mContext);
		editText.setText(charSequence);

		new AlertDialog.Builder(this).setTitle("请输入关卡数据").setIcon(android.R.drawable.ic_dialog_info).setView(editText).setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				String sss = editText.getText().toString();

				if (!TextUtils.isEmpty(sss)) {
					String sssString = getFilterString(sss);
					if (sssString != null) {

						// 输入正确,构建这一关
						String ssarr[] = sssString.split("\\n");
						// LogUtils.Print(ssarr.length);

						MyLevel myLevel = new MyLevel();
						myLevel.ID = "自定义";
						myLevel.author = "Default";
						myLevel.levDatas = Arrays.asList(ssarr);
						List<MyLevel> list = new ArrayList<LevelChooseItem.MyLevel>();
						list.add(myLevel);

						// 存储用户的关卡
						addInputLevel(myLevel);

						SokoGameActivity.lunch(mContext, 0, list, -1);

					} else {
						// ToastUtils.makeTextAndShow(mContext, "输入错误!");
					}
				} else {
					ToastUtils.makeTextAndShow(mContext, "输入为空!");
				}
			}

			private boolean isCharOK(char ch) {
				switch (ch) {
				case S_BOX:
				case S_BOX_IN:
				case S_EMPTY:
				case S_MAN:
				case S_MAN_IN:
				case S_POINT:
				case S_EMPTY2:
				case S_WALL:
				case S_EMPTY3:
					return true;
				default:
					return false;
				}
			}

			private String getFilterString(String sss) {
				if (!TextUtils.isEmpty(sss)) {
					String datas[] = sss.split("\\n");
					StringBuilder sbBuffer = new StringBuilder();
					boolean isStart = false;
					for (int i = 0; i < datas.length; i++) {
						if (datas[i].length() == 0) {
							if (isStart == true)
								return sbBuffer.substring(0);
							else {
								continue;
							}
						} else {
							isStart = true;
							char chs[] = datas[i].toCharArray();
							for (char ch : chs) {
								if (isCharOK(ch))
									sbBuffer.append(ch);
								else {
									LogUtils.Print(ch);
									DialogCreator.create(mContext, "信息", "输入错误！【" + ch + "】不是有效字符!");
									return null;
								}
							}
							sbBuffer.append("\n");
						}
					}
					return sbBuffer.substring(0);
				}
				return sss;
			}
		}).setNegativeButton("取消", null).show();
	}

	// 添加输入关卡
	private void addInputLevel(MyLevel myLevel) {
		String data = MySharepreferences.getString(mContext, USER_LIST, USER_LIST);
		LevelChooseItem leItem;
		if (data == null) {
			leItem = new LevelChooseItem();
			leItem.title = "用户输入关卡";
			leItem.auther = "Default";
		} else {
			leItem = JSON.parseObject(data, LevelChooseItem.class);
		}
		if (myLevel != null) {
			String ID_MD5 = LevelChooseItem.getLevelMD5(myLevel);
			for (MyLevel level : leItem.listLev) {
				String md5 = LevelChooseItem.getLevelMD5(level);
				if (md5.equals(ID_MD5)) {
					ToastUtils.makeTextAndShow("输入关卡已存在列表");
					return;
				}
			}
			LevelInfo levelInfo = SaveDataUtls.getLevelInfoData(ID_MD5);

			if (levelInfo != null) {
				myLevel.isSolution = !TextUtils.isEmpty(levelInfo.solution);
				myLevel.isFinished = levelInfo.isFinished;
				myLevel.passDate = levelInfo.passDate;
			}
			leItem.listLev.add(myLevel);
		}
		data = JSON.toJSONString(leItem);
		// 存档
		MySharepreferences.putString(mContext, USER_LIST, USER_LIST, data);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		final FileListItem item = listItems.get(arg2);
		final String key = item.fileString;

		showLoadingDialog();
		new Thread(new Runnable() {

			@Override
			public void run() {

				String data = MySharepreferences.getString(mContext, key, key);
				if (TextUtils.isEmpty(data)) {
					// 用户自定义列表
					if (USER_LIST.equals(key)) {// 为空
						LevelChooseItem leItem = new LevelChooseItem();
						leItem.title = "用户输入关卡";
						leItem.auther = "Default";
						data = JSON.toJSONString(leItem);
						MySharepreferences.putString(mContext, key, key, data);
					} else {
						try {
							InputStream input = getAssets().open(FOLDER + "/" + key);
							data = ParaseLevelTools.paraseFile2String(item.name, key, input);
							if (!TextUtils.isEmpty(data))
								MySharepreferences.putString(mContext, key, key, data);
						} catch (Exception e) {
						}
					}
				}
				final String dataString = data;

				SokoCollectionListActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						hideLoadingDialog();
						if (dataString != null) {
							Intent intent = new Intent(mContext, SokoLevelListActivity.class);
							intent.putExtra(EXTRA1, key);
							intent.putExtra(EXTRA2, dataString);
							intent.putExtra(EXTRA3, item.name);
							intent.putExtra(EXTRA4, USER_LIST.equals(key));
							startActivity(intent);
						} else {
							DialogCreator.create(mContext, "错误", "未知格式或格式错误！\n文件名：" + key);
						}
					}
				});

			}
		}).start();

	}

	List<FileListItem> listItems;

	public final static String USER_LIST = "userlist";

	@Override
	protected void initData() {

		findViewByID(R.id.tv_export).setVisibility(View.VISIBLE);
		findViewById(R.id.back).setOnClickListener(this);
		ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setOnItemClickListener(this);

		listItems = new ArrayList<FileListItem>();

		String sssString[] = null;
		try {
			sssString = this.getAssets().list(FOLDER);

		} catch (IOException e) {
		}

		String fileNames[] = { "BoxWorld 100", "Microban", "Microban II", "Microban III", "Microban IV", "Minicosmos",// 6
																														// 8
				"Microcosmos",// 7 7
				"Nabokosmos",// 8 10
				"Picokosmos",// 9 11
				"Cosmopoly",// 10 2
				"Cosmonotes",// 11 1
				"Myriocosmos",// 12 9
				"Yoshio Musare Auto-generated",// 13 12
				"Yoshio Musare Handmade" };// 14 13
		int types[] = { 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2 };
		int index[] = { 0, 3, 4, 5, 6, 8, 7, 10, 11, 2, 1, 9, 12, 13 };
		for (int i = 0; i < fileNames.length; i++) {
			listItems.add(new FileListItem(types[i], fileNames[i], sssString[index[i]]));
		}

		listItems.add(new FileListItem(3, "Du Peloux", sssString[sssString.length - 2]));// 倒数第二个
		listItems.add(new FileListItem(4, "Loma", sssString[sssString.length - 1]));// 倒数第一个
		listItems.add(new FileListItem(5, "User List", USER_LIST));// 用户自定义数据

		LogUtils.Print(listItems);

		int type = -1;
		for (FileListItem item : listItems) {
			if (item.type != type) {
				type = item.type;
			} else {
				item.typeString = null;
			}
		}

		// FileListAdapter fileListAdapter = new FileListAdapter(mContext,
		// listItems);

		listView.setAdapter(new SuperAdapter<FileListItem>(mContext, listItems, R.layout.item_filelist) {

			@Override
			protected void onBind(BaseViewHolder holder, FileListItem item, int position) {
				TextView tv1 = holder.getView(R.id.textView1);
				if (TextUtils.isEmpty(item.typeString)) {
					tv1.setVisibility(View.GONE);
				} else {
					tv1.setVisibility(View.VISIBLE);
					tv1.setText(item.typeString);
				}
				holder.setText(R.id.textView2, item.name);
			}
		});

	}

	@Override
	protected int setViewId() {
		return R.layout.activity_soko_select;
	}

	@Override
	public int[] setClickID() {
		return new int[] { R.id.tv_sd_level, R.id.tv_export };
	}

}
