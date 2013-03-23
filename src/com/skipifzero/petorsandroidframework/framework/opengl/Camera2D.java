package com.skipifzero.petorsandroidframework.framework.opengl;

import android.opengl.GLES10;

import com.skipifzero.petorsandroidframework.framework.vector.BaseVector2;
import com.skipifzero.petorsandroidframework.framework.vector.Vector2;

/**
 * A class used for setting the OpenGL viewport and setting the projection type to 2D.
 * Basically the size and position of this camera is relative to an internal coordinate system.
 * 
 * @author Peter Hillerström
 * @version 1
 */
public class Camera2D {
	private final Vector2 position = new Vector2(0,0);
	private double frustrumX, frustrumY;
	
	/**
	 * Creates a Camera with the specified size and position.
	 * @param centerX the center position on the x-axis
	 * @param centerY the center position on the y-axis
	 * @param frustrumX the width of the camera
	 * @param frustrumY the height of the camera
	 */
	public Camera2D(double centerX, double centerY, double frustrumX, double frustrumY) {
		this.position.set(centerX, centerY);
		this.frustrumX = frustrumX;
		this.frustrumY = frustrumY;
	}
	
	/**
	 * Moves the camera and initializes various OpenGL stuff so you won't have to the manually.
	 * Must be run first each frame before using SpriteBatcher.
	 * @param viewWidth the width of the GLSurfaceView
	 * @param viewHeight the height of the GLSurfaceView
	 */
	public void initialize(int viewWidth, int viewHeight) {
		GLES10.glViewport(0, 0, viewWidth, viewHeight);
		GLES10.glMatrixMode(GLES10.GL_PROJECTION);
		GLES10.glLoadIdentity();
		
		float halfFrustrumX = (float)frustrumX/2;
		float halfFrustrumY = (float)frustrumY/2;
		float xPos = (float)position.getX();
		float yPos = (float)position.getY();
		GLES10.glOrthof(xPos - halfFrustrumX, xPos + halfFrustrumX, yPos - halfFrustrumY, yPos + halfFrustrumY, 1, -1);
		
		GLES10.glMatrixMode(GLES10.GL_MODELVIEW);
		GLES10.glLoadIdentity();
	}
	
	/**
	 * Changes size of the camera.
	 * @param frustrumX
	 * @param frustrumY
	 */
	public void changeSize(double frustrumX, double frustrumY) {
		this.frustrumX = frustrumX;
		this.frustrumY = frustrumY;
	}
	
	/**
	 * Sets the position of this camera.
	 * @param position the position
	 */
	public void setPosition(BaseVector2 position) {
		this.position.set(position);
	}
	
	/**
	 * Gets the position of this camera.
	 * Returns a reference to the internal position. Be careful.
	 * @return position of this camera
	 */
	public Vector2 getPosition() {
		return position;
	}
	
	/**
	 * Gets the position of this camera.
	 * Returns a copy of the internal position, more safe.
	 * @return position of this cammera
	 */
	public Vector2 getPositionCopy() {
		return position.clone();
	}
}
