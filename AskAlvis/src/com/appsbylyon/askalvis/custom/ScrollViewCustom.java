package com.appsbylyon.askalvis.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.appsbylyon.askalvis.interfaces.ScrollViewListener;

public class ScrollViewCustom extends ScrollView {
	private ScrollViewListener scrollViewListener = null;
    
	
	public ScrollViewCustom(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public ScrollViewCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollViewCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }
}