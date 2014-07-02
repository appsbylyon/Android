package com.appsbylyon.askalvis.interfaces;

import com.appsbylyon.askalvis.custom.ScrollViewCustom;

public interface ScrollViewListener 
{
	void onScrollChanged(ScrollViewCustom scrollView, 
            int x, int y, int oldx, int oldy);
}
