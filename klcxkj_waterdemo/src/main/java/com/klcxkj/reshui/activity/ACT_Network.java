package com.klcxkj.reshui.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.klcxkj.klcxkj_waterdemo.R;
import com.klcxkj.reshui.network.PostRequest;
import com.klcxkj.reshui.util.NetWorkUtil;
import com.klcxkj.reshui.widget.LoadingDialogProgress;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 抽象类，用于网络请求时用到的json和图片等
 */
public abstract class ACT_Network extends ACT_Base {

	protected RequestQueue mQueue;
	protected LoadingDialogProgress progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}else {
			View tView =findViewById(R.id.top_menu_view);
			tView.setVisibility(View.GONE);
		}
		mQueue = Volley.newRequestQueue(getApplicationContext());// thread
																	// pool(4)
	}

	protected void showMenu(String str){
		TextView title = (TextView) findViewById(R.id.top_title);
		title.setText(str);
		LinearLayout backBtn = (LinearLayout) findViewById(R.id.top_btn_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

	}

	protected void sendPostRequest(final String url,
			HashMap<String, String> params) {
		if (!NetWorkUtil.isNetworkAvailable(ACT_Network.this)) {
			toast("当前网络不可用，请检查您的网络");
			return;
		}
		PostRequest request = new PostRequest(url, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				handleResponse(url, response);
			}

		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				handleErrorResponse(url, error);
			}
		}, params);
		request.setTag(TAG);
		mQueue.add(request);
	}

	protected void handleResponse(String url, JSONObject json) {
	}

	protected void handleErrorResponse(String url, VolleyError error) {
	}


	public void onStop() {
		super.onStop();
		mQueue.cancelAll(TAG);
	}

}
