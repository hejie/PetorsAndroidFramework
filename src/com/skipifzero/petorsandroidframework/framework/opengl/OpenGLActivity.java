package com.skipifzero.petorsandroidframework.framework.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public abstract class OpenGLActivity extends Activity implements Renderer {
	
	private GLSurfaceView glSurfaceView;
	
	private long startTime = 0;
	private long lastFPSCount = 0;
	private int frameCount = 0;
	private int fps = 0;
	private double deltaTime = 0;
	
	/*
	 * Activity methods.
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		glSurfaceView = new GLSurfaceView(this);
		glSurfaceView.setRenderer(this);
		
		setContentView(glSurfaceView);
		
		//Call subclass's create method.
		create();
	}
	
	@Override
    public void onResume() {
    	super.onResume();
    	glSurfaceView.onResume();
	}
	
	@Override
    public void onPause() {
		pause();
		glSurfaceView.onPause();
    	super.onPause();
    }
	
	/*
	 * OpenGL Renderer methods.
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		startTime = System.nanoTime();
		lastFPSCount = startTime;
		frameCount = 0;
		fps = 0;
		deltaTime = 0;
		
		//Call subclass's resume method.
		resume();
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		//Do nothing.
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {		
		
		//Calculates delta.
		deltaTime = (System.nanoTime()-startTime) / 1000000000.0;
        startTime = System.nanoTime();
        
        //Calculates current fps.
        frameCount++;
        if(startTime - lastFPSCount >= 1000000000.0){
			fps = frameCount;
			Log.d("FPS", "FPS: " + fps);
			frameCount = 0;
			lastFPSCount = System.nanoTime();
		}
		
        //Calls abstract update and draw methods.
        update(deltaTime, fps);
        draw(deltaTime, fps);
	}
	
	
	/*
	 * Abstract methods.
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Called in OpenGLActivity's "onCreate" method.
	 * Should be used as an ordinary constructor.
	 */
	public abstract void create();
	
	/**
	 * Called in OpenGLActivity's "onSurfaceCreated" method.
	 * Should be called after "create" and every time the Activity is resumed.
	 * Since it is called in "onSurfaceCreated" and not in "onResume" the view should be initialized,
	 * meaning the view should return its real size instead of 0.
	 * 
	 * OpenGL "Texture" and "TextureRegion"'s should be initialized here.
	 */
	public abstract void resume();
	
	public abstract void update(double deltaTime, int fps);
	
	public abstract void draw(double deltaTime, int fps);
	
	/**
	 * Called in OpenGLActivity's "onPause" method.
	 */
	public abstract void pause();
	
	/**
	 * Called in OpenGLActivity's "onDestroy" method.
	 */
	public abstract void destroy();
	
	/*
	 * Methods.
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Returns the width of the GLSurfaceView in pixels.
	 * @return width
	 */
	public int getViewWidth() {
		return glSurfaceView.getWidth();
	}
	
	/**
	 * Returns the height of the GLSurfaceView in pixels.
	 * @return height
	 */
	public int getViewHeight() {
		return glSurfaceView.getHeight();
	}
	
	/**
	 * Returns a reference to the GLSurfaceView.
	 * @return glSurfaceView
	 */
	public GLSurfaceView getGLSurfaceView() {
		synchronized(this) {
			return glSurfaceView;
		}
	}
}
