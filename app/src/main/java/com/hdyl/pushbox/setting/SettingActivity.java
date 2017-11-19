package com.hdyl.pushbox.setting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.hdyl.pushbox.R;
import com.hdyl.pushbox.base.BaseActivity;
import com.hdyl.pushbox.base.ConstData;
import com.hdyl.pushbox.file.ChooseFileFragment;
import com.hdyl.pushbox.soko.SokoView;

public class SettingActivity extends BaseActivity {

	View viewRootView;
	TextView tvPositionSwitch;
	TextView tvSkinCoverSwitch;
	@Override
	public void finish() {
		SettingConfig.saveConfig();
		super.finish();
	}

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.back:

			finish();
			break;
		case R.id.ll_sytle:
			config.exchangeTheme();
			initUI();
			break;
		case R.id.ll_position:
		case R.id.tv_position_switch:
			config.exchangePosition();
			initUI();
			break;
		case R.id.btn_add:// 添加
			if (config.getBgStyle() == 2) {
				String fileExts = "jpg,png,gif,jepg";
				ChooseFileFragment.lunch(mContext, "选择背景图片", true, fileExts, REQUSTCODE);
			} else {
				ColorPickDialog dialog = new ColorPickDialog(mContext, viewRootView);
				dialog.show();
			}
			break;
		case R.id.ll_skin:// 皮肤
			config.exchangeSkin();
			
			initUI();
			break;
		case R.id.btn_skin_choose:
			ChooseFileFragment.lunch(mContext, "选择皮肤", false, null, REQUSTCODE_FOLDER);
			break;
		case R.id.ll_skin_cover:
		case R.id.tv_skin_cover_switch:
			config.exchangeCover();
			initUI();
			break;
		}
	}

	final static int REQUSTCODE = 88;
	final static int REQUSTCODE_FOLDER = 89;

	SeekBar sBar;

	TextView tvSpeed, tv_bg_style;

	TextView tvAdd;
	Button tvBtnSkinAdd;

	TextView tvPathSkin, tvSkinName;

	SokoView sokoView,sokoViewCover;

	@Override
	protected void initData() {
		viewRootView = findViewById(R.id.rootview);
		sBar = (SeekBar) findViewById(R.id.seekBar1);
		tvSpeed = (TextView) findViewById(R.id.tv_speed_var);
		tv_bg_style = (TextView) findViewById(R.id.tv_bg_style);
		tvAdd = findViewByID(R.id.btn_add);
		tvPositionSwitch = findViewByID(R.id.tv_position_switch);
		tvPathSkin = findViewByID(R.id.tv_bg_skin_path);
		tvSkinName = findViewByID(R.id.tv_bg_skin);
		tvBtnSkinAdd = findViewByID(R.id.btn_skin_choose);
		sokoView = findViewByID(R.id.sokoView1);
		sokoViewCover=findViewByID(R.id.sokoView2);
		
		tvSkinCoverSwitch=findViewByID(R.id.tv_skin_cover_switch);
		
		int testData[][] = new int[1][7];
		for (int i = 0; i < 7; i++) {
			testData[0][i] = i + 1;
		}
		sokoView.setArrDatas(testData);
		
		
		int coverData[][]={{ID_WALL,ID_WALL},{ID_WALL,ID_WALL}};
		
		sokoViewCover.setArrDatas(coverData);
		
		sBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				config.setPgVar(arg1);
				initUI();
			}
		});
		initUI();
	}

	SettingConfig config = SettingConfig.getInstence();

	private void initUI() {

		sBar.setProgress(config.getProgressVar());
		tvSpeed.setText("1秒$步".replace("$", (config.getProgressVar() + 1) + ""));
		tv_bg_style.setText(config.getBgName());
		tvAdd.setVisibility(View.INVISIBLE);
		switch (config.getBgStyle()) {
		case 0:
		case 1:
			break;
		case 2:// 图片背景
			tvAdd.setVisibility(View.VISIBLE);
			tvAdd.setText("选择图片");
			break;
		case 3:
			tvAdd.setText("选择颜色");
			tvAdd.setVisibility(View.VISIBLE);
			break;
		}

		config.setBgWithType(viewRootView);

		tvPositionSwitch.setText(config.isPosition ? "开启" : "关闭");
		
		tvSkinCoverSwitch.setText(config.isSkinCover?"开启":"关闭");
		


		tvPathSkin.setText(config.skinStyle != 0 ? (config.getSkinPath() == null ? "自定义路径无效" : config.getSkinPath()) : "默认皮肤路径");

		tvBtnSkinAdd.setVisibility(config.skinStyle != 0 ? View.VISIBLE : View.INVISIBLE);

		tvSkinName.setText(config.getSkinName());
		sokoView.init();
		sokoView.invalidate();
		
		sokoViewCover.init();
		sokoViewCover.invalidate();
		

	}

	@Override
	protected int setViewId() {
		return R.layout.activity_setting;
	}

	@Override
	protected String getPageName() {
		return null;
	}

	@Override
	public int[] setClickID() {
		return new int[] {R.id.tv_position_switch,R.id.back, R.id.ll_sytle, R.id.btn_add, R.id.ll_position, R.id.ll_skin, R.id.btn_skin_choose, R.id.ll_skin_cover ,R.id.tv_skin_cover_switch};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		super.onActivityResult(requestCode, resultCode, arg2);
		if (resultCode == RESULT_OK) {

			if (requestCode == REQUSTCODE) {
				String sssString = arg2.getStringExtra(EXTRA1);
				config.setUserImg(sssString);
			} else if (requestCode == REQUSTCODE_FOLDER) {
				String sssString = arg2.getStringExtra(EXTRA1);
				Bitmap[] bitmaps = ConstData.getUserSkinFromFile(sssString);
				if (bitmaps != null) {
					ConstData.setBitmaps(bitmaps);
					config.setSkinPath(sssString);
				} else {
					config.setSkinPath(null);
				}
				initUI();
			}
		}

	}
}
