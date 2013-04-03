package com.skipifzero.petorsandroidframework.framework.opengl;


/**
 * An abstract class combining a GLController and a GLView into one "GLScreen". Useful if you have a
 * GLController with only one GLView, or if you're porting a program from an older version of my
 * framework, or if you're to simply to lazy to make proper mvc. ;)
 * 
 * @author Peter Hillerström
 * @since 2013-04-03
 * @version 1
 */
public abstract class GLScreen implements GLController, GLView {

	private final GLActivity glActivity;
	
	public GLScreen(GLActivity glActivity) {
		this.glActivity = glActivity;
	}
	
	/**
	 * Returns the GLActivity running this GLController.
	 * @return the GLActivity running this GLController
	 */
	public final GLActivity getGLActivity() {
		return glActivity;
	}
		
	@Override
	public final void update(double deltaTime, int fps) {
		update(fps, deltaTime);
		draw(deltaTime, fps);
	}
	
	/**
	 * Called once each frame.
	 * @param deltaTime the time in seconds since the last frame
	 * @param fps the amount of rendered frames the last second
	 */
	public abstract void update(int fps, double deltaTime); //Yes, I'm clearly a genius.
	
	@Override
	public abstract void draw(double deltaTime, int fps);

	@Override
	public abstract void onResume();

	@Override
	public abstract void onPause();

	@Override
	public abstract void dispose();
}