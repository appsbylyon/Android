package com.appsbylyon.guessinggame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

/**
 * About activity
 * 
 * Modified: 5/31/2014
 * 
 * @author Adam Lyon
 *
 */
public class About extends Activity implements OnClickListener {
	private Button doneButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);
		
		doneButton = (Button) findViewById(R.id.aboutDoneButton);
		
		doneButton.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View arg0) 
	{
		this.finish();
	}
}// End of About class
