package com.hdyl.pushbox.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import u.aly.da;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
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
import com.hdyl.pushbox.soko.SokoLevelListActivity;
import com.hdyl.pushbox.soko.tool.ParaseLevelTools;
import com.hdyl.pushbox.tools.LogUtils;
import com.hdyl.pushbox.tools.MySharepreferences;
import com.hdyl.pushbox.tools.ShareCacheUtil;
import com.hdyl.pushbox.tools.ToastUtils;
import com.hdyl.pushbox.tools.Tools;

public class MyFileListActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener {

	final static String EX_FILE_SUFIX = "lgd_sufix_";

	List<LocalSavedItem> listLocalSavedItems = new ArrayList<LocalSavedItem>();

	public String defaultPath;

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.tv_sd_level:
			ChooseFileFragment.lunch(mContext, "选择扩展关卡集", true, ParaseLevelTools.exts, REQUEST_CODE);
			break;
		case R.id.back:
			finish();
			break;
		}

	}

	@Override
	public int[] setClickID() {
		return new int[] { R.id.tv_sd_level };
	}

	// 读本地的列表
	private void readLocalFile() {
		String sss = ShareCacheUtil.getString(mContext, "saved_levels");
		if (sss != null) {
			try {
				List<LocalSavedItem> list = JSON.parseArray(sss, LocalSavedItem.class);
				listLocalSavedItems.addAll(list);
			} catch (Exception e) {
			}
		}
		LocalSavedItem.showTitleSortHand(listLocalSavedItems);
		listView.setAdapter(adapterLevelList);
	}

	/* int deep = 1; */

	TextView textView;

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		LocalSavedItem item = listLocalSavedItems.get(position);
		String key = EX_FILE_SUFIX + item.name;

		String data = MySharepreferences.getString(this, key, key);
		if (TextUtils.isEmpty(data)) {
			try {
				InputStream input = new FileInputStream(item.localPath);// 从本地读文件
				data = ParaseLevelTools.paraseFile2String(item.showName, key, input);
				if (!TextUtils.isEmpty(data))
					MySharepreferences.putString(this, key, key, data);
			} catch (Exception e) {
			}
		}

		if (data != null) {
			Intent intent = new Intent(this, SokoLevelListActivity.class);
			intent.putExtra(EXTRA1, key);
			intent.putExtra(EXTRA2, data);
			intent.putExtra(EXTRA3, item.showName);
			startActivity(intent);

		} else {
			DialogCreator.create(mContext, "错误", "未知格式或格式错误！\n文件名：" + key);
		}

	}

	SuperAdapter<LocalSavedItem> adapterLevelList;

	ListView listView;

	TextView tvRightMenu;

	String txtFormatString;

	TextView tvPath;

	@Override
	protected void initData() {
		textView = (TextView) findViewById(R.id.textView1);
		TextView text = (TextView) findViewById(R.id.back);
		text.setOnClickListener(this);
		text.setText("扩展");
		tvRightMenu = (TextView) findViewById(R.id.tv_sd_level);

		tvPath = (TextView) findViewById(R.id.textView2);
		tvPath.setVisibility(View.VISIBLE);
		listView = (ListView) findViewById(R.id.listView1);
		listView.setOnItemClickListener(this);

		listView.setOnItemLongClickListener(this);

		adapterLevelList = new SuperAdapter<LocalSavedItem>(mContext, listLocalSavedItems, R.layout.item_local_file_list) {

			@Override
			protected void onBind(BaseViewHolder holder, LocalSavedItem item, int position) {
				TextView textView1 = holder.getView(R.id.textView1);

				if (!item.isShowTitle) {
					textView1.setVisibility(View.GONE);
				} else {
					textView1.setVisibility(View.VISIBLE);
					textView1.setText(item.typeString);
				}
				holder.setText(R.id.textView2, item.showName);
				holder.setText(R.id.textView3, item.date);
				holder.setText(R.id.textView4, item.localPath);
			}
		};

		txtFormatString = "支持文件格式：" + ParaseLevelTools.getSupportExtString(ParaseLevelTools.exts);

		tvRightMenu.setText("SD卡导入");
		textView.setText("请选择关卡集");
		tvPath.setText(txtFormatString);

		readLocalFile();// 读本地存的数据
		registerForContextMenu(listView);

	}

	// private int countDividerChar(String temString) {
	// int count = 0;
	// for (char ch : temString.toCharArray()) {
	// if (ch == '/') {
	// count++;
	// }
	// }
	// return count;
	// }

	@Override
	protected int setViewId() {
		return R.layout.activity_soko_select;
	}

	@Override
	protected String getPageName() {
		return null;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {

		currentIndex = arg2;
		return false;
	}

	int currentIndex = 0;

	private void bieMing(final int arg2) {
		final EditText editText = new EditText(this);
		editText.setText(listLocalSavedItems.get(arg2).showName);
		editText.selectAll();
		new AlertDialog.Builder(this).setTitle("请输入关卡别名").setIcon(android.R.drawable.ic_dialog_info).setView(editText).setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				String sss = editText.getText().toString().trim();
				if (sss.length() != 0) {
					listLocalSavedItems.get(arg2).showName = sss;
				}
				saveDatas();
				adapterLevelList.notifyDataSetChanged();

			}
		}).setNegativeButton("取消", null).show();
	}

	private static final int ITEM1 = Menu.FIRST;
	private static final int ITEM2 = Menu.FIRST + 1;
	private static final int ITEM3 = Menu.FIRST + 2;

	// 上下文菜单，本例会通过长按条目激活上下文菜单
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		// menu.setHeaderTitle("人物简介");
		// 添加菜单项
		menu.add(0, ITEM1, 0, "设置别名");
		menu.add(0, ITEM2, 0, "删除关卡");
		menu.add(0, ITEM3, 0, "取消");
	}

	// 菜单单击响应
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// 获取当前被选择的菜单项的信息
		// AdapterContextMenuInfo info =
		// (AdapterContextMenuInfo)item.getMenuInfo();
		// Log.i("braincol",String.valueOf(info.id));
		switch (item.getItemId()) {
		case ITEM1:
			// 在这里添加处理代码
			bieMing(currentIndex);
			break;
		case ITEM2:
			// 在这里添加处理代码
			listLocalSavedItems.remove(currentIndex);
			LocalSavedItem.showTitleSortHand(listLocalSavedItems);
			adapterLevelList.notifyDataSetChanged();
			saveDatas();
			break;
		case ITEM3:
			// 在这里添加处理代码

			break;
		}
		return true;
	}

	final static int REQUEST_CODE = 88;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			String filePath = data.getStringExtra(EXTRA1);
			File file = new File(filePath);
			String name = file.getName();
			for (LocalSavedItem loItem : listLocalSavedItems) {
				if (name.equals(loItem.name)) {
					ToastUtils.makeTextAndShow(mContext, "读取的文件已存在,导入失败");
					return;
				}
			}
			LocalSavedItem localItem = new LocalSavedItem();
			localItem.date = Tools.getNowTimeString();
			localItem.name = file.getName();
			localItem.typeString = localItem.name.substring(localItem.name.length() - 3).toUpperCase();
			localItem.localPath = file.getAbsolutePath();
			localItem.showName = localItem.name.substring(0, localItem.name.length() - 4);
			listLocalSavedItems.add(localItem);
			LocalSavedItem.showTitleSortHand(listLocalSavedItems);
			listView.setAdapter(adapterLevelList);
			saveDatas();
			LogUtils.Print(localItem);
		}
	}

	public void saveDatas() {
		ShareCacheUtil.putString(mContext, "saved_levels", JSON.toJSONString(listLocalSavedItems));
	}

}
