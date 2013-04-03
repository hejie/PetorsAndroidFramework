package com.skipifzero.petorsandroidframework;

import com.skipifzero.petorsandroidframework.framework.opengl.GLActivity;
import com.skipifzero.petorsandroidframework.framework.opengl.GLScreen;

public class MainActivity extends GLActivity {

	@Override
	public GLScreen getInitialGLController(GLActivity glActivity) {
		return new MainScreen(glActivity);
	}

	@Override
	public boolean enableFullscreenMode() {
		return false;
	}
	
}
