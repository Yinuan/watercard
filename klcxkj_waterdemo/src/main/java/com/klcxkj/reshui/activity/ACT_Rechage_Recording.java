package com.klcxkj.reshui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.klcxkj.klcxkj_waterdemo.R;
import com.klcxkj.reshui.adpater.LRechargeRecrodingAdpater;
import com.klcxkj.reshui.entry.CardInfo;
import com.klcxkj.reshui.entry.RechargeRecording;
import com.klcxkj.reshui.entry.RechargeRecrodingResult;
import com.klcxkj.reshui.entry.UserInfo;
import com.klcxkj.reshui.tools.StringConfig;
import com.klcxkj.reshui.util.AppPreference;
import com.klcxkj.reshui.util.GlobalTools;
import com.klcxkj.reshui.widget.LoadingDialogProgress;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * author : yinjuan
 * time： 2017/6/9 13:54
 * email：yin.juan2016@outlook.com
 * Description:充值记录
 */
public class ACT_Rechage_Recording extends ACT_Network {

    private ListView listView;
    private static final int QUERY_TYPE =4; //查询支付宝的充值记录
    private int count=1; //页数
    private List<RechargeRecording> mDatas;//数据源
    private List<RechargeRecording> listDatas;//分页显示数据
    private RechargeRecrodingResult recrodingResult;  //解析实体类
    private LRechargeRecrodingAdpater rAdpater;//充值适配器
    private Handler mHandler =new Handler();
    private int maxCount;
    private UserInfo userInfo;
    private CardInfo cardInfo;
    private ImageView data_null;//无数据
    private SmartRefreshLayout refreshLayout;
    private LoadingDialogProgress progress;
    private static final String RECHARGE_RECORDING = StringConfig.BASE_URL+"tStudent/getStuBillList?";//充值记录
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act__rechage__recording);
        showMenu("充值记录");
        initView();
        initdata();
        bindView();
    }

    private void bindView() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                mDatas.clear();
                listDatas.clear();
                refreshLayout.setEnableLoadmore(true);
                initdata();
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mDatas.size()-listDatas.size()<=20 ){//20-40
                            if (mDatas.size()==listDatas.size()){
                                //不在上拉
                                refreshLayout.setEnableLoadmore(false);
                            }else {
                                for (int i = maxCount; i <mDatas.size() ; i++) {
                                    listDatas.add(mDatas.get(i));
                                }
                            }

                        }else { //40
                            maxCount=maxCount+20;
                            for (int i = maxCount-20; i <maxCount ; i++) {
                                listDatas.add(mDatas.get(i));
                            }
                        }
                        rAdpater.notifyDataSetChanged();
                    }
                },1800);


            }
        });
    }


    private void initView() {
        listDatas =new ArrayList<>();
        mDatas =new ArrayList<>();
        rAdpater =new LRechargeRecrodingAdpater(this);
        rAdpater.setList(listDatas);
        listView = (ListView) findViewById(R.id.listView_rechage_recording);
        data_null = (ImageView) findViewById(R.id.recroding_data_null);
        listView.setAdapter(rAdpater);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);

    }
    //初始化数据
    private void initdata() {
        userInfo = AppPreference.getInstance().getUserInfo() ;
        maxCount =20;
       progress = GlobalTools.getInstance().showDailog(this,"加载..");
        loadDataFromServer();

    }
    private void loadDataFromServer(){
        HashMap<String,String> map =new HashMap<>();
        map.put("PrjID",userInfo.getPrjID()+"");
        map.put("EmployeeID",userInfo.getEmployeeID()+"");
        map.put("PageIndex",count+"");
        map.put("queryType",QUERY_TYPE+"");
        map.put("ServerIP",userInfo.getServerIP());
        map.put("ServerPort",userInfo.getServerPort()+"");
        sendPostRequest(RECHARGE_RECORDING,map);
    }


    @Override
    protected void handleErrorResponse(String url, VolleyError error) {
        super.handleErrorResponse(url, error);
        progress.dismiss();
        if(error instanceof TimeoutError){
            toast(R.string.timeout_error);
        }else{
            toast(R.string.operate_error);
        }
    }

    @Override
    protected void handleResponse(String url, JSONObject json) {
        super.handleResponse(url, json);
        progress.dismiss();
        Gson gson =new Gson();
        recrodingResult =gson.fromJson(json.toString(),RechargeRecrodingResult.class);
        if (recrodingResult.getSuccess().equals("true")){
            if (recrodingResult.getObj()!=null) {
                mDatas.addAll(recrodingResult.getObj());
                if (mDatas.size()>20){
                    for (int i = 0; i <20 ; i++) {
                        listDatas.add(mDatas.get(i));
                    }
                }else {
                    listDatas.addAll(mDatas);
                }
                data_null.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
            }else {
                data_null.setVisibility(View.VISIBLE);
                refreshLayout.setVisibility(View.GONE);
            }
            rAdpater.notifyDataSetChanged();
        }else {
            toast(recrodingResult.getMsg());
        }
    }






}
