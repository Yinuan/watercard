package com.klcxkj.reshui.network;

import android.content.Context;

import com.android.volley.RequestQueue;

/**
 * 网络操作类的请求
 * @author goldong
 *
 */
public class OperRequest{
	private Context mContext;
	private RequestQueue mQueue;
	
	public OperRequest(Context context, RequestQueue queue){
		this.mContext = context;
		this.mQueue = queue;
	}
	
	public void send(final String url){
		send(url, null, null);
	}
	
	public void send(final String url, final SuccCallBack succCallback, final FailCallBack failCallback){

	}
	
	public static abstract class SuccCallBack{
		public abstract void afterSucc();
	}
	
	public static abstract class FailCallBack{
		public abstract void afterFail();
	}
}