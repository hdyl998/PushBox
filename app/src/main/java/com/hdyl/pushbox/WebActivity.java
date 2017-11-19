package com.hdyl.pushbox;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

public class WebActivity extends Activity implements OnClickListener {

	WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
		setContentView(R.layout.activity_web);
		TextView textView = (TextView) findViewById(R.id.textViewa);
		webView = (WebView) findViewById(R.id.progressBarWebView1);
		String string = getIntent().getStringExtra("url");
		webView.loadUrl(string);
		String titldString = getIntent().getStringExtra("title");
		textView.setText(titldString);
		findViewById(R.id.textView3).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		finish();
	}
	// WebView valueView=UICreator.createFormFiledTextValue(view.getContext());
	// return valueView;
}
