package com.appsbylyon.askalvis;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appsbylyon.askalvis.answergenerator.AnswerGenerator;
import com.appsbylyon.askalvis.fragments.SettingsFragment;
import com.appsbylyon.askalvis.fragments.SettingsFragment.SettingsInteractor;

/**
 * Main Class for App
 * 
 * Modified: 6/12/14
 * 
 * @author Adam Lyon
 *
 */
public class AskAlvis extends Activity implements SensorEventListener, AnimationListener, OnCompletionListener, SettingsInteractor
{
	private static final int FADING_IN_BUBBLE_1 = 0;
	private static final int FADING_IN_BUBBLE_2 = 1;
	private static final int FADING_OUT_BUBBLE_1 = 3;
	private static final int FADING_OUT_BUBBLE_2 = 4;
	private static final int NO_EVENT_WAITING = 0;
	private static final int START_VOICE_WAITING = 1;
	private static final int REQUEST_CODE = 1234;
	
	private final int SHAKE_THRESHOLD = 400;
	private final int LEFT_MARGIN = 5;
	private final int CLOUD_DURATION = 2000;
	private final int FADE_DURATION = 1000;
	private final int HOVER_DURATION = 2000;
	
	private final double SIZE_CONSTANT_WIDTH = 0.008333;
	private final double SIZE_CONSTANT_HEIGHT = 0.005482;
	private final double ALVIS_WIDTH_HEIGHT_RATIO = 1.93;
	private final double ALVIS_X_POS_ADJUST = 0.05;
	private final double LAMP_SCALE_FACTOR = 0.6;
	private final double ALVIS_SCALE_FACTOR = 0.4;
	private final double LAMP_WIDTH_HEIGHT_RATIO = 0.55913;
	private final double LAMP_TOUCH_X_MIN = 0.15;
	private final double LAMP_TOUCH_X_MAX = 0.4666;
	private final double LAMP_TOUCH_Y_MIN = 0.8168;
	private final double LAMP_TOUCH_Y_MAX = 0.9594;
	private final double LAMP_TOUCH_DISTANCE = 0.4;
	private final double BUBBLE_WIDTH = 0.4;
	private final double BUBBLE_BOTTOM = 0.5;
	
	
	private int shakeCount = 0;
	private int lampRubCount = 0;
	private int alvisWidth;
	private int alvisHeight;
	private int alvisPos;
	private int alvisBottom;
	private int xHoverPos;
	private int width;
	private int lampTouchXMin;
	private int lampTouchXMax;
	private int lampTouchYMin;
	private int lampTouchYMax;
	private int lampTouchDistance;
	private int lastTouchX = 0;
	private int lastTouchY = 0;
	private int bubbleWidth;
	private int bubbleBottom;
	private int currBubbleFadeIn;
	private int currBubbleFadeOut;
	private int waitingEvent = NO_EVENT_WAITING;
	
	private boolean alvisPresent = false;
	private boolean alvisReadyForTap = false;
	private boolean isAlvisMovingRight = true;
	private boolean playSounds = false;
	private boolean isLampShaking = false;
	private boolean questionAsked = false;
	private boolean waitForAnswer = false;
	private boolean canDoSpeechToText = true;
	private boolean bubbleOneVisible = false;
	private boolean bubbleTwoVisible = false;
	
	private float x;
	private float y;
	private float z;
	private float lastx;
	private float lasty;
	private float lastz;
	
	private double moveDistance = 0;
	
	private long lastUpdate;
	private long[] shakeLampVibPattern = {0,100,50,100,50,100}; 
	private long alvisLastRepeatStartTime;
	
	private AnswerGenerator answerGen = new AnswerGenerator();;
	private AnswerReceiver answerReceiver;
	
	private SensorManager sensorManager;
	
	private Sensor accSensor;
	
	private ImageView lampImage;
	private ImageView alvis;
	private ImageView cloud;
	
	private TextView header;
	private TextView bubble1;
	private TextView bubble2;
	
	private Typeface customFont;
	
	private AnimationSet cloudSet = new AnimationSet(false);
	private AnimationSet alvisIn = new AnimationSet(false);
	
	private Animation shakeLamp1;
	
	private TranslateAnimation alvisHover;
	private TranslateAnimation alvisExitLamp;
	private TranslateAnimation staticAnim;
	
	private ScaleAnimation cloudGrow;
	
	private AlphaAnimation fadeIn;
	private AlphaAnimation fadeOut;
	private AlphaAnimation bubbleFadeIn;
	private AlphaAnimation bubbleFadeOut;
	
	private MediaPlayer mediaPlayer;
	private MediaPlayer mediaPlayer2;
	
	private SharedPreferences sharedPrefs;
	
	private Vibrator vibrator;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ask_alvis);
		
		PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            showToast("Cannot Do Voice Recognition", Toast.LENGTH_SHORT);
        	canDoSpeechToText = false;
        }
		
        customFont = Typeface.createFromAsset(getAssets(), "fonts/arabian_night.ttf");
		
		IntentFilter filter = new IntentFilter(AnswerReceiver.ANSWER_READY);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		answerReceiver = new AnswerReceiver();
		registerReceiver(answerReceiver, filter);
		
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		boolean prefsIntialized = sharedPrefs.getBoolean(getResources().getString(R.string.pref_prefs_initialized), false);
		if (!prefsIntialized) 
		{
			SharedPreferences.Editor editor = sharedPrefs.edit();
			editor.putBoolean(getResources().getString(R.string.pref_prefs_initialized), true);
			editor.putBoolean(getResources().getString(R.string.pref_play_sounds), true);
			editor.commit();
		}
		
		playSounds = sharedPrefs.getBoolean(getResources().getString(R.string.pref_play_sounds), true);
		
		vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		
		lampImage = (ImageView) findViewById(R.id.lamp_image);
		alvis = (ImageView) findViewById(R.id.alvis);
		cloud = (ImageView) findViewById(R.id.cloud);
		header = (TextView) findViewById(R.id.main_header);
		bubble1 = (TextView) findViewById(R.id.speech_bubble1);
		bubble2 = (TextView) findViewById(R.id.speech_bubble2);
		shakeLamp1 = AnimationUtils.loadAnimation(this, R.anim.shake_lamp);
		
		header.setTypeface(customFont);
		header.setBackgroundResource(R.drawable.scroll3_9);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		shakeLamp1.setAnimationListener(this);
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		width = size.x;
		int height = size.y;
		int lampWidth = (int) (LAMP_SCALE_FACTOR*width);
		int yHoverStartPos = (int) ((SIZE_CONSTANT_HEIGHT)*height+1);
		int yHoverEndPos = (int) ((SIZE_CONSTANT_HEIGHT*height*6)+1);
		int lampExitStartPos = (int) ((SIZE_CONSTANT_HEIGHT*height*8)+1);
		alvisPos = (int) (lampWidth-(width/2)+(width*ALVIS_X_POS_ADJUST));
		alvisWidth = (int) (ALVIS_SCALE_FACTOR*width);
		alvisHeight = (int) (alvisWidth * ALVIS_WIDTH_HEIGHT_RATIO);
		alvisBottom = (int) (height-(lampWidth*LAMP_WIDTH_HEIGHT_RATIO));
		xHoverPos = (int)((SIZE_CONSTANT_WIDTH*width*5)+1);
		lampTouchXMin = (int) ((width * LAMP_TOUCH_X_MIN)+1);
		lampTouchXMax = (int) ((width * LAMP_TOUCH_X_MAX)+1);
		lampTouchYMin = (int) ((height * LAMP_TOUCH_Y_MIN)+1);
		lampTouchYMax = (int) ((height * LAMP_TOUCH_Y_MAX)+1);
		lampTouchDistance = (int) (width * LAMP_TOUCH_DISTANCE);
		bubbleWidth = (int) (width * BUBBLE_WIDTH);
		bubbleBottom = (int) (width * BUBBLE_BOTTOM);
		
		
		alvis.setMaxWidth(alvisWidth);
		cloud.setMaxWidth(alvisWidth);
		bubble1.setWidth(bubbleWidth);
		bubble2.setWidth(bubbleWidth);
		bubble1.setTypeface(customFont);
		bubble2.setTypeface(customFont);
		bubble1.setY(bubbleBottom);
		bubble2.setY(bubbleBottom);
		alvis.setVisibility(View.INVISIBLE);
		cloud.setVisibility(View.INVISIBLE);
		bubble1.setVisibility(View.INVISIBLE);
		bubble2.setVisibility(View.INVISIBLE);
		
		
		
		lampImage.setMaxWidth(lampWidth);
		lampImage.setX(LEFT_MARGIN);
		
		cloudGrow = new ScaleAnimation (0, 1, 0, 1,Animation.ABSOLUTE,((int)((alvisWidth/2)-(width*ALVIS_X_POS_ADJUST))),Animation.ABSOLUTE,((int)(ALVIS_WIDTH_HEIGHT_RATIO*alvisWidth)));
		cloudGrow.setDuration(CLOUD_DURATION);
		
		alvisExitLamp = new TranslateAnimation((alvisPos),(alvisPos-xHoverPos),lampExitStartPos,yHoverStartPos);
		alvisExitLamp.setDuration(CLOUD_DURATION);
		
		fadeOut = new AlphaAnimation (1, 0); //first argument is start alpha, second is finish alpha
		fadeOut.setDuration(FADE_DURATION);
		fadeOut.setStartOffset(CLOUD_DURATION);
				
		bubbleFadeOut = new AlphaAnimation (1, 0); //first argument is start alpha, second is finish alpha
		bubbleFadeOut.setDuration(FADE_DURATION);
		bubbleFadeOut.setFillAfter(true);
		bubbleFadeOut.setAnimationListener(this);
		
		cloudSet.addAnimation(cloudGrow);
		cloudSet.addAnimation(alvisExitLamp);
		cloudSet.addAnimation(fadeOut);
		cloudSet.setFillAfter(true);
		
		fadeIn = new AlphaAnimation (0,1);
		fadeIn.setDuration(FADE_DURATION);
		fadeIn.setStartOffset(CLOUD_DURATION);
		
		bubbleFadeIn = new AlphaAnimation (0,1);
		bubbleFadeIn.setDuration(FADE_DURATION);
		bubbleFadeOut.setFillAfter(true);
		bubbleFadeIn.setAnimationListener(this);
		
		staticAnim = new TranslateAnimation((alvisPos-xHoverPos),(alvisPos-xHoverPos),yHoverStartPos,yHoverStartPos);
		staticAnim.setDuration(FADE_DURATION);
		staticAnim.setStartOffset(CLOUD_DURATION);
		
		alvisIn.addAnimation(fadeIn);
		alvisIn.addAnimation(staticAnim);
		alvisIn.setFillAfter(true);
		alvisIn.setAnimationListener(this);
		
		alvisHover = new TranslateAnimation((alvisPos-xHoverPos),(alvisPos+xHoverPos),yHoverStartPos,yHoverEndPos);
		alvisHover.setDuration(HOVER_DURATION);
		alvisHover.setRepeatCount(Animation.INFINITE);
		alvisHover.setRepeatMode(Animation.REVERSE);
		alvisHover.setAnimationListener(this);
	}
	
	
	
	private void showToast(String message, int duration) 
	{
		Toast.makeText(this, message, duration).show();
	}
	
	@Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accSensor);
    }
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.ask_alvis, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int id = item.getItemId();
		Intent menuIntent = null;
		switch(id) 
		{
		case R.id.action_about:
			menuIntent = new Intent(this, About.class);
			break;
		case R.id.action_settings:
			FragmentManager fm = getFragmentManager();
			SettingsFragment settingsFrag = new SettingsFragment();
			settingsFrag.show(fm, "settings_fragment");
			break;
		case R.id.action_help:
			menuIntent = new Intent(this, Help.class);
			break;
		}
		if (menuIntent !=null) 
		{
			startActivity(menuIntent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSensorChanged(SensorEvent event) 
	{
		long curTime = System.currentTimeMillis();
	    
	    if ((curTime - lastUpdate) > 100) 
	    {
		    long diffTime = (curTime - lastUpdate);
		    lastUpdate = curTime;
			float N[] = new float[9];
            float orientation[] = new float[3];
            SensorManager.getOrientation(N, orientation);
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
            
            float speed = Math.abs(x+y+z-lastx-lasty-lastz) / diffTime * 10000;
            
            if (speed > SHAKE_THRESHOLD)
            {
            	shakeCount++;
            }
            else 
            {
            	shakeCount = 0;
            }
            if (shakeCount == 2) 
            {
            	if (questionAsked && !waitForAnswer && !canDoSpeechToText) 
            	{
            		getAnswer();
            		
            	}
            	shakeCount = 0;
            }
            lastx = x;
            lasty = y;
            lastz = z;
	    }
	}
	
	private void getAnswer() 
	{
		playSound(R.raw.please_wait);
		waitForAnswer = true;
		swapBubble(getResources().getString(R.string.alvis_thinking));
		Intent answerIntent = new Intent(this, AnswerGenerator.class);
		startService(answerIntent);
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
	    int x = (int)event.getX();
	    int y = (int)event.getY();
	    switch (event.getAction()) 
	    {
	        case MotionEvent.ACTION_DOWN:
	        	lastTouchX = x;
	        	lastTouchY = y;
	        	if (this.alvisPresent && this.alvisReadyForTap) 
	        	{
	        		double pos = (double) (System.currentTimeMillis()-alvisLastRepeatStartTime)/(this.HOVER_DURATION)*(2*xHoverPos);
	        		double currAlvisLeft;
	        		if (isAlvisMovingRight) 
	        		{
	        			currAlvisLeft = (width/2)+(-xHoverPos)+pos;
	        		} 
	        		else 
	        		{
	        			currAlvisLeft = (width/2)+(+xHoverPos) - pos;
	        		}
	        		if (((x >= currAlvisLeft) && (x <= (currAlvisLeft + alvisWidth))) && ((y <= alvisBottom) && (y >= (alvisBottom - alvisHeight))))
	        		{
	        			swapBubble(getResources().getString(R.string.alvis_speak_question));
	        			if (!canDoSpeechToText) 
	        			{
	        				header.setText(getResources().getString(R.string.title_shake_for_answer));
	        				playSound(R.raw.speak_your_question);
		        			}
	        			else 
	        			{
	        				header.setText(getResources().getString(R.string.header_speak));
	        				waitingEvent = START_VOICE_WAITING;
	        				playSound(R.raw.speak_your_question);
		        			}
	        			questionAsked = true;
	        			alvisReadyForTap = false;
	        		}
	        	}
	        	
	        case MotionEvent.ACTION_MOVE:
	        	if ((!alvisPresent) && (!isLampShaking) && ((lastTouchX >= lampTouchXMin) && (lastTouchX <= lampTouchXMax))
	        		&& ((lastTouchY >= lampTouchYMin) &&(lastTouchY <= lampTouchYMax)))
	        	{
	        		double distance = Math.sqrt((Math.pow((lastTouchX-x), 2) + Math.pow((lastTouchY-y), 2)));
        			moveDistance += distance;
        			if (moveDistance > lampTouchDistance) 
        			{
        				doLampShake();
        			}
	        	}
	        	lastTouchX = x;
	        	lastTouchY = y;
	        case MotionEvent.ACTION_UP:
	    }
	return false;
	}

	private void doLampShake() 
	{
		if (!alvisPresent && !isLampShaking) 
		{
			isLampShaking = true;
			if (lampRubCount == 2) 
			{
				playSound(R.raw.lamp_shake);
				playSound(R.raw.lamp_exit);
				lampImage.startAnimation(shakeLamp1);
				vibrator.vibrate(CLOUD_DURATION);
				alvis.startAnimation(alvisIn);
				cloud.startAnimation(cloudSet);
				alvisPresent = true;
			}
			else 
			{
				playSound(R.raw.lamp_shake);
				lampImage.startAnimation(shakeLamp1);
				vibrator.vibrate(shakeLampVibPattern, -1);
				lampRubCount++;
			}
		}
	}
	
	private void startEvent() 
	{
		switch(waitingEvent) 
		{
		case START_VOICE_WAITING:
			waitingEvent = NO_EVENT_WAITING;
			startVoiceRecognitionActivity();
			break;
		}
	}

	@Override
	public void onAnimationEnd(Animation anim) 
	{
		if (anim == alvisIn) 
		{
			alvis.startAnimation(alvisHover);
			alvisLastRepeatStartTime = System.currentTimeMillis();
			alvisReadyForTap = true;
			header.setText(getResources().getString(R.string.title_tap_alvis));
			playSound(R.raw.alvis_laugh);
		}
		if (anim.equals(shakeLamp1)) 
		{
			isLampShaking = false;
			moveDistance = 0;
		}
		if (anim == bubbleFadeIn) 
		{
			switch (currBubbleFadeIn) 
			{
			case FADING_IN_BUBBLE_1:
				bubble1.setVisibility(View.VISIBLE);
				break;
			case FADING_IN_BUBBLE_2:
				bubble2.setVisibility(View.VISIBLE);
				break;
			}
		}
		if (anim == bubbleFadeOut) 
		{
			switch (currBubbleFadeOut) 
			{
			case FADING_OUT_BUBBLE_1:
				bubble1.setVisibility(View.INVISIBLE);
				break;
			case FADING_OUT_BUBBLE_2:
				bubble2.setVisibility(View.INVISIBLE);
				break;
			}
		}
	}
	
	@Override
	public void onAnimationRepeat(Animation anim)
	{
		if (anim.equals(alvisHover)) 
		{
			isAlvisMovingRight = !isAlvisMovingRight;
			alvisLastRepeatStartTime = System.currentTimeMillis();
		}
	}
	
	/**
	 * Method to play a sound.
	 * 
	 * @param resId id of sound to play
	 */
	private void playSound(int resId) 
	{
		if(playSounds) 
		{
			if (mediaPlayer ==  null) 
			{
				mediaPlayer = MediaPlayer.create(this, resId);
				mediaPlayer.setOnCompletionListener(this);
				mediaPlayer.start();
			}
			else 
			{
				mediaPlayer2 = MediaPlayer.create(this, resId);
				mediaPlayer2.setOnCompletionListener(this);
				mediaPlayer2.start();
			}
		}
		else 
		{
			startEvent();
		}
	}// End of playSound method
	

	@Override
	public void onCompletion(MediaPlayer mp) 
	{
		if (mp.equals(mediaPlayer)) 
		{
			mediaPlayer.release();
			mediaPlayer = null;
		}
		else if (mp.equals(mediaPlayer2)) 
		{
			mediaPlayer2.release();
			mediaPlayer2 = null;
		}
		startEvent();
	}
	
	


	@Override
	public void setPlaySounds(boolean playSounds) {
		sharedPrefs.edit().putBoolean(getResources().getString(R.string.pref_play_sounds), playSounds).commit();
		this.playSounds = playSounds;
	}
	
	@Override
	public boolean isPlaySounds() 
	{
		return playSounds;
	}
	
	private void startVoiceRecognitionActivity()
    {
        if (canDoSpeechToText) 
        {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask Alvis Your Question!");
	        startActivityForResult(intent, REQUEST_CODE);
        }
    }
 
    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            if ((matches.size() > 0) && (matches != null)) 
            {
	            switch(answerGen.isValidQuestion(matches)) 
	            {
	            case AnswerGenerator.BAD_QUESTION:
	            	questionAsked = false;
	        		waitForAnswer = false;
	        		alvisReadyForTap = true;
	        		swapBubble(getResources().getString(R.string.alvis_bad_question));
	        		header.setText(getResources().getString(R.string.title_tap_alvis));
	        		playSound(R.raw.please_ask_yes_no);
	        		break;
	            case AnswerGenerator.UNKNOWN_QUESTION:
	            	questionAsked = false;
	        		waitForAnswer = false;
	        		alvisReadyForTap = true;
	        		swapBubble(getResources().getString(R.string.alvis_unknown_question));
	        		header.setText(getResources().getString(R.string.title_tap_alvis));
	        		playSound(R.raw.did_not_understand);
	        		break;
	            case AnswerGenerator.GOOD_QUESTION:
	            	getAnswer();
	            	break;
	            }
            }
            else 
            {
            	questionAsked = false;
        		waitForAnswer = false;
        		alvisReadyForTap = true;
        		swapBubble("Something went wrong...");
        		header.setText(getResources().getString(R.string.title_tap_alvis));
        		
            }
        }
    	else 
        {
        	questionAsked = false;
    		waitForAnswer = false;
    		alvisReadyForTap = true;
    		header.setText(getResources().getString(R.string.title_tap_alvis));
    		
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void swapBubble(String message) 
    {
    	if (bubbleOneVisible) 
    	{
    		bubble2.setText(message);
    		this.currBubbleFadeOut = FADING_OUT_BUBBLE_1;
    		this.currBubbleFadeIn = FADING_IN_BUBBLE_2;
    		bubble1.startAnimation(bubbleFadeOut);
    		bubble2.startAnimation(bubbleFadeIn);
    		bubbleOneVisible = false;
    		bubbleTwoVisible = true;
    	}
    	else if(bubbleTwoVisible) 
    	{
    		bubble1.setText(message);
    		this.currBubbleFadeOut = FADING_OUT_BUBBLE_2;
    		this.currBubbleFadeIn = FADING_IN_BUBBLE_1;
    		bubble2.startAnimation(bubbleFadeOut);
    		bubble1.startAnimation(bubbleFadeIn);
    		bubbleOneVisible = true;
    		bubbleTwoVisible = false;
    	}
    	else 
    	{
    		bubble1.setText(message);
    		this.currBubbleFadeIn = FADING_IN_BUBBLE_1;
    		bubble1.startAnimation(bubbleFadeIn);
    		bubbleOneVisible = true;
    	}
    }
    
    /**
     * Inner class to receive the answer.
     * 
     * @author Adam Lyon
     *
     */
    public class AnswerReceiver extends BroadcastReceiver 
	{
		public static final String ANSWER_READY = "com.appsbylyon.intent.action.ANSWER_READY";
		
		public AnswerReceiver() 
		{
			super();
		}
		
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			int answer = intent.getIntExtra(AnswerGenerator.ANSWER, AnswerGenerator.NO_ANSWER);
			switch (answer) 
    		{
    		case AnswerGenerator.DEFINITE_NO:
    			swapBubble(getResources().getString(R.string.answer_definite_no));
    			playSound(R.raw.definitely_not);
    			break;
    		case AnswerGenerator.HIGH_NO:
    			swapBubble(getResources().getString(R.string.answer_high_no));
    			playSound(R.raw.highly_unlikely);
    			break;
    		case AnswerGenerator.MEDIUM_NO:
    			swapBubble(getResources().getString(R.string.answer_medium_no));
    			playSound(R.raw.probably_not);
    			break;
    		case AnswerGenerator.LIGHT_NO:
    			swapBubble(getResources().getString(R.string.answer_light_no));
    			playSound(R.raw.small_chance_no);
    			break;
    		case AnswerGenerator.NEUTRAL:
    			swapBubble(getResources().getString(R.string.answer_neutral));
    			playSound(R.raw.i_dont_know);
    			break;
    		case AnswerGenerator.LIGHT_YES:
    			swapBubble(getResources().getString(R.string.answer_light_yes));
    			playSound(R.raw.small_chance_of_yes);
    			break;
    		case AnswerGenerator.MEDIUM_YES:
    			swapBubble(getResources().getString(R.string.answer_medium_yes));
    			playSound(R.raw.probably_yes);
    			break;
    		case AnswerGenerator.HIGH_YES:
    			swapBubble(getResources().getString(R.string.answer_high_yes));
    			playSound(R.raw.highly_likely);
    			break;
    		case AnswerGenerator.DEFINITE_YES:
    			swapBubble(getResources().getString(R.string.answer_definite_yes));
    			playSound(R.raw.definitely_yes);
    			break;
    		default:
    			swapBubble("Something Went Wrong");
    		}
    		questionAsked = false;
    		waitForAnswer = false;
    		alvisReadyForTap = true;
    		header.setText(getResources().getString(R.string.title_tap_alvis));
		}
	}
    
	@Override
	public void onAnimationStart(Animation anim){}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {}

	
}
