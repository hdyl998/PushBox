package com.hdyl.pushbox.soko;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.hdyl.pushbox.base.ConstData;
import com.hdyl.pushbox.base.Consts;
import com.hdyl.pushbox.setting.SettingConfig;

public class FillCornerManager implements Consts {
	// 覆盖边角
	public void fillCorner(Canvas canvas, int arr[][], int size, int xOffSet, int yOffSet) {
		SettingConfig config = SettingConfig.getInstence();

		Bitmap bitmapCorner = ConstData.getBitmaps()[ID_WALL];

		int width = bitmapCorner.getWidth();
		int height = bitmapCorner.getHeight();

		Rect rect = null;
		Rect rect2 = null;
		if (config.isSkinCover) {
			for (int i = 0; i < arr.length - 1; i++) {
				for (int j = 0; j < arr[0].length - 1; j++) {
					if (isRight(arr, i, j) && isRight(arr, i + 1, j) && isRight(arr, i, j + 1) && isRight(arr, i + 1, j + 1)) {
						if (rect == null) {
							rect = new Rect(width / 4, height / 4, width * 3 / 4, height * 3 / 4);
							rect2 = new Rect();
						}
						rect2.left = j * size + xOffSet + 3*size / 4;
						rect2.top = i * size + yOffSet + 3*size / 4;
						rect2.right = j * size + size + xOffSet + size / 4;
						rect2.bottom = i * size + size + yOffSet + size / 4;
						canvas.drawBitmap(bitmapCorner, rect, rect2, null);
					}
				}
			}

		}
	}

	private boolean isRight(int arr[][], int row, int col) {
		if (ConstData.inArray(arr, row, col) && arr[row][col] == ID_WALL) {
			return true;
		}
		return false;
	}
}
