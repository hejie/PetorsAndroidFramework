package com.skipifzero.petorsandroidframework;

import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.opengl.GLES10;

import com.skipifzero.petorsandroidframework.framework.DisplayScaling;
import com.skipifzero.petorsandroidframework.framework.input.PooledTouchInput;
import com.skipifzero.petorsandroidframework.framework.input.TouchInput;
import com.skipifzero.petorsandroidframework.framework.math.FinalVector2;
import com.skipifzero.petorsandroidframework.framework.opengl.Camera2D;
import com.skipifzero.petorsandroidframework.framework.opengl.FontRenderer;
import com.skipifzero.petorsandroidframework.framework.opengl.GLActivity;
import com.skipifzero.petorsandroidframework.framework.opengl.GLScreen;
import com.skipifzero.petorsandroidframework.framework.opengl.SpriteBatcher;
import com.skipifzero.petorsandroidframework.framework.opengl.Texture;
import com.skipifzero.petorsandroidframework.framework.opengl.TextureRegion;
import com.skipifzero.petorsandroidframework.framework.opengl.TextureUtil;

public class MainScreen implements GLScreen {

	private final GLActivity glActivity;
	
	private final TouchInput input;
	private final DisplayScaling scaling;
	
	private final FinalVector2 middleOfScreenDP;
	private final double viewWidthDP, viewHeightDP;
	
	private final Camera2D camera;
	private final SpriteBatcher batcher;
	private final FontRenderer font;
	
	private final TextureUtil texUtil;
	private Texture texture;
	private final TextureRegion backgroundRegion, objectRegion, redPixRegion;
	
	
	private double angle = 0;
	
	public MainScreen(GLActivity glActivity) {
		this.glActivity = glActivity;
		
		this.input = new PooledTouchInput(glActivity.getGLSurfaceView(), 10);
		this.scaling = new DisplayScaling(glActivity.getGLSurfaceView(), glActivity.getWindowManager());
		
		this.viewWidthDP = scaling.getViewWidthDps();
		this.viewHeightDP = scaling.getViewHeightDps();
		this.middleOfScreenDP = new FinalVector2(viewWidthDP/2, viewHeightDP/2);
		
		this.camera = new Camera2D(middleOfScreenDP.getX(), middleOfScreenDP.getY(), viewWidthDP, viewHeightDP); 
		this.batcher = new SpriteBatcher(100);
		this.font = new FontRenderer.Builder().build();
		
		this.texUtil = new TextureUtil("textures", Config.ARGB_8888);
		this.backgroundRegion = texUtil.getTextureRegion("BackgroundV1_512x512.png");
		this.objectRegion = texUtil.getTextureRegion("ObjectV1_128x128.png");
		this.redPixRegion = texUtil.getTextureRegion("1pxRed512.png");
	}
	
	@Override
	public void onResume() {
		texUtil.load(glActivity.getAssets()); //Reloads textures
		this.texture = texUtil.getTextureAtlas();
	}
	
	@Override
	public void update(double deltaTime, int fps) {
		input.update();
		
		angle += 50*deltaTime;
		if(angle > 360) {
			angle -= 360;
		}
	}

	@Override
	public void draw(double deltaTime, int fps) {
		//Set clear color and clear screen.
		GLES10.glClearColor(0, 0, 0, 1);
		GLES10.glClear(GLES10.GL_COLOR_BUFFER_BIT);
		
		//Enable alpha blending.
		GLES10.glEnable(GLES10.GL_BLEND);
		GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);
					
		//Enable textures
		GLES10.glEnable(GLES10.GL_TEXTURE_2D);
	
		camera.initialize(glActivity.getViewWidth(), glActivity.getViewHeight());
		
		batcher.beginBatch(texture);
		
		batcher.draw(middleOfScreenDP, viewWidthDP, viewHeightDP, backgroundRegion);
		
		batcher.draw(middleOfScreenDP, 25, 25, objectRegion);
		
		batcher.renderBatch();
		
		font.begin(Color.argb(255, 255, 0, 0));
		
		font.draw(middleOfScreenDP, 50, angle, "Hello World");
		
		font.render();
	}

	@Override
	public void onPause() {
		
	}

	@Override
	public void dipose() {
		texUtil.dispose();
	}
}
