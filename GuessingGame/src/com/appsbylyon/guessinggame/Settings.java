package com.appsbylyon.guessinggame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Setting Activity
 * 
 * Modified: 5/31/2014
 * 
 * @author Adam Lyon
 *
 */
public class Settings extends PreferenceActivity implements OnPreferenceChangeListener
{
	private EditTextPreference minGuess;
	private EditTextPreference maxGuess;
	
	private int intMinGuess = 0;
	private int intMaxGuess = 0;
	
	private SharedPreferences sharedPrefs;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settings);
		
		this.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.prefgradient));
		
		minGuess = (EditTextPreference) getPreferenceScreen().findPreference("prefMinGuess");
		maxGuess = (EditTextPreference) getPreferenceScreen().findPreference("prefMaxGuess");
	
		minGuess.setOnPreferenceChangeListener(this);
		maxGuess.setOnPreferenceChangeListener(this);
		
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		intMinGuess = Integer.parseInt(sharedPrefs.getString("prefMinGuess", "1"));
		intMaxGuess = Integer.parseInt(sharedPrefs.getString("prefMaxGuess", "100"));
		
	}

	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		if (arg1.toString().length() > 0) 
		{
			try 
			{
				int parsedInt = Integer.parseInt(arg1.toString());
				if (parsedInt > 2147483645) 
				{
					throw new NumberFormatException();
				}
				if (arg0.equals(minGuess)) 
				{
					int newMinGuess = Integer.parseInt(arg1.toString());
					if (newMinGuess >= intMaxGuess) 
					{
						Toast.makeText(this, getResources().getString(R.string.error_new_min_above_max), Toast.LENGTH_LONG).show();
						return false;
					}
					else 
					{
						if (newMinGuess == 0) 
						{
							Toast.makeText(this, getResources().getString(R.string.error_min_value_zero), Toast.LENGTH_SHORT).show();
							return false;
						}
						else 
						{
							Toast.makeText(this, getResources().getString(R.string.min_value_change_success)+" "+newMinGuess, Toast.LENGTH_LONG).show();
							intMinGuess = newMinGuess;
							return true;
						}
					}
				}
				else if (arg0.equals(maxGuess)) 
				{
					int newMaxGuess = Integer.parseInt(arg1.toString());
					if (newMaxGuess <= intMinGuess) 
					{
						Toast.makeText(this, getResources().getString(R.string.error_new_max_below_min), Toast.LENGTH_LONG).show();
						return false;
					}
					else 
					{
						
						Toast.makeText(this, getResources().getString(R.string.max_value_change_success)+" "+newMaxGuess, Toast.LENGTH_LONG).show();
						intMaxGuess = newMaxGuess;
						return true;
					}
				}
			}
			catch (NumberFormatException NFE) 
			{
				Toast.makeText(this, getResources().getString(R.string.error_invallid_input), Toast.LENGTH_LONG).show();
			}
		}
		return false;
	}
}// End of Settings class
