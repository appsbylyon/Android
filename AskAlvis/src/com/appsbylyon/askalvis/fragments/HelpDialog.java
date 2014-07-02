package com.appsbylyon.askalvis.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.appsbylyon.askalvis.Help;
import com.appsbylyon.askalvis.R;
import com.appsbylyon.askalvis.custom.ScrollViewCustom;
import com.appsbylyon.askalvis.custom.TypefaceSingleton;
import com.appsbylyon.askalvis.interfaces.ScrollViewListener;

/**
 * Help Dialog Class
 * 
 * @author Adam Lyon
 *
 */
public class HelpDialog extends DialogFragment implements OnClickListener, ScrollViewListener
{
	private static final double DIALOG_TEXT_WIDTH = 0.6;
	private Button ok;
	
	private TextView dialogText;
	private TextView showMoreText;
	
	private ScrollViewCustom scroll;
	
	private int helpChoice;
	private int screenWidth;
	
	private Typeface customFont;
	
	private TypefaceSingleton tfs;

    public HelpDialog() {
    	
    }
    
    public interface HelpDialogInterface {
    	void showAToast(String message);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	tfs = TypefaceSingleton.getInstance();
    	customFont = tfs.getTypeface();
    	screenWidth = tfs.getScreenWidth();
    	helpChoice = this.getArguments().getInt(getResources().getString(R.string.help_dialog_choice));
        View view = inflater.inflate(R.layout.help_dialog_fragment, container);
        
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        ok = (Button) view.findViewById(R.id.help_dialog_ok_button);
        scroll = (ScrollViewCustom) view.findViewById(R.id.help_dialog_scroll);
        scroll.setScrollViewListener(this);
        dialogText = (TextView) view.findViewById(R.id.help_dialog_text);
        showMoreText = (TextView) view.findViewById(R.id.show_more_text);
        
        dialogText.setWidth((int)(screenWidth*DIALOG_TEXT_WIDTH));
        
        ok.setTypeface(customFont);
        dialogText.setTypeface(customFont);
        showMoreText.setTypeface(customFont);
        
        dialogText.setText("");
        int resourceToLoad = 0;
        
        switch(helpChoice) 
        {
        case Help.WHO_IS_ALVIS:
        	resourceToLoad = R.raw.who_is_alvis;
        	break;
        case Help.HOW_IT_WORKS:
        	resourceToLoad = R.raw.how_it_works;
        	break;
        case Help.HOW_TO_USE:
        	resourceToLoad = R.raw.how_to_use;
        	break;
        }
        try 
        {
        	InputStreamReader input = new InputStreamReader(getResources().openRawResource(resourceToLoad));
        	BufferedReader br = new BufferedReader(input);
        	String line;
        	while ((line = br.readLine()) != null) 
        	{
        		dialogText.append(line);
        		dialogText.append("\n");
        	}
        	br.close();
        }
        catch (IOException IOE) 
        {
        	// No error handling needed
        }
        
        ViewTreeObserver vto = scroll.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressLint("NewApi")
			@Override
            public void onGlobalLayout() {
                if (!scroll.canScrollVertically(1)) 
                {
                	showMoreText.setVisibility(View.INVISIBLE);;
                }
            	ViewTreeObserver obs = scroll.getViewTreeObserver();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }

        });
        

        ok.setOnClickListener(this);
        
        return view;
    }
    
    
    
	@Override
	public void onClick(View v) 
	{
		switch(v.getId()) 
		{
		case R.id.help_dialog_ok_button:
			this.dismiss();
			break;
		}
	}

	private void sendMessage(String message) 
	{
		HelpDialogInterface activity = (HelpDialogInterface) getActivity();
		activity.showAToast(message);
	}
	
	@Override
	public void onScrollChanged(ScrollViewCustom scrollView, int x, int y,
			int oldx, int oldy) {
		 // We take the last son in the scrollview
	    View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
	    int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

	    // if diff is zero, then the bottom has been reached
	    if (diff == 0) {
	    	showMoreText.setVisibility(View.INVISIBLE);
	        //sendMessage("At Bottom!");
	    }
		
	}

	
	
		
	
}
