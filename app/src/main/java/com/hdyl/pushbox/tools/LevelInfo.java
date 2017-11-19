package com.hdyl.pushbox.tools;

public class LevelInfo {

	public int level;
	public String levelString;
	public int bestStep = 99999;

	public boolean isPass = false;

	public boolean canOpen = false;

	public boolean canOpenOrPass() {
		return canOpen || isPass;
	}

	@Override
	public String toString() {
		return "LevelInfo [level=" + level + ", levelString=" + levelString + ", bestStep=" + bestStep + ", isPass=" + isPass + "]";
	}

}
