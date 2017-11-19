package com.hdyl.pushbox.soko;

public class FileListItem {

	public String typeString;// 归类 经典关卡 内置初级关卡 内置普通关卡 导入关卡

	public int type;// 0 1 2 3 4
	public String name;// 标题
	public String fileString;// 文件名

	public FileListItem(int type, String name, String fileName) {
		this.type = type;
		this.name = name;
		this.fileString = fileName;// 文件所在的位置
		this.typeString=titless[type];
	}
	public final static String titless[]={"内置经典关卡","内置初级关卡","内置普通关卡","内置已解关卡","Box关卡","用户输入关卡"};

}
