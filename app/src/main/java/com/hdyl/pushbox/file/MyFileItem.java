package com.hdyl.pushbox.file;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class MyFileItem implements Comparable<MyFileItem> {

	public boolean isFile = false;// 是否是文件
	public String name;
	public String absPath;
	public boolean isFirst = false;

	@Override
	public int compareTo(MyFileItem another) {
		if (isFirst)
			return 1;

		if (this.isFile == another.isFile) {
			Comparator cmp = Collator.getInstance(Locale.CHINA);
			return cmp.compare(name, another.name);
		}

		if (this.isFile) {
			return 1;
		}
		return -1;
	}

}
