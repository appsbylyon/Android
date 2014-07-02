package com.appsbylyon.tipcalculator;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Class to provide data verification
 * and tip calculation functionality
 * 
 * Modified: 5/22/2014
 * 
 * @author Adam Lyon
 *
 */
public class TipCalculator 
{
	/**Constant used to convert decimal percentage to readable percentage and vise versa. */
	private static final BigDecimal PERCENT_DIVISOR = new BigDecimal("100");
	
	/** Big Decimal type variables for all attributes. */
	private BigDecimal billAmount;
	private BigDecimal tipPercent = new BigDecimal("0");
	private BigDecimal tipAmount;
	private BigDecimal totalBill;
	/** Amount Each Person will pay when a bill is split. */
	private BigDecimal splitAmount;
	/** Number of people to split the bill between. */
	private BigDecimal numOfSplits;
	
	/** Locale used for currency formatting. */
	private Locale locale = new Locale("en", "US");
	
	/**
	 * Method that verfies an entered Bill amount is valid
	 * 
	 * @param stringBillValue Data entered by the user
	 * @return true if value is valid, false if it is not
	 */
	public boolean isValidBill(String stringBillValue) 
	{
		boolean isValid = true;
		double billValue;
		
		//Check bill amount
		if (stringBillValue.length() > 0) 
		{
			try 
			{
				billValue = Double.parseDouble(stringBillValue);
				if (billValue <= 0) 
				{
					isValid = false;
				}
			}
			catch (NumberFormatException NFE) 
			{
				isValid = false;
			}
		}
		else 
		{
			isValid = false;
		}
		return isValid;
	}// end of isValidBill method
	
	/**
	 * Method that verifies the amount entered for a tip is valid
	 * 
	 * @param stringTipValue Value that the user entered
	 * @return true if information is valid, false if it not
	 */
	public boolean isValidTip(String stringTipValue) 
	{
		boolean isValid = true;
		double tipValue;
		
		if (stringTipValue.length() > 0) 
		{
			try 
			{
				tipValue = Double.parseDouble(stringTipValue);
				if ((tipValue < 0) || (tipValue > 100)) 
				{
					isValid = false;
				}
			}
			catch (NumberFormatException NFE) 
			{
				isValid = false;
			}
		}
		else 
		{
			isValid = false;
		}
		return isValid;
	}// End of isValidTip method
	
	/**
	 * Method used to determine if the user entered a valid number of people to split
	 * the bill between
	 * @param splitNumber Value that the user entered
	 * @return boolean value indicating whether or not the entered value is acceptable.
	 */
	public boolean isValidSplit(String splitNumber) 
	{
		boolean isValid = true;
		double splitValue;
		
		if (splitNumber.length() > 0) 
		{
			try 
			{
				splitValue = Double.parseDouble(splitNumber);
				if ((splitValue < 2)) 
				{
					isValid = false;
				}
			}
			catch (NumberFormatException NFE) 
			{
				isValid = false;
			}
		}
		else 
		{
			isValid = false;
		}
		return isValid;
	}// End of isValidSplit method
	
	/**
	 * Setter method for the tip amount and bill
	 * @param stringBillAmount Amount of the bill
	 * @param stringTipAmount Percentage of the tip
	 */
	public void setValuesTipPercent (String stringBillAmount, String stringTipPercent) 
	{
		billAmount = new BigDecimal(stringBillAmount);
		tipPercent = new BigDecimal(stringTipPercent).divide(PERCENT_DIVISOR);
		
		tipAmount = new BigDecimal(billAmount.multiply(tipPercent).toString());
		totalBill = new BigDecimal(billAmount.add(tipAmount).toString());
	}//End of setValues method
	
	/**
	 * Setter method used when a user enters the dollar value of the tip amount
	 * rather than the percentage.
	 * @param stringBillAmount Amount of the bill.
	 * @param stringTipAmount Dollar value of the tip.
	 */
	public void setValuesTipAmount (String stringBillAmount, String stringTipAmount) 
	{
		billAmount = new BigDecimal(stringBillAmount);
		tipAmount = new BigDecimal(stringTipAmount);
		
		tipPercent.setScale(5);
		tipPercent = tipAmount.divide(billAmount, 4, BigDecimal.ROUND_HALF_EVEN);
		totalBill = billAmount.add(tipAmount);
	}// End of setValuesTipAmount method
	
	/**
	 * Method used to retrieve a human readable percantage.
	 * For example '0.17' will be converted to '17%'
	 * @return Human readable percentage value
	 */
	public String getReadablePercent() 
	{
		return tipPercent.multiply(PERCENT_DIVISOR).setScale(0, BigDecimal.ROUND_HALF_EVEN).toString()+"%";
	}//End of getReadablePercent method
	
	/**
	 * Method used to return the amount of the bill in a string formatted for display.
	 * Example: '43.23' will return as '$42.23'
	 * @return Human readable bill amount formatted all pretty like.
	 */
	public String getFormatedBillAmount () 
	{
		return NumberFormat.getCurrencyInstance(locale).format(billAmount);
	}// End of getFormatedBillAmount method
	
	/**
	 * Method that returns the calculated tip amount
	 * @return String contained the tip amount
	 */
	public String getTipAmount() 
	{
		return NumberFormat.getCurrencyInstance(locale).format(tipAmount);
	}// End of getTipAmount method
	
	/**
	 * Method that returns the calculated bill total
	 * @return String containing the bill total
	 */
	public String getBillTotal() 
	{
		return NumberFormat.getCurrencyInstance(locale).format(totalBill);
	}// End of getBillTotal method
	
	/**
	 * Method used to set the number of patrons to split the bill between
	 * @param splitNum Number of patrons to split the bill between
	 */
	public void setSplit (String splitNum) 
	{
		numOfSplits = new BigDecimal(splitNum);
		splitAmount = new BigDecimal(totalBill.divide(numOfSplits, 2, BigDecimal.ROUND_CEILING).toString());
	}// End of setSplit method
	
	/**
	 * Method used to retreive a human readable version of the amount each patron
	 * will pay when the bill is being split
	 * @return String that shows what each person will pay
	 */
	public String getSplit () 
	{
		return NumberFormat.getCurrencyInstance(locale).format(splitAmount);
	}// End of getSplit method
}// End of TipCalculator Class
