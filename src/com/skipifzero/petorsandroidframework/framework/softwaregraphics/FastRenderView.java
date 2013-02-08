package com.skipifzero.petorsandroidframework.framework.softwaregraphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class FastRenderView extends SurfaceView implements Runnable {
	
	private Updatable updateable;
	private Thread renderThread = null;
	private SurfaceHolder holder;
	private volatile boolean running = false;
	
	public FastRenderView(Context context, Updatable updateable) {
		super(context);
		this.updateable = updateable;
		this.holder = getHolder();
	}
	
	@Override
	public void run() {
		Rect screenRect = new Rect();
		long startTime = System.nanoTime();
		long lastFPSCount = startTime;
		int frameCount = 0;
		int fps = 0;
		double deltaTime = 0;
		
		while(running){
			//Checks if the SurfaceView is ready to be written to.
    		if(!holder.getSurface().isValid()){ 
				continue;
			}
    		
    		//Calculates delta.
    		deltaTime = (System.nanoTime()-startTime) / 1000000000.0;
            startTime = System.nanoTime();
            
            //Calculates current fps.
            frameCount++;
            if(startTime - lastFPSCount >= 1000000000.0){
				fps = frameCount;
				frameCount = 0;
				lastFPSCount = System.nanoTime();
			}
            
            //Updates Updateable and gets framebuffer to draw.
            updateable.update(deltaTime, fps);
            Bitmap framebuffer = updateable.draw(deltaTime, fps);
            
            //Checks how big current screen is, scales and draws framebuffer to it.
            Canvas canvas = holder.lockCanvas();
            canvas.getClipBounds(screenRect);
            canvas.drawBitmap(framebuffer, null, screenRect, null);
            holder.unlockCanvasAndPost(canvas);
 		}
	}
	
	public void resume(){
		running = true;
		renderThread = new Thread(this);
		renderThread.start();
	}
	
	public void pause(){
		running = false;
		while(true){
			try{
				renderThread.join();
				break;
			}catch(InterruptedException e){
				//Do nothing, try again.
			}
		}
	}
}
