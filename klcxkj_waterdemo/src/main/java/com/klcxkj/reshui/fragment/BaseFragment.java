package com.klcxkj.reshui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.klcxkj.klcxkj_waterdemo.R;
import com.klcxkj.reshui.entry.UpdatePassResult;
import com.klcxkj.reshui.entry.UserInfo;
import com.klcxkj.reshui.network.CookieRequest;
import com.klcxkj.reshui.network.PostRequest;
import com.klcxkj.reshui.tools.StringConfig;
import com.klcxkj.reshui.util.AppPreference;
import com.klcxkj.reshui.util.NetWorkUtil;
import com.klcxkj.reshui.widget.LoadingDialogProgress;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.android.volley.VolleyLog.TAG;


/**
 * author : yinjuan
 * time： 2017/3/30 09:41
 * email：yin.juan@yuendong.com
 * Description:fragment基类
        */
public abstract class BaseFragment extends Fragment {

    protected RequestQueue mQueue;
    protected ImageLoader mImageLoader;
    protected Handler handler =new Handler();
    protected LoadingDialogProgress progress;
    protected View mView;//视图
    //   界面是否显示
    protected boolean isVisiable;
    //布局是否加载完成
    protected boolean isPrepared;

    protected static final String RECHARGE_RECORDING = StringConfig.BASE_URL+"tStudent/getStuBillList?";//账单记录


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //构建布局
        mView = inflater.inflate(getLayoutId(), container, false);
        //初始化消息队列
        mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());// thread
        // pool(4)

        //界面布局及加载初始化
        isPrepared=true;
        isPrepared=true;
        //初始化控件
        initLayout();
        //打印日记的初始化

        return mView;
    }


    /**
     * 加载布局文件
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 加载控件
     */

    protected abstract void initLayout();

    /**
     * 懒加载的设计
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            //碎片显示
            onVisible();
        }else {
            //不显示碎片
            onInvisiable();
        }
    }

    /**
     * 不显示碎片
     */
    protected void onInvisiable() {

    }

    /**
     * 显示碎片
     */
    protected void onVisible() {
        loadLayout();
    }

    /**
     * 初始化布局
     */
    public abstract void loadLayout();

    /**
     * 获取用户信息
     * @return
     */
    /*public UserBo getUser() {
        return AppPreference.getInstance().getLoginUser();
    }*/

    /**
     * 2017/8/31
     * 获取用户信息
     * @return
     */
    public UserInfo getNewUser(){
        return AppPreference.getInstance().getUserInfo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    protected void sendPostRequest(final String url,
                                   HashMap<String, String> params) {
        if (!NetWorkUtil.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), "当前网络不可用，请检查您的网络", Toast.LENGTH_SHORT).show();
            if (progress !=null){
                progress.dismiss();
            }
            return;
        }
        PostRequest request = new PostRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleResponse(url, response);
            }

        }, new Response.ErrorListener() {
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

    protected void sendGetRequest(final String url) {
        if (!NetWorkUtil.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), "当前网络不可用，请检查您的网络", Toast.LENGTH_SHORT).show();
            try {
                if (progress !=null){
                    progress.dismiss();
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

            return;
        }
        Log.e("water", "sendGetRequest url = " + url);
        CookieRequest jsonRequet = new CookieRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject result) {
                        Log.d("BaseFragment", "result:" + result);
                        try {
                            Gson gson = new Gson();
                            UpdatePassResult resp = gson.fromJson(result.toString(), UpdatePassResult.class);
                            if (resp.getSuccess().equals("false")) {
                                if (!TextUtils.isEmpty(resp.getMsg())) {
                                    Toast.makeText(getActivity(), resp.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                                loadError(result);
                            } else {
                                // toast(resp.getMsg());

                                parseJson(result, url);
                                loadDatas();
                            }
                        } catch (Exception e) {
                            e.fillInStackTrace();
                            loadError(null);
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                loadError(null);
                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(),  R.string.timeout_error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.operate_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        jsonRequet.setTag(TAG);
        mQueue.add(jsonRequet);
    }

    /**
     * 土司封装
     * @param
     */
    private static Toast toast;
    protected void toast(String text) {
        if (toast == null) {
            toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
    }

    protected abstract int parseJson(JSONObject result, String url)
            throws JSONException;
    protected abstract void loadDatas();
    protected abstract void loadError(JSONObject result);

    public void onStop() {
        super.onStop();
        mQueue.cancelAll(TAG);
    }
}
