package com.klcxkj.reshui.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CookieRequest extends JsonObjectRequest {
	private final static String COOKIE_TICKET_KEY =  "ticket|" ;
	private Map<String,String> mHeaders=new HashMap<String,String>(1);

    public CookieRequest(String url, JSONObject jsonRequest, Listener listener,
                         ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }




	public CookieRequest(int method, String url, JSONObject jsonRequest, Listener listener,
                         ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }
    
    public void setCookie(String cookie){
        mHeaders.put("Cookie", COOKIE_TICKET_KEY + cookie);
       
    }

    @Override
    public Map getHeaders() throws AuthFailureError {

        return mHeaders;
    }

}
