package com.appsbylyon.tipcalculator;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main Activity for the application
 * 
 * Modified: 5/22/2014
 * 
 * @author Adam Lyon
 *
 */
public class MainActivity extends ActionBarActivity {
	/** Instance of the class used for data verification and calculations. */
	private TipCalculator tipCalc = new TipCalculator();
	
	/** Declare all elements of the UI. */
	EditText etBillAmount;
	EditText etTipPercent;
	EditText splitValue;
	
	RadioButton tipPercentButton;
	RadioButton tipAmountButton;
	
	CheckBox splitCheck;
	
	TextView tvTipAmount;
	TextView tvBillTotal;
	TextView tvTipLabel;
	TextView splitLabel;
	TextView tipPercentLabel;
	TextView billAmountLabel;
	TextView tipAmountLabel;
	TextView billTotalLabel;
	TextView peopleStaticLabel;
	TextView peopleLabel;
	TextView perPersonStaticLabel;
	TextView perPersonLabel;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
			
		}
		
		
	}
	
	/**
	 * Inherited from ActionBarActivity, called when fragment is created.
	 */
	@Override
	public View onCreatePanelView (int featureId) 
	{
		//Set attribute pointers
		etBillAmount = (EditText) findViewById(R.id.billamount);
		etTipPercent = (EditText) findViewById(R.id.tippercent);
		splitValue = (EditText) findViewById(R.id.splitNum);
		
		tipPercentButton = (RadioButton) findViewById(R.id.tipPercentOption);
		tipAmountButton= (RadioButton) findViewById(R.id.tipAmountOption);
		
		splitCheck = (CheckBox) findViewById(R.id.splitCheckBox); 
				
		tvTipLabel = (TextView) findViewById(R.id.tvTipLabel);
		splitLabel = (TextView) findViewById(R.id.splitTextView);
		tipPercentLabel = (TextView) findViewById(R.id.tipLabel);
		billAmountLabel = (TextView) findViewById(R.id.billAmountLabel);
		tipAmountLabel = (TextView) findViewById(R.id.tipAmountLabel);
		billTotalLabel = (TextView) findViewById(R.id.billTotalLabel);
		peopleStaticLabel = (TextView) findViewById(R.id.numOfPeopleStaticLabel);
		peopleLabel = (TextView) findViewById(R.id.numOfPeopleLabel);
		perPersonStaticLabel = (TextView) findViewById(R.id.perPersonStaticLabel);
		perPersonLabel = (TextView) findViewById(R.id.perPersonLabel);
		
		//Initialize UI to default display
		splitCheck.setChecked(false);
		splitLabel.setVisibility(View.INVISIBLE);
		splitValue.setVisibility(View.INVISIBLE);
		perPersonLabel.setVisibility(View.INVISIBLE);
		perPersonStaticLabel.setVisibility(View.INVISIBLE);
		peopleStaticLabel.setVisibility(View.INVISIBLE);
		peopleLabel.setVisibility(View.INVISIBLE);
		
		//Make checkbox and radio button outlines white for UI theme
		int checkId = Resources.getSystem().getIdentifier("btn_check_holo_dark", "drawable", "android");
		int radioId = Resources.getSystem().getIdentifier("btn_radio_holo_dark", "drawable", "android");
		
		splitCheck.setButtonDrawable(checkId);
		tipPercentButton.setButtonDrawable(radioId);
		tipAmountButton.setButtonDrawable(radioId);
		
		//Set result display labels to ""
		clearResultLabels();
		
		//Listener for when the text is changed in the bill amount EditText
		etBillAmount.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {}
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count)
	        {
	        	/**
	        	 * Verify the user didn't enter something like '32.323' if they
	        	 * did enter too many decimal places remove them so '32.323'
	        	 * becomes 32.32
	        	 */
	        	String billAmountText = etBillAmount.getText().toString();
	        	String newBillAmountText = checkInput(billAmountText);
	        	// If the number was changed update the display
	        	if (billAmountText.compareTo(newBillAmountText) != 0) 
	        	{
		        	etBillAmount.setText(newBillAmountText);
		        	etBillAmount.setSelection(newBillAmountText.length());
	        	}
	        	clearResultLabels();
	        }
	    }); 
		
		//Listener for when the user changes the value in the tip EditText
		etTipPercent.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {}
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count)
	        {
	        	if (etTipPercent.getText().toString().length() > 0) 
	        	{
	        		if (tipPercentButton.isChecked()) 
		        	{
		        		// Simply verify that the user didn't enter a value greater than 100%
	        			int percentValue = Integer.parseInt(etTipPercent.getText().toString());
		        		if (percentValue > 100) 
		        		{
		        			showToast("Tip Percent Cannot Be More Than 100%");
		        			etTipPercent.setText("100");
		        			etTipPercent.setSelection(etTipPercent.getText().toString().length());
		        		}
		        	}
	        		else 
	        		{
	        			//Same deal as in the code above for the Bill Amount
	        			String tipAmountValue = etTipPercent.getText().toString();
	        			String newTipAmountValue = checkInput(tipAmountValue);
	        			if (tipAmountValue.compareTo(newTipAmountValue) != 0) 
	        			{
	        				etTipPercent.setText(newTipAmountValue);
	        				etTipPercent.setSelection(newTipAmountValue.length());
	        			}
	        		}
	        	}
	        	clearResultLabels();
	        }
	    });
		
		//Listener for when the user changes the value in the tip EditText
		this.splitValue.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {}
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count)
	        {
	        	clearResultLabels();
	        }
	    });
		return null;
	}// End of onCreatePanelView method
	
	/**
	 * Method to check the user input in real time for values exceeding 2 decimal places
	 * @param input value the user entered
	 * @return The corrected value if it needed changed or the passed in string if it did not
	 */
	private String checkInput (String input) 
	{
		String returnValue = input;
		String delims = "[.]";
    	String [] splitArray = input.split(delims);
    	if (splitArray.length > 1) 
    	{
    		if (splitArray[1].length() > 2) 
    		{
    			returnValue = input.substring(0, (input.length()-1));
    		}
    	}
    	return returnValue;
	}// End of checkInput Method
	
	/**
	 * A lazy way to show toasts. Also, I always forget to add the .show()
	 * so less typing and toast always shows
	 * @param message Message to display.
	 */
	private void showToast(String message) 
	{
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}// End of showToast method
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			return rootView;
		}
	}
	
	/**
	 * Method to change the UI based on whether or not the user wants
	 * to split the bill or not
	 * @param v
	 */
	public void splitCheckClick (View v) 
	{
		if (splitCheck.isChecked()) 
		{
			splitLabel.setVisibility(View.VISIBLE);
			splitValue.setVisibility(View.VISIBLE);
			perPersonLabel.setVisibility(View.VISIBLE);
			perPersonStaticLabel.setVisibility(View.VISIBLE);
			peopleStaticLabel.setVisibility(View.VISIBLE);
			peopleLabel.setVisibility(View.VISIBLE);
		}
		else 
		{
			splitLabel.setVisibility(View.INVISIBLE);
			splitValue.setVisibility(View.INVISIBLE);
			perPersonLabel.setVisibility(View.INVISIBLE);
			perPersonStaticLabel.setVisibility(View.INVISIBLE);
			peopleStaticLabel.setVisibility(View.INVISIBLE);
			peopleLabel.setVisibility(View.INVISIBLE);
		}
		clearResultLabels();
	}// End of splitCheckClick method
	
	/**
	 * Method to change the UI based on whether or not the user wants to
	 * enter the tip percentage or the tip amount.
	 * @param v
	 */
	public void tipOptionChange (View v) 
	{
		if(tipPercentButton.isChecked()) 
		{
			etTipPercent.setInputType(InputType.TYPE_CLASS_NUMBER);
			tvTipLabel.setText(R.string.tv_tip_prompt_percent);
			etTipPercent.setText("");
		}
		else 
		{
			etTipPercent.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			tvTipLabel.setText(R.string.tv_tip_prompt_amount);
			etTipPercent.setText("");
		}
		clearResultLabels();
	}// End of tipOptionChange method
	
	/**
	 * Method that utilizes the TipCalculator object to calculate the results and
	 * display them.
	 * 
	 * @param v
	 */
	public void calculateBill (View v) 
	{
		String sBillAmount = etBillAmount.getText().toString();
		String sTipAmount = etTipPercent.getText().toString();
		boolean doTipPercent = tipPercentButton.isChecked();
		boolean doBillSplit = splitCheck.isChecked();
		String sNumOfPeople = splitValue.getText().toString();
		
		//Verify user entered valid values before performing calculations on them.
		if (tipCalc.isValidBill(sBillAmount)) 
		{
			if (tipCalc.isValidTip(sTipAmount)) 
			{
				if (doTipPercent) 
				{
					tipCalc.setValuesTipPercent(sBillAmount, sTipAmount);
				}
				else 
				{
					tipCalc.setValuesTipAmount(sBillAmount, sTipAmount);
				}
				if (doBillSplit) 
				{
					if (tipCalc.isValidSplit(sNumOfPeople)) 
					{
						tipCalc.setSplit(sNumOfPeople);
						showResults(doBillSplit);
					}
					else 
					{
						showToast("Number of people to split bill must be 2 or more!");
						clearResultLabels();
					}
				}
				else 
				{
					showResults(doBillSplit);
				}
			}
			else 
			{
				showToast("Please Enter A Valid Tip Amount!");
				clearResultLabels();
			}
		}
		else 
		{
			showToast("Please Enter A Valid Bill Amount!");
			clearResultLabels();
		}
	}// End of calculateBill Method
	
	/**
	 * Display the results of the calculation
	 * @param doSplit
	 */
	private void showResults (boolean doSplit) 
	{
		this.billAmountLabel.setText(tipCalc.getFormatedBillAmount());
		this.billTotalLabel.setText(tipCalc.getBillTotal());
		this.tipAmountLabel.setText(tipCalc.getTipAmount());
		this.tipPercentLabel.setText(tipCalc.getReadablePercent());
		if (doSplit) 
		{
			this.peopleLabel.setText(splitValue.getText().toString());
			this.perPersonLabel.setText(tipCalc.getSplit());
		}
	}// End of showResults method
	
	/**
	 * Method to clear the result labels
	 */
	private void clearResultLabels () 
	{
		billAmountLabel.setText("");
		billTotalLabel.setText("");
		tipAmountLabel.setText("");
		tipPercentLabel.setText("");
		peopleLabel.setText("");
		perPersonLabel.setText("");
	}// end of clearResultLabels method
	
	/**
	 * Method to reset the inputs
	 */
	private void resetInputs () 
	{
		this.etBillAmount.setText("");
		this.etTipPercent.setText("");
		this.tipPercentButton.setChecked(true);
		this.splitCheck.setChecked(false);
		this.splitValue.setText("2");
	}// end of resetInputs method
	
	/**
	 * Method to reset entire UI to the intial state.
	 * @param v
	 */
	public void clearAll (View v) 
	{
		resetInputs();
		clearResultLabels();
		splitCheckClick(v);
	}// end of clearAll method
}// end of MainActivity class
