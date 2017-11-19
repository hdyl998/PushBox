package com.hdyl.pushbox.soko;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.Timer;

import u.aly.r;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hdyl.pushbox.R;
import com.hdyl.pushbox.base.BaseActivity;
import com.hdyl.pushbox.base.ConstData;
import com.hdyl.pushbox.base.DialogCreator;
import com.hdyl.pushbox.setting.SettingConfig;
import com.hdyl.pushbox.soko.LevelChooseItem.LevelInfo;
import com.hdyl.pushbox.soko.LevelChooseItem.MyLevel;
import com.hdyl.pushbox.soko.SokoGameView.StepData;
import com.hdyl.pushbox.soko.tool.ClipboardTools;
import com.hdyl.pushbox.soko.tool.SaveDataUtls;
import com.hdyl.pushbox.soko.tool.ShareUtils;
import com.hdyl.pushbox.tools.ToastUtils;

public class SokoGameActivity extends BaseActivity {

	SokoGameView gameView;
	TextView tvLevel, tvStep, tvBest;
	// MediaPlayer mp;

	Timer timer;

	boolean isPause = false;

	public void setLevelText(String level) {
		tvLevel.setText("" + level);
	}

	public void setStep(int step) {
		tvStep.setText("" + step);
	}

	public void setBest(int best) {
		if (best == 0) {
			tvBest.setText("--");
		} else {
			tvBest.setText(best + "");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		isPause = true;
	}

	@Override
	protected void onResume() {
		isPause = false;
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.textViewBack:
			if (gameView.isInAction()) {
				return;
			}
			gameView.backOneStep();
			break;
		case R.id.textViewRefresh:
			if (gameView.isInAction()) {
				ToastUtils.makeTextAndShow("正在演示中，无法开始新的游戏");
				return;
			}
			gameView.newGame();
			break;
		case R.id.textViewExit:
			openContextMenu(arg0);
			break;
		}
	}

	public static void lunch(BaseActivity mContext, int index, List<MyLevel> listLevels, int RequstCode) {
		Intent intent = new Intent(mContext, SokoGameActivity.class);
		intent.putExtra(EXTRA1, index);
		intent.putExtra(EXTRA2, (Serializable) listLevels);
		mContext.startActivityForResult(intent, RequstCode);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initData() {
		// indexType=getIntent().getExtras().getInt("index");

		tvLevel = (TextView) findViewById(R.id.tv_level);

		tvStep = (TextView) findViewById(R.id.tv_highscore);

		tvBest = (TextView) findViewById(R.id.tv_best);

		// findViewById(R.id.tv_up).setOnClickListener(this);
		findViewById(R.id.textViewRefresh).setOnClickListener(this);
		View viewMenuView = findViewById(R.id.textViewExit);
		viewMenuView.setOnClickListener(this);
		findViewById(R.id.textViewBack).setOnClickListener(this);
		// viewMenuView.
		gameView = (SokoGameView) findViewById(R.id.gameView);
		int currentIndex = getIntent().getIntExtra(EXTRA1, 0);
		List<MyLevel> listMyLevels = (List<MyLevel>) getIntent().getSerializableExtra(EXTRA2);

		gameView.setCurrentIndex(currentIndex);
		gameView.setListMyLevels(listMyLevels);

		registerForContextMenu(viewMenuView);

	}

	private static final int ITEM1 = Menu.FIRST;
	private static final int ITEM2 = Menu.FIRST + 1;
	private static final int ITEM3 = Menu.FIRST + 2;
	private static final int ITEM4 = Menu.FIRST + 3;
	private static final int ITEM5 = Menu.FIRST + 4;

	private static final int ITEM6 = Menu.FIRST + 5;
	private static final int ITEM7 = Menu.FIRST + 6;
	private static final int ITEM8 = Menu.FIRST + 7;
	private static final int ITEM9 = Menu.FIRST + 8;

	@Override
	public void openContextMenu(View view) {
		super.openContextMenu(view);
		boolean isHasSolution=!TextUtils.isEmpty(gameView.getLevelInfo().solution);
		if (isHasSolution) {
			menuItemAct.setTitle("演示方案(共$步)".replace("$", gameView.getLevelInfo().solution.length() + ""));
			menuItemAct.setVisible(true);
		} else {
			menuItemAct.setVisible(false);
		}

		menuItemPaste.setVisible(Build.VERSION.SDK_INT >= 11&&isHasSolution);

		menuItemLast.setVisible(gameView.hasLastLevel());
		menuItemNext.setVisible(gameView.hasNextLevel());
	}

	// 上下文菜单，本例会通过长按条目激活上下文菜单
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		// menu.setHeaderTitle("人物简介");
		// 添加菜单项
		menu.add(0, ITEM1, 0, "演示方案");
		menu.add(0, ITEM2, 0, "粘帖方案");
		menu.add(0, ITEM5, 0, "复制方案");
		menu.add(0, ITEM6, 0, "复制关卡");
		menu.add(0, ITEM7, 0, "分享关卡");
		menu.add(0, ITEM8, 0, "上一关");
		menu.add(0, ITEM9, 0, "下一关");
		menu.add(0, ITEM4, 0, "关于");
		menu.add(0, ITEM3, 0, "退出");
		menuItemAct = menu.findItem(ITEM1);

		menuItemPaste = menu.findItem(ITEM5);

		menuItemLast = menu.findItem(ITEM8);
		menuItemNext = menu.findItem(ITEM9);
		
	}

	MenuItem menuItemAct, menuItemPaste, menuItemLast, menuItemNext,menuItemCopy;

	class MyActThread extends Thread {

		boolean isRun = false;
		String data;

		public MyActThread(String data) {
			this.data = data;
		}

		int sleepTime = SettingConfig.getInstence().sleepTime;

		@Override
		public void run() {
			isRun = true;
			gameView.startAction();
			char chs[] = data.toCharArray();

			int index = 0;
			while (isRun && index < chs.length) {
				try {
					sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				gameView.pushActingDirection(chs[index]);
				index++;
			}

			try {
				sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			gameView.stopAction();
			if (isRun) {
				SokoGameActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (gameView.isWin()) {
							ToastUtils.makeTextAndShow("演示完毕!");

							LevelInfo levelInfo = gameView.getLevelInfo();
							boolean isTrue = levelInfo.setSolution(data);// 本地存档优化存储
							levelInfo.isFinished = gameView.getMyLevel().isFinished;
							SaveDataUtls.saveLevelInfoData(levelInfo);
							gameView.setLevelOK();
						} else {
							ToastUtils.makeTextAndShow("演示完毕!但方案信息存在错误！");
						}
					}
				});
			}
		}
	}

	MyActThread myActThread;

	@Override
	public void finish() {
		if (myActThread != null) {
			myActThread.isRun = false;
		}
		super.finish();
	}

	// 菜单单击响应
	@SuppressLint("NewApi")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ITEM1:
			actSolotion(gameView.getLevelInfo().solution);
			break;
		case ITEM2:
			inputSolution();
			break;
		case ITEM3:
			finish();
			break;
		case ITEM5:
			ClipboardTools.copy2Clipboard(mContext, gameView.getLevelInfo().solution);
			break;
		case ITEM4:
			DialogCreator.create(mContext, item.getTitle().toString(), gameView.getMyLevel().getLevelInfo());
			break;
		case ITEM6:// 复制关卡
			ClipboardTools.copy2Clipboard(mContext, LevelChooseItem.getLevelString(gameView.getMyLevel()));
			break;
		case ITEM7:// 分享
			ShareUtils.shareInfo(mContext, gameView.getMyLevel());
			break;
		case ITEM8:
			gameView.lastLevel();
			break;
		case ITEM9:
			gameView.nextLevel();
			break;
		}
		return true;
	}

	public void actSolotion(String sssString) {
		if (gameView.isInAction() == true) {
			ToastUtils.makeTextAndShow(mContext, "正在演示中...请稍后");
			return;
		}
		if(sssString==null){
			return;
		}
		gameView.newGame();
		myActThread = new MyActThread(sssString);
		myActThread.start();

	}

	private boolean isCharOK(char ch) {
		switch (ch) {
		case 'r':
		case 'R':
		case 'L':
		case 'l':
		case 'U':
		case 'u':
		case 'd':
		case 'D':
			return true;
		default:
			return false;
		}
	}

	private String getFilterString(String sss) {
		if (!TextUtils.isEmpty(sss)) {
			char chs[] = sss.toCharArray();
			StringBuffer sbBuffer = new StringBuffer();
			for (char ch : chs) {
				if (isCharOK(ch))
					sbBuffer.append(ch);
				else {
					return null;
				}
			}
			return sbBuffer.substring(0);
		}
		return sss;
	}

	@SuppressLint("NewApi")
	private void inputSolution() {

		final EditText editText = new EditText(this);
		CharSequence ssCharSequence = ClipboardTools.getStringFromClipboard(mContext);
		if (ssCharSequence != null) {
			editText.setText(getFilterString(ssCharSequence.toString()));
		}

		new AlertDialog.Builder(this).setTitle("请输入关卡Solution(仅限输入LlRrUuDd)").setIcon(android.R.drawable.ic_dialog_info).setView(editText).setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (gameView.isInAction()) {
					ToastUtils.makeTextAndShow(mContext, "正在演示中...请稍后");
					return;
				}
				String sss = editText.getText().toString().trim();

				if (!TextUtils.isEmpty(sss)) {
					String sssString = getFilterString(sss);
					if (sssString != null) {
						actSolotion(sssString);
					} else {
						ToastUtils.makeTextAndShow(mContext, "输入错误!");
					}
				} else {
					ToastUtils.makeTextAndShow(mContext, "输入为空!");
				}
			}
		}).setNegativeButton("取消", null).show();

	}

	@Override
	protected int setViewId() {
		return R.layout.activity_soko_main;
	}

	@Override
	protected String getPageName() {
		return "游戏首页";
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("aa", gameView.getGameData());
//		ToastUtils.makeTextAndShow("存");
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		GameData gameData=(GameData) savedInstanceState.getSerializable("aa");
		gameView.resumeGame(gameData);
		
		ToastUtils.makeTextAndShow("内存不足读取中..");
	}
	
	
	public  static class GameData implements Serializable{
		public int currentStep = 0;// 当前步数

		// data
		public List<MyLevel> listMyLevels;
		public LevelInfo levelInfo;
		public MyLevel myLevel;
		public int currentIndex;

		public boolean isGameWin = false;

		public boolean isRightFalg = false;

		public boolean isShowPosition = false;

		public HashSet<Integer> hashSetPassLevel;// 通关的关卡
		
		public Stack<StepData> stepDatas;
		
		
		
		public int WIDTH = 18;
		public int HEIGHT = 12;

		public int yOffSet = 0;
		public int xOffSet = 0;
		
		public int  size;
		public int [][]arr;
		
	}
	
	
}
