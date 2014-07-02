package com.appsbylyon.guessinggame;


/**
 * Class to control game state.
 * 
 * Modified: 5/27/2014
 * 
 * @author Adam Lyon
 *
 */
public class Game 
{
	private int minGuess;
	private int maxGuess;
	private int answer;
	private int range;
	private int randNum;
	
	public static final int GUESS_TOO_LOW = -1;
	public static final int GUESS_CORRECT = 0;
	public static final int GUESS_TOO_HIGH = 1;
	public static final int GUESS_OUT_OF_RANGE = 2;
	
	/**
	 * Method that generates a random number between the given range.
	 * The app will crash if setRange is not done first.
	 */
	public void generateAnswer() 
	{
		range = maxGuess - minGuess + 1;
		randNum = (int) (Math.random() * range);
		answer = randNum + minGuess;
	}// end of generateAnswer() method
	
	/**
	 * Retreives the answer.
	 * @return String value of the answer.
	 */
	public String getAnswer() 
	{
		return Integer.toString(answer);
	}// end of getAnswer method
	
	/**
	 * Method that compares the users guess to the answer and
	 * returns the result
	 * 
	 * @param StringGuess The Users Guess
	 * @return int value indicating the result of the guess
	 */
	public int makeGuess(String StringGuess) 
	{
		int guess = Integer.parseInt(StringGuess);
		int returnStatus;
		if ((guess < minGuess) || (guess > maxGuess)) 
		{
			returnStatus = Game.GUESS_OUT_OF_RANGE;
		}
		else 
		{
			if (guess < answer) 
			{
				returnStatus = Game.GUESS_TOO_LOW;
			}
			else if (guess > answer) 
			{
				returnStatus = Game.GUESS_TOO_HIGH;
			}
			else 
			{
				returnStatus = Game.GUESS_CORRECT;
			}
		}
		return returnStatus;
	}// end of makeGuess method
	
	/**
	 * Method to the set the range of the randomly generated number
	 * @param minGuess low end of the range inclusive
	 * @param maxGuess high end of the range inclusive
	 */
	public void setRange(int minGuess, int maxGuess) 
	{
		this.minGuess = minGuess;
		this.maxGuess = maxGuess;
	}// end of setRange method
}// end of Game class
