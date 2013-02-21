package com.skipifzero.petorsandroidframework;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES10;

import com.skipifzero.petorsandroidframework.framework.DisplayScaling;
import com.skipifzero.petorsandroidframework.framework.FileIO;
import com.skipifzero.petorsandroidframework.framework.input.Input;
import com.skipifzero.petorsandroidframework.framework.input.PooledInput;
import com.skipifzero.petorsandroidframework.framework.opengl.Camera2D;
import com.skipifzero.petorsandroidframework.framework.opengl.FileTexture;
import com.skipifzero.petorsandroidframework.framework.opengl.FontRenderer;
import com.skipifzero.petorsandroidframework.framework.opengl.FontRenderer.HorizontalAlignment;
import com.skipifzero.petorsandroidframework.framework.opengl.FontRenderer.VerticalAlignment;
import com.skipifzero.petorsandroidframework.framework.opengl.OpenGLActivity;
import com.skipifzero.petorsandroidframework.framework.opengl.SpriteBatcher;
import com.skipifzero.petorsandroidframework.framework.opengl.Texture;
import com.skipifzero.petorsandroidframework.framework.opengl.TextureRegion;
import com.skipifzero.petorsandroidframework.framework.opengl.TextureUtil;
import com.skipifzero.petorsandroidframework.framework.vector.FinalVector2;

public class MainActivity extends OpenGLActivity {

	private FileIO file;
	private Input input;
	private DisplayScaling scaling;
	
	private SpriteBatcher batcher;
	private Camera2D camera;
	private FontRenderer font;
	
	private Texture backgroundTexture;
	private TextureRegion backgroundRegion;
	
	
	private FinalVector2 middleOfScreen;
	private double viewWidthDP, viewHeightDP;
	
	private FinalVector2 backgroundPos;
	
	private float angle = 0;
	
	private TextureUtil texUtil;
	
	@Override
	public void create() {
		file = new FileIO(getAssets());
		input = new PooledInput(getGLSurfaceView(), 10);
		scaling = new DisplayScaling(getGLSurfaceView(), getWindowManager());
		
		batcher = new SpriteBatcher(100);
	}

	@Override
	public void resume() {
		viewWidthDP = scaling.getViewWidthDps();
		viewHeightDP = scaling.getViewHeightDps();
		middleOfScreen = new FinalVector2(viewWidthDP/2, viewHeightDP/2);
		
		camera = new Camera2D(middleOfScreen.getX(), middleOfScreen.getY(), viewWidthDP, viewHeightDP);
		
		backgroundPos = new FinalVector2(middleOfScreen.getX(), viewHeightDP - viewWidthDP/2);
		
		this.font = new FontRenderer.Builder().build();
		
		backgroundTexture = new FileTexture(file, "BackgroundV1_512x512.png", Bitmap.Config.ARGB_8888);
		backgroundRegion = new TextureRegion(backgroundTexture, 0, 0, 512, 512);
		
		
		texUtil = new TextureUtil(getAssets(), "textures", Bitmap.Config.ARGB_8888);
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
	
		camera.initialize(getViewWidth(), getViewHeight());
		batcher.beginBatch(backgroundTexture);
		batcher.draw(middleOfScreen, 5, 5, backgroundRegion);
		//batcher.draw(backgroundPos, viewWidthDP, viewWidthDP, backgroundRegion);
		batcher.renderBatch();
		
		
		batcher.beginBatch(texUtil.getTextureAtlas());
		//batcher.draw(middleOfScreen, viewWidthDP, viewWidthDP, texUtil.getTextureAtlasRegion());
		//batcher.draw(middleOfScreen, viewWidthDP, viewWidthDP, texUtil.getTextureRegion("BackgroundV1_512x512.png"));
		batcher.draw(middleOfScreen, viewWidthDP, viewWidthDP, texUtil.getTextureRegion("1pxRed512.png"));
		batcher.renderBatch();
		
//		font.begin(Color.RED);
//		
//		font.setHorizontalAlignment(HorizontalAlignment.LEFT);
//		font.setVerticalAlignment(VerticalAlignment.TOP);
//		font.draw(0, (float)viewHeightDP, 16, "Å Top left corner");
//		
//		font.setHorizontalAlignment(HorizontalAlignment.LEFT);
//		font.setVerticalAlignment(VerticalAlignment.CENTER);
//		font.draw(0, 8, 16, "Normal 16. Random blagjefeofj");
//		font.draw(0, 64 - 16, 32, "Normal 32. åÄÅajö <->");
//		font.draw(0, 350, 32, "VA AV WAR RAW");
//		
//		font.setHorizontalAlignment(HorizontalAlignment.RIGHT);
//		font.draw((float)viewWidthDP, (float)(viewHeightDP-16), 32, "th?2042[]()=> END");
//		
//		font.render();
//		
//		
//		font.begin(Color.argb(100, 255, 255, 255));
//		
//		font.setHorizontalAlignment(HorizontalAlignment.RIGHT);
//		font.setVerticalAlignment(VerticalAlignment.CENTER);
//		
//		font.draw((float)middleOfScreen.getX(), (float)middleOfScreen.getY(), 32f, angle, "Rotating text");
//		
//		
//		font.setHorizontalAlignment(HorizontalAlignment.LEFT);
//		font.setVerticalAlignment(VerticalAlignment.TOP);
//		
//		font.draw((float)viewWidthDP, (float)viewHeightDP, 20, 270, "Side text (180 degrees)");
//		
//		font.render();
//		
//		
//		
//		String[] strings = texUtil.getTextureNames();
//		if(strings != null) {
//		
//			font.setHorizontalAlignment(HorizontalAlignment.LEFT);
//			font.setVerticalAlignment(VerticalAlignment.TOP);
//			font.begin(Color.WHITE);
//		
//			for(int i = 0; i < strings.length; i++) {
//				font.draw(0, 32 + 32*i, 32, strings[i]);
//			}
//			
//			font.render();
//		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		backgroundTexture.dispose();
	}
}
