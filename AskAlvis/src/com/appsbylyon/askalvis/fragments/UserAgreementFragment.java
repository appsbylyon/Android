package com.appsbylyon.askalvis.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.appsbylyon.askalvis.R;
import com.appsbylyon.askalvis.custom.ScrollViewCustom;
import com.appsbylyon.askalvis.custom.TypefaceSingleton;
import com.appsbylyon.askalvis.interfaces.ScrollViewListener;

/**
 * Class for the user agreement
 * 
 * @author Adam Lyon
 *
 */
public class UserAgreementFragment extends DialogFragment  implements OnClickListener, OnKeyListener, ScrollViewListener
{
	public interface UserAgreementFragmentListener 
	{
		void onUserClick(int result);
		void errorOccurred(String errorMessage);
		void showMessage(String message);
	}
	
	private Typeface customFont;
	
	private TypefaceSingleton tfs;
	
	public static final int USER_AGREED = 0;
	public static final int USER_DECLINED = 1;
	
	private Button accept;
	private Button decline;
	
	private TextView agreementText;
	
	private ScrollViewCustom scroll;

    public UserAgreementFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_agreement_fragment, container);
        getDialog().setTitle(getResources().getString(R.string.user_agreement_title));
        getDialog().setOnKeyListener(this);
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        tfs = TypefaceSingleton.getInstance();
    	customFont = tfs.getTypeface();
        
        accept = (Button) view.findViewById(R.id.accept_button);
        decline = (Button) view.findViewById(R.id.decline_button);
        scroll = (ScrollViewCustom) view.findViewById(R.id.agreement_scroll);
        scroll.setScrollViewListener(this);
        accept.setEnabled(false);
        
        agreementText = (TextView) view.findViewById(R.id.agreement_text);
        agreementText.setText("");
        
        accept.setTypeface(customFont);
        decline.setTypeface(customFont);
        agreementText.setTypeface(customFont);
        
        try 
        {
        	InputStreamReader input = new InputStreamReader(getResources().openRawResource(R.raw.user_agreement));
        	BufferedReader br = new BufferedReader(input);
        	String line;
        	while ((line = br.readLine()) != null) 
        	{
        		agreementText.append(line);
        		agreementText.append("\n");
        	}
        }
        catch (IOException IOE) 
        {
        	sendError(IOE.getMessage());
        }
        
        accept.setOnClickListener(this);
        decline.setOnClickListener(this);
        return view;
    }

	@Override
	public void onClick(View v) 
	{
		int result = -1;
		switch(v.getId()) 
		{
		case R.id.accept_button:
			result = UserAgreementFragment.USER_AGREED;
			break;
		case R.id.decline_button:
			result = UserAgreementFragment.USER_DECLINED;
			break;
		}
		UserAgreementFragmentListener activity = (UserAgreementFragmentListener) getActivity();
		activity.onUserClick(result);
		this.dismiss();
	}
	
	private void sendError(String errorMessage) 
	{
		UserAgreementFragmentListener activity = (UserAgreementFragmentListener) getActivity();
		activity.errorOccurred(errorMessage);
	}
	
	private void sendMessage(String message) 
	{
		UserAgreementFragmentListener activity = (UserAgreementFragmentListener) getActivity();
		activity.showMessage(message);
	}

	@Override
	public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent arg2) {
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			UserAgreementFragmentListener activity = (UserAgreementFragmentListener) getActivity();
			activity.onUserClick(UserAgreementFragment.USER_DECLINED);
		}
		return false;
	}

	@Override
	public void onScrollChanged(ScrollViewCustom scrollView, int x, int y,
			int oldx, int oldy) {
		 // We take the last son in the scrollview
	    View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
	    int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

	    // if diff is zero, then the bottom has been reached
	    if (diff == 0) {
	    	accept.setEnabled(true);
	        
	    }
	}
}
