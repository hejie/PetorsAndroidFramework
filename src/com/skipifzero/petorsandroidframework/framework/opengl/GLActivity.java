package com.skipifzero.petorsandroidframework.framework.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.media.AudioManager;
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
 * Extend this class, implement the needed methods, supply a GLController to start with and
 * start this Activity.
 * 
 * @author Peter Hillerström
 * @since 2013-04-03
 * @version 3
 */
public abstract class GLActivity extends Activity implements Renderer {
	
	//States used to make sure everything in the GLController happens on the rendering thread.
	private enum State {
		STARTING, RUNNING, PAUSING, FINISHING, SLEEPING;
	}
	
	private GLSurfaceView glSurfaceView;
	private GLController glController;
	
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
		
		//Sets VolumeControlStream to STREAM_MUSIC
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
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
			if(state == State.STARTING) { //Gets initial GLController if program is starting up.
				glController = getInitialGLController(this);
			}
			state = State.RUNNING; //Surface was created, so program is running.
			glController.onResume();
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
				
				//Updates current GLController and GLView
				glController.update(deltaTime, fps);
				break;
			
			case PAUSING:
				glController.onPause();
				synchronized(this) {
					this.state = State.SLEEPING;
					this.notifyAll();
				}
				break;
			
			case FINISHING:
				glController.onPause();
				glController.dispose();
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
	 * This method should return the initial GLController, it may not return null. Is called on the
	 * rendering thread in the onSurfaceCreated methods.
	 * Probably not the standard way of doing things, but don't really have a choice due to how
	 * Android activities work.
	 * @return initial GLController
	 */
	public abstract GLController getInitialGLController(GLActivity glActivity);
	
	/**
	 * This method returns whether fullscreen should be enabled or not.
	 * @return whether fullscreen should be enabled or not
	 */
	public abstract boolean enableFullscreenMode();
	
	/*
	 * Public Methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	/**
	 * Changes GLController
	 * Can be called from anywhere within a GLController. Will pause and dipose of old GLController.
	 * @throws IllegalArgumentException if new GLController is null
	 * @param glController the new GLController
	 */
	public void changeGLController(GLController glController, boolean catchBackKey) {
		if(glController == null) {
			throw new IllegalArgumentException("New GLController is null, not allowed.");
		}
		
		//Dispose of old GLController
		this.glController.onPause();
		this.glController.dispose();
		
		//Resumes new GLController
		glController.onResume();
		glController.update(0.0, 0); //TODO: Not sure if necessary or good idea.
		this.glController = glController;
		
		catchBackKey(catchBackKey);
	}
	
	/**
	 * Special version of changeGLController(), won't call pause() or dispose() in old GLController. 
	 * Can for example be used if assets are transferred between GLController's.
	 * Can be called from anywhere within a GLController. Will pause and dipose of old GLController.
	 * @throws IllegalArgumentException if new GLController is null
	 * @param glController the new GLController
	 */
	public void changeGLControllerDontDispose(GLController glController, boolean catchBackKey) {
		if(glController == null) {
			throw new IllegalArgumentException("New GLController is null, not allowed.");
		}
		
		//Resumes new GLController
		glController.onResume();
		glController.update(0.0, 0); //TODO: Not sure if necessary or good idea.
		this.glController = glController;
		
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
