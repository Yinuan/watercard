package com.klcxkj.reshui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.klcxkj.klcxkj_waterdemo.R;
import com.klcxkj.reshui.entry.BaseBo;
import com.klcxkj.reshui.entry.CardInfo;
import com.klcxkj.reshui.entry.UserInfo;
import com.klcxkj.reshui.tools.StringConfig;
import com.klcxkj.reshui.util.AppPreference;
import com.klcxkj.reshui.util.GlobalTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * author : yinjuan
 * time： 2017/6/9 13:54
 * email：yin.juan2016@outlook.com
 * Description:绑定卡片
 */
public class ACT_CampusCardBind extends ACT_Network {

	private String bind_url = StringConfig.BASE_URL+"tStudent/sTudentBinddingCard?"; //绑定
	private String unbind_url = StringConfig.BASE_URL + "tStudent/cancelBinding?"; //解除绑定
	private String queryCardInfo = StringConfig.BASE_URL + "tStudent/studentGetCardInfo?";  //查询卡片信息
	private Button mButtonBindNext;
	private EditText mEditCardID, mEditPassword;
	private UserInfo mUserInfo;
	private  CardInfo cardInfo;
	private RelativeLayout layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_campus_card_bind);

		initView();
		initdata();
		bindEvent();
		
	}

	private void initdata() {
		mUserInfo =AppPreference.getInstance().getUserInfo();
		Log.d("ACT_CampusCardBind", "mUserInfo:" + mUserInfo);
		cardInfo =AppPreference.getInstance().getCardInfo();
		Intent intent =getIntent();
		String cardIsBind =intent.getStringExtra("isBind");
		if (cardIsBind.equals("0")){
			mButtonBindNext.setText("绑定");
		}else {
			if (cardInfo !=null){
				mButtonBindNext.setText("卡片解绑");
				layout.setVisibility(View.GONE);
				mEditCardID.setText(cardInfo.getCardID());
				mEditCardID.setEnabled(false);
			}else {
				mButtonBindNext.setText("卡片已绑定，但无卡片信息");
				loadCardDatasforServer();
			}
		}

	}

	private void initView() {
		mButtonBindNext = (Button)this.findViewById(R.id.button_bind_next);
		mEditCardID = (EditText)this.findViewById(R.id.cardID);
		mEditPassword = (EditText)this.findViewById(R.id.password);
		layout = (RelativeLayout) findViewById(R.id.card_pin);
		showMenu("绑定校园卡");
	}

	private void bindEvent() {


		mButtonBindNext.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				if (mButtonBindNext.getText().toString().equals("绑定")){
					if (TextUtils.isEmpty(mEditCardID.getText())) {
						toast("请输入卡号");
						return;
					}
					if (TextUtils.isEmpty(mEditPassword.getText())) {
						toast("请输入PIN码");
						return;
					}

					HashMap<String,String> map =new HashMap<String, String>();
					map.put("PrjID",mUserInfo.getPrjID()+"");
					map.put("CardID", mEditCardID.getText().toString());
					map.put("CardPwd",mEditPassword.getText().toString());
					map.put("telPhone",mUserInfo.getTelPhone());
					map.put("ServerIP",mUserInfo.getServerIP()+"");
					map.put("ServerPort",mUserInfo.getServerPort()+"");
					sendPostRequest(bind_url,map);
					mButtonBindNext.setEnabled(false);
					progress = GlobalTools.getInstance().showDailog(ACT_CampusCardBind.this,"绑定中..");
				}else if (mButtonBindNext.getText().toString().equals("卡片解绑")){
					HashMap<String,String> map =new HashMap<>();
					map.put("telPhone",mUserInfo.getTelPhone());
					map.put("ServerIP",mUserInfo.getServerIP());
					map.put("ServerPort",mUserInfo.getServerPort()+"");
					sendPostRequest(unbind_url,map);
					progress = GlobalTools.getInstance().showDailog(ACT_CampusCardBind.this,"解除绑定中..");
				}
			}
		});
	}

	@Override
	protected void handleErrorResponse(String url, VolleyError error) {
		super.handleErrorResponse(url, error);
		progress.dismiss();
		mButtonBindNext.setEnabled(true);
		if(error instanceof TimeoutError){
			toast(R.string.timeout_error);
		}else{
			toast(R.string.operate_error);
		}
	}

	@Override
	protected void handleResponse(String url, JSONObject json) {
		super.handleResponse(url, json);
		//将卡片信息中的绑卡ID保存起来
		Log.d("ACT_CampusCardBind", "json:" + json);
		Gson gson =new Gson();
		BaseBo baseBo =gson.fromJson(json.toString(),CardInfo.class);
		if (baseBo.isSuccess()){
			if (url.contains(bind_url)){
				toast("绑定成功");
				CardInfo cardInfoNew =gson.fromJson(json.toString(),CardInfo.class);
				//更新用户信息
				mUserInfo.setEmployeeID(Integer.valueOf(cardInfoNew.getEmployeeID()));
				AppPreference.getInstance().saveLoginUser(mUserInfo);
				EventBus.getDefault().postSticky("cardIsbinded");
				//查询卡片信息
				loadCardDatasforServer();
			}else if (url.contains(unbind_url)){
				//删除卡片信息
				progress.dismiss();
				AppPreference.getInstance().deleteCardInfo();
				//①更新userInfo
				mUserInfo.setEmployeeID(0);
				AppPreference.getInstance().saveLoginUser(mUserInfo);
				EventBus.getDefault().postSticky("cardrelievebinded");
				finish();

			}else if (url.contains(queryCardInfo)){//查询卡片信息
				progress.dismiss();
				cardInfo =gson.fromJson(json.toString(),CardInfo.class);
				if (cardInfo !=null){
					//保存卡片信息
					AppPreference.getInstance().saveCardInfo(json.toString());
				}
				finish();
			}
		}else {
			toast(baseBo.getMsg());
			progress.dismiss();
		}

	}

	/**
	 * 查询卡片信息
	 */
	private void loadCardDatasforServer() {
		HashMap<String,String> map =new HashMap<>();
		map.put("PrjID",mUserInfo.getPrjID()+"");
		map.put("EmployeeID",mUserInfo.getEmployeeID()+"");
		map.put("ServerIP",mUserInfo.getServerIP());
		map.put("ServerPort",mUserInfo.getServerPort()+"");
		sendPostRequest(queryCardInfo,map);
		Log.d("ACT_CardCenter", "userInfo.getPrjID():" + mUserInfo.getPrjID());
		Log.d("ACT_CardCenter", "userInfo.getEmployeeID():" + mUserInfo.getEmployeeID());
		Log.d("ACT_CardCenter", mUserInfo.getServerIP());
		Log.d("ACT_CardCenter", "userInfo.getServerPort():" + mUserInfo.getServerPort());
		progress = GlobalTools.getInstance().showDailog(ACT_CampusCardBind.this,"查询卡片信息");
	}

}
