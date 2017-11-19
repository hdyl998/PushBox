package com.hdyl.pushbox.soko;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hdyl.pushbox.base.ConstData;
import com.hdyl.pushbox.base.Consts;
import com.hdyl.pushbox.base.DialogCreator;
import com.hdyl.pushbox.setting.SettingConfig;
import com.hdyl.pushbox.soko.LevelChooseItem.LevelInfo;
import com.hdyl.pushbox.soko.LevelChooseItem.MyLevel;
import com.hdyl.pushbox.soko.SokoGameActivity.GameData;
import com.hdyl.pushbox.soko.tool.ArrFormatTools;
import com.hdyl.pushbox.soko.tool.SaveDataUtls;
import com.hdyl.pushbox.tools.LogUtils;
import com.hdyl.pushbox.tools.Tools;

public class SokoGameView extends View implements Consts {

	Context context;

	SokoGameActivity mainActivity;
	int currentStep = 0;// 当前步数

	// data
	List<MyLevel> listMyLevels;
	LevelInfo levelInfo;
	MyLevel myLevel;
	int currentIndex;

	boolean isGameWin = false;

	boolean isRightFalg = false;

	boolean isShowPosition = false;

	HashSet<Integer> hashSetPassLevel;// 通关的关卡

	
	int WIDTH = 18;
	int HEIGHT = 12;

	int yOffSet = 0;
	int xOffSet = 0;
	
	Point pointMan;
	
	
	
	
	public void newGame() {
		if(this.isAction){
			return;
		}
		
		initGameInfo();
		setArrDatas(ArrFormatTools.getArrWithFormat(myLevel.levDatas));
		isGameWin = false;
		currentStep = 0;
		mainActivity.setStep(currentStep);
		size = Math.min(width / WIDTH, height / HEIGHT);
		yOffSet = (height - size * HEIGHT) / 2;
		xOffSet = (width - size * WIDTH) / 2;
		stepDatas.clear();
		invalidate();
	}
	
	//从saveInstance中恢复数据
	public void resumeGame(GameData gameData){
		this.currentIndex=gameData.currentIndex;
		this.currentStep=gameData.currentStep;
		this.listMyLevels=gameData.listMyLevels;
		this.isGameWin=gameData.isGameWin;
		this.stepDatas=gameData.stepDatas;
		this.size=gameData.size;
		this.xOffSet=gameData.xOffSet;
		this.yOffSet=gameData.yOffSet;
		
		
		initGameInfo();
		mainActivity.setStep(currentStep);
		invalidate();
	}
	
	public GameData getGameData(){
		GameData gameData=new GameData();
		gameData.currentIndex=this.currentIndex;
		gameData.currentStep=this.currentStep;
		gameData.listMyLevels=this.listMyLevels;
		gameData.isGameWin=this.isGameWin;
		gameData.stepDatas=this.stepDatas;
		gameData.size=this.size;
		gameData.xOffSet=this.xOffSet;
		gameData.yOffSet=this.yOffSet;
		return gameData;
	}
	

	public SokoGameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mainActivity = (SokoGameActivity) (this.context = context);
	}

	public List<MyLevel> getListMyLevels() {
		return listMyLevels;
	}

	public void setListMyLevels(List<MyLevel> listMyLevels) {
		this.listMyLevels = listMyLevels;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public LevelInfo getLevelInfo() {
		return levelInfo;
	}

	public MyLevel getMyLevel() {
		return myLevel;
	}

	public void setLevelInfo(LevelInfo levelInfo) {
		this.levelInfo = levelInfo;
	}

	public void setMyLevel(MyLevel level) {
		this.myLevel = level;
	}

	Bitmap[] bitmaps;

	int size;

	/***
	 * 下一关
	 */
	public void nextLevel() {

		if (!hasNextLevel()) {
			DialogCreator.create(context,"提示", "暂无下一关");
		} else {
			currentIndex++;
			newGame();
		}
	}

	public boolean hasNextLevel() {
		if (currentIndex + 1 >= listMyLevels.size()) {
			return false;
		}
		return true;
	}

	public boolean hasLastLevel() {
		if (currentIndex - 1 < 0) {
			return false;
		}
		return true;
	}

	/***
	 * 上一关
	 */
	public void lastLevel() {
		if (!hasLastLevel()) {
			DialogCreator.create(context,"提示", "暂无上一关");

		} else {
			currentIndex--;
			newGame();
		}
	}

	private void init() {
		bitmaps = ConstData.getBitmaps();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(Tools.dip2px(context, 14));
		paint.setColor(Color.parseColor("#dd4a4a"));
		fontHeight = getFontHeight();
		isShowPosition = SettingConfig.getInstence().isPosition;
	}

	int fontHeight;

	public int getFontHeight() {
		FontMetrics fm = paint.getFontMetrics();
		return ((int) Math.ceil(fm.descent - fm.top) + 2) / 2;
	}

	

	Paint paint;

	public void setArrDatas(int arr[][]) {
		this.arr = arr;
		WIDTH = arr[0].length;
		HEIGHT = arr.length;
	}

	private void initGameInfo() {
		MyLevel myLevel = listMyLevels.get(currentIndex);
		// 不相等时才处理
		if (myLevel != this.myLevel) {
			String ID_MD5 = LevelChooseItem.getLevelMD5(myLevel);
			LevelInfo levelInfo = SaveDataUtls.getLevelInfoData(ID_MD5);
			LogUtils.Print(levelInfo);
			if (levelInfo == null) {
				levelInfo = new LevelInfo();// 新创建的关卡信息
				levelInfo.idMD5 = ID_MD5;
				levelInfo.isFinished=myLevel.isFinished;
				levelInfo.passDate=myLevel.passDate;
			} else {// 读到有数据
				// 外面显示无方案，里面有方案
				// if (myLevel.isSolution == false &&
				// !TextUtils.isEmpty(levelInfo.solution)) {
				// setResult(RESULT_FIRST_USER);
				// }
			}
			setLevelInfo(levelInfo);
			setMyLevel(myLevel);
			mainActivity.setBest(levelInfo.bestStep);
			mainActivity.setLevelText((currentIndex + 1) + "");
			bitmap = null;// 重置画布
		}

	}



	int arr[][];

	RectF rectF = new RectF();

	Bitmap bitmap;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (arr == null || width == 0) {
			return;
		}

		if (bitmap == null) {
			bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			Canvas canvas2 = new Canvas(bitmap);
			for (int i = 0; i < HEIGHT; i++) {
				for (int j = 0; j < WIDTH; j++) {
					int var = arr[i][j];
					if (var == ID_WALL) {
						rectF.left = j * size + xOffSet;
						rectF.top = i * size + yOffSet;
						rectF.right = j * size + size + xOffSet;
						rectF.bottom = i * size + size + yOffSet;
						Bitmap bitmap1 = ConstData.getWallBitmap(arr, i, j);
						canvas2.drawBitmap(bitmap1, null, rectF, null);
					}
				}
			}
			new FillCornerManager().fillCorner(canvas2, arr, size, xOffSet, yOffSet);
		}
		canvas.drawBitmap(bitmap, 0, 0, null);

		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0; j < WIDTH; j++) {
				int var = arr[i][j];
				if (var != ID_BLACK && var != ID_WALL) {
					rectF.left = j * size + xOffSet;
					rectF.top = i * size + yOffSet;
					rectF.right = j * size + size + xOffSet;
					rectF.bottom = i * size + size + yOffSet;
					canvas.drawBitmap(bitmaps[var], null, rectF, null);

				}
			}
		}

		if (!isShowPosition) {
			return;
		}
		Point manPoint = getManPoint();

		int var = Tools.dip2px(context, 8) / 3;

		for (int j = 0; j < WIDTH; j++) {
			String sssString = getIndexStringVar(j);
			int x = j * size + xOffSet + size / 2;
			int y = yOffSet - fontHeight / 2;

			if (y - fontHeight - var < 0) {
				y = fontHeight + var;
			}

			if (manPoint.y == j) {
				int colorBefore = paint.getColor();
				canvas.drawRoundRect(getRectF(x, y), fontHeight / 3, fontHeight / 3, paint);
				paint.setColor(Color.WHITE);
				canvas.drawText(sssString, x, y, paint);
				paint.setColor(colorBefore);
			} else {
				canvas.drawText(sssString, x, y, paint);
			}

		}

		int NUM = 1;
		for (int i = 0; i < HEIGHT; i++) {
			int x = xOffSet + fontHeight;
			int y = yOffSet + size / 2 + i * size + fontHeight / 2;

			if (manPoint.x == i) {
				int colorBefore = paint.getColor();
				canvas.drawRoundRect(getRectF(x, y), fontHeight / 3, fontHeight / 3, paint);
				paint.setColor(Color.WHITE);
				canvas.drawText((NUM + i) + "", x, y, paint);
				paint.setColor(colorBefore);
			} else {
				canvas.drawText((NUM + i) + "", x, y, paint);
			}

		}
	}

	/***
	 * 得到char的索引号
	 * 
	 * @return
	 */
	public String getIndexStringVar(int index) {

		int num = index / 26;
		int yuNum = index % 26;
		String sssString = "";
		switch (num) {
		case 0:// A-Z
			sssString += ((char) ('A' + yuNum));
			break;
		case 1:// a-z
			sssString += ((char) ('a' + yuNum));
			break;
		default:
			int index2 = num - 2;// 除开1、2后的最新索引
			int num2 = index2 / 26;// 最新索引所属组
			int yuNum2 = index2 % 26;// 所属组的序号

			switch (num2) {
			case 0:
				sssString = ((char) ((yuNum2) + 'A')) + "" + ((char) ('A' + yuNum));
				break;
			case 1:
				sssString = ((char) ((yuNum2) + 'a')) + "" + ((char) ('a' + yuNum));
				break;
			default:
				sssString = "" + index;
				break;
			}
			break;
		}
		return sssString;
	}

	private RectF getRectF(int x, int y) {
		int var = Tools.dip2px(context, 8);
		rectF.left = x - var;
		rectF.top = y - fontHeight - var / 3;
		rectF.right = x + var;
		rectF.bottom = y + var / 3;
		return rectF;
	}

	public boolean isWin()// 是否完成
	{
		for (int i = 0; i < HEIGHT; i++)
			for (int j = 0; j < WIDTH; j++)
				if (arr[i][j] == ID_POINT || arr[i][j] == ID_MAN_IN)
					return false;
		return true;
	}

	// 存储过关数据
	private void savePassInfo() {

		StringBuilder sBuffer = new StringBuilder();
		for (StepData stepData : stepDatas) {
			sBuffer.append(stepData.getCurrentDirection());
		}
		String curSolution = sBuffer.substring(0);

		boolean isTrue = levelInfo.setSolution(curSolution);// 本地存档优化存储
		if (isTrue||levelInfo.isFinished==false) {
			levelInfo.isFinished = true;
			SaveDataUtls.saveLevelInfoData(levelInfo);
			mainActivity.setBest(levelInfo.bestStep);
		}
		setLevelOK();

	}

	/***
	 * 设置回传数据
	 */
	public void setLevelOK() {
		// 存一下通关的关卡，给前一个页面去更新数据
		if (hashSetPassLevel == null) {
			hashSetPassLevel = new HashSet<Integer>();
		}
		hashSetPassLevel.add(this.currentIndex);
		Intent intent = new Intent();
		intent.putExtra(EXTRA1, hashSetPassLevel);
		mainActivity.setResult(Activity.RESULT_OK, intent);// 只要通关就有
	}

	public void exChangeData() {
		isRightFalg = !isRightFalg;
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
				// System.out.println("找到解！");
				return c;
			}
			if (nextSet.isEmpty()) {
				// System.out.println("无解！");
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

	public void pushActingDirection(char ch) {
		Point point = getManPoint();
		if (point != null) {
			int x = 0, y = 0;
			switch (ch) {
			case 'r':
			case 'R':
				y = 1;
				break;
			case 'l':
			case 'L':
				y = -1;
				break;
			case 'u':
			case 'U':
				x = -1;
				break;
			case 'd':
			case 'D':
				x = 1;
				break;
			}
			Message message = handler.obtainMessage();
			MyData myData = new MyData();
			message.obj = myData;
			myData.point = point;
			myData.xOffset = x;
			myData.yOffset = y;
			handler.sendMessage(message);

		}
	}

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
		if (current == ID_GROUND) {
			isChange = true;
			arr[x][y] = ID_MAN;
		} else if (current == ID_POINT) {
			isChange = true;
			arr[x][y] = ID_MAN_IN;
		} else if (current == ID_BOX) {// 箱子可可移动箱子
			if (next == ID_GROUND) {// 空
				isChange = true;
				arr[x][y] = ID_MAN;
				arr[p2.x][p2.y] = ID_BOX;
			} else if (next == ID_POINT) {// 目标点
				isChange = true;
				arr[x][y] = ID_MAN;
				arr[p2.x][p2.y] = ID_BOX_IN;
			}
		} else if (current == ID_BOX_IN) {// 目标
			if (next == ID_GROUND) {
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

			if (!isAction) {// 不为动画演示
				// 入栈
				StepData stepData = new StepData(start, current, next, p0.x, p0.y, x, y, p2.x, p2.y);
				stepDatas.add(stepData);

			}

			arr[p0.x][p0.y] = start == ID_MAN ? ID_GROUND : ID_POINT;
			currentStep++;
			mainActivity.setStep(currentStep);
			invalidate();

			if (!isAction && isWin()) {
				savePassInfo();// 存通关信息
				isGameWin = true;
				DialogCreator.create(mainActivity, "通关", "恭喜您！完成第" + (currentIndex + 1) + "关！\n是否进入下一关?", "是", "否", onClickListener);
			} else {

			}
		}

	}

	private DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			switch (arg1) {
			case 0:
				nextLevel();
				break;

			default:
				break;
			}
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if(isInAction()){
				DialogCreator.create(mainActivity, "正在演示中...请稍后");
				return true;
			}
			
			
			if (isGameWin) {// 游戏结束直接什么也没有
				DialogCreator.create(mainActivity, "通关", "已完成第" + (currentIndex + 1) + "关！\n是否进入下一关?", "是", "否", onClickListener);
				return super.onTouchEvent(event);
			}

			if (isInThread == true) {// 处在绘制界面里面
				return super.onTouchEvent(event);
			}
			int y = (int) ((event.getX() - xOffSet) / size);
			int x = (int) ((event.getY() - yOffSet) / size);
			if (y < 0 || x < 0)
				return true;
			Point p0 = getManPoint();
			if (Math.abs(p0.x - x) == 1 && p0.y == y || Math.abs(p0.y - y) == 1 && p0.x == x) {
				pushDirection(p0, -p0.x + x, -p0.y + y);
			} else {
				if (inArray(x, y)) {// 防越界
					if (arr[x][y] == ID_GROUND || arr[x][y] == ID_POINT) {
						Cell cell = findWayBySearch(p0.x, p0.y, x, y);
						if (cell == null) {
						} else {
							new DrawThread(cell).start();
						}
					}
				}
			}
			return true;
		}
		return super.onTouchEvent(event);
	}

	class DrawThread extends Thread {
		int sleepTime = 50;
		List<Cell> list = new ArrayList<Cell>();

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
		public static final int Empty = ID_GROUND;
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
	static class Cell {
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
	public static class StepData implements Serializable{
		int last;// 上一步
		int current;// 当前
		int next;// 下一步
		int lastx, lasty;
		int currentx, currenty;
		int nextx, nexty;

		// int step = 1;

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
			Log.e("aa", this.getCurrentDirection() + "");
		}

		public char getCurrentDirection() {
			int x = currentx - lastx;

			int addVar = isBox(current) ? -32 : 0;

			if (x == 1) {// X 是ROW ,竖直方向
				return (char) ('d' + addVar);
			}

			if (x == -1) {
				return (char) ('u' + addVar);
			}
			int y = currenty - lasty;// Y是COL ，COL 是水平的
			if (y == 1) {
				return (char) ('r' + addVar);
			}
			if (y == -1) {
				return (char) ('l' + addVar);
			}
			return 'A';
		}
	}

	/**
	 * 返回上一步
	 */
	public void backOneStep() {
		if (isGameWin == true || isInThread == true)// 游戏状态不对，直接返回
			return;

		if (!stepDatas.isEmpty()) {

			new BackStepThread().start();

		} else {
			DialogCreator.create(context, "已撤销完毕~");
		}
	}

	// 设置回跳步数
	public void backStep(StepData s) {
		currentStep--;
		setArrData(s.lastx, s.lasty, s.last);
		setArrData(s.currentx, s.currenty, s.current);
		setArrData(s.nextx, s.nexty, s.next);
		mainActivity.setStep(currentStep);
		invalidate();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				MyData myData = (MyData) msg.obj;
				pushDirection(myData.point, myData.xOffset, myData.yOffset);
				break;

			case 1:
				backStep((StepData) msg.obj);
				break;
			}

		}
	};

	private static boolean isBox(int var) {
		return var == ID_BOX || var == ID_BOX_IN;
	}

	class BackStepThread extends Thread {
		int sleepTime = 50;

		public BackStepThread() {
			isInThread = true;
		}

		@Override
		public void run() {
			StepData s = stepDatas.pop();
			sendMsg(s);
			try {
				sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (!isBox(s.current)) {
				if (stepDatas.isEmpty()) {
					isInThread = false;
					return;
				}
				s = stepDatas.pop();
				sendMsg(s);
				try {
					sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			isInThread = false;
		}

		private void sendMsg(StepData stepData) {
			Message message = handler.obtainMessage();
			message.obj = stepData;
			message.what = 1;
			handler.sendMessage(message);
		}

	}

	private void setArrData(int row, int col, int var) {
		if (inArray(row, col))
			arr[row][col] = var;
	}

	private boolean isInThread = false;// 处在线程绘制中
	private boolean isAction = false;// 是否是演示方案

	public void startAction() {
		isInThread = true;
		isAction = true;
	}

	public boolean isInAction() {
		return isAction||isInThread;
	}

	public boolean isInThread() {
		return isInThread;
	}

	public void setInThread(boolean isInThread) {
		this.isInThread = isInThread;
	}

	public void stopAction() {
		isInThread = false;
		isAction = false;
	}

	Stack<StepData> stepDatas = new Stack<StepData>();

}
