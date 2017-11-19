package com.hdyl.pushbox.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.hdyl.pushbox.R;
import com.hdyl.pushbox.setting.SettingConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;

public abstract class BaseActivity2 extends FragmentActivity {

	protected Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(null);
		mContext = this;
		MobclickAgent.setScenarioType(mContext, EScenarioType.E_UM_NORMAL);
	}

	protected String getPageName() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected void onResume() {
		super.onResume();
		View view = findViewById(R.id.rootview);
		if (view != null)
			SettingConfig.getInstence().setBgWithType(view);

		MobclickAgent.onPageStart(getPageName());
		MobclickAgent.onResume(mContext);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getPageName());
		MobclickAgent.onPause(mContext);
	}

}
