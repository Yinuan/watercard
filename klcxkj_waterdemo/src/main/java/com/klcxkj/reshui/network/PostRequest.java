package com.klcxkj.reshui.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class PostRequest extends Request<JSONObject> {
	    private Map<String, String> mMap;
	    private Listener<JSONObject> mListener;
		private final static String COOKIE_TICKET_KEY =  "ticket|" ;
		private Map<String,String> mHeaders=new HashMap<String,String>(1);
	    public PostRequest(String url, Listener<JSONObject> listener, ErrorListener errorListener, Map<String, String> map) {
	        super(Method.POST, url, errorListener);
	             
	        mListener = listener;
	        mMap = map;
	    }
	     
	    //mMap是已经按照前面的方式,设置了参数的实例
	    @Override
	    protected Map<String, String> getParams() throws AuthFailureError {
	        return mMap;
	    }
	     
	    //此处因为response返回值需要json数据,和JsonObjectRequest类一样即可
	    @Override
	    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
	        try {
	            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
	                 
	            return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
	        } catch (UnsupportedEncodingException e) {
	            return Response.error(new ParseError(e));
	        } catch (JSONException je) {
	            return Response.error(new ParseError(je));
	        }
	    }
	    @Override
	    protected void deliverResponse(JSONObject response) {
	        mListener.onResponse(response);
	    }
	    
	    public void setCookie(String cookie){
	        mHeaders.put("Cookie", COOKIE_TICKET_KEY + cookie);
	       
	    }

	    @Override
	    public Map getHeaders() throws AuthFailureError {

	        return mHeaders;
	    }
	    
		public static HashMap<String, String> getRequestParams(Object bean) throws IllegalArgumentException, IllegalAccessException {
			HashMap<String, String> result = new HashMap<String, String>();
			Class clazz = bean.getClass();
			Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				Object value = field.get(bean);
				result.put(field.getName(), value+"");
			}
			return result;
		}
	}