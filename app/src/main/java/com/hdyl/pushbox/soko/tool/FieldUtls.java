package com.hdyl.pushbox.soko.tool;

import java.lang.reflect.Field;

public class FieldUtls {
	public static Object getFieldValue(Object obj, String fieldName) throws Exception {
		Field f = obj.getClass().getDeclaredField(fieldName);
		f.setAccessible(true);
		return f.get(obj);
	}

	public static void setFieldValue(Object obj, String fieldName, Object fieldValue) throws Exception {
		Field f = obj.getClass().getDeclaredField(fieldName);
		f.setAccessible(true);
		f.set(obj, fieldValue);
	}

	// 前提是该Class的static块初始化不能有问题，否则会报找不到Class
	// 获取静态属性字段
	public static Object getFieldValue(Class c, String fieldName) throws Exception {
		Field f = c.getDeclaredField(fieldName);
		f.setAccessible(true);
		return f.get(null);
	}

	// 设置静态属性字段值
	public static void setFieldValue(Class c, String fieldName, Object fieldValue) throws Exception {
		Field f = c.getDeclaredField(fieldName);
		f.setAccessible(true);
		f.set(null, fieldValue);
	}
}
