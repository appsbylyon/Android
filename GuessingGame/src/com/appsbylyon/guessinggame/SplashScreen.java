package com.appsbylyon.guessinggame;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Splash screen activity
 * 
 * @author Adam Lyon
 *
 */
public class SplashScreen extends Activity 
{
	private ImageView logo;
	private Animation anim;
	
	private static final int SHOW_SPLASH_TIME = 2500;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_screen);
		logo = (ImageView) findViewById(R.id.logo);
		anim = AnimationUtils.loadAnimation(this, R.anim.spin);
		logo.startAnimation(anim);
		new Handler().postDelayed(new Runnable() 
		{
			@Override
			public void run() 
			{
				Intent mainActivity = new Intent(SplashScreen.this, GuessingGame.class);
				startActivity(mainActivity);
				finish();
			}
			
		}, SHOW_SPLASH_TIME);
	}
}//end of SplashScreen class
