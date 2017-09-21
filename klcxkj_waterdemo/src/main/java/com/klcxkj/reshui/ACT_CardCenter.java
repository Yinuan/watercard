package com.klcxkj.reshui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.klcxkj.reshui.entry.CardInfo;
import com.klcxkj.reshui.entry.UserInfo;
import com.klcxkj.reshui.tools.StringConfig;
import com.klcxkj.reshui.util.AppPreference;
import com.klcxkj.reshui.util.GlobalTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.HashMap;

public class ACT_CardCenter extends ACT_Network implements View.OnClickListener{

    private Button ntn1,btn2,btn3,btn4,btn5,btn6;
    public static String register = StringConfig.BASE_URL + "tStudent/studentRegedit?";//注册地址
    public static String login = StringConfig.BASE_URL+"tStudent/sTudentLogIn?"; //登陆
    private String queryCardInfo = StringConfig.BASE_URL + "tStudent/studentGetCardInfo?";  //查询卡片信息
    private UserInfo userInfo;
    private CardInfo cardInfo;
    private String tellPhone;
    private String loginPass;
    private String projectId;

    private RelativeLayout cardUnBind;
    private LinearLayout cardBind;
    private RelativeLayout rechangeLine,lossLine,fillLine,applyLine;
    private Button bind; //绑定按钮
    private TextView cardId;
    private TextView cardStatus;
    private TextView cardMonney;
    private TextView cardRemain;  //未领金额
    /**
     * 卡片未绑定  --》 绑卡/申卡
     * 卡片绑定 --》 卡片解绑/充值/挂失/补卡
     */
    private int cardIsBind; //卡片是否绑定
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_card_center);
        //注册
        showMenu("卡片管理");
         initView();

        initData(); //登陆
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String msg){

        if (msg.equals("cardIsbinded")){
            ntn1.setText("(卡片)解绑");
        }else if (msg.equals("cardrelievebinded")){
            ntn1.setText("(卡片)绑定");
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {

        cardUnBind =findViewById(R.id.home_card_unbind);
        cardBind =findViewById(R.id.home_card_binded);
        bind =findViewById(R.id.home_card_bindBtn);
        cardId =findViewById(R.id.text_card_ID);
        cardStatus =findViewById(R.id.text_card_statu);
        cardMonney =findViewById(R.id.text_card_monney);
        cardRemain =findViewById(R.id.text_card_monney_remain);
        rechangeLine =findViewById(R.id.line_rechange);
        lossLine =findViewById(R.id.line_loss);
        fillLine =findViewById(R.id.line_fill);
        applyLine =findViewById(R.id.line_apply);
        rechangeLine.setOnClickListener(this);
        lossLine.setOnClickListener(this);
        fillLine.setOnClickListener(this);
        applyLine.setOnClickListener(this);
    }


    private void initData() {
        userInfo = AppPreference.getInstance().getUserInfo();
        cardInfo =AppPreference.getInstance().getCardInfo();
        if (userInfo ==null){
            HashMap<String,String> map =new HashMap<>();
            map.put("prjRecId",127+"");
            map.put("telPhone","18565651403");
            map.put("logPwd","123456");
            sendPostRequest(register,map);
        }else {
            cardIsBind =userInfo.getEmployeeID();
            if (cardIsBind ==0){
                cardBind.setVisibility(View.GONE);
                cardUnBind.setVisibility(View.VISIBLE);
            }else {
                cardBind.setVisibility(View.VISIBLE);
                cardUnBind.setVisibility(View.GONE);
                if (cardInfo ==null){
                    loadCardDatasforServer();
                }else {
                   showView();
                }
            }

        }

    }

    /**
     * 登陆
     */
    private void loginingToServer() {
        progress = GlobalTools.getInstance().showDailog(this,"登录中");
        HashMap<String,String> map =new HashMap<>();
        map.put("telPhone","18565651403");
        map.put("logPwd","123456");
        sendPostRequest(login,map);
    }

    /**
     * 查询卡片信息
     */
    private void loadCardDatasforServer() {
        HashMap<String,String> map =new HashMap<>();
        map.put("PrjID",userInfo.getPrjID()+"");
        map.put("EmployeeID",userInfo.getEmployeeID()+"");
        map.put("ServerIP",userInfo.getServerIP());
        map.put("ServerPort",userInfo.getServerPort()+"");
        sendPostRequest(queryCardInfo,map);
        progress = GlobalTools.getInstance().showDailog(ACT_CardCenter.this,"查询卡片信息");
    }
    @Override
    protected void handleErrorResponse(String url, VolleyError error) {
        super.handleErrorResponse(url, error);
        progress.dismiss();
    }

    @Override
    protected void handleResponse(String url, JSONObject json) {
        super.handleResponse(url, json);

        Log.d("ACT_CardCenter", "json:" + json);
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
                Log.d("ACT_CardCenter", "userInfo:" + userInfo.getPrjID());
                Log.d("ACT_CardCenter", "url;=="+StringConfig.BASE_URL);
                cardIsBind =userInfo.getEmployeeID();
                if (cardIsBind ==0){
                    cardBind.setVisibility(View.GONE);
                    cardUnBind.setVisibility(View.VISIBLE);
                }else {
                    cardBind.setVisibility(View.VISIBLE);
                    cardUnBind.setVisibility(View.GONE);
                    if (cardInfo ==null){
                        loadCardDatasforServer();
                    }else {
                        showView();
                    }
                }
            }else {
                toast(userInfo.getMsg());
            }

        }else if (url.contains(queryCardInfo)){
            progress.dismiss();
            cardInfo =gson.fromJson(json.toString(),CardInfo.class);
            if (cardInfo !=null){
                //保存卡片信息
                AppPreference.getInstance().saveCardInfo(json.toString());
                showView();
            }else {
                toast("查询卡片信息失败");
            }

        }

    }

    private void showView() {
        cardId.setText(cardInfo.getCardID());
        String status ="";
        switch (Integer.valueOf(cardInfo.getnCardStatusID())){
            case 0:
                status ="正常";
                break;
            case 1:
                status ="挂失";
                break;
            case 2:
                status ="退卡";
                break;
            case 3:
                status ="未领卡";
                break;
            case 4:
                status ="销户";
                break;
        }
        cardStatus.setText(status);
        cardMonney.setText(cardInfo.getnCardValue());
        cardRemain.setText(cardInfo.getAccountMoney());
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.home_card_bindBtn) {
            Intent intent = new Intent(ACT_CardCenter.this, ACT_CampusCardBind.class);
            intent.putExtra("isBind", cardIsBind + "");
            startActivity(intent);

        } else if (i == R.id.line_rechange) {
            if (cardIsBind == 0) {
                toast("卡片未绑定");
                return;
            }
            startActivity(new Intent(ACT_CardCenter.this, ACT_Rechage.class));

        } else if (i == R.id.line_loss) {
            if (cardIsBind == 0) {
                toast("卡片未绑定");
                return;
            }
            startActivity(new Intent(ACT_CardCenter.this, ACT_CampusCardLoss.class));

        } else if (i == R.id.line_fill) {
            if (cardIsBind == 0) {
                toast("卡片未绑定");
                return;
            }
            startActivity(new Intent(ACT_CardCenter.this, ACT_FillCard.class));

        } else if (i == R.id.line_apply) {
            if (cardIsBind == 1) {
                toast("已有卡片绑定");
                return;
            }
            startActivity(new Intent(ACT_CardCenter.this, ACT_CampusCardApply.class));

        }

    }
}
