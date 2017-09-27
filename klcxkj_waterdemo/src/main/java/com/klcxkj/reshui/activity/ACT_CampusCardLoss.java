package com.klcxkj.reshui.activity;

import android.content.Context;
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

import org.json.JSONObject;

import java.util.HashMap;


/**
 * author : yinjuan
 * time： 2017/6/9 13:54
 * email：yin.juan2016@outlook.com
 * Description:卡片挂失
 */
public class ACT_CampusCardLoss extends ACT_Network {
//	http://211.149.224.58:6002/tStudent/studentLostAndUnLostCard?PrjID=5&EmployeeID=3&intStatus=0
	private String url = StringConfig.BASE_URL+"tStudent/studentLostAndUnLostCard?";
	private EditText mEditCardID, mEditCardName, mEditCardStatus;
	private Button mButtonSubmit;
	private UserInfo userInfo;
	private CardInfo mCardInfo;
	private CardInfo cardIn;//用来解析的对象
	private HashMap<String, String> mStatusMap;
	private LoadingDialogProgress progress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_campus_card_loss);
		initView();
		bindEvent();
		initViewFromIntent();
	}
	
	private void initView() {

		mEditCardID = (EditText)this.findViewById(R.id.edit_cardID);
		mEditCardName = (EditText)this.findViewById(R.id.edit_user_name);
		mEditCardStatus = (EditText)this.findViewById(R.id.edit_card_status);
		mButtonSubmit = (Button)this.findViewById(R.id.button_bind_next);

	}
	
	private void initViewFromIntent() {
		mCardInfo = AppPreference.getInstance().getCardInfo();
		userInfo =AppPreference.getInstance().getUserInfo();
		if (mCardInfo!=null){
			mEditCardID.setText(mCardInfo.getCardID()+"");
			mEditCardName.setText(mCardInfo.getEmployeeName());
			int statusId = mCardInfo.getNCardStatusID();
			String statusName ="";
			switch (statusId){
				case 0://正常
					statusName="正常";
					break;
				case 1://挂失
					statusName="挂失";
					break;
				case 2://退卡
					statusName="退卡";
					break;
				case 3://未领卡
					statusName="未领卡";
					break;
				case 4://销户
					statusName="销户";
					break;

			}
			mEditCardStatus.setText(statusName);
			if (statusName.equals("正常")){
				showMenu("挂失");
			}else {
				showMenu("解挂");
			}

		}

	}

	private void bindEvent() {

		mButtonSubmit.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				if (mEditCardStatus.getText().toString().equals("正常")|| mEditCardStatus.getText().toString().equals("挂失")){
					showpop();
				}

			}
		});
	}

	@Override
	protected void handleErrorResponse(String url, VolleyError error) {
		super.handleErrorResponse(url, error);
		progress.dismiss();
		if(error instanceof TimeoutError){
			toast(R.string.timeout_error);
		}else{
			toast(R.string.operate_error);
		}
		mButtonSubmit.setEnabled(true);
	}

	@Override
	protected void handleResponse(String url, JSONObject json) {
		super.handleResponse(url, json);
		progress.dismiss();
		Gson gson =new Gson();
		BaseBo baseBo =gson.fromJson(json.toString(),BaseBo.class);
		if (baseBo.isSuccess()){
			cardIn =gson.fromJson(json.toString(),CardInfo.class);
			if (cardIn.getNCardStatusID()==1){//挂失状态

				toast("挂失成功");
				mCardInfo.setNCardStatusID(1);
				showMenu("解挂");
				mEditCardStatus.setText("挂失");
			}else {	//正常
				mCardInfo.setNCardStatusID(0);
				mEditCardStatus.setText("正常");
				toast("解挂成功");
				showMenu("挂失");
			}
			AppPreference.getInstance().saveCardInfos(mCardInfo);
			finish();
		}else {
			toast(baseBo.getMsg());
		}
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
	private void showpop() {
		View view = LayoutInflater.from(ACT_CampusCardLoss.this).inflate(R.layout.pop_style_4, null);
		final AutoCompleteTextView value = (AutoCompleteTextView) view.findViewById(R.id.pop_4_value);
		value.setHint("请输入密码");
		Button btn_ok = (Button) view.findViewById(R.id.pop_4_confrim);
		Button btn_cancle = (Button) view.findViewById(R.id.pop_4_cancle);
		TextView title = (TextView) view.findViewById(R.id.pop_4_title);
		title.setText("提示:请输入6位数交易密码");
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
			//验证密码
				String password = value.getText().toString();
				String pa =AppPreference.getInstance().getPassWord();
				String doingName ="";
				Log.d("ACT_CampusCardLoss","password:"+ pa);
				if (!TextUtils.isEmpty(password) && password.length() ==6) {
					if (pa !=null && pa.equals(password)){
						HashMap<String,String> map =new HashMap<String, String>();
						map.put("PrjID",userInfo.getPrjID()+"");
						map.put("EmployeeID",userInfo.getEmployeeID()+"");
						if (mCardInfo.getNCardStatusID()==1) {
							map.put("intStatus",0+"");
							doingName ="解挂中";
						}else if (mCardInfo.getNCardStatusID()==0) {
							map.put("intStatus",1+"");
							doingName ="挂失中";
						}
						map.put("ServerIP",userInfo.getServerIP());
						map.put("ServerPort",userInfo.getServerPort()+"");
						sendPostRequest(url,map);
						progress = GlobalTools.getInstance().showDailog(ACT_CampusCardLoss.this,doingName);
						mButtonSubmit.setEnabled(false);

					}else {
						toast("操作失败，请重置交易密码");
					}
					popupWindow.dismiss();
				} else {
//							DialogUtil.dismissAlertDialog();
//							PopupWindowUtil.showPopupWindow(ACT_CampusCardLoss.this, ACT_CampusCardLoss.this.findViewById(R.id.layout_navbar));
					toast("请正确输入交易密码");
				}
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
