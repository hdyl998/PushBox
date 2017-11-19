package com.hdyl.pushbox.setting;

import u.aly.co;

import com.hdyl.pushbox.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ColorPickDialog extends Dialog {
	SeekBar seekBarRed;
	SeekBar seekBarBlue;
	SeekBar seekBarGreen;
	SettingConfig config = SettingConfig.getInstence();
	View viewExample;

	TextView tvRed, tvBlue, tvGreen;

	View activityView;
	
	public ColorPickDialog(Context context,View activityView) {
		super(context);
		this.setCanceledOnTouchOutside(false);
		this.setCancelable(true);
		
		this.activityView=activityView;
		this.setTitle("颜色选择");
		setContentView(R.layout.dialog_color_picker);

		
		seekBarRed = (SeekBar) findViewById(R.id.seekBarRed);

		seekBarGreen = (SeekBar) findViewById(R.id.seekBarGreen);

		seekBarBlue = (SeekBar) findViewById(R.id.seekBarBlue);

		tvRed = (TextView) findViewById(R.id.tv_red);
		tvBlue = (TextView) findViewById(R.id.tv_blue);
		tvGreen = (TextView) findViewById(R.id.tv_green);

		seekBarRed.setProgress(config.colorRed);

		seekBarGreen.setProgress(config.colorGreen);
		seekBarBlue.setProgress(config.colorBlue);

		tvRed.setText(config.colorRed + "");
		tvBlue.setText(config.colorBlue + "");
		tvGreen.setText(config.colorGreen + "");
		
		
		seekBarBlue.setOnSeekBarChangeListener(onSeekBarChangeListener);
		seekBarGreen.setOnSeekBarChangeListener(onSeekBarChangeListener);
		seekBarRed.setOnSeekBarChangeListener(onSeekBarChangeListener);

		viewExample = findViewById(R.id.tv_example);
		config.setBgWithType(viewExample);

		findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});
	}

	private OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {

		}

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			config.colorRed = seekBarRed.getProgress();
			config.colorBlue = seekBarBlue.getProgress();
			config.colorGreen = seekBarGreen.getProgress();

			tvRed.setText(config.colorRed + "");
			tvBlue.setText(config.colorBlue + "");
			tvGreen.setText(config.colorGreen + "");

			config.setBgWithType(viewExample);
			
			config.setBgWithType(activityView);
		}
	};

}
