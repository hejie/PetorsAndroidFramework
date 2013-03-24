package com.skipifzero.petorsandroidframework.framework.input;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

/**
 * A class used for checking if the back button is pressed or not.
 * 
 * @author Peter Hillerström
 * @version 1
 */

public class BackKeyInput implements OnKeyListener {

	private static final int BACK_KEY_CODE = 4;
	private boolean catchBackKey;
	
	private boolean backPressed = false;
	
	public BackKeyInput(View view, boolean catchBackKey) {
		view.setOnKeyListener(this);
		this.catchBackKey = catchBackKey;
		
		//Gives view focus so it can receive key events.
		view.setFocusableInTouchMode(true);
		view.requestFocus();
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		
		if(keyCode == BACK_KEY_CODE) {
			if(event.getAction() == KeyEvent.ACTION_DOWN) {
				synchronized(this) {
					backPressed = true;
					return catchBackKey;
				}
			}
			else if(event.getAction() == KeyEvent.ACTION_UP) {
				synchronized(this) {
					backPressed = false;
					return catchBackKey;
				}
			}
		}
		
		return false;
	}
	
	public boolean isBackPressed() {
		return backPressed;
	}
	
	public void catchBackKey(boolean catchBackKey) {
		this.catchBackKey = catchBackKey;
	}
}
