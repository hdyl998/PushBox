package com.hdyl.pushbox.soko;

import java.util.List;

import u.aly.bi;
import u.aly.co;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.hdyl.pushbox.base.ConstData;
import com.hdyl.pushbox.base.Consts;
import com.hdyl.pushbox.setting.SettingConfig;
import com.hdyl.pushbox.soko.tool.ArrFormatTools;

public class SokoView extends View implements Consts {

	Bitmap bitmaps[];

	public SokoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void init() {
		bitmaps = ConstData.getBitmaps();
		bitmap = null;
	}

	int WIDTH = 18;
	int HEIGHT = 12;

	int arr[][];

	Bitmap bitmap;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (arr != null && width != 0) {
			if (bitmap == null) {
				bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
				Canvas canvas2 = new Canvas(bitmap);

				Rect rect = new Rect();

				for (int i = 0; i < HEIGHT; i++) {
					for (int j = 0; j < WIDTH; j++) {
						int var = arr[i][j];
						if (var != ID_BLACK) {

							Bitmap bitmap = null;
							if (var == ID_WALL) {
								bitmap = ConstData.getWallBitmap(arr, i, j);
							} else {
								bitmap = bitmaps[var];
							}
							rect.left = j * size + xOffSet;
							rect.top = i * size + yOffSet;
							rect.right = j * size + size + xOffSet;
							rect.bottom = i * size + size + yOffSet;
							canvas2.drawBitmap(bitmap, null, rect, null);
						}
					}
				}
				new FillCornerManager().fillCorner(canvas2, arr, size, xOffSet, yOffSet);
			}
			canvas.drawBitmap(bitmap, 0, 0, null);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (bitmap != null) {
			bitmap.recycle();
		}

	}

	public void setArrDatas(List<String> listDatas) {
		setArrDatas(ArrFormatTools.getArrWithFormat(listDatas));
	}

	public void setArrDatas(int arr[][]) {
		this.arr = arr;
		initdatas();
	}

	private void initdatas() {
		if (bitmap != null) {
			bitmap.recycle();
		}
		bitmap = null;

		if (arr != null) {
			WIDTH = arr[0].length;
			HEIGHT = arr.length;
			size = Math.min(width / WIDTH, height / HEIGHT);
			yOffSet = (height - size * HEIGHT) / 2;
			xOffSet = (width - size * WIDTH) / 2;
		}
		invalidate();
	}

	public int[][] getArr() {
		return arr;
	}

	int width, height;

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		initdatas();
	}

	int size;
	int yOffSet = 0;
	int xOffSet = 0;

}
