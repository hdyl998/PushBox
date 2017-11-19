package com.hdyl.pushbox.file;

import java.util.Collections;
import java.util.List;

import com.hdyl.pushbox.soko.FileListItem;

public class LocalSavedItem implements Comparable<LocalSavedItem> {

	public String date;// 导入的时间
	public String localPath;// 导入的路径
	public String name;// 名字，创建文件存储文件 的依据
	public String showName;// 显示的名字，初始时与名字相同但可以更改
	public String typeString;

	public boolean isShowTitle;

	@Override
	public int compareTo(LocalSavedItem arg0) {
		return arg0.typeString.compareTo(typeString);
	}

	public static void showTitleSortHand(List<LocalSavedItem> listItems) {
		Collections.sort(listItems);
		String type = null;
		for (LocalSavedItem item : listItems) {
			if (!item.typeString.equals(type)) {
				type = item.typeString;
				item.isShowTitle = true;
			} else {
				item.isShowTitle = false;
			}
		}
	}
}
