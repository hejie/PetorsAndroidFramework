package com.skipifzero.petorsandroidframework.framework.opengl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.skipifzero.petorsandroidframework.framework.FileIO;

public final class TextureUtil {
	private static final int MAX_TEXTURE_SIZE = 8192;
	private static final float TEXTURE_REGION_DELTA = 0.375f;
	
	private final String textureDirectory;
	private final String[] textureRegionStrings;
	private Texture texture;
	private TextureRegion textureAtlasRegion;
	private TextureRegion[] textureRegions;
	
	/**
	 * Creates a new TextureUtil with the content from the specified directory in the assets folder.
	 * @param assets the AssetManager
	 * @param textureDirectory the directory to read textures from
	 * @param quality the quality of the generated texture atlas
	 */
	public TextureUtil(AssetManager assets, String textureDirectory, Bitmap.Config quality) {
		this.textureDirectory = textureDirectory;
		this.textureRegionStrings = loadFileNames(assets);
		
		Bitmap[] bitmaps = loadBitmaps(assets, this.textureDirectory, this.textureRegionStrings, quality);	
		int largestBitmapSize = getLargestBitmapSize(bitmaps);
		int textureSize = getTextureSize(largestBitmapSize, bitmaps.length);
				
		//Create bitmap atlas and drawing tools.
		Bitmap bitmapAtlas = Bitmap.createBitmap(textureSize, textureSize, quality);
		Canvas canvas = new Canvas(bitmapAtlas);
		Paint paint = new Paint();
		
		//Arrays with values for creation of TextureRegions.
		float[] xLeftArray = new float[bitmaps.length];
		float[] yTopArray = new float[bitmaps.length];
		
		//Temp variables 
		float xLeft = 0;
		float yTop = 0;
		
		//Draws all textures to the texture atlas.
		for(int i = 0; i < bitmaps.length; i++) {
			canvas.drawBitmap(bitmaps[i], xLeft, yTop, paint);
			
			//Stores drawn location for creation of TextureRegions.
			xLeftArray[i] = xLeft;
			yTopArray[i] = yTop;
			
			//Updates location to draw next texture.
			xLeft += largestBitmapSize;
			if(xLeft + largestBitmapSize > textureSize) {
				yTop += largestBitmapSize;
				xLeft = 0;
			}
		}
		
		//Creates OpenGL texture from bitmap texture atlas.
		texture = new BitmapTexture(bitmapAtlas);
		textureAtlasRegion = new TextureRegion(texture, 0, 0, textureSize, textureSize);
		
		bitmapAtlas.recycle(); //Recycles bitmap texture atlas.
		
		//Creates TextureRegions for each individual texture on the texture atlas.
		textureRegions = new TextureRegion[bitmaps.length];
		for(int i = 0; i < bitmaps.length; i++) {
			textureRegions[i] = new TextureRegion(texture, xLeftArray[i] + TEXTURE_REGION_DELTA, yTopArray[i] + TEXTURE_REGION_DELTA, bitmaps[i].getWidth() - 2*TEXTURE_REGION_DELTA, bitmaps[i].getHeight() - 2*TEXTURE_REGION_DELTA);
		
			bitmaps[i].recycle(); //Recycles bitmap texture.
		}
	}
	
	/**
	 * Returns this TextureUtils directory path (relative to asset folder).
	 * @return this TextureUtils directory path
	 */
	public String getTextureDirectory() {
		return textureDirectory;
	}
	
	/**
	 * Returns a String array with all the filenames of the files in the texture directory.
	 * @return texture names
	 */
	public String[] getTextureNames() {
		return textureRegionStrings;
	}
	
	/**
	 * Returns the texture atlas.
	 * @return texture atlas
	 */
	public Texture getTextureAtlas() {
		return texture;
	}
	
	/**
	 * Returns a TextureRegion containing the whole texture atlas.
	 * @return TextureRegion containing the whole texture atlas
	 */
	public TextureRegion getTextureAtlasRegion() {
		return textureAtlasRegion;
	}
	
	/**
	 * Returns a TextureRegion containing the specified texture.
	 * @param textureFileName the file name of the texture
	 * @return TextureRegion containing the specified texture
	 */
	public TextureRegion getTextureRegion(String textureFileName) {
		return textureRegions[Arrays.binarySearch(textureRegionStrings, textureFileName)];
	}
	
	/**
	 * Returns a TextureRegion array containing all the textures in this TextureUtil's directory.
	 * @return TextureRegion array containing all the textures in this TextureUtil's directory
	 */
	public TextureRegion[] getTextureRegions() {
		return textureRegions;
	}
	
	/*
	 * Private methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	private int getTextureSize(int largestBitmapSize, int amountOfBitmaps) {
		int textureSize = 128;
		while(true) {
			int cellsPerRowOrCol = textureSize / largestBitmapSize;
			
			if(cellsPerRowOrCol*cellsPerRowOrCol >= amountOfBitmaps) {
				break;
			}
			
			textureSize *= 2;
		}
		if(textureSize > MAX_TEXTURE_SIZE) {
			throw new IllegalArgumentException("Couldn't fit textures on atlas, size needed " + textureSize + ", max size " + MAX_TEXTURE_SIZE + ".");
		}
		return textureSize;
	}
	
	private int getLargestBitmapSize(Bitmap[] bitmaps) {
		int width = 0;
		int height = 0;
		for(Bitmap bitmap : bitmaps) {
			if(bitmap.getWidth() > width) {
				width = bitmap.getWidth();
			}
			if(bitmap.getHeight() > height) {
				height = bitmap.getHeight();
			}
		}
		return width > height ? width : height;
	}
	
	private Bitmap[] loadBitmaps(AssetManager assets, String directory, String[] fileNames, Bitmap.Config config) {
		FileIO file = new FileIO(assets);
		Bitmap[] bitmaps = new Bitmap[fileNames.length];
		for(int i = 0; i < fileNames.length; i++) {
			bitmaps[i] = file.loadBitmap(directory + File.separator + fileNames[i], config);
		}
		return bitmaps;
	}
	
	private String[] loadFileNames(AssetManager assets) {
		try {
			String[] files = assets.list(textureDirectory);
			Arrays.sort(files);
			return files;
		} catch (IOException e) {
			throw new IllegalArgumentException("Couldn't load texture file names from texture directory");
		}
	}
}
