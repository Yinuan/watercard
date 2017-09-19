package com.klcxkj.reshui.adpater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.klcxkj.klcxkj_waterdemo.R;
import com.klcxkj.reshui.entry.RechangeValue;


/**
 * autor:OFFICE-ADMIN
 * time:2017/8/29
 * email:yinjuan@klcxkj.com
 * description:充值金额选择
 */

public class GRechangeAdapter extends MyAdapter<RechangeValue> {
    /**
     * 构造方法描述:基类构造方法
     *
     * @param mContext
     */
    public GRechangeAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view==null){
            view = View.inflate(mContext, R.layout.item_rechange,null);
        }
        RechangeValue rechangeValue =getItem(position);
        Button btn =ViewHolder.get(view,R.id.rechange_btn);
        btn.setText(rechangeValue.getValue());
        if (rechangeValue.getIsCheck().equals("0")){ //0为未选中状态
            btn.setBackgroundResource(R.drawable.btn_rechange_none);
            btn.setTextColor(mContext.getResources().getColor(R.color.txt_two));
        }else {
            btn.setBackgroundResource(R.drawable.btn_rechange_style);
            btn.setTextColor(mContext.getResources().getColor(R.color.text_hint_miss));
        }
        return view;
    }
}
