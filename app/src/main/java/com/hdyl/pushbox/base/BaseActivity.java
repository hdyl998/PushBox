package com.hdyl.pushbox.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public abstract class BaseActivity extends BaseActivity2 implements OnClickListener, Consts, IMyHelper {

	protected abstract int setViewId();

	protected BaseActivity mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		if (setViewId() != 0) {
			setContentView(setViewId());
		} else if (setView() != null) {
			setContentView(setView());
		}
		findView();
		initData();
		initListener();
	}

	protected View setView() {
		return null;
	}

	protected void findView() {

	}

	@SuppressWarnings("unchecked")
	protected <T extends View> T findViewByID(int id) {
		return (T) findViewById(id);
	}

	protected abstract void initData();

	public void onClick(View view) {
	}

	public int[] setClickID() {
		return null;
	}

	private void initListener() {
		int ids[] = setClickID();
		if (ids != null) {
			for (int i : ids) {
				findViewById(i).setOnClickListener(this);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	IMyHelperImpl myHelperImpl;

	private IMyHelperImpl getHelperImpl() {
		if (myHelperImpl == null) {
			myHelperImpl = new IMyHelperImpl(mContext);
		}
		return myHelperImpl;
	}

	public void startNewActivity(Class<? extends BaseFragment> clazz, String... strings) {
		getHelperImpl().startNewActivity(clazz, strings);
	}

	public void startNewActivityForRestlt(Class<? extends BaseFragment> clazz, int requestCode, String... strings) {
		getHelperImpl().startNewActivityForRestlt(clazz, requestCode, strings);
	}

	public void startActivity(Class<? extends Activity> clazz) {
		getHelperImpl().startActivity(clazz);
	}

	public void startActivityForRestlt(Class<? extends Activity> clazz, int requestCode) {
		getHelperImpl().startActivityForRestlt(clazz, requestCode);
	}

	public void showLoadingDialog() {
		getHelperImpl().showLoadingDialog();
	}

	public void showLoadingDialog(String message) {
		getHelperImpl().showLoadingDialog(message);
	}

	public void hideLoadingDialog() {
		getHelperImpl().hideLoadingDialog();
	}

	// public String download(String urlStr) {
	// System.out.println("下载开始：" + urlStr);
	// try {
	// /*
	// * 通过URL取得HttpURLConnection 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
	// * <uses-permission android:name="android.permission.INTERNET" />
	// */
	// URL url = new URL(urlStr);
	// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	// // conn.setRequestProperty("contentType", "GBK");
	// conn.setConnectTimeout(10 * 1000);
	//
	// conn.setRequestProperty("Content-Type",
	// "application/x-www-form-urlencoded");
	// //
	// conn.setRequestProperty("Connection", "keep-alive");
	// conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
	//
	// // conn.geti
	// //
	// // String contentEncoding = conn.getHeader("Content-Encoding");
	// // // 如果对内容进行了压缩，则解压
	// // if (null != contentEncoding && contentEncoding.indexOf("gzip") !=
	// // -1) {
	// // try {
	// // final GZIPInputStream gzipInputStream = new GZIPInputStream(
	// // stream);
	// //
	//
	// // conn.setRequestMethod("GET");
	// // 取得inputStream，并进行读取
	// InputStream input = conn.getInputStream();
	// BufferedReader in = new BufferedReader(new InputStreamReader(input,
	// "gbk"));
	// String line = null;
	// StringBuilder sb = new StringBuilder();
	// while ((line = in.readLine()) != null) {
	// sb.append(line);
	// }
	// LogUtils.Print(sb.toString());
	//
	// return sb.toString();
	//
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }

}
