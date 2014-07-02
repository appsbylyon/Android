package com.appsbylyon.askalvis.answergenerator;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.IntentService;
import android.content.Intent;

import com.appsbylyon.askalvis.AskAlvis.AnswerReceiver;

/**
 * Class to calculate the Answer
 * 
 * @author Adam Lyon
 *
 */
public class AnswerGenerator extends IntentService
{
	public static final String ANSWER = "answer";
	
	public static final int NO_ANSWER = -10;
	public static final int DEFINITE_NO = -4;
	public static final int HIGH_NO = -3;
    public static final int MEDIUM_NO = -2;
    public static final int LIGHT_NO = -1;
    public static final int NEUTRAL = 0;
    public static final int LIGHT_YES = 1;
    public static final int MEDIUM_YES = 2;
    public static final int HIGH_YES = 3;
    public static final int DEFINITE_YES = 4;
    
    public static final int GOOD_QUESTION = 1;
    public static final int UNKNOWN_QUESTION = 0;
    public static final int BAD_QUESTION = -1;
    
    private final int DEFINITE = 20;
    private final int HIGH = 10;
    private final int MEDIUM = 5;
    
    public AnswerGenerator() 
    {
    	super("AnswerGenerator");
    }
    
	public int getAnswer() 
    {
        int baseLoops = 1000000;
        int loopsMultiplier = 5;
        int numOfLoops = baseLoops * loopsMultiplier;
        int lastResult = -1;
        int zeroStreak = 1;
        int oneStreak = 1;
        int difference = 0;
        int biggestZeroStreak = 0;
        int biggestOneStreak = 0;
        int sumOfLoops = 0;
        
        for (int i = 0; i < numOfLoops; i++) 
        {
            int result = (int) ((Math.random() * 2));
            if (lastResult == -1) 
            {
                lastResult = result;
            }
            else if (lastResult == 0 && result == 0) 
            {
                zeroStreak++;

            } 
            else if (lastResult == 0 && result == 1) 
            {
                if (zeroStreak > biggestZeroStreak) 
                {
                    biggestZeroStreak = zeroStreak;
                }
                zeroStreak = 1;
            } 
            else if (lastResult == 1 && result == 1) 
            {
                oneStreak++;
            }
            else if(lastResult == 1 && result == 0) 
            {
                if (oneStreak > biggestOneStreak) 
                {
                    biggestOneStreak = oneStreak;
                }
                oneStreak = 1;
            }
            lastResult = result;
            sumOfLoops += result;
                
        }
        
        difference = (0-(((numOfLoops/2)-sumOfLoops)*2));
        
        int streakDifference = biggestOneStreak - biggestZeroStreak;
        int zeroMinusAve = (biggestZeroStreak -21);
        int oneMinusAve = (biggestOneStreak - 21);
        int result = streakDifference+(0-zeroMinusAve)+(oneMinusAve);
        double weight = (difference/(numOfLoops*.000358));
        double resultWeight = (result*weight);
        double finalResult = (result-resultWeight);
        if (finalResult < -DEFINITE) 
        {
            return DEFINITE_NO;
        } else if (finalResult < -HIGH) 
        {
            return HIGH_NO;
        } else if (finalResult < -MEDIUM)
        {
            return MEDIUM_NO;
        } else if (finalResult < 0) 
        {
            return LIGHT_NO;
        } else if (finalResult == 0) 
        {
            return NEUTRAL;
        } else if (finalResult > DEFINITE) 
        {
            return DEFINITE_YES;
        } else if (finalResult > HIGH) 
        {
            return HIGH_YES;
        } else if (finalResult > MEDIUM) 
        {
            return MEDIUM_YES;
        } else  
        {
            return LIGHT_YES;
        }
        
    }

	@Override
	protected void onHandleIntent(Intent intent) {
		int result = getAnswer();
		Intent sendAnswer = new Intent();
		sendAnswer.setAction(AnswerReceiver.ANSWER_READY);
		sendAnswer.addCategory(Intent.CATEGORY_DEFAULT);
		sendAnswer.putExtra(ANSWER, result);
		sendBroadcast(sendAnswer);
	}
	
	/**
	 * Method to determine if a question can be answered with yes or no
	 * @param spokenWords
	 * @return
	 */
	public int isValidQuestion(ArrayList<String> spokenWords) 
    {
        String firstWord = spokenWords.get(0).toString().toLowerCase().trim();
        
        
        String searchRegexGood = "^can|^will|^do|^are|^am|^is";
        String searchRegexBad = "^why|^what|^who|^where|^when|^how";
        Pattern myPatternGood = Pattern.compile(searchRegexGood);
        Pattern myPatternBad = Pattern.compile(searchRegexBad);
        Matcher myMatcherGood;
        Matcher myMatcherBad;
         myMatcherGood = myPatternGood.matcher(firstWord);
            if (myMatcherGood.find()) 
            {
                return GOOD_QUESTION;
            }
            myMatcherBad = myPatternBad.matcher(firstWord);
            if (myMatcherBad.find()) 
            {
                return BAD_QUESTION;
            }
        
        return UNKNOWN_QUESTION;
    }
}
