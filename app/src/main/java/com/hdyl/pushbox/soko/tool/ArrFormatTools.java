package com.hdyl.pushbox.soko.tool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.graphics.Point;

import com.hdyl.pushbox.base.Consts;

public class ArrFormatTools implements Consts {
	public static int[][] getArrWithFormat(List<String> listString) {
		ArrFormatTools tools = new ArrFormatTools();
		int arr[][] = tools.getArr(listString);
		if (arr == null) {
			arr = new int[1][1];
		}
		tools.WIDTH1 = arr[0].length;
		tools.HEIGHT1 = arr.length;

		tools.changeInnerEmpty2Way(arr);
		return arr;
	}

	private  Point getManPoint(int arr[][])// 找到人
	{
		Point point = new Point();
		int i, j;
		for (i = 0; i < HEIGHT1; i++) {// ROW
			for (j = 0; j < WIDTH1; j++)
				// COL
				if (arr[i][j] == ID_MAN || arr[i][j] == ID_MAN_IN) {
					point.x = i;
					point.y = j;
					return point;
				}
		}
		return null;
	}

	// 把数据里面的空格，转换成道路
	private void changeInnerEmpty2Way(int arr[][]) {
		Point point = getManPoint(arr);
		if (point == null) {
			return;
		}
		Set<Point> checkedPoint = new HashSet<Point>();// 找过的点
		checkedPoint.add(point);
		Set<Point> from = new HashSet<Point>();
		from.add(point);// 先加一个

		while (true) {
			Set<Point> tempPoints = getHashSet(from, checkedPoint, arr);
			if (tempPoints.size() == 0) {
				break;
			}
			from = tempPoints;
		}

	}

	private Set<Point> getHashSet(Set<Point> from, Set<Point> checkedPoint, int arr[][]) {

		Iterator<Point> iterator = from.iterator();
		HashSet<Point> nextCheck = new HashSet<Point>();
		while (iterator.hasNext()) {
			Point cur = iterator.next();
			for (int r = cur.x - 1; r < cur.x + 2; r++)
				for (int c = cur.y - 1; c < cur.y + 2; c++)
					if (((cur.x + cur.y + r + c) % 2 != 0) && inArray(r, c)) {// 上、下、左、右四种情况
						Point point = new Point(r, c);
						if (arr[r][c] != ID_WALL && !checkedPoint.contains(point)) {
							nextCheck.add(point);
							checkedPoint.add(point);
							if (arr[r][c] == ID_BLACK) {
								arr[r][c] = ID_GROUND;
							}
						}
					}
		}
		return nextCheck;
	}

	public boolean inArray(int row, int col) {// 数组越界判断，越界为假，正常为真
		if (row >= HEIGHT1 || col >= WIDTH1 || row < 0 || col < 0)
			return false;
		return true;
	}

	public int WIDTH1;
	public int HEIGHT1;

	public int[][] getArr(List<String> listDatas) {
		int width = 0;
		int height = 0;

		height = listDatas.size();
		for (String ss : listDatas) {
			if (ss.length() > width) {
				width = ss.length();
			}
		}

		if (width == 0 || height == 0) {
			return null;
		}

		int arr[][] = new int[height][width];
		for (int i = 0; i < listDatas.size(); i++) {
			String string = listDatas.get(i);
			int j = 0;
			for (char cc : string.toCharArray()) {
				int var = ID_BLACK;
				switch (cc) {
				case S_MAN:// 人
					var = ID_MAN;
					break;
				case S_MAN_IN:// 人在目标
					var = ID_MAN_IN;
					break;
				case S_BOX:// 箱子
					var = ID_BOX;
					break;
				case S_BOX_IN:// 箱子在目标
					var = ID_BOX_IN;
					break;
				case S_WALL:// 墙
					var = ID_WALL;
					break;
				case S_POINT:// 目标点
					var = ID_POINT;
					break;
				case S_EMPTY:// 地板
				case S_EMPTY2:// 地板
				case S_EMPTY3:// 地板
					var = ID_BLACK;
					break;
				}
				arr[i][j] = var;
				j++;
			}
		}
		return arr;
	}
}
