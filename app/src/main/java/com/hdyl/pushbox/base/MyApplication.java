package com.hdyl.pushbox.base;

import android.app.Application;

public class MyApplication extends Application {

	public static MyApplication instance;
//	private RequestQueue requestQueue;
//	public DisplayImageOptions options;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
//		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
//		ImageLoader.getInstance().init(config);
//		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.ic_launcher) // 设置图片下载期间显示的图片
//				.showImageForEmptyUri(R.drawable.ic_launcher) // 设置图片Uri为空或是错误的时候显示的图片
//				.showImageOnFail(R.drawable.ic_launcher) // 设置图片加载或解码过程中发生错误显示的图片
//				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
//				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
//				// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
//				.build(); // 创建配置过得DisplayImageOption对象
//		getScreenHW(this);
	}
//
//	public RequestQueue getRequestQueue() {
//		if (requestQueue == null)
//			requestQueue = Volley.newRequestQueue(instance);// 新建队列容器
//		return requestQueue;
//	}

	public static MyApplication getInstance() {
		return instance;
	}
//
//	public void getScreenHW(Context context) {
//		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		// Display display = manager.getDefaultDisplay();
//		// int width =display.getWidth();
//		// int height=display.getHeight();
//
//		DisplayMetrics dm = new DisplayMetrics();
//		manager.getDefaultDisplay().getMetrics(dm);
//		int width2 = dm.widthPixels;
//		int height2 = dm.heightPixels;
//		WIDTH = width2;
//	}
//
//	public static int WIDTH = 0;

}
