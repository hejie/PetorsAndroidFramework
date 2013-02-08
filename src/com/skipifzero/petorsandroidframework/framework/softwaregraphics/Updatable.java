package com.skipifzero.petorsandroidframework.framework.softwaregraphics;

import android.graphics.Bitmap;

public interface Updatable {
	public void update(double deltaTime, int fps);
	public Bitmap draw(double deltaTime, int fps);
}
