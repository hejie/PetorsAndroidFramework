package com.skipifzero.petorsandroidframework.framework.opengl;

public interface Texture {
	
	public void bind();
	public void unbind();
	public void setSmoothing(boolean smoothing);
	public void dispose();
	public boolean isSmoothed();
	public int getWidth();
	public int getHeight();
}
