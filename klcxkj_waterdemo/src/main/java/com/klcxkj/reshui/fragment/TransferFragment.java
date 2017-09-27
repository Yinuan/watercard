package com.klcxkj.reshui.fragment;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.klcxkj.klcxkj_waterdemo.R;
import com.klcxkj.reshui.adpater.LRechargeRecrodingAdpater;
import com.klcxkj.reshui.entry.RechargeRecording;
import com.klcxkj.reshui.entry.RechargeRecrodingResult;
import com.klcxkj.reshui.util.GlobalTools;
import com.klcxkj.reshui.widget.LoadingDialogProgress;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * autor:OFFICE-ADMIN
 * time:2017/8/25
 * email:yinjuan@klcxkj.com
 * description:
 */

public class TransferFragment extends BaseFragment {
    private static final int QUERY_TYPE =1; //1转账2充值3消费
    private int count=1; //页数
    private int maxCount =20; //一页最大list
    private LRechargeRecrodingAdpater rAdpater;//充值适配器
    private SmartRefreshLayout smartRefreshLayout;
    private List<RechargeRecording> mDatas  =new ArrayList<>();//数据源
    private List<RechargeRecording> listDatas =new ArrayList<>();//分页加载的数据
    private RechargeRecrodingResult recrodingResult;  //解析实体类
    private ListView transferListview;
    private LoadingDialogProgress progress;
    private RelativeLayout layout_null;  //无数据视图

    @Override
    protected int getLayoutId() {
        return R.layout.act_campus_card_bill;
    }

    @Override
    protected void initLayout() {
        initview();
        initdata();
        bindview();

    }



    private void initview() {
        layout_null = (RelativeLayout) mView.findViewById(R.id.data_null);
        transferListview = (ListView) mView.findViewById(R.id.listView_transfer);
        smartRefreshLayout = (SmartRefreshLayout) mView.findViewById(R.id.refreshLayout);
        rAdpater =new LRechargeRecrodingAdpater(getActivity());
        rAdpater.setList(listDatas);
        transferListview.setAdapter(rAdpater);

    }

    private void initdata() {
        if (recrodingResult ==null){
            progress= GlobalTools.getInstance().showDailog(getActivity(),"加载");
            loadBillFromServer();
        }else {
            if (recrodingResult.getObj() ==null || recrodingResult.getObj().size()==0){
                smartRefreshLayout.setVisibility(View.GONE);
                layout_null.setVisibility(View.VISIBLE);
            }
        }


    }
    private void loadBillFromServer(){
        StringBuffer sb =new StringBuffer(RECHARGE_RECORDING);
        sb.append("PrjID="+getNewUser().getPrjID());
        sb.append("&EmployeeID="+getNewUser().getEmployeeID());
        sb.append("&PageIndex="+count);
        sb.append("&queryType="+QUERY_TYPE);
        sb.append("&ServerIP="+getNewUser().getServerIP());
        sb.append("&ServerPort="+getNewUser().getServerPort());
        sendGetRequest(sb.toString());
    }

    private void bindview() {
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                listDatas =new ArrayList<>();
                mDatas =new ArrayList<>();
                maxCount=20;
                smartRefreshLayout.setEnableLoadmore(true);
                loadBillFromServer();
            }
        });
        smartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000);
               handler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       if (mDatas.size()-listDatas.size()<=20 ){//20-40
                           if (mDatas.size()==listDatas.size()){
                               //不在上拉
                               smartRefreshLayout.setEnableLoadmore(false);
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
               },1950);

            }
        });
    }


    @Override
    public void loadLayout() {

    }

    @Override
    protected int parseJson(JSONObject result, String url) throws JSONException {
        Log.d("TransferFragment", "result:" + result);
        Gson gson =new Gson();
        recrodingResult =gson.fromJson(result.toString(),RechargeRecrodingResult.class);
        return 0;
    }

    @Override
    protected void loadDatas() {
        progress.dismiss();
        if (recrodingResult.getObj() !=null && recrodingResult.getObj().size()>0){
           mDatas.addAll(recrodingResult.getObj());
           /* for (int i = 0; i <10 ; i++) {
                mDatas.add(new RechargeRecording("2017-09-05 20:26:34.0",1000.5+i,"转账"));
            }
            for (int i = 0; i <10 ; i++) {
                mDatas.add(new RechargeRecording("2017-08-05 20:26:34.0",1000.5+i,"转账"));
            }
            for (int i = 0; i <10 ; i++) {
                mDatas.add(new RechargeRecording("2017-07-05 20:26:34.0",1000.5+i,"转账"));
            }
            for (int i = 0; i <10 ; i++) {
                mDatas.add(new RechargeRecording("2016-09-05 20:26:34.0",1000.5+i,"转账"));
            }
            for (int i = 0; i <10 ; i++) {
                mDatas.add(new RechargeRecording("2015-09-05 20:26:34.0",1000.5+i,"转账"));
            }*/
            if (mDatas.size()>20){
                for (int i = 0; i <20 ; i++) {
                    listDatas.add(mDatas.get(i));
                }
            }else {
                listDatas.addAll(mDatas);
            }
            rAdpater.notifyDataSetChanged();

        }else {
            smartRefreshLayout.setVisibility(View.GONE);
            layout_null.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void loadError(JSONObject result) {
       progress.dismiss();
    }



}
