package com.skipifzero.petorsandroidframework.framework.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.skipifzero.petorsandroidframework.framework.input.BackKeyInput;

/**
 * Class used to make it easier to create Activities using OpenGL ES.
 * 
 * How to use:
 * Extend this class, implement the needed methods, supply a GLScreen to start with and
 * start this Activity.
 * 
 * @author Peter Hillerström
 * @version 1
 */
public abstract class GLActivity extends Activity implements Renderer {
	
	//States used to make sure everything in the GLScreen happens on the rendering thread.
	private enum State {
		STARTING, RUNNING, PAUSING, FINISHING, SLEEPING;
	}
	
	private GLSurfaceView glSurfaceView;
	private GLScreen glScreen;
	
	private volatile State state;
	
	//Variables for calculating and storing deltatime and fps.
	private long startTime = 0;
	private long lastFPSCount = 0;
	private int frameCount = 0;
	private int fps = 0;
	private double deltaTime = 0;
	private StringBuilder fpsBuilder = new StringBuilder("FPS: ");
	
	private BackKeyInput keyInput;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.state = State.STARTING;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); //Removes title bar.
		if(enableFullscreenMode()) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
		glSurfaceView = new GLSurfaceView(this);
		glSurfaceView.setRenderer(this);
		
		setContentView(glSurfaceView);
		
		
		keyInput = new BackKeyInput(glSurfaceView, false);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		glSurfaceView.onResume();
	}
	
	@Override
	public void onPause() {
		synchronized(this) {
			if(isFinishing()) {
				state = State.FINISHING;
			} else {
				state = State.PAUSING;
			}
			
			//Wait for rendering thread to finish cleaning up.
			try {
				this.wait();
			} catch(InterruptedException e) {
			}
		}
		glSurfaceView.onPause();
		super.onPause();
	}
	
	/*
	 * OpenGL Renderer methods.
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		//Resets fps counter.
		startTime = System.nanoTime();
		lastFPSCount = startTime;
		frameCount = 0;
		fps = 0;
		deltaTime = 0;
		
		synchronized(this) {
			if(state == State.STARTING) { //Gets initial GLScreen if program is starting up.
				glScreen = getInitialGLScreen(this);
			}
			state = State.RUNNING; //Surface was created, so program is running.
			glScreen.onResume();
		}
	}
	
	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		//Do nothing.
	}
	
	@Override
	public void onDrawFrame(GL10 unused) {
		//Stores state in local variable to avoid synchronizing bugs.
		State localState = null;
		synchronized(this) {
			localState = this.state;
		}
		
		switch(localState) {
			case RUNNING:
				//Calculates delta.
				deltaTime = (System.nanoTime()-startTime) / 1000000000.0;
				startTime = System.nanoTime();
				
				//Calculates current fps.
				frameCount++;
				if(startTime - lastFPSCount >= 1000000000.0){
					fps = frameCount;
					fpsBuilder.delete(5, fpsBuilder.length());
					fpsBuilder.append(fps);
					Log.d("FPS", fpsBuilder.toString());
					frameCount = 0;
					lastFPSCount = System.nanoTime();
				}
				
				//Calls current GLScreen's update and draw methods.
				glScreen.update(deltaTime, fps);
				glScreen.draw(deltaTime, fps);
				break;
			
			case PAUSING:
				glScreen.onPause();
				synchronized(this) {
					this.state = State.SLEEPING;
					this.notifyAll();
				}
				break;
			
			case FINISHING:
				glScreen.onPause();
				glScreen.dipose();
				synchronized(this) {
					this.state = State.SLEEPING;
					this.notifyAll();
				}
				break;
			
			case STARTING:
			case SLEEPING:
			default:
				throw new AssertionError("Illegal state in GLActivity");
		}
	}
	
	/*
	 * Abstract methods.
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * This method should return the initial GLScreen, it may not return null. Is called on the
	 * rendering thread in the onSurfaceCreated methods.
	 * Probably not the standard way of doing things, but don't really have a choice due to how
	 * Android activities work.
	 * @return initial GLScreen
	 */
	public abstract GLScreen getInitialGLScreen(GLActivity glActivity);
	
	/**
	 * This method returns the fullscreen status.
	 * True = fullscreen enabled
	 * False = fullscreen disabled
	 * @return fullScreen
	 */
	public abstract boolean enableFullscreenMode();
	
	/*
	 * Public Methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Changes screen.
	 * Can be called from anywhere within a GLScreen. Will pause and dipose of old GLScreen.
	 * @throws IllegalArgumentException if new GLScreen is null
	 * @param glScreen the new GLScreen
	 */
	public void changeGLScreen(GLScreen glScreen, boolean catchBackKey) {
		if(glScreen == null) {
			throw new IllegalArgumentException("New GLScreen is null, not allowed.");
		}
		
		//Dispose of old GLScreen
		this.glScreen.onPause();
		this.glScreen.dipose();
		
		//Resumes new GLScreen
		glScreen.onResume();
		glScreen.update(0.0, 0); //TODO: Not sure if necessary or good idea.
		this.glScreen = glScreen;
		
		catchBackKey(catchBackKey);
	}
	
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
	
	/**
	 * Returns true if back button is pressed, false otherwise.
	 * @return whether back is pressed or not
	 */
	public boolean isBackPressed() {
		return keyInput.isBackPressed();
	}
	
	/**
	 * Sets if back button presses should be caught or not. If they are caught they will do nothing,
	 * if they aren't caught they will exit the Activity.
	 * @param catchBackKey
	 */
	public void catchBackKey(boolean catchBackKey) {
		keyInput.catchBackKey(catchBackKey);
	}
}
