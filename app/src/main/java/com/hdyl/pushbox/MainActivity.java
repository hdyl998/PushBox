package com.hdyl.pushbox;

import java.util.Timer;
import java.util.TimerTask;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.hdyl.pushbox.base.BaseActivity;

public class MainActivity extends BaseActivity implements OnClickListener {

	GameView gameView;
	TextView tvLevel, tvStep, tvBest;
	// MediaPlayer mp;

	boolean isPause = false;

	public void setLevel(int level) {
		tvLevel.setText("" + level);
	}

	public void setStep(int step) {
		tvStep.setText("" + step);
	}

	public void setBest(int best) {
		if (best == 99999) {
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
		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.textViewBack:
			gameView.backOneStep();
			break;
		case R.id.textViewRefresh:
			gameView.newGame();
			break;
		case R.id.textViewExit:
			finish();
			// gameView.nextLevel();
			break;
		}
	}

	@Override
	protected void initData() {
		// indexType=getIntent().getExtras().getInt("index");

		tvLevel = (TextView) findViewById(R.id.tv_level);

		tvStep = (TextView) findViewById(R.id.tv_highscore);

		tvBest = (TextView) findViewById(R.id.tv_best);

		// findViewById(R.id.tv_up).setOnClickListener(this);
		findViewById(R.id.textViewRefresh).setOnClickListener(this);
		findViewById(R.id.textViewExit).setOnClickListener(this);
		findViewById(R.id.textViewBack).setOnClickListener(this);

		gameView = (GameView) findViewById(R.id.gameView);

		int leve = getIntent().getExtras().getInt("level");

		gameView.setLevel(leve);

	}

	@Override
	protected int setViewId() {
		return R.layout.activity_main;
	}

	@Override
	protected String getPageName() {
		return "��Ϸ��ҳ";
	}
}
