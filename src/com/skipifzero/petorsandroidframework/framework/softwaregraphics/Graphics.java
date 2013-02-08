package com.skipifzero.petorsandroidframework.framework.softwaregraphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class Graphics {

	private Canvas canvas;
	private Rect tempRect;
	private Paint paint;
	
	//Temp variables
	private int leftTemp;
	private int topTemp;
	private int rightTemp;
	private int bottomTemp;
	
	public Graphics(Bitmap framebuffer){
		canvas = new Canvas(framebuffer);
		tempRect = new Rect();
		paint = new Paint();
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	
	public void drawBitmap(double xCenter, double yCenter, double width, double height, double screenYPixels, double scaleFactor, Bitmap bitmap){
		leftTemp = (int)Math.round(xCenter - width/2);
		topTemp = (int)Math.round(screenYPixels - (yCenter - height/2));
		rightTemp = (int)Math.round(xCenter + width/2);
		bottomTemp = (int)Math.round(screenYPixels - (yCenter + height/2));
		tempRect.set(leftTemp, topTemp, rightTemp, bottomTemp);
		canvas.drawBitmap(bitmap, null, tempRect, null);
	}
	
	public void setTextColor(int color){
		paint.setColor(color);
	}
	
	public void setFont(Typeface font){
		paint.setTypeface(font);
	}
	
	public void setTextAlign(Paint.Align textAlign){
		paint.setTextAlign(textAlign);
	}
	
	public void drawText(String text, double x, double yCenter, int size){
		paint.setTextSize(size);
		canvas.drawText(text, (float)x, (float)yCenter+size/2, paint);
	}
}
