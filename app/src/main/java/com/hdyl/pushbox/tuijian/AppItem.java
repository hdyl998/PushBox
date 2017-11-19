package com.hdyl.pushbox.tuijian;

public class AppItem {

	public int icon;
	public String name;
	public String introduce="益智休闲";
	public String url;
	public AppItem(int icon, String name, String introduce, String url) {
		super();
		this.icon = icon;
		this.name = name;
		this.introduce = introduce;
		this.url = url;
	}


	public AppItem(){
		super();
	}
}
