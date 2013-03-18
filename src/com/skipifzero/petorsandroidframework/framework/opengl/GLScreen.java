package com.skipifzero.petorsandroidframework.framework.opengl;

public interface GLScreen {
	
	/**
	 * Called once each frame, called before draw().
	 * @param deltaTime the time in seconds since the last frame
	 * @param fps the amount of rendered frames the last second
	 */
	public void update(double deltaTime, int fps);
	
	/**
	 * Called once each frame, called after update().
	 * @param deltaTime the time in seconds since the last frame
	 * @param fps the amount of rendered frames the last second
	 */
	public void draw(double deltaTime, int fps);
	
	/**
	 * Called when Activity is resumed and when this GLScreen is created.
	 */
	public void onResume();
	
	/**
	 * Called when Activity is paused.
	 */
	public void onPause();
	
	/**
	 * Called when Activity is destroyed and when this GLScreen is destroyed.
	 */
	public void dipose();
}
