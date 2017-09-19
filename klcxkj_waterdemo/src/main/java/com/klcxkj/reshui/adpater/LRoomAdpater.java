package com.klcxkj.reshui.adpater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.klcxkj.klcxkj_waterdemo.R;


/**
 * autor:OFFICE-ADMIN
 * time:2017/9/8
 * email:yinjuan@klcxkj.com
 * description:宿舍listview适配器
 */

public class LRoomAdpater extends MyAdapter<String>{
    /**
     * 构造方法描述:基类构造方法
     *
     * @param mContext
     */
    public LRoomAdpater(Context mContext) {
        super(mContext);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view ==null){
            view = View.inflate(mContext, R.layout.item_room_,null);
        }
        String str =getItem(position);
        TextView roomName =ViewHolder.get(view,R.id.item_room_address);
        roomName.setText(str);
        return view;
    }
}
