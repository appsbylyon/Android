package com.appsbylyon.guessinggame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main user interface for the Guessing Game app
 * 
 * Modified: 5/31/2014
 * 
 * @author Adam Lyon
 *
 */
public class GuessingGame extends ActionBarActivity implements OnClickListener {
	
	protected static final int MSG_MP_RELEASE = 1;
	
	private TextView guessRangeLabel;
	private TextView guessResultLabel;
	private TextView answerLabel;
	
	private Button guessButton;
	
	private EditText userGuess;
	
	private CheckBox showAnswer;
	
	private Game game = new Game();
	
	private boolean gameOn = false;
	
	private SharedPreferences sharedPrefs;
	
	private String strRangeLabel = "Label Not Set";
	
	private boolean playSounds = false;
	private boolean intialized = false;
	
	private MediaPlayer mp;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guessing_game);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public void onResume() 
	{
		super.onResume();
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		/**
		 * The folowing line will only evaluate false the first time the app is ran.
		 * the min and max value are intialized into the preferences so that when
		 * the user goes to settings the values are not blank.
		 */
		boolean prefsIntialized = sharedPrefs.getBoolean("prefsIntialized", false);
		if (!prefsIntialized) 
		{
			SharedPreferences.Editor editor = sharedPrefs.edit();
			editor.putString("prefMinGuess", "1");
			editor.putString("prefMaxGuess", "100");
			editor.putBoolean("prefsIntialized", true);
			editor.commit();
		}
		int intMinGuess = Integer.parseInt(sharedPrefs.getString("prefMinGuess", "1"));
		int intMaxGuess = Integer.parseInt(sharedPrefs.getString("prefMaxGuess", "100"));
		playSounds = sharedPrefs.getBoolean("prefPlaySounds", false);
		game.setRange(intMinGuess,intMaxGuess );
		strRangeLabel = (intMinGuess-1)+ " and " + (intMaxGuess+1);
		if (guessRangeLabel != null) 
		{
			guessRangeLabel.setText(strRangeLabel);
		}
		if (intialized) 
		{
			resetGame();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.guessing_game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) 
		{
		case R.id.action_settings:
			Intent settingsIntent = new Intent(this, Settings.class);
			startActivity(settingsIntent);
			break;
		case R.id.action_about:
			Intent aboutIntent = new Intent(this, About.class);
			startActivity(aboutIntent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_guessing_game,
					container, false);
			return rootView;
		}
	}
	
	@Override
	public View onCreatePanelView(int featureId) 
	{
		guessRangeLabel = (TextView) findViewById(R.id.guessing_range_label);
		guessResultLabel = (TextView) findViewById(R.id.guess_result_label);
		answerLabel = (TextView) findViewById(R.id.answer_label);
		
		userGuess = (EditText) findViewById(R.id.user_guess);
		
		guessButton = (Button) findViewById(R.id.guess_button);
		
		showAnswer = (CheckBox) findViewById(R.id.show_answer);
		
		guessRangeLabel.setText(strRangeLabel);
		
		
		guessButton.setOnClickListener(this);
		showAnswer.setOnClickListener(this);
		
		int checkId = Resources.getSystem().getIdentifier("btn_check_holo_dark", "drawable", "android");
		showAnswer.setButtonDrawable(checkId);
		
		resetGame();
		intialized = true;
		
		return null;
	}
	
	private void showToast(String message) 
	{
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			case R.id.guess_button:
				if (gameOn) 
				{
					makeGuess();
				}
				else 
				{
					resetGame();
				}
				break;
			case R.id.show_answer:
				showAnswerToggle();
				break;
		}
	}
	
	/**
	 * Method that retrieves the users guess and responds based on the result
	 * returned by the Game class.
	 */
	private void makeGuess() 
	{
		String userGuessString = userGuess.getText().toString();
		String resultString = "PROBLEM!";
		if (userGuessString.length() > 0) 
		{
			switch (game.makeGuess(userGuessString)) 
			{
				case Game.GUESS_TOO_LOW:
					resultString = (userGuessString+" "+getResources().getString(R.string.result_text_too_low));
					playSound(R.raw.bad_guess);
					break;
				case Game.GUESS_CORRECT:
					resultString = (userGuessString+" "+getResources().getString(R.string.result_text_correct));
					gameOn = false;
					guessButton.setText(getResources().getString(R.string.button_play_again_text));
					playSound(R.raw.celebrate);
					break;
				case Game.GUESS_TOO_HIGH:
					resultString = (userGuessString+" "+getResources().getString(R.string.result_text_too_high));
					playSound(R.raw.bad_guess);
					break;
				case Game.GUESS_OUT_OF_RANGE:
					resultString = (userGuessString+" "+getResources().getString(R.string.result_text_out_of_range));
					playSound(R.raw.bad_guess);
					break;
			}
			if (!guessResultLabel.isShown()) 
			{
				guessResultLabel.setVisibility(View.VISIBLE);
			}
			guessResultLabel.setText(resultString);
			userGuess.setText("");
		}
		else 
		{
			showToast(getResources().getString(R.string.toast_no_value_entered));
		}
	}// end of makeGuess method
	
	/**
	 * Method to play a sound.
	 * 
	 * @param resId id of sound to play
	 */
	private void playSound(int resId) 
	{
		if(playSounds) 
		{
			mp = MediaPlayer.create(this, resId);
			mp.setOnCompletionListener(new OnCompletionListener() 
			{
				@Override
				public void onCompletion(final MediaPlayer mp) 
				{
					mp.release();
				}
			});
			mp.start();
		}
	}// End of playSound method
	
	/**
	 * Method to reset the game.  Called when user presses "Play Again" or when
	 * returning to the activity.
	 */
	private void resetGame() 
	{
		game.generateAnswer();
		guessButton.setText(getResources().getString(R.string.button_guess_text));
		userGuess.setText("");
		guessResultLabel.setVisibility(View.INVISIBLE);
		answerLabel.setVisibility(View.INVISIBLE);
		showAnswer.setChecked(false);
		gameOn = true;
	}// End of resetGame method
	
	/**
	 * Method that responds to the user checking the "Show Answer" checkbox
	 */
	private void showAnswerToggle() 
	{
		if (showAnswer.isChecked()) 
		{
			answerLabel.setText(getResources().getString(R.string.answer_label)+" "+game.getAnswer());
			answerLabel.setVisibility(View.VISIBLE);
			playSound(R.raw.fail);
		}
		else 
		{
			answerLabel.setVisibility(View.INVISIBLE);
		}
	}// End of showAnswerToggle method
}// End of GuessingGame class
