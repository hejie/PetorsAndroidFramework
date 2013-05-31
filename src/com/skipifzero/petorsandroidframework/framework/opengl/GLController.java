package com.skipifzero.petorsandroidframework.framework.opengl;

/**
 * Interface for a controller to be used with a GLActivity.
 * 
 * A recommendation is to make sure the implementation of this interface gets an instance of the
 * GLActivity it is running in. Otherwise it will be impossible to do stuff like checking if the
 * back button is pressed, or switch to other GLControllers.
 * 
 * @author Peter Hillerström
 * @since 2013-04-03
 * @version 1
 */
public interface GLController {

	/**
	 * Called once each frame.
	 * @param deltaTime the time in seconds since the last frame
	 * @param fps the amount of rendered frames the last second
	 */
	public void update(double deltaTime, int fps);
	
	/**
	 * Called when Activity is resumed and when this GLController is created/resumed.
	 */
	public void onResume();
	
	/**
	 * Called when Activity is paused and before it is destroyed.
	 */
	public void onPause();
	
	/**
	 * Called when Activity is destroyed and when this GLController is destroyed.
	 */
	public void dispose();
	
	/**
	 * Whether this GLController should catch the back key or not.
	 * @return whether this GLController should catch the back key or not
	 */
	public boolean catchBackKey();
}
