package com.hdyl.pushbox;//package com.hdyl.pushbox;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.TextView;
//
//public class LoadingDialog extends Dialog implements OnClickListener {
//
//	TextView textView;
//
//	public LoadingDialog(Context context, boolean cancelable) {
//		super(context, R.style.my_dialog);
//		this.setCancelable(cancelable);
//		setContentView(R.layout.dialog_loading);
//		textView = (TextView) findViewById(R.id.textView1);
//		findViewById(R.id.button1).setOnClickListener(this);
//
//	}
//
//	public LoadingDialog(Context context) {
//		this(context, false);
//	}
//
//	public LoadingDialog(Context context, String msg) {
//		this(context, false);
//		textView.setText(msg);
//	}
//
//	public LoadingDialog(Context context, String title, String left, String right, View.OnClickListener clickListener) {
//		this(context, false);
//		textView.setText(title);
//		Button view001 = (Button) findViewById(R.id.button1);
//		view001.setOnClickListener(clickListener);
//		view001.setText(left);
//		Button view002 = (Button) findViewById(R.id.button2);
//		view002.setOnClickListener(clickListener);
//		view002.setVisibility(View.VISIBLE);
//		view002.setText(right);
//	}
//
//	public void setTvText(String msg) {
//		textView.setText(msg);
//	}
//
//	@Override
//	public void onClick(View arg0) {
//		switch (arg0.getId()) {
//		case R.id.button1:
//			dismiss();
//			break;
//		case R.id.button2:
//			dismiss();
//			break;
//		}
//	}
//}
