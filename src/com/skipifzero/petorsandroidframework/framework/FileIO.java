package com.skipifzero.petorsandroidframework.framework;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Typeface;

public class FileIO {
	
	private AssetManager assets;
	
	public FileIO(AssetManager assets){
		this.assets = assets;
	}
	
	//Loads and returns a Bitmap.
	public Bitmap loadBitmap(String fileName, Bitmap.Config config){
		Options options = new Options();
		options.inPreferredConfig = config;
		
		Bitmap bitmap = null;
		InputStream in = null;
		
		//Load bitmap.
		try{
			in = assets.open(fileName);
			bitmap = BitmapFactory.decodeStream(in);
		}catch(IOException e){
			throw new RuntimeException("Couldn't load bitmap from asset file: \"" + fileName + "\"");
		}
		
		//Throws an RuntimeException if it all somehow failed.
		if(bitmap == null){
			throw new RuntimeException("Couldn't load bitmap from asset file: \"" + fileName + "\"");
		}
		
		//Try to close the input stream.
		if(in != null){
			try{
				in.close();
			}catch(IOException e){
				//Do nothing.
			}
		}
		
		return bitmap;
	}
	
	//Load and return Typeface font.
	public Typeface loadFont(String fileName){
		return Typeface.createFromAsset(assets, fileName);
	}
 }
