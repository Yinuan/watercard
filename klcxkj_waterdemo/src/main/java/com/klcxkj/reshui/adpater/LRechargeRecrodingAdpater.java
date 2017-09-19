package com.klcxkj.reshui.adpater;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.klcxkj.klcxkj_waterdemo.R;
import com.klcxkj.reshui.entry.RechargeRecording;
import com.klcxkj.reshui.util.TimeUtil;

import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/9/6
 * email:yinjuan@klcxkj.com
 * description:充值记录的适配器
 */

public class LRechargeRecrodingAdpater extends MyAdapter<RechargeRecording>{

    private List<RechargeRecording> mData =getList();

    /**
     * 构造方法描述:基类构造方法
     *
     * @param mContext
     */
    public LRechargeRecrodingAdpater(Context mContext) {
        super(mContext);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view ==null){
            view = View.inflate(mContext, R.layout.list_item_recharge_recording,null);
        }
        LinearLayout layout =ViewHolder.get(view,R.id.item_recording_top_layout);
        TextView timeMon =ViewHolder.get(view,R.id.item_recharge_time_mon);
        TextView value =ViewHolder.get(view,R.id.item_recharge_value);
        TextView title =ViewHolder.get(view,R.id.item_recording_title);
        TextView mon =ViewHolder.get(view,R.id.item_recording_month);
        RechargeRecording recording =getItem(position);
        String timeBe =recording.getDT();
        //截取后的时间
        String timeAf=timeBe.substring(0,timeBe.length()-2);
        Log.d("LRechargeRecrodingAdpat","timeAfter:=="+ timeAf);
        //获取时间戳
        String str = TimeUtil.formatDisplayTime(timeAf,null);
        //当前
        String curStr = TimeUtil.getCurMonth();
        String curYear =curStr.substring(0,4);
        String curMonth =curStr.substring(5,7);
        //服务器数据的
        //年份
        String year =timeAf.substring(0,4);
        //月份
        String serMonth =timeAf.substring(5,7);
        //显示月份
        String month ="";
        if (year.equals(curYear)){  //同一年
            if (curMonth.equals(serMonth)){
                month ="本月";
            }else {
                month =serMonth+"月";

            }
        }else {//不是同一年
            month=year+"年"+serMonth+"月";
        }

        mon.setText(month);
        timeMon.setText(str);
        String recordingName =recording.getFlagName();
        title.setText(recordingName);
        if (recordingName.equals("转账")){
            value.setText("-"+recording.getDMoney());
            value.setTextColor(mContext.getResources().getColor(R.color.monney));
        }else if (recordingName.equals("消费")){
            value.setText("-"+recording.getDMoney());
            value.setTextColor(mContext.getResources().getColor(R.color.monney));
        }else {
            value.setText("+"+recording.getDMoney());
            value.setTextColor(mContext.getResources().getColor(R.color.code_get));
        }

        //刷选
        if (position>0){
            RechargeRecording recording1 =getItem(position-1);
            if (recording.getDT().substring(0,7).equals( recording1.getDT().substring(0,7))){
                layout.setVisibility(View.GONE);
            }else {
                layout.setVisibility(View.VISIBLE);
            }
        }
        return view;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }



    /**
     * 第一次出现的时间日期
     */
    private String sb;
    private boolean setPosition(String str){
        boolean b =false;
        if (str.equals(sb)) {
            return b;
        }
        for (RechargeRecording re:mData) {
           String string = re.getDT().substring(0,7);
            if (str.equals(string)){
               sb =str;
                b=true;
            }
        }
        return b;
    }
}
