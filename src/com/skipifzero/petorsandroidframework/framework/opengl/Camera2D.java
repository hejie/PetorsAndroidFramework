package com.skipifzero.petorsandroidframework.framework.opengl;

import android.opengl.GLES10;

import com.skipifzero.petorsandroidframework.framework.vector.BaseVector2;
import com.skipifzero.petorsandroidframework.framework.vector.Vector2;

public class Camera2D {
	private final Vector2 position = new Vector2(0,0);
	private double frustrumX, frustrumY;
	
	public Camera2D(double x, double y, double frustrumX, double frustrumY) {
		this.position.set(x,y);
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
	
	public void changeSize(double frustrumX, double frustrumY) {
		this.frustrumX = frustrumX;
		this.frustrumY = frustrumY;
	}
	
	public void setPosition(BaseVector2 position) {
		this.position.set(position);
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	public Vector2 getPositionCopy() {
		return position.clone();
	}
}
