package com.hdyl.pushbox.base;

import android.R.anim;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;

import com.hdyl.pushbox.R;

public class FragmentHolderActivity extends BaseActivity {

	Fragment fragment;

	@Override
	protected void initData() {
		Intent intent = getIntent();
		Class<?> class1 = (Class<?>) intent.getSerializableExtra(ID_MAIN);
		try {
			fragment = (Fragment) class1.newInstance();
			FragmentManager manager = getSupportFragmentManager();
			manager.beginTransaction().add(android.R.id.content, fragment).commit();
		} catch (Exception e) {
		}
	}

	@Override
	protected int setViewId() {
		return 0;
	}
}
