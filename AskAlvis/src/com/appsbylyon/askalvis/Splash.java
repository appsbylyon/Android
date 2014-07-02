package com.appsbylyon.askalvis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.appsbylyon.askalvis.custom.TypefaceSingleton;
import com.appsbylyon.askalvis.fragments.UserAgreementFragment;
import com.appsbylyon.askalvis.fragments.UserAgreementFragment.UserAgreementFragmentListener;

/**
 * Splash Screen Activity
 * 
 * Modified: 6/12/214
 * 
 * @author Adam Lyon
 *
 */
public class Splash extends FragmentActivity implements UserAgreementFragmentListener
{
	private boolean hasUserAgreed;
	
	private Typeface customFont;
	
	private TypefaceSingleton tfs;
	
	private SharedPreferences sharedPrefs;
	
	private static final int SHOW_SPLASH_TIME = 2500;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		hasUserAgreed = sharedPrefs.getBoolean(getResources().getString(R.string.pref_has_user_agreed), false);
		customFont = Typeface.createFromAsset(getAssets(), "fonts/arabian_night.ttf");
		
		tfs = TypefaceSingleton.getInstance();
		tfs.setTypeface(customFont);
		
		new Handler().postDelayed(new Runnable() 
		{
			@Override
			public void run() 
			{
				//CHANGE TO (!hasUserAgreed)
				if (!hasUserAgreed) 
				{
					showUserAgreementDialog();
				} 
				else 
				{
					startMainActivity();
				}
			}
			
		}, SHOW_SPLASH_TIME);
		
	}
	
	private void showUserAgreementDialog() 
	{
		FragmentManager fm = getSupportFragmentManager();
		UserAgreementFragment userAgreement = new UserAgreementFragment();
		userAgreement.show(fm, "user_agreement_fragment");
	}

	@Override
	public void onUserClick(int result) 
	{
		switch (result) 
		{
		case UserAgreementFragment.USER_AGREED:
			Toast.makeText(this, getResources().getString(R.string.user_agreed_toast), Toast.LENGTH_SHORT).show();
			sharedPrefs.edit().putBoolean(getResources().getString(R.string.pref_has_user_agreed), true).commit();
			startMainActivity();
			break;
		case UserAgreementFragment.USER_DECLINED:
			Toast.makeText(this, getResources().getString(R.string.user_declined_toast), Toast.LENGTH_SHORT).show();
			finish();
		}
		
	}
	
	private void startMainActivity() 
	{
		Intent mainActivity = new Intent(Splash.this, AskAlvis.class);
		startActivity(mainActivity);
		finish();
	}
	
	@Override
	public void errorOccurred(String errorMessage) {
		Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
	}

	@Override
	public void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		
	}
}// End of Splash class