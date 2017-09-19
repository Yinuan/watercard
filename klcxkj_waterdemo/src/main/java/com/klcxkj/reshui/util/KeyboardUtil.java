package com.klcxkj.reshui.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtil {
//	调用隐藏系统默认的输入法
	public static void hideSoftKeyboard(Activity activity) {
		     InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		     if (activity.getCurrentFocus() == null) {
		    	 return;
		     }
		     inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0); 
	}
	
	public static void showOrHideSoftKeyboard (Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	public static void showSoftKeyboard (Context activity, View view) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}
	
	public static void hideSoftKeyboard (Context activity, View view) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘 
	}
	
	public static boolean isKeyboardShow(Activity activity) {
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
		return isOpen;
	}
}
