package com.appsbylyon.askalvis.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import com.appsbylyon.askalvis.R;
import com.appsbylyon.askalvis.custom.TypefaceSingleton;
import com.appsbylyon.askalvis.fragments.UserAgreementFragment.UserAgreementFragmentListener;

/**
 * Settings fragment class
 * 
 * @author Adam Lyon
 *
 */
public class SettingsFragment extends DialogFragment implements OnClickListener 
{
	private static final double DIALOG_TEXT_WIDTH = 0.6;
	private Button done;
	
	private CheckBox playSounds;
	
	private Typeface customFont;
	
	private TypefaceSingleton tfs;
	
	private SharedPreferences sharedPrefs;
	
	private boolean playSoundValue;
	
	private SettingsInteractor settingsInteractor;
	
	public interface SettingsInteractor {
		boolean isPlaySounds();
		void setPlaySounds(boolean playSounds);
	}
	
	public SettingsFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
	{
		settingsInteractor = (SettingsInteractor) getActivity();
		tfs = TypefaceSingleton.getInstance();
    	customFont = tfs.getTypeface();
    	View view = inflater.inflate(R.layout.setting_dialog_fragment, container);
        
        
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        done = (Button) view.findViewById(R.id.settings_dialog_ok_button);
        playSounds = (CheckBox) view.findViewById(R.id.play_sounds_check);
        
        done.setTypeface(customFont);
        playSounds.setTypeface(customFont);
        
        done.setOnClickListener(this);
        playSounds.setOnClickListener(this);
        
        int checkId = Resources.getSystem().getIdentifier("btn_check_holo_dark", "drawable", "android");
		playSounds.setButtonDrawable(checkId);
		
		playSoundValue = settingsInteractor.isPlaySounds();
		
		playSounds.setChecked(playSoundValue);
		
        return view;
    }

	
	@Override
	public void onClick(View view) 
	{
		switch(view.getId()) 
		{
		case R.id.settings_dialog_ok_button:
			settingsInteractor.setPlaySounds(playSoundValue);
			this.dismiss();
			break;
		case R.id.play_sounds_check:
			playSoundValue = playSounds.isChecked();
			break;
		}
	}
}
