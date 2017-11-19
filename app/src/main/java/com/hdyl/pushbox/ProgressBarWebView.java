package com.hdyl.pushbox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/***
 * 甯﹁繘搴︽潯鐨刉EBVIEW
 */
public class ProgressBarWebView extends WebView {

	private Context context;

	public ProgressBarWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ProgressBarWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ProgressBarWebView(Context context) {
		super(context);
		init(context);
	}

	private ProgressBar dialogView;// 绛夊緟瀵硅瘽锟??
	private onPageFinishedListener mPageFinshedListener;

	public interface onPageFinishedListener {
		void onlivePageFinished(View v);
	}

	/**
	 * 鐩戝惉缃戦〉鍔犺浇瀹屾瘯
	 * */
	public void setPageFinishListener(onPageFinishedListener mlistener) {
		this.mPageFinshedListener = mlistener;
	}

	/**
	 * 鏂规硶锟?? init
	 * 
	 * 鍔熻兘鎻忚堪:鍒濆锟??
	 * 
	 * @param context
	 *            涓婁笅鏂囧锟??
	 * @return void
	 * 
	 *         </br>throws
	 */
	@SuppressWarnings("deprecation")
	private void init(final Context context) {
		this.context = context;
		this.getSettings().setJavaScriptEnabled(true);
		// 璁剧疆鍙互鏀寔缂╂斁
		this.getSettings().setSupportZoom(true);
		// 璁剧疆鍑虹幇缂╂斁宸ュ叿
		this.getSettings().setBuiltInZoomControls(true);
		this.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		this.getSettings().setLoadWithOverviewMode(true);
		// this.setInitialScale();// 为25%，最小缩放等级
		this.clearCache(true);
		dialogView = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
		dialogView.setIndeterminate(true);
		dialogView.setIndeterminateDrawable(getResources().getDrawable(android.R.color.holo_purple));
		this.addView(dialogView);
		dialogView.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
		dialogView.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, getResources().getDisplayMetrics());
		getSettings().setJavaScriptEnabled(true);// 鍙互浣跨敤javaScriptEnalsed
		getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		addJavascriptInterface(new JsObject(), "demo");
		setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				dialogView.setVisibility(View.VISIBLE);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				if (url.startsWith("tel:")) {// 鎵撶數锟??
					// Intent intent = new Intent(Intent.ACTION_CALL,
					// Uri.parse(url));
					// context.startActivity(intent);
				} else if (url.startsWith("sms:")) {// 鍙戠煭锟??
					Uri uri = Uri.parse("smsto:" + url.split(":")[1]);
					Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
					sendIntent.putExtra("sms_body", "");
					context.startActivity(sendIntent);
				} else {// 姝ｅ父澶勭悊url
					view.loadUrl(url);
				}
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				dialogView.setVisibility(View.GONE);
				if (mPageFinshedListener != null) {
					mPageFinshedListener.onlivePageFinished(view);
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				dialogView.setVisibility(View.GONE);
			}
		});
		setWebChromeClient(new MyWebChromeClient());// 璁剧疆娴忚鍣ㄥ彲寮圭獥
		
		
		this.setDownloadListener(new MyWebViewDownLoadListener());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (this.canGoBack()) {
				this.goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 娴忚鍣ㄥ彲寮圭獥
	 * 
	 * @author Administrator
	 * 
	 */
	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			if (dialogView != null && newProgress > 89) {
				dialogView.setVisibility(View.GONE);
			}
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
			new AlertDialog.Builder(context).setTitle("App Titler").setMessage(message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					result.confirm();
				}
			}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					result.cancel();
				}
			}).create().show();

			return true;
		}
	}

	/**
	 * js璋冪敤class
	 * 
	 * @author Administrator
	 * 
	 */
	class JsObject {// @JavascriptInterface鏄负浜嗘敮锟??.2鍙婁互涓婄殑js浜や簰,涓嶆敮锟??.0浠ヤ笂鐨凙ndroid绯荤粺.
		@JavascriptInterface
		public void loginsuc(final String code, final boolean flag) {
		}

		@JavascriptInterface
		public String toString() {
			return "demo";
		}
	}

	private class MyWebViewDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivity(intent);
		}

	}
}
