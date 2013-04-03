package com.skipifzero.petorsandroidframework.framework.opengl;

/**
 * Interface for a view to be used with a GLActivity and GLController.
 * 
 * @author Peter Hillerström
 * @since 2013-04-03
 * @version 1
 */
public interface GLView {
	
	/**
	 * Should be called once each frame from the current GLControllers update method.
	 * @param deltaTime the time in seconds since the last frame
	 * @param fps the amount of rendered frames the last second
	 */
	public void draw(double deltaTime, int fps);
	
	/**
	 * Should be called when Activity is resumed and when this GLView is created/resumed.
	 */
	public void onResume();
	
	/**
	 * Should be called when Activity is paused and when this GLView is paused.
	 */
	public void onPause();
	
	/**
	 * Should be called when Activity is destroyed and when this GLView is destroyed.
	 */
	public void dispose();
}
