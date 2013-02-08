package com.skipifzero.petorsandroidframework.framework.input;

import java.util.List;

public interface Input {
	
	/**
	 * Updates the TouchEvents.
	 * Should be called first in every frame.
	 */
	public void update();
	
	/**
	 * Returns a list of active TouchEvents.
	 * @return a list of TouchEvents.
	 */
	public List<TouchEvent> getTouchEvents();
}
