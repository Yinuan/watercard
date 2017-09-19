package com.klcxkj.reshui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.klcxkj.klcxkj_waterdemo.R;
import com.klcxkj.reshui.activity.ACT_CampusCardApply;
import com.klcxkj.reshui.activity.ACT_CampusCardBind;
import com.klcxkj.reshui.activity.ACT_CampusCardLoss;
import com.klcxkj.reshui.activity.ACT_FillCard;
import com.klcxkj.reshui.activity.ACT_Network;
import com.klcxkj.reshui.activity.ACT_Rechage;
import com.klcxkj.reshui.entry.BaseBo;
import com.klcxkj.reshui.entry.UserInfo;
import com.klcxkj.reshui.tools.StringConfig;
import com.klcxkj.reshui.util.AppPreference;
import com.klcxkj.reshui.util.GlobalTools;

import org.json.JSONObject;

import java.util.HashMap;

public class Main2Activity extends ACT_Network implements View.OnClickListener{

    private Button ntn1,btn2,btn3,btn4,btn5,btn6;
    public static String register = StringConfig.BASE_URL + "tStudent/studentRegedit?";//注册地址
    public static String login = StringConfig.BASE_URL+"tStudent/sTudentLogIn?"; //登陆
    private String queryCardInfo = StringConfig.BASE_URL + "tStudent/studentGetCardInfo?";  //查询卡片信息
    private UserInfo userInfo;
    /**
     * 卡片未绑定  --》 绑卡/申卡
     * 卡片绑定 --》 卡片解绑/充值/挂失/补卡
     */
    private int cardIsBind; //卡片是否绑定

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //注册
        showMenu("卡片管理");
        initData(); //登陆
        ntn1 =findViewById(R.id.button1);
        btn2 =findViewById(R.id.button2);
        btn3 =findViewById(R.id.button3);
        btn4 =findViewById(R.id.button4);
        btn5 =findViewById(R.id.button5);
        btn6 =findViewById(R.id.button6);
        ntn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
    }



    private void initData() {
        if (userInfo ==null){
            HashMap<String,String> map =new HashMap<>();
            map.put("prjRecId",85+"");
            map.put("telPhone","18711337001");
            map.put("logPwd","123456");
            sendPostRequest(register,map);
        }else {
            cardIsBind =userInfo.getEmployeeID();
           if (cardIsBind ==1){
               ntn1.setText("解绑");
           }else {
               ntn1.setText("绑卡");
           }

        }

    }

    private void loginingToServer() {
        progress = GlobalTools.getInstance().showDailog(this,"登录中");
        HashMap<String,String> map =new HashMap<>();
        map.put("telPhone","18711337001");
        map.put("logPwd","123456");
        sendPostRequest(login,map);
    }

    @Override
    protected void handleErrorResponse(String url, VolleyError error) {
        super.handleErrorResponse(url, error);
        progress.dismiss();
    }

    @Override
    protected void handleResponse(String url, JSONObject json) {
        super.handleResponse(url, json);

        Log.d("Main2Activity", "json:" + json);
        Gson gson =new Gson();
        if (url.contains(register)){
            BaseBo basBo =gson.fromJson(json.toString(),BaseBo.class);
            if (basBo.isSuccess()){
                loginingToServer();
            }else {
                if (basBo.getErrorCode() .equals("1")) {
                   // toast(rBean.getMsg());
                    //登陆
                    loginingToServer();
                }
            }

        }else  if (url.contains(login)){
            progress.dismiss();
            userInfo =gson.fromJson(json.toString(), UserInfo.class);
            if (userInfo.getSuccess().equals("true")){
                AppPreference.getInstance().saveLoginUser(userInfo);
                Log.d("Main2Activity", "userInfo:" + userInfo.getPrjID());

                cardIsBind =userInfo.getEmployeeID();
                if (cardIsBind ==1){
                    ntn1.setText("解绑");
                }else {
                    ntn1.setText("绑卡");

                }
            }else {
                toast(userInfo.getMsg());
            }

        }

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.button1) {
            Intent intent = new Intent(Main2Activity.this, ACT_CampusCardBind.class);
            intent.putExtra("isBind", cardIsBind + "");
            startActivity(intent);

        } else if (i == R.id.button2) {
            if (cardIsBind == 0) {
                toast("卡片未绑定");
                return;
            }
            startActivity(new Intent(Main2Activity.this, ACT_Rechage.class));

        } else if (i == R.id.button3) {
            if (cardIsBind == 0) {
                toast("卡片未绑定");
                return;
            }
            startActivity(new Intent(Main2Activity.this, ACT_CampusCardLoss.class));

        } else if (i == R.id.button4) {
            if (cardIsBind == 0) {
                toast("卡片未绑定");
                return;
            }
            startActivity(new Intent(Main2Activity.this, ACT_FillCard.class));

        } else if (i == R.id.button5) {
            if (cardIsBind == 1) {
                toast("已有卡片绑定");
                return;
            }
            startActivity(new Intent(Main2Activity.this, ACT_CampusCardApply.class));

        }

    }


}
