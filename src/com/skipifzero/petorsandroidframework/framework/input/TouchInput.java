package com.skipifzero.petorsandroidframework.framework.input;

import java.util.List;

/**
 * An interface for getting touch input.
 * @author Peter Hillerström
 * @version 1
 */

public interface TouchInput {
	
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
