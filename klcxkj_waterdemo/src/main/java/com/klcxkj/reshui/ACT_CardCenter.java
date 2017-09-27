package com.klcxkj.reshui;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.klcxkj.klcxkj_waterdemo.R;
import com.klcxkj.reshui.activity.ACT_ApplyCard_Rechge;
import com.klcxkj.reshui.activity.ACT_BillActivity;
import com.klcxkj.reshui.activity.ACT_CampusCardApply;
import com.klcxkj.reshui.activity.ACT_CampusCardBind;
import com.klcxkj.reshui.activity.ACT_CampusCardLoss;
import com.klcxkj.reshui.activity.ACT_Network;
import com.klcxkj.reshui.activity.ACT_PassSetting;
import com.klcxkj.reshui.activity.ACT_Rechage;
import com.klcxkj.reshui.entry.BaseBo;
import com.klcxkj.reshui.entry.CardInfo;
import com.klcxkj.reshui.entry.CardSelfIsOk;
import com.klcxkj.reshui.entry.UpdatePassResult;
import com.klcxkj.reshui.entry.UserInfo;
import com.klcxkj.reshui.tools.StringConfig;
import com.klcxkj.reshui.util.AppPreference;
import com.klcxkj.reshui.util.GlobalTools;
import com.klcxkj.reshui.widget.LoadingDialogProgress;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.HashMap;

public class ACT_CardCenter extends ACT_Network implements View.OnClickListener{


    public static String register = StringConfig.BASE_URL + "tStudent/studentRegedit?";//注册地址
    public static String login = StringConfig.BASE_URL+"tStudent/getStuInfoByTelPhone?"; //登陆
    private String queryCardInfo = StringConfig.BASE_URL + "tStudent/studentGetCardInfo?";  //查询卡片信息
    private static final String queryCardSelf =StringConfig.BASE_URL+"/tStudent/getHasChargeDev?";//自助办卡，补卡
    private String unbind_url = StringConfig.BASE_URL + "tStudent/cancelBinding?"; //解除绑定
    private UserInfo userInfo;
    private CardInfo cardInfo;
    private String tellPhone;
  //  private String loginPass;
    private String projectId;

    private RelativeLayout cardUnBind;
    private LinearLayout cardBind;
    private RelativeLayout rechangeLine,lossLine,fillLine,applyLine,billLine,passLine;
    private Button bind; //绑定按钮
    private TextView cardText; //绑定提示文字
    private TextView cardId;
    private TextView cardStatus;
    private TextView cardMonney;
    private TextView cardRemain;  //未领金额
    private LoadingDialogProgress progress;
    private  TextView right;
    private SmartRefreshLayout smartRefreshLayout;

    /**
     * 卡片未绑定  --》 绑卡/申卡
     * 卡片绑定 --》 卡片解绑/充值/挂失/补卡
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_card_center);
        //注册
        TextView title = (TextView) findViewById(R.id.top_title);
        right = (TextView) findViewById(R.id.top_right_text);
        right.setText("解除绑定");
        right.setVisibility(View.GONE);

        title.setText("卡片管理");
        LinearLayout backBtn = (LinearLayout) findViewById(R.id.top_btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
         initView();

        initData(); //登陆
        bindview();
        EventBus.getDefault().register(this);
    }



    private void registerPhone(){
        HashMap<String,String> map =new HashMap<>();
        map.put("prjRecId",projectId);
        map.put("telPhone",tellPhone);
        map.put("logPwd","963214");
        sendPostRequest(register,map);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ACT_CardCenter", "onResume.log");
        if (userInfo !=null){
            if (!CardIsBinded()){ //未绑定
                right.setVisibility(View.GONE);
                cardBind.setVisibility(View.GONE);
                cardUnBind.setVisibility(View.VISIBLE);
            }else {
                showView();
                loadCardDatasforServer();
                cardBind.setVisibility(View.VISIBLE);
                cardUnBind.setVisibility(View.GONE);
                right.setVisibility(View.VISIBLE);

            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String msg){

        if (msg.equals("cardIsbinded")){
            //更新卡片状态
            if (CardIsBinded()){
                cardBind.setVisibility(View.VISIBLE);
                cardUnBind.setVisibility(View.GONE);
            }else {
                cardBind.setVisibility(View.GONE);
                cardUnBind.setVisibility(View.VISIBLE);
            }
        }else if (msg.equals("applyCardIsSucess")){
            userInfo =AppPreference.getInstance().getUserInfo();
            loadCardDatasforServer();
        }

    }

    private boolean CardIsBinded() {
        userInfo = AppPreference.getInstance().getUserInfo();
        if (userInfo.getEmployeeID()==0) { //未绑卡
            return false;
        }
        return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 判断是否可以自助办卡，补卡
     */
    private void queryCardSelfIsOk(){
        HashMap<String,String> map =new HashMap<>();
        map.put("PrjID",userInfo.getPrjID()+"");
        map.put("ServerIP",userInfo.getServerIP());
        map.put("ServerPort",userInfo.getServerPort()+"");
        sendPostRequest(queryCardSelf,map);
    }

    private void initView() {
        smartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);

        cardUnBind = (RelativeLayout) findViewById(R.id.home_card_unbind);
        cardBind = (LinearLayout) findViewById(R.id.home_card_binded);
        bind = (Button) findViewById(R.id.home_card_bindBtn);
        cardId = (TextView) findViewById(R.id.text_card_ID);
        cardText = (TextView) findViewById(R.id.home_card_bindBtn_tv);
        cardStatus = (TextView) findViewById(R.id.text_card_statu);
        cardMonney = (TextView) findViewById(R.id.text_card_monney);
        cardRemain = (TextView) findViewById(R.id.text_card_monney_remain);
        rechangeLine = (RelativeLayout) findViewById(R.id.line_rechange);
        passLine = (RelativeLayout) findViewById(R.id.line_pass);
        lossLine = (RelativeLayout) findViewById(R.id.line_loss);
        fillLine = (RelativeLayout) findViewById(R.id.line_fill);
        applyLine = (RelativeLayout) findViewById(R.id.line_apply);
        billLine = (RelativeLayout) findViewById(R.id.line_bill);
        rechangeLine.setOnClickListener(this);
        lossLine.setOnClickListener(this);
        fillLine.setOnClickListener(this);
        applyLine.setOnClickListener(this);
        billLine.setOnClickListener(this);
        bind.setOnClickListener(this);
        right.setOnClickListener(this);
        passLine.setOnClickListener(this);
    }

    private void bindview() {
        smartRefreshLayout.setEnableLoadmore(false);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                loadCardDatasforServer();
            }
        });
    }
    private void initData() {
        Intent intent =getIntent();
        tellPhone =intent.getStringExtra("tellPhoneNum");
        //loginPass =intent.getStringExtra("passWord");
        projectId =intent.getStringExtra("prjRecId");
        progress = GlobalTools.getInstance().showDailog(this,"登录中");
       loginingToServer();



    }

    /**
     * 登陆
     */
    private void loginingToServer() {
        HashMap<String,String> map =new HashMap<>();
        map.put("telPhone",tellPhone);
       // map.put("logPwd",loginPass);
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

    }
    @Override
    protected void handleErrorResponse(String url, VolleyError error) {
        super.handleErrorResponse(url, error);
        if (progress !=null){
            progress.dismiss();
        }

    }

    @Override
    protected void handleResponse(String url, JSONObject json) {
        super.handleResponse(url, json);
        Gson gson =new Gson();
        if (url.contains(register)){
            Log.d("ACT_CardCenter", "register:" + json);
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

            Log.d("ACT_CardCenter", "login:" + json);
            BaseBo basBo =gson.fromJson(json.toString(),BaseBo.class);
            if (basBo.isSuccess()){
                if (progress !=null){
                    progress.dismiss();
                }
                userInfo =gson.fromJson(json.toString(), UserInfo.class);
                if (userInfo.getSuccess().equals("true")){
                    AppPreference.getInstance().saveLoginUser(userInfo);
                    if (!CardIsBinded()){
                        cardBind.setVisibility(View.GONE);
                        cardUnBind.setVisibility(View.VISIBLE);
                        right.setVisibility(View.GONE);
                    }else {
                        cardBind.setVisibility(View.VISIBLE);
                        cardUnBind.setVisibility(View.GONE);
                        right.setVisibility(View.VISIBLE);
                        progress = GlobalTools.getInstance().showDailog(ACT_CardCenter.this,"查询卡片信息");
                        loadCardDatasforServer();
                        showView();

                    }
                }else {
                    toast(userInfo.getMsg());
                }
            }else {
                //注册
                registerPhone();
            }


        }else if (url.contains(queryCardInfo)){
            if (progress !=null){
                progress.dismiss();
            }
            cardInfo =gson.fromJson(json.toString(),CardInfo.class);
            if (cardInfo.getCardID() !=0){
                //保存卡片信息
                AppPreference.getInstance().saveCardInfo(json.toString());
                showView();
            }else {
                toast("查询卡片信息失败");
            }

        }else if (url.contains(queryCardSelf)){ //查询卡机
            CardSelfIsOk cardIsOk =gson.fromJson(json.toString(),CardSelfIsOk.class);
            if (cardIsOk.getHasChargeDev() ==1){
                //可以自助办卡，补卡
                userInfo.setHasChargeDev(1);
                //更新用户信息
                AppPreference.getInstance().saveLoginUser(userInfo);
            }
        }else if (url.contains(unbind_url)){
            UpdatePassResult resp = gson.fromJson(json.toString(), UpdatePassResult.class);
            if (resp.getSuccess().equals("true")){
                //①更新userInfo
                userInfo.setEmployeeID(0);
                AppPreference.getInstance().saveLoginUser(userInfo);
                //②删除卡片缓存
                AppPreference.getInstance().deleteCardInfo();
                //
                cardBind.setVisibility(View.GONE);
                cardUnBind.setVisibility(View.VISIBLE);
                right.setVisibility(View.GONE);
                cardText.setText("绑定校园卡以查看余额");
                bind.setText("绑卡");
                toast(resp.getMsg());
            }
        }

    }

    private void showView() {
        if (cardInfo ==null){
            return;
        }
        if (cardInfo.getPrefillMoney() !=0){
            cardBind.setVisibility(View.GONE);
            cardUnBind.setVisibility(View.VISIBLE);
            bind.setText("充值");
            cardText.setText("已申卡或已补卡待充值，需预充"+cardInfo.getPrefillMoney()+"元才可领卡");
        }
        cardId.setText(cardInfo.getCardID()+"");
        String status ="";
        switch (cardInfo.getNCardStatusID()){
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
        cardStatus.setText("("+status+")");
        cardMonney.setText(cardInfo.getNCardValue()+"");
        cardRemain.setText(cardInfo.getAccountMoney()+"");
    }

    private String getcardStats(){
        if (cardInfo ==null){
            return "未知";
        }
        String status ="";
        switch (cardInfo.getNCardStatusID()){
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
        return status;
    }
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.home_card_bindBtn) {
            if (bind.getText().toString().equals("绑卡")){
                Intent intent = new Intent(ACT_CardCenter.this, ACT_CampusCardBind.class);
                startActivity(intent);
            }else if (bind.getText().toString().equals("充值")){
                Intent intent = new Intent(ACT_CardCenter.this, ACT_ApplyCard_Rechge.class);
                intent.putExtra("perMonny",cardInfo.getPrefillMoney()+"");
                startActivity(intent);
            }


        } else if (i == R.id.line_rechange) {
            if (!CardIsBinded()) {
                toast("请先申请或绑定卡片");
                return;
            }
            if (cardInfo.getNCardStatusID() ==0 || cardInfo.getNCardStatusID() ==3){
                startActivity(new Intent(ACT_CardCenter.this, ACT_Rechage.class));
            }else {
                toast("您的卡片状态是:"+getcardStats()+",不能充值");
            }


        } else if (i == R.id.line_loss) {
            if (!CardIsBinded()) {
                toast("请先申请或绑定卡片");
                return;
            }else {
                if (cardInfo.getNCardStatusID()==1 || cardInfo.getNCardStatusID()==0){
                    startActivity(new Intent(ACT_CardCenter.this, ACT_CampusCardLoss.class));
                }else {

                    toast("您的卡片状态是:"+getcardStats()+",不能挂失");
                }
            }


        } else if (i == R.id.line_fill) {
            if (!CardIsBinded()) {
                toast("请先申请或绑定卡片");
                return;
            }
            if (cardInfo.getNCardStatusID() ==1){ //挂失
                int msg = userInfo.getHasChargeDev();
                if (msg==1){
                  //  startActivity(new Intent(ACT_CardCenter.this, ACT_FillCard.class));
                    showPop2();
                }else {
                   // toast("自助办卡功能暂未开通，请到管理中心去办理");
                    showPop2();
                }
            }else {
                toast("您的卡片状态是:"+getcardStats()+",不能补卡");
            }


        } else if (i == R.id.line_apply) {
            if (CardIsBinded()) {
                toast("已有卡片绑定");
                return;
            }
            int msg = userInfo.getHasChargeDev();
            if (msg==1){
                startActivity(new Intent(ACT_CardCenter.this, ACT_CampusCardApply.class));
            }else {
                //toast("自助办卡功能暂未开通，请到管理中心去办理");
                showPop2();
            }

        }else if (i ==R.id.line_pass){  //修改密码
           if (CardIsBinded()){
               startActivity(new Intent(ACT_CardCenter.this, ACT_PassSetting.class));
           }else {
               toast("请先申请或绑定卡片");
           }

        }else if (i ==R.id.line_bill){ //账单中心
            if (CardIsBinded()) {
                startActivity(new Intent(ACT_CardCenter.this, ACT_BillActivity.class));
            }else {
                toast("请先申请或绑定卡片");
            }
        }else if (i==R.id.top_right_text){ //解除绑定
            showPop();
        }

    }
    private int getWidth(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int width =(widthPixels*3)/4;
        return  width;
    }

    private void showPop() {
        View view1 = LayoutInflater.from(ACT_CardCenter.this).inflate(R.layout.pop_style_2b, null);
        TextView title = (TextView) view1.findViewById(R.id.pop_title);
        TextView content = (TextView) view1.findViewById(R.id.pop_content);
        Button btn_ok = (Button) view1.findViewById(R.id.pop_btn_confrim);
        Button btn_cacle = (Button) view1.findViewById(R.id.pop_btn_cancle);
        title.setText("警告");
        content.setText("\u3000"+getResources().getString(R.string.pop_content));
        btn_ok.setText("解除绑定");
        btn_cacle.setText("再看看先");

        final PopupWindow popupWindow = new PopupWindow(view1, getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ColorDrawable cd = new ColorDrawable(0x000000);
        popupWindow.setBackgroundDrawable(cd);
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);
        popupWindow.setFocusable(false);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(false);
        //点击外部消失
        //  popupWindow.setOutsideTouchable(true);
        //设置可以点击
        popupWindow.setTouchable(true);
        // 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 软键盘不会挡着popupwindow
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.showAtLocation(view1, Gravity.CENTER, 0, 0);
        //popupWindow.showAsDropDown(mSubmit);
        btn_cacle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                HashMap<String,String> map =new HashMap<String, String>();
                map.put("telPhone",userInfo.getTelPhone());
                map.put("ServerIP",userInfo.getServerIP());
                map.put("ServerPort",userInfo.getServerPort()+"");
                sendPostRequest(unbind_url,map);
            }
        });
        // 监听菜单的关闭事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        // 监听触屏事件
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                return false;
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }

    private void showPop2() {
        View view = LayoutInflater.from(ACT_CardCenter.this).inflate(R.layout.pop_style_3, null);
        TextView title = (TextView) view.findViewById(R.id.pop_3_content);
        Button btn = (Button) view.findViewById(R.id.pop_3_btn);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setText("\u3000"+"自助办卡功能暂未开通，请到管理中心去办理");
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ColorDrawable cd = new ColorDrawable(0x000000);
        popupWindow.setBackgroundDrawable(cd);
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);
        popupWindow.setFocusable(false);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        //  popupWindow.setOutsideTouchable(true);
        //设置可以点击
        popupWindow.setTouchable(true);
        // 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 软键盘不会挡着popupwindow
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //popupWindow.showAsDropDown(mSubmit);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                //
            }
        });
        // 监听菜单的关闭事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Log.d("ACT_CampusCardApply", "走没走");
            }
        });
        // 监听触屏事件
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                return false;
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }
}
