package com.appsbylyon.askalvis.custom;

import android.graphics.Typeface;

/**
 * Class to hold certain values to pass between classes
 * 
 * @author Adam Lyon
 *
 */
public class TypefaceSingleton 
{
	private static final TypefaceSingleton instance = new TypefaceSingleton();
	
	private Typeface customFont;
	private int screenWidth;
	
	private TypefaceSingleton() {}
	
	public static TypefaceSingleton getInstance()
	{
		return instance;
	}
	
	public void setTypeface(Typeface typeface) 
	{
		instance.customFont = typeface;
	}
	
	public Typeface getTypeface() 
	{
		return instance.customFont;
	}
	
	public void setScreenWidth(int width) 
	{
		screenWidth = width;
	}
	
	public int getScreenWidth() 
	{
		return screenWidth;
	}
}
