package com.klcxkj.reshui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.klcxkj.reshui.widget.LoadingDialogProgress;

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
	private String AlterPass = StringConfig.BASE_URL+"tStudent/forgetLogInPwd?"; //密码
	private Button mButtonBindNext;
	private EditText mEditCardID, mEditPassword;
	private UserInfo mUserInfo;
	private  CardInfo cardInfo;
	private RelativeLayout layout;

	private LoadingDialogProgress progress;
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
		if (mUserInfo.getEmployeeID() ==0){
			mButtonBindNext.setText("绑定");
		}else {
			if (cardInfo !=null){
				mButtonBindNext.setText("卡片解绑");
				layout.setVisibility(View.GONE);
				mEditCardID.setText(cardInfo.getCardID()+"");
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
				}

			}
		});
	}

	@Override
	protected void handleErrorResponse(String url, VolleyError error) {
		super.handleErrorResponse(url, error);
		if (progress!=null){
			progress.dismiss();
		}
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
		BaseBo baseBo =gson.fromJson(json.toString(),BaseBo.class);
		if (baseBo.isSuccess()){
			if (url.contains(bind_url)){
				//toast("绑定成功");
				if (progress!=null){
					progress.dismiss();
				}
				CardInfo cardInfoNew =gson.fromJson(json.toString(),CardInfo.class);
				//更新用户信息
				mUserInfo.setEmployeeID(cardInfoNew.getEmployeeID());
				AppPreference.getInstance().saveLoginUser(mUserInfo);
				EventBus.getDefault().postSticky("cardIsbinded");
				//查询卡片信息
				loadCardDatasforServer();
			}else if (url.contains(unbind_url)){
				//删除卡片信息
				if (progress!=null){
					progress.dismiss();
				}
				AppPreference.getInstance().deleteCardInfo();
				//①更新userInfo
				mUserInfo.setEmployeeID(0);
				AppPreference.getInstance().saveLoginUser(mUserInfo);
				EventBus.getDefault().postSticky("cardIsbinded");
				finish();

			}else if (url.contains(queryCardInfo)){//查询卡片信息
				if (progress!=null){
					progress.dismiss();
				}
				cardInfo =gson.fromJson(json.toString(),CardInfo.class);
				if (cardInfo !=null){
					//保存卡片信息
					AppPreference.getInstance().saveCardInfo(json.toString());
				}
				showpop();
			}else if (url.contains(AlterPass)){
				toast(baseBo.getMsg());
				//保存密码
				AppPreference.getInstance().savePassWord(pass);
				finish();
			}
		}else {
			toast(baseBo.getMsg());
			if (progress!=null){
				progress.dismiss();
			}
			mButtonBindNext.setEnabled(true);
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

	private int getWidth(){
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int widthPixels = metrics.widthPixels;
		int width =(widthPixels*3)/4;
		return  width;
	}
	/**
	 * 选择其他金额
	 */
	private String pass;
	private void showpop() {
		View view = LayoutInflater.from(ACT_CampusCardBind.this).inflate(R.layout.pop_style_4, null);
		final AutoCompleteTextView value = (AutoCompleteTextView) view.findViewById(R.id.pop_4_value);
		Button btn_ok = (Button) view.findViewById(R.id.pop_4_confrim);
		Button btn_cancle = (Button) view.findViewById(R.id.pop_4_cancle);
		TextView tv = (TextView) view.findViewById(R.id.pop_4_title);
		tv.setText("提示:请输入6位数交易密码");
		final PopupWindow popupWindow = new PopupWindow(view, getWidth(),
				ViewGroup.LayoutParams.WRAP_CONTENT);
		ColorDrawable cd = new ColorDrawable(0x000000);
		popupWindow.setBackgroundDrawable(cd);
		WindowManager.LayoutParams lp=getWindow().getAttributes();
		lp.alpha = 0.4f;
		getWindow().setAttributes(lp);
		//注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(false);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		//点击外部消失
		//  popupWindow.setOutsideTouchable(true);
		//设置可以点击
		popupWindow.setFocusable(true);
		// 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 软键盘不会挡着popupwindow
		popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				final InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(1000, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		},50);
		//popupWindow.showAsDropDown(mSubmit);
		btn_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
				//参数：1，自己的EditText。2，时间。
			}
		});
		btn_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				pass =value.getText().toString();
				if (pass.length()!=6){
					toast("请设置6位密码数字");
					return;
				}
				popupWindow.dismiss();
				//设置密码

				HashMap<String,String> map =new HashMap<String, String>();
				map.put("telPhone",mUserInfo.getTelPhone());
				map.put("LogPwd",pass);
				sendPostRequest(AlterPass,map);

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

		//设置软件盘不挡
		popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
		popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

	}

}
