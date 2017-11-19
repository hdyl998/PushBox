package com.hdyl.pushbox.file;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.hdyl.pushbox.R;
import com.hdyl.pushbox.base.BaseActivity;
import com.hdyl.pushbox.base.BaseFragment;
import com.hdyl.pushbox.base.adapter.BaseViewHolder;
import com.hdyl.pushbox.base.adapter.SuperAdapter;
import com.hdyl.pushbox.soko.tool.ParaseLevelTools;
import com.hdyl.pushbox.tools.LogUtils;
import com.hdyl.pushbox.tools.MySharepreferences;
import com.hdyl.pushbox.tools.ToastUtils;

public class ChooseFileFragment extends BaseFragment implements OnItemClickListener {

	@Override
	protected int setViewId() {
		return R.layout.activity_soko_select;
	}

	String fileExt;// 文件扩展名

	TextView tvPath;
	ListView listView;
	SuperAdapter<MyFileItem> adapterFileChoose;
	List<MyFileItem> listItems;
	public String defaultPath;

	final static String FOLDER = "sokolevels";
	String NAME_KEY_PATH = "last_path";

	String filesExts[];

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.tv_sd_level:
			String tvPathString=tvPath.getText().toString();
			MySharepreferences.putString(mContext, FOLDER, NAME_KEY_PATH,tvPathString);
			if (isFile == false) {
				Intent intent=new Intent();
				intent.putExtra(EXTRA1, tvPathString);
				mContext.setResult(Activity.RESULT_OK,intent);
			}
			mContext.finish();
			break;
		case R.id.back:
			mContext.finish();
			break;
		}

	}

	@Override
	public int[] setClickId() {
		return new int[] { R.id.tv_sd_level, R.id.back };
	}

	boolean isFile;

	private String chooseTitle;

	@Override
	protected void initData() {

		Intent intent = mContext.getIntent();
		chooseTitle = intent.getStringExtra(EXTRA1);// 标题

		NAME_KEY_PATH += chooseTitle;

		String sssString = intent.getStringExtra(EXTRA2);
		isFile = sssString.equals("1");
		TextView textView = (TextView) findViewById(R.id.textView1);
		if (isFile) {
			String str3 = intent.getStringExtra(EXTRA3);
			String string = "文件格式：" + ParaseLevelTools.getSupportExtString(str3);
			textView.setText(string);
			filesExts = str3.split(",");
		} else {
			textView.setText("请选择文件夹");
		}
		TextView text = (TextView) findViewById(R.id.back);
		text.setOnClickListener(this);
		text.setText(chooseTitle);
		TextView tvRightMenu = (TextView) findViewById(R.id.tv_sd_level);
		tvRightMenu.setText("完成");
		tvPath = (TextView) findViewById(R.id.textView2);
		tvPath.setVisibility(View.VISIBLE);
		listView = (ListView) findViewById(R.id.listView1);
		listView.setOnItemClickListener(this);

		adapterFileChoose = new SuperAdapter<MyFileItem>(mContext, listItems, R.layout.item_file) {

			@Override
			protected void onBind(BaseViewHolder holder, MyFileItem item, int position) {
				if (item.isFirst) {
					holder.setImageResource(R.id.imageView1, R.drawable.ic_file);
					holder.setText(R.id.textView1, "/");
				} else {
					holder.setImageResource(R.id.imageView1, item.isFile ? R.drawable.ic_file01 : R.drawable.ic_file);
					holder.setText(R.id.textView1, item.name);
				}
			}
		};

		defaultPath = MySharepreferences.getString(mContext, FOLDER, NAME_KEY_PATH);
		if (defaultPath == null) {
			defaultPath = getSavePath();

			if (defaultPath == null) {
				ToastUtils.makeTextAndShow("不支持存储卡");
				mContext.finish();
				return;
			}
			/* ToastUtils.makeTextAndShow(defaultPath); */
			// deep = 0 + 1;
		} else {

			/*
			 * String sssString = getSavePath(); int NormalNum =
			 * countDividerChar(sssString); int tmpNum =
			 * countDividerChar(defaultPath); deep = NormalNum - tmpNum + 1;
			 */
		}
		File file = new File(defaultPath);
		if (!file.exists()) {
			file.mkdir();
		}
		listView.setAdapter(adapterFileChoose);
		addDetoryFile(file);
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
			return Environment.getExternalStorageDirectory() + "/" + HandongApplication + "/" + PushBox + "/";
		} else {
			return null;
		}
	}

	public List<MyFileItem> getListFiles(File file) {
		File[] files = file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File arg0) {
				if (isFile) {
					if (arg0.isFile()) {
						return ParaseLevelTools.isFileExtRight(arg0.getName(), filesExts);
					}
				} else {
					if (arg0.isFile()) {
						return false;
					}

				}
				if (!arg0.getName().startsWith(".")) {// 文件夹
					return true;
				}
				return false;
			}
		});

		List<MyFileItem> list = new ArrayList<MyFileItem>();
		if (files != null) {
			for (File f : files) {
				list.add(file2MyFileItem(f));
			}
			Collections.sort(list);
		}
		return list;
	}

	private MyFileItem file2MyFileItem(File f) {
		MyFileItem item = new MyFileItem();
		item.isFile = f.isFile();
		item.name = f.getName();
		item.absPath = f.getAbsolutePath();
		return item;

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		MyFileItem item = listItems.get(position);
		File file = new File(item.absPath);
		if (item.isFile) {// 文件
			Intent intent = new Intent();
			intent.putExtra(EXTRA1, item.absPath);
			mContext.setResult(Activity.RESULT_OK, intent);
			MySharepreferences.putString(mContext, FOLDER, NAME_KEY_PATH, file.getParentFile().toString());
			mContext.finish();
		} else {// 文件夹

			// if (item.isFirst) {
			// deep--;// 向上--
			// } else {
			// deep++;
			// }

			// 得到文件的列表
			addDetoryFile(file);

		}
	}

	private void addDetoryFile(File file) {
		listItems = getListFiles(file);
		Collections.sort(listItems);
		// 取该文件的上一级，以便返回
		if (file.getParentFile() != null /* && deep >= 0 */) {
			MyFileItem fileItem = file2MyFileItem(file.getParentFile());
			fileItem.isFirst = true;
			// 加到第一个
			listItems.add(0, fileItem);
		}
		LogUtils.Print(file.getParentFile() + "");
		tvPath.setText(file.getAbsolutePath());
		adapterFileChoose.setDatas(listItems);
	}

	/**
	 * 
	 * @param title
	 *            标题
	 * @param isFile
	 *            是否是文件
	 * @param filter
	 *            文件过虑器
	 */

	public static void lunch(BaseActivity mContext, String title, boolean isFile, String fileExts, int requestCode) {
		mContext.startNewActivityForRestlt(ChooseFileFragment.class, requestCode, title, isFile ? "1" : "0", fileExts);

	}

}
