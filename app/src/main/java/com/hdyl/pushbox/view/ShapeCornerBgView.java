package com.hdyl.pushbox.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

import com.hdyl.pushbox.tools.Tools;

public class ShapeCornerBgView extends TextView {
	Paint paint;
	int lineWidth;

	public ShapeCornerBgView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();

		lineWidth = Tools.dip2px(context, 1);

	}

	@SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas) {
		paint.setAntiAlias(true); // 设置画笔为无锯齿
		paint.setColor(color); // 设置画笔颜色
		// canvas.drawColor(Color.WHITE); // 白色背景
		paint.setStrokeWidth(lineWidth); // 线宽
		// paint.setStyle(Style.STROKE); // 空心效果
		// Rect r1 = new Rect(); // Rect对象
		// r1.left = 50; // 左边
		// r1.top = 50; // 上边
		// r1.right = 450; // 右边
		// r1.bottom = 250; // 下边
		// canvas.drawRect(r1, paint); // 绘制矩形
		RectF r2 = new RectF(); // RectF对象
		r2.left = 0; // 左边
		r2.top = 0; // 上边
		r2.right = width; // 右边
		r2.bottom = height; // 下边
		int ww = Tools.dip2px(getContext(), randus);
		canvas.drawRoundRect(r2, ww, ww, paint); // 绘制圆角矩形
		super.onDraw(canvas);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
	}

	int color = Color.parseColor("#dd4a4a");
	int randus = 2;
	int width, height;

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
		invalidate();
	}

	public int getRandus() {
		return randus;
	}

	public void setRandus(int randus) {
		this.randus = randus;
	}

}
