package com.klcxkj.reshui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.klcxkj.klcxkj_waterdemo.R;
import com.klcxkj.reshui.adpater.LRoomAdpater;
import com.klcxkj.reshui.entry.Ban;
import com.klcxkj.reshui.entry.BaseBo;
import com.klcxkj.reshui.entry.BuildingAndRoomName;
import com.klcxkj.reshui.entry.CardInfo;
import com.klcxkj.reshui.entry.Room;
import com.klcxkj.reshui.entry.RoomResult;
import com.klcxkj.reshui.entry.UserInfo;
import com.klcxkj.reshui.tools.StringConfig;
import com.klcxkj.reshui.util.AppPreference;
import com.klcxkj.reshui.util.GlobalTools;
import com.klcxkj.reshui.widget.ClearEditText;
import com.klcxkj.reshui.widget.LoadingDialogProgress;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ACT_RoomChose extends ACT_Network {

    private String buildingId ="";  //楼栋ID
    private String buildingName ="";  //楼栋ID
    private List<String> data;
    private List<Room> roomDatas;
    private LRoomAdpater adpater;
    private ListView listView;
    private ClearEditText clearEditText;
    private TextView roomAddress;
    private UserInfo userInfo;
    private static String ROOM_URL = StringConfig.BASE_URL + "tStudent/studentGetRoomInfoByBuildindID?"; //房间
    private static String UPDATE_INFO = StringConfig.BASE_URL + "tStudent/studentUpdateUserInfo?"; //更新个人信息

    private String cType;  //选择楼栋类型
    private LoadingDialogProgress progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act__building_chose);
        initView();
        initData();
        bindView();
        Log.d("ACT_RoomChose", Thread.currentThread().getName());
    }



    private void initView() {
        showMenu("房间号选择");
        Intent intent =getIntent();
        buildingId =intent.getStringExtra("buildingID");
        buildingName =intent.getStringExtra("buildingName");
        cType =intent.getStringExtra("type");
        clearEditText = (ClearEditText) findViewById(R.id.room_search);
        roomAddress = (TextView) findViewById(R.id.room_address);
        listView = (ListView) findViewById(R.id.list_room);
        roomAddress.setText("当前: "+buildingName);

    }



    private void initData() {
       progress = GlobalTools.getInstance().showDailog(this,"加载");
        userInfo =AppPreference.getInstance().getUserInfo();
        loadDataFromSever();
        data =new ArrayList<>();
        adpater =new LRoomAdpater(this);
        adpater.setList(data);
        listView.setAdapter(adpater);

    }

    private void bindView() {
        //listciew
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //提交修改
                if (cType.equals("mineCenter")){
                   String roomId = roomDatas.get(position).getRoomID();
                    progress =GlobalTools.getInstance().showDailog(ACT_RoomChose.this,"提交");
                    updateUserInfoToServer(roomId);
                }else if (cType.equals("ACT_CampusCardApply")){
                    // fangjianhao
                    Room room =roomDatas.get(position);
                    Ban build =new Ban(buildingId,buildingName);
                    EventBus.getDefault().postSticky(new BuildingAndRoomName(room,build));
                    EventBus.getDefault().postSticky("choseRoom");
                    finish();
                }


            }
        });
        //搜素
        clearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ("".equals(editable.toString())){
                    adpater =new LRoomAdpater(ACT_RoomChose.this);
                    adpater.setList(data);
                    listView.setAdapter(adpater);
                    return;
                }
                List<String> sData =new ArrayList<String>();
                for (String room:data) {
                    if (room.contains(editable.toString())){
                        sData.add(room);
                    }
                }
                adpater =new LRoomAdpater(ACT_RoomChose.this);
                adpater.setList(sData);
                listView.setAdapter(adpater);
            }
        });
    }

    /**
     * 向服务器提交个人资料的修改
     */
    private void updateUserInfoToServer( String roomId) {
        UserInfo userinfo = AppPreference.getInstance().getUserInfo();
        CardInfo cardInfo =AppPreference.getInstance().getCardInfo();
        HashMap<String,String> map =new HashMap<>();
        map.put("TelPhone",userinfo.getTelPhone());
        map.put("PrjID",userinfo.getPrjID()+"");
        map.put("EmployeeID",cardInfo.getEmployeeID()+"");
        map.put("EmployeeName",cardInfo.getEmployeeName());
        map.put("RoomID",roomId);
        map.put("SexID",cardInfo.getSexID());
        map.put("Identifier",cardInfo.getIdentifier()+"123456");
        map.put("ServerIP",userinfo.getServerIP());
        map.put("ServerPort",userinfo.getServerPort()+"");
        map.put("headIcon","");
        Log.d("ACT_RoomChose", "map:" + map);
        sendPostRequest(UPDATE_INFO,map);

    }

    /**
     * 请求服务器的房间号数据
     */
    private void loadDataFromSever() {

        HashMap<String,String> map =new HashMap<>();
        map.put("PrjID",userInfo.getPrjID()+"");
        map.put("BuildingID",buildingId);
        map.put("ServerIP",userInfo.getServerIP());
        map.put("ServerPort",userInfo.getServerPort()+"");
        sendPostRequest(ROOM_URL,map);
    }




    /**
     * post提交的回掉
     * @param url
     * @param json
     */
    @Override
    protected void handleResponse(String url, JSONObject json) {
        super.handleResponse(url, json);
        progress.dismiss();
        Log.d("ACT_RoomChose", "handleResponse"+json.toString());
        Gson gson =new Gson();
        if (url.contains(ROOM_URL)){
            RoomResult result1 =gson.fromJson(json.toString(),RoomResult.class);
            if (result1.getObj() !=null){
                roomDatas =result1.getObj();
                for (int i = 0; i < roomDatas.size(); i++) {
                    data.add(roomDatas.get(i).getRoomName());
                }
                adpater.notifyDataSetChanged();
            }
        }else if (url.contains(UPDATE_INFO)){
            BaseBo result =gson.fromJson(json.toString(),BaseBo.class);
            if (result !=null){
                if (result.isSuccess()) {
                    //提示成功
                    toast(result.getMsg());
                    //结束
                    finish();
                }
            }else {
                toast("数据解析错误");
                Log.d("ACT_RoomChose", "result ==null");
            }
        }


    }

    @Override
    protected void handleErrorResponse(String url, VolleyError error) {
        super.handleErrorResponse(url, error);
       progress.dismiss();
        if (error instanceof TimeoutError) {
            toast(R.string.timeout_error);
        } else {
            toast(R.string.operate_error);
        }
        Log.d("ACT_RoomChose","handleErrorResponse"+ error.toString());
    }
}
