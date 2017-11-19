package com.hdyl.pushbox.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import u.aly.da;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hdyl.pushbox.base.MyApplication;

import dalvik.system.DexFile;

public class Tools {

	@SuppressLint("SimpleDateFormat")
	public static String getDistenceTime(String time) {
		try {
			SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date begin = dfs.parse(time);
			Date end = new Date();
			long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒
			long day1 = between / (24 * 3600);// 得到天数
			long hour1 = between % (24 * 3600) / 3600;
			long minute1 = between % 3600 / 60;
			long second1 = between % 60;

			if (day1 > 30) {
				return time.substring(0, 10);
			}
			if (day1 >= 1) {
				return day1 + "天前";
			}
			if (hour1 >= 1) {
				return hour1 + "小时前";
			}
			if (minute1 >= 1) {
				return minute1 + "分钟前";
			}
			return second1 + "秒前";
		} catch (Exception e) {
		}

		return "刚刚";
	}

	public static List<String> getClassName(String packageName) {
		List<String> classNameList = new ArrayList<String>();
		try {

			DexFile df = new DexFile(MyApplication.instance.getPackageCodePath());// 通过DexFile查找当前的APK中可执行文件
			Enumeration<String> enumeration = df.entries();// 获取df中的元素
															// 这里包含了所有可执行的类名
															// 该类名包含了包名+类名的方式
			while (enumeration.hasMoreElements()) {// 遍历
				String className = (String) enumeration.nextElement();

				if (className.contains(packageName)) {// 在当前所有可执行的类里面查找包含有该包名的所有类
					classNameList.add(className);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classNameList;
	}

	public static String getDateFormatString(String dateString, String format) {
		if (dateString == null)
			return "";
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
			SimpleDateFormat sf = new SimpleDateFormat(format);
			return sf.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getNowTimeString() {
		try {
			Date date = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sf.format(date);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public static int string2Integer(String str) {
		if (str == null)
			return 0;
		try {
			if (str.startsWith("+")) {
				str = str.substring(1);
			}

			return Integer.parseInt(str);
		} catch (NumberFormatException e) {// 转换失败
			// e.printStackTrace();
		}
		return 0;
	}

	public static float string2Float(String str) {
		if (str == null)
			return 0;
		try {
			return Float.parseFloat(str);
		} catch (NumberFormatException e) {// 转换失败
			// e.printStackTrace();
		}
		return 0.0f;
	}

	public static void makeToast(Context context, String msg) {
		Toast.makeText(context, msg, 0).show();
	}

	public static String getOnePiece(String data, String from, String end, int index) {
		String result = "";
		StringBuffer reStr = new StringBuffer();
		reStr.append(from + "(.*?)" + end);

		// reStr.append("<(\\S*?) [^>/]*>.*?</\1>|<.*? />");

		Pattern pattern = Pattern.compile(reStr.toString());
		Matcher matcher = pattern.matcher(data);
		int count = 0;
		while (matcher.find()) {
			count++;
			if (count == index)
				return matcher.group(1);
		}
		return result;
	}

	public static String getParameter(String data, String para) {
		String result = "";
		StringBuffer reStr = new StringBuffer();
		reStr.append("<");
		reStr.append(para);
		reStr.append(" [^>]*>");
		reStr.append("(.*?)");
		reStr.append("</");
		reStr.append(para);
		reStr.append(">");
		// reStr.append("<(\\S*?) [^>/]*>.*?</\1>|<.*? />");

		Pattern pattern = Pattern.compile(reStr.toString());
		Matcher matcher = pattern.matcher(data);

		while (matcher.find()) {
			String string = data.substring(matcher.start(), matcher.end());
			System.out.println(string);
		}
		return result;
	}

	public static String getOnePiece(String data, String from, String end) {
		String result = "";
		StringBuffer reStr = new StringBuffer();
		reStr.append(from + "(.*?)" + end);

		// reStr.append("<(\\S*?) [^>/]*>.*?</\1>|<.*? />");

		Pattern pattern = Pattern.compile(reStr.toString());
		Matcher matcher = pattern.matcher(data);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return result;
	}

	public static String stringArray2Object(String source, Class<? extends Object> cls) {
		Field[] flds = cls.getFields();// 得到字段
		for (Field field : flds) {
			Class<? extends Object> clazz = field.getType();
			if (clazz != String.class && clazz != int.class && clazz != List.class) {
				String name = field.getName();// 得到字段名
				source = source.replace("\"" + name + "\":[]", "\"" + name + "\":{}");
			}
		}
		return source;
	}

	public static boolean isObjectEmpty(Object object) {
		return object == null || JSON.toJSONString(object).length() <= 2 || object.toString().replace(" ", "").length() <= 2;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static String download(String urlStr) {
		System.out.println("下载开始：" + urlStr);
		try {
			/*
			 * 通过URL取得HttpURLConnection 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
			 * <uses-permission android:name="android.permission.INTERNET" />
			 */
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// conn.setRequestProperty("contentType", "GBK");
			conn.setConnectTimeout(10 * 1000);
			// conn.setRequestMethod("GET");
			// 取得inputStream，并进行读取
			InputStream input = conn.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(input, "utf-8"));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return String
	 * @方法名: getVerName
	 * @功能描述:获得APK版本名称
	 */
	public static String getVerName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
