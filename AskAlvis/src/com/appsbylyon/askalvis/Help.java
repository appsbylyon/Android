package com.appsbylyon.askalvis;

import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.appsbylyon.askalvis.custom.TypefaceSingleton;
import com.appsbylyon.askalvis.fragments.HelpDialog;
import com.appsbylyon.askalvis.fragments.HelpDialog.HelpDialogInterface;

/**
 * Activty for Help Screen
 * 
 * Modified: 6/12/14
 * 
 * @author Adam Lyon
 *
 */
public class Help extends FragmentActivity implements OnClickListener, HelpDialogInterface
{
	public static final int WHO_IS_ALVIS = 1;
	public static final int HOW_IT_WORKS = 2;
	public static final int HOW_TO_USE = 3;
	
	private Typeface customFont;
	
	private TextView title;
	
	private Button whoIsAlvis;
	private Button howItWorks;
	private Button howToUse;
	
	private TypefaceSingleton tfs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		tfs = TypefaceSingleton.getInstance();
		
		customFont = tfs.getTypeface();
		
		tfs.setScreenWidth(size.x);
		
		title = (TextView) findViewById(R.id.help_title);
		
		whoIsAlvis = (Button) findViewById(R.id.help_who_is_alvis_button);
		howItWorks = (Button) findViewById(R.id.help_how_it_works_button);
		howToUse = (Button) findViewById(R.id.help_how_to_use_button);
		
		title.setTypeface(customFont);
		whoIsAlvis.setTypeface(customFont);
		howItWorks.setTypeface(customFont);
		howToUse.setTypeface(customFont);
		
		whoIsAlvis.setOnClickListener(this);
		howItWorks.setOnClickListener(this);
		howToUse.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Bundle dialogChoice = new Bundle();
		switch(v.getId()) 
		{
		case R.id.help_who_is_alvis_button:
			dialogChoice.putInt(getResources().getString(R.string.help_dialog_choice), Help.WHO_IS_ALVIS); 
			break;
		case R.id.help_how_it_works_button:
			dialogChoice.putInt(getResources().getString(R.string.help_dialog_choice), Help.HOW_IT_WORKS); 
			break;
		case R.id.help_how_to_use_button:
			dialogChoice.putInt(getResources().getString(R.string.help_dialog_choice), Help.HOW_TO_USE); 
			break;
		}
		
		FragmentManager fm = getSupportFragmentManager();
		HelpDialog helpDialog = new HelpDialog();
		helpDialog.setArguments(dialogChoice);
		
		helpDialog.show(fm, "help_dialog_fragment");
	}

	@Override
	public void showAToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		
	}
}// End of Help Class