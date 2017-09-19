package com.klcxkj.reshui.activity;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.klcxkj.reshui.util.KeyboardUtil;


/**
 * author : yinjuan
 * time： 2017/6/9 13:54
 * email：yin.juan2016@outlook.com
 * Description:基本活动类
 */

public class ACT_Base extends Activity {
//	protected static CardInfo mCardInfo;
	protected String TAG = getClass().getSimpleName();
	private static Toast toast;
    protected void toast(int id) {
    	String text = getResources().getString(id);
    	toast(text);
    }

	/**
	 * 土司封装
	 * @param
     */
    protected void toast(String text) {
        if (toast == null) {
        	toast = Toast.makeText(ACT_Base.this, text, Toast.LENGTH_SHORT);
        } else {
        	toast.setText(text);
        }
        toast.show();
    }


	public void onClick_back(View view){
		this.finish();
	}



	public void setupUI(View view) {
		 
        //Set up touch listener for non-text box views to hide keyboard. 

		if(!(view instanceof EditText)) {
		
			   view.setOnTouchListener(new OnTouchListener() {
			
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					KeyboardUtil.hideSoftKeyboard(ACT_Base.this);  //Main.this是我的activity名
					 
			        return false; 
				} 
			
			  }); 
		} 
		
		//If a layout container, iterate over children and seed recursion. 
		if (view instanceof ViewGroup) {
		    for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
		        View innerView = ((ViewGroup) view).getChildAt(i);
		        setupUI(innerView); 
		    } 
		} 
}
}
