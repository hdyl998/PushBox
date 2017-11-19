package com.hdyl.pushbox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hdyl.pushbox.base.ConstData;
import com.hdyl.pushbox.base.DialogCreator;
import com.hdyl.pushbox.tools.DatabaseHelper;
import com.hdyl.pushbox.tools.LevelInfo;

public class GameView extends View {

	Context context;

	MainActivity mainActivity;
	int currentStep = 0;// 当前步数

	boolean isGameWin = false;

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		 mainActivity = (MainActivity) (this.context = context);
	 }

	// int mScreenWidth, mScreenHeight;

	// public void configScreenSize(Context context) {
	// DisplayMetrics display = new DisplayMetrics();
	// WindowManager manager = (WindowManager)
	// context.getSystemService(Context.WINDOW_SERVICE);
	// manager.getDefaultDisplay().getMetrics(display);
	// mScreenHeight = display.heightPixels;//
	// // Math.max(display.heightPixels,display.widthPixels);
	// mScreenWidth = display.widthPixels;//
	// Math.min(display.heightPixels,display.widthPixels);
	// }

	Bitmap[] bitmaps;
	private int level = 1;

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	int size;

	public void nextLevel() {
		level++;
		newGame();
	}

	public void lastLevel() {
		level--;
		newGame();
	}

	static final int ID_BLACK = 0;
	static final int ID_WALL = 1;
	static final int ID_EMPTY = 2;
	static final int ID_POINT = 3;
	static final int ID_BOX = 4;
	static final int ID_MAN = 6;
	static final int ID_MAN_IN = 7;
	static final int ID_BOX_IN = 5;

	private void init() {

		bitmaps = ConstData.getBitmaps();

		// int ids[] = { R.drawable.p6,// 0//black
		// R.drawable.p1,// 1wall
		// R.drawable.p2,// 2way
		// R.drawable.p3,// 3point
		// R.drawable.p4,// 4box
		// R.drawable.p5,// 5boxin
		// R.drawable.p6,// 6man
		// R.drawable.p62 // 7manin
		// };
		// bitmaps = new Bitmap[ids.length];
		// for (int i = 0; i < bitmaps.length; i++) {
		// bitmaps[i] = BitmapFactory.decodeResource(getResources(), ids[i]);
		// }
	}

	LevelInfo levelInfo;
	int WIDTH = 18;
	int HEIGHT = 12;
	DatabaseHelper dHelper;

	int yOffSet = 0;
	int xOffSet = 0;

	public void newGame() {
		isGameWin = false;
		currentStep = 0;
		if (dHelper == null)
			dHelper = new DatabaseHelper(context);
		levelInfo = dHelper.selectInfos(level);
		if (levelInfo != null) {
			mainActivity.setLevel(level);
			mainActivity.setStep(currentStep);
			mainActivity.setBest(levelInfo.bestStep);
			String levString = levelInfo.levelString;

			if (levelInfo.level > 100) {
				// Log.e("aa", levString);
				String aaa[] = levString.split(",");
				HEIGHT = Integer.parseInt(aaa[1]);
				WIDTH = Integer.parseInt(aaa[2]);
				levString = aaa[3];

				int a = 0;
				arr = new int[HEIGHT][WIDTH];
				for (int i = 0; i < HEIGHT; i++)
					for (int j = 0; j < WIDTH; j++) {
						{
							arr[i][j] = levString.charAt(a++) - '0';
						}
					}
				arr = clearArrOfBlack(arr);
				changeArr(arr);

			} else {
				arr = new int[12][18];
				int a = 0;
				for (int j = 0; j < 18; j++) {
					for (int i = 0; i < 12; i++) {
						arr[12 - i - 1][18 - j - 1] = levString.charAt(a++) - '0';
					}
				}
				// 57关的问题
				if (level == 57) {
					int tmp = arr[7][5];
					arr[7][5] = arr[5][13];
					arr[5][13] = tmp;
				}
				arr = clearArrOfBlack(arr);
			}

		} else {
			arr = null;
		}
		size = Math.min(width / HEIGHT, height / WIDTH);
		yOffSet = (height - size * WIDTH) / 2;
		xOffSet = (width - size * HEIGHT) / 2;
		stepDatas.clear();
		invalidate();
	}

	int arr[][];

	private void changeArr(int arr[][]) {

		for (int i = 0; i < HEIGHT; i++) {
			boolean isIn = false;
			int index = getRowIndex(i);
			if (index != -1)
				for (int j = 0; j < WIDTH && j < index; j++) {
					{
						if (arr[i][j] != 0 && isIn == false) {
							isIn = true;
						}
						if (isIn) {
							if (arr[i][j] == 0) {
								arr[i][j] = ID_EMPTY;
							}
						}
					}
				}
		}

		for (int i = 0; i < HEIGHT; i++) {
			boolean isIn = false;
			int index = getRowIndex2(i);
			if (index != -1)
				for (int j = WIDTH - 1; j >= 0 && j > index; j--) {
					{
						if (arr[i][j] != 0 && isIn == false) {
							isIn = true;
						}
						if (isIn) {
							if (arr[i][j] == 0) {
								arr[i][j] = ID_EMPTY;
							}
						}
					}
				}
		}
		if (isBoxNearEmpty()) {
			for (int i = 0; i < HEIGHT; i++) {
				for (int j = 0; j < WIDTH; j++) {
					{
						if (arr[i][j] == ID_BLACK) {
							arr[i][j] = ID_EMPTY;
						}
					}
				}
			}
		}

	}

	private boolean isBoxNearEmpty() {
		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0; j < WIDTH; j++) {
				{
					if (arr[i][j] == ID_BOX) {
						int aa[] = { i + 1, i + 1, i - 1, i - 1 };
						int bb[] = { j + 1, j - 1, j + 1, j - 1 };
						for (int cc = 0; cc < 4; cc++) {
							if (inArray(aa[cc], bb[cc])) {
								if (arr[aa[cc]][bb[cc]] == ID_BLACK) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	/***
	 * 清除掉里面的全是空格的问题
	 * 
	 * @param arr
	 * @return
	 */
	public int[][] clearArrOfBlack(int arr[][]) {
		WIDTH = arr[0].length;
		HEIGHT = arr.length;
		int starti = 0, endi = HEIGHT, startj = 0, endj = WIDTH;
		for (int j = 0; j < WIDTH; j++) {
			boolean is = isColEmpty(arr, j);
			if (is == false) {
				startj = j;
				break;
			}
		}

		for (int j = WIDTH - 1; j >= 0; j--) {
			boolean is = isColEmpty(arr, j);
			if (is == false) {
				endj = j;
				endj++;
				break;
			}
		}

		for (int i = 0; i < HEIGHT; i++) {
			boolean is = isRowEmpty(arr, i);
			if (is == false) {
				starti = i;
				break;
			}
		}

		for (int i = HEIGHT - 1; i >= 0; i--) {
			boolean is = isRowEmpty(arr, i);
			if (is == false) {
				endi = i;
				endi++;
				break;
			}
		}

		int aaa[][] = new int[endi - starti][endj - startj];
		for (int aa = startj; aa < endj; aa++)
			for (int bb = starti; bb < endi; bb++) {
				aaa[bb - starti][aa - startj] = arr[bb][aa];
			}
		WIDTH = aaa[0].length;
		HEIGHT = aaa.length;
		return aaa;
	}

	private boolean isRowEmpty(int arr[][], int row) {
		for (int i = 0; i < WIDTH; i++) {
			if (arr[row][i] != 0) {
				return false;
			}
		}
		return true;
	}

	private boolean isColEmpty(int arr[][], int col) {
		for (int i = 0; i < HEIGHT; i++) {
			if (arr[i][col] != 0) {
				return false;
			}
		}
		return true;
	}

	private int getRowIndex(int row) {
		for (int i = WIDTH - 1; i >= 0; i--) {
			if (arr[row][i] == ID_WALL) {
				return i;
			}
		}
		return -1;
	}

	private int getRowIndex2(int row) {
		for (int i = 0; i < WIDTH; i++) {
			if (arr[row][i] == ID_WALL) {
				return i;
			}
		}
		return -1;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (arr != null) {

			for (int i = 0; i < HEIGHT; i++) {
				for (int j = 0; j < WIDTH; j++) {
					int var = arr[i][j];
					if (var != ID_BLACK) {
						canvas.drawBitmap(bitmaps[var], null, new Rect(i * size + xOffSet, j * size + yOffSet, i * size + size + xOffSet, j * size + size + yOffSet), null);
					}/*
					 * else { canvas.drawBitmap(bitmaps[ID_WALL], null, new
					 * xOffSet, i * size + size), null); }
					 */
				}
			}
		}
	}

	private boolean isWin()// 是否完成
	{
		int i, j;
		for (i = 0; i < HEIGHT; i++)
			for (j = 0; j < WIDTH; j++)
				if (arr[i][j] == ID_POINT || arr[i][j] == ID_MAN_IN)
					return false;
		isGameWin = true;
		return true;
	}

	public boolean inArray(int row, int col) {// 数组越界判断，越界为假，正常为真
		if (row >= HEIGHT || col >= WIDTH || row < 0 || col < 0)
			return false;
		return true;
	}

	public Cell findCell(Set<Cell> from, Set<Cell> next, int arr[][]) {// 在第一set中找它的四周的单元
		Iterator<Cell> it = from.iterator();
		Cell cur;
		Cell cell;
		while (it.hasNext()) {
			cur = it.next();
			for (int r = cur.row - 1; r < cur.row + 2; r++)
				for (int c = cur.col - 1; c < cur.col + 2; c++)
					if (((cur.row + cur.col + r + c) % 2 != 0) && inArray(r, c)) {// 上、下、左、右四种情况
						if (arr[r][c] == Flag.EndPoint) {
							cell = new Cell(r, c, cur);
							return cell;
						} else if (arr[r][c] == Flag.Empty) {
							cell = new Cell(r, c, cur);
							arr[r][c] = Flag.FindWay;
							next.add(cell);
						}
					}
		}
		return null;
	}

	/**
	 * 
	 * @param row1起点坐标
	 * @param col1
	 * @param row2终点坐标
	 * @param col2
	 * @return
	 */
	public Cell findWayBySearch(int row1, int col1, int row2, int col2) {// 通过遍历的方法查找路
		int[][] arr = new int[HEIGHT][WIDTH];// 复制一下原来的数组
		for (int i = 0; i < HEIGHT; i++)
			for (int j = 0; j < WIDTH; j++) {
				arr[i][j] = this.arr[i][j];
				if (arr[i][j] == ID_POINT) {// 特殊处理
					arr[i][j] = Flag.Empty;
				}
			}
		Set<Cell> set = new HashSet<Cell>();
		arr[row1][col1] = Flag.Empty;
		arr[row2][col2] = Flag.EndPoint;// 终点目标
		Cell cell = new Cell(row1, col1, null);// 起点
		set.add(cell);
		Set<Cell> nextSet;
		while (true) {
			nextSet = new HashSet<Cell>();
			Cell c = findCell(set, nextSet, arr);
			if (c != null) {
				System.out.println("找到解！");
				return c;
			}
			if (nextSet.isEmpty()) {
				System.out.println("无解！");
				return null;
			}
			set = nextSet;
		}
	}

	static class MyData {
		int xOffset, yOffset;

		Point point;

		@Override
		public String toString() {
			return "MyData [xOffset=" + xOffset + ", yOffset=" + yOffset + ", point=" + point + "]";
		}

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			MyData myData = (MyData) msg.obj;
			// Log.e("aa", myData + "");
			pushDirection(myData.point, myData.xOffset, myData.yOffset);
		}
	};

	public void pushDirection(Point p0, int xOffset, int yOffset) {
		int x = p0.x + xOffset;
		int y = p0.y + yOffset;

		Point p2 = new Point(p0.x + 2 * (x - p0.x), p0.y + 2 * (y - p0.y));// 下个点

		if (!inArray(x, y)) {
			return;
		}

		int start = arr[p0.x][p0.y];
		int current = arr[x][y];
		int next = 0;
		if (inArray(p2.x, p2.y)) {// 在界内
			next = arr[p2.x][p2.y];
		} else {
			next = -1;// 越界
		}
		boolean isChange = false;
		if (current == ID_EMPTY) {
			isChange = true;
			arr[x][y] = ID_MAN;
		} else if (current == ID_POINT) {
			isChange = true;
			arr[x][y] = ID_MAN_IN;
		} else if (current == ID_BOX) {// 箱子可可移动箱子
			if (next == ID_EMPTY) {// 空
				isChange = true;
				arr[x][y] = ID_MAN;
				arr[p2.x][p2.y] = ID_BOX;
			} else if (next == ID_POINT) {// 目标点
				isChange = true;
				arr[x][y] = ID_MAN;
				arr[p2.x][p2.y] = ID_BOX_IN;
			}
		} else if (current == ID_BOX_IN) {// 目标
			if (next == ID_EMPTY) {
				isChange = true;
				arr[x][y] = ID_MAN_IN;
				arr[p2.x][p2.y] = ID_BOX;
			} else if (next == ID_POINT) {
				isChange = true;
				arr[x][y] = ID_MAN_IN;
				arr[p2.x][p2.y] = ID_BOX_IN;
			}
		}
		if (isChange == true) {// 发生了改变
			// 入栈
			StepData stepData = new StepData(start, current, next, p0.x, p0.y, x, y, p2.x, p2.y);
			stepDatas.add(stepData);

			arr[p0.x][p0.y] = start == ID_MAN ? ID_EMPTY : ID_POINT;
			currentStep++;
			mainActivity.setStep(currentStep);
			invalidate();
			if (isWin()) {

				if (levelInfo.isPass == false) {
					LevelActivity.isChange = true;
				}

				if (currentStep < levelInfo.bestStep) {
					levelInfo.bestStep = currentStep;
					levelInfo.isPass = true;
					dHelper.updateInfo(levelInfo);
					DialogCreator.create(mainActivity, "通关", "恭喜您！打破记录!\n您的步数为【" + currentStep + "】步！\n完成第" + level + "关!", "下一关", "关闭", onClickListener);

				} else {

					if (levelInfo.isPass == false) {
						levelInfo.isPass = true;
						dHelper.updateInfo(levelInfo);
					}
					DialogCreator.create(mainActivity, "恭喜您！完成第" + level + "关！", "下一关", "关闭", onClickListener);
				}
				// Log.e("aa",
				// level+" level "+ConstData.MAX_LEVEL+" maxlevel"+ConstData.currentLevel+" maxlevel");
				// if (level <= ConstData.MAX_LEVEL) {
				// if (level + 1 >= ConstData.currentLevel) {
				// ConstData.saveCurrentLevel(context, level + 1);
				//
				// }
				// } else {
				// }
			} else {

			}
		}

	}

	/***
	 * 是否全部关卡都完成了
	 * 
	 * @return
	 */
	private boolean isAllLevelFinish() {
		List<LevelInfo> levelInfos = dHelper.selectAllInfos(level > 100 ? 1 : 0);
		for (int i = levelInfos.size() - 1; i >= 0; i--) {
			LevelInfo info = levelInfos.get(i);
			if (info.isPass == false) {
				return false;
			}
		}
		return true;
	}

	private DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface arg0, int arg1) {

			switch (arg1) {
			case 0:
				int max = ConstData.MAX_LEVEL;
				if (level > 100) {
					max = ConstData.MAX_LEVEL2;
				}
				if (level + 1 <= max) {
					nextLevel();// 所有关卡还没有全部完成
				} else {
					if (isAllLevelFinish()) {// 是否全部关卡都完成了
						boolean isNew = level > 100;
						String sssString = "恭喜您！已完成所有关卡!\n还有101-200关希望您能挑战通过哦！~~\n寒冬已至\n986850427@qq.com\n2016年10月26日";
						if (isNew) {
							sssString = "恭喜您！已完成所有关卡!\n不得不佩服你是高手中的高手~\n一路走来不容易~\n谢谢支持~~*^_^*\n更多关卡请联系作者，敬请期待更多版本~\n作者：寒冬已至\n986850427@qq.com\n2016年10月26日";
						}
						DialogCreator.create(mainActivity, sssString);

					} else {
						DialogCreator.create(mainActivity, "还有关卡尚未通关~~\n请继续加油哦~~*^_^*");

					}
				}
				break;

			default:
				break;
			}

		}

	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (isGameWin) {// 游戏结束直接什么也没有

				DialogCreator.create(mainActivity, "已完成第" + level + "关", "下一关", "关闭", onClickListener);

				return super.onTouchEvent(event);
			}
			int x = (int) ((event.getX() - xOffSet) / size);
			int y = (int) ((event.getY() - yOffSet) / size);

			// Log.e("lgdx", x + " " + y);
			if (y < 0 || x < 0)
				return true;
			Point p0 = getManPoint();
			if (Math.abs(p0.x - x) == 1 && p0.y == y || Math.abs(p0.y - y) == 1 && p0.x == x) {
				pushDirection(p0, -p0.x + x, -p0.y + y);
			} else {
				if (isInThread == true) {// 处在绘制界面里面
					return super.onTouchEvent(event);
				}
				if (inArray(x, y)) {// 防越界
					if (arr[x][y] == ID_EMPTY || arr[x][y] == ID_POINT) {
						Cell cell = findWayBySearch(p0.x, p0.y, x, y);
						if (cell == null) {
							// ToastUtils.makeTextAndShow(context, "此路不通");
						} else {
							new DrawThread(cell).start();
							// Log.v("aa", "aaaa");
						}
					}
				}
			}
			return true;
		}
		return super.onTouchEvent(event);
	}

	class DrawThread extends Thread {

		List<Cell> list = new ArrayList<Cell>();

		int sleepTime = 50;

		public DrawThread(Cell cell) {
			isInThread = true;
			while (cell != null) {
				if (cell.from != null) {
					list.add(0, cell);
					//
					// Log.v("aa", cell + "");
					cell = cell.from;
				} else {
					list.add(0, cell);
					break;
				}
			}
		}

		@Override
		public void run() {
			Cell cellStart = list.get(0);
			for (int i = 1; i < list.size(); i++) {
				try {
					sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Cell cell = list.get(i);
				// Log.v("aa", cell + "");

				Message message = handler.obtainMessage();
				MyData myData = new MyData();
				message.obj = myData;
				myData.point = cellStart.getPoint();
				myData.xOffset = cell.row - cellStart.row;
				myData.yOffset = cell.col - cellStart.col;
				handler.sendMessage(message);
				cellStart = cell;
			}
			isInThread = false;
		}
	}

	private Point getManPoint()// 找到人
	{
		Point point = new Point();
		int i, j;
		for (i = 0; i < HEIGHT; i++)
			for (j = 0; j < WIDTH; j++)
				if (arr[i][j] == ID_MAN || arr[i][j] == ID_MAN_IN) {
					point.x = i;
					point.y = j;
					return point;
				}
		return null;
	}

	int width, height;

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;

		init();
		newGame();
	}

	// @Override
	// protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// setMeasuredDimension(size * HEIGHT, size * WIDTH);
	// }

	static class Flag {
		public static final int Empty = ID_EMPTY;
		public static final int EndPoint = -1;
		public static final int ConnectWay = -2;
		public static final int FindWay = -3;
	}

	/**
	 * 数组中的一个记录当前坐标，和父结点的单元
	 * 
	 * @author Administrator
	 * 
	 */
	class Cell {
		public int row;
		public int col;
		public Cell from;// 指向父节点

		public Cell(int row, int col, Cell from) {
			this.row = row;
			this.col = col;
			this.from = from;
		}

		public Point getPoint() {
			return new Point(row, col);
		}

		@Override
		public String toString() {
			return "row " + row + "  col" + col;
		}
	}

	/**
	 * 记录每步的数据
	 * 
	 * @author liugd
	 * 
	 */
	class StepData {
		int last;// 上一步
		int current;// 当前
		int next;// 下一步
		int lastx, lasty;
		int currentx, currenty;
		int nextx, nexty;
		int step = 1;

		public StepData(int last, int current, int next, int lastx, int lasty, int currentx, int currenty, int nextx, int nexty) {
			this.last = last;
			this.current = current;
			this.next = next;
			this.lastx = lastx;
			this.lasty = lasty;
			this.currentx = currentx;
			this.currenty = currenty;
			this.nextx = nextx;
			this.nexty = nexty;
		}
	}

	/**
	 * 返回上一步
	 */
	public void backOneStep() {
		if (isGameWin == true)// 游戏状态不对，直接返回
			return;

		if (!stepDatas.isEmpty()) {
			StepData s = stepDatas.pop();
			for (int i = 0; i < s.step; i++) {
				// arr[s.lastx][s.lasty] = s.last;
				// arr[s.currentx][s.currenty] = s.current;
				// 防止越界
				// arr[s.nextx][s.nexty] = s.next;
				setArrData(s.lastx, s.lasty, s.last);
				setArrData(s.currentx, s.currenty, s.current);
				setArrData(s.nextx, s.nexty, s.next);
				currentStep--;
				mainActivity.setStep(currentStep);
				invalidate();
			}
		} else {
			DialogCreator.create(context, "已撤销完毕~");
		}
	}

	private void setArrData(int row, int col, int var) {
		if (inArray(row, col))
			arr[row][col] = var;
	}

	boolean isInThread = false;
	Stack<StepData> stepDatas = new Stack<StepData>();

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		// for (int i = 0; i < bitmaps.length; i++) {
		// bitmaps[i].recycle();
		// }
		// bitmaps = null;
	}

}
