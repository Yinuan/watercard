package com.klcxkj.reshui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.klcxkj.klcxkj_waterdemo.R;
import com.klcxkj.reshui.entry.ApplyCard;
import com.klcxkj.reshui.entry.Ban;
import com.klcxkj.reshui.entry.BaseBo;
import com.klcxkj.reshui.entry.BuildingAndRoomName;
import com.klcxkj.reshui.entry.Room;
import com.klcxkj.reshui.entry.UserInfo;
import com.klcxkj.reshui.tools.StringConfig;
import com.klcxkj.reshui.util.AppPreference;
import com.klcxkj.reshui.util.GlobalTools;
import com.klcxkj.reshui.widget.LoadingDialogProgress;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * author : yinjuan
 * time： 2017/6/9 13:54
 * email：yin.juan2016@outlook.com
 * Description:自助申卡
 */
public class ACT_CampusCardApply extends ACT_Network implements View.OnClickListener {
	private String applyCard = StringConfig.BASE_URL + "tStudent/studentAutoMakeCard?";
	private String AlterPass = StringConfig.BASE_URL+"tStudent/forgetLogInPwd?"; //密码
	private LinearLayout layout_all;//布局parent
	private EditText mEditName;//name
	private RadioGroup sex;
	private EditText mEditStudentNumber; //身份证
	private RelativeLayout building_layout,room_layout;
	private TextView mBuildingID,mRoomID;//楼栋和房间号
	private Button mButtonBindNext;//提交按钮
	private UserInfo userInfo;
	private String sexNumber;//性别
	private String buildingId; //楼栋号
	private String buildingName;//楼栋名字
	private Room aRoom;
	private Ban aBuild;

	private LoadingDialogProgress progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_campus_card_apply);
		initView();
		bindEvent();
		showPop();
		//时间总线注册
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//时间总线注销
		EventBus.getDefault().unregister(this);
	}

	/**
	 * 时间总线的消息接收窗口
	 * @param brName
	 */
	@Subscribe (threadMode = ThreadMode.MAIN)
	public void onEvent(BuildingAndRoomName brName){
		aRoom =brName.getRoom();
		aBuild =brName.getBuild();
		buildingName =aBuild.getBuildingName();
		buildingId =aBuild.getBuildingID();
		mBuildingID.setText(aBuild.getBuildingName());
		mRoomID.setText(aRoom.getRoomName());

	}

	private void initView() {
		showMenu("自助办卡");
		mEditName = (EditText) findViewById(R.id.apply_name);
		sex = (RadioGroup) findViewById(R.id.apply_sex);
		mEditStudentNumber = (EditText) findViewById(R.id.apply_IDCard);
		building_layout = (RelativeLayout) findViewById(R.id.apply_building_layout);
		room_layout = (RelativeLayout) findViewById(R.id.apply_room_layout);
		mBuildingID = (TextView) findViewById(R.id.apply_building);
		mRoomID = (TextView) findViewById(R.id.apply_room);
		mButtonBindNext = (Button) findViewById(R.id.apply_btn);
		layout_all = (LinearLayout) findViewById(R.id.apply_layout);

	}


	private void bindEvent() {
		//姓名输入
		mEditName.setFilters(new InputFilter[] { new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

				try {
					int len = 0;
					boolean more = false;
					do {
						SpannableStringBuilder builder = new SpannableStringBuilder(
								dest).replace(dstart, dend,
								source.subSequence(start, end));
						len = builder.toString().getBytes("UTF-8").length;
						more = len > 30;
						if (more) {
							end--;
							source = source.subSequence(start, end);
						}
					} while (more);
					return source;
				} catch (UnsupportedEncodingException e) {
					return "Exception";
				}
			}
		} });
		//性别选择
		sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup,  int checkId) {
				RadioButton radioButton = (RadioButton) findViewById(checkId);
				String str =radioButton.getText().toString();
				if (str.equals("男")){
					sexNumber ="1";
				}else {
					sexNumber ="0";
				}
				Log.d("ACT_CampusCardApply", "raSex:"+sexNumber);
			}
		});
		mButtonBindNext.setOnClickListener(this);
		building_layout.setOnClickListener(this);
		room_layout.setOnClickListener(this);

	}

private void submitDataToserver(String pass){
	progress = GlobalTools.getInstance().showDailog(ACT_CampusCardApply.this,"提交..");
	//String pass = AppPreference.getInstance().getPassWord();
	userInfo =AppPreference.getInstance().getUserInfo();
	Log.d("ACT_CampusCardApply", "passssss:"+pass);
	Log.d("ACT_CampusCardApply", "sexxxxx:"+sexNumber);
	HashMap<String, String> params = new HashMap<String, String>();
	params.put("telPhone", userInfo.getTelPhone());
	params.put("PrjID", userInfo.getPrjID()+"");
	params.put("EmployeeName", mEditName.getText().toString());
	params.put("SexID", sexNumber);
	params.put("RoomID", aRoom.getRoomID());
	params.put("identifier", mEditStudentNumber.getText()
			.toString());
	params.put("UserPwd", pass);
	params.put("ServerIP", userInfo.getServerIP());
	params.put("ServerPort", userInfo.getServerPort()+"");
	Log.d("ACT_CampusCardApply", "params:" + params);
	sendPostRequest(applyCard, params);
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

	}

	@Override
	protected void handleResponse(String url, JSONObject json) {
		super.handleResponse(url, json);
		progress.dismiss();
		Log.d("ACT_CampusCardApply", "json:" + json);
		Gson gson =new Gson();
		BaseBo baseBo =gson.fromJson(json.toString(),BaseBo.class);
		if (url.contains(applyCard)){
			if (baseBo.isSuccess()){
				ApplyCard card =gson.fromJson(json.toString(),ApplyCard.class);
				toast(card.getMsg());
				//更新用户资料
				UserInfo userInfo =AppPreference.getInstance().getUserInfo();
				userInfo.setEmployeeID(card.getEmployeeID());
				AppPreference.getInstance().saveLoginUser(userInfo);
				Log.d("ACT_CampusCardApply", "applyCardIsSucess");
				EventBus.getDefault().postSticky("applyCardIsSucess");
				AppPreference.getInstance().savePassWord(pass);
				showPop2();
			}else {
				toast(baseBo.getMsg());
			}
		}else if (url.contains(AlterPass)){
			toast(baseBo.getMsg());
			//

			finish();

		}


	}


	@Override
	public void onClick(View view) {
		int i = view.getId();
		if (i == R.id.apply_building_layout) {
			Intent intent1 = new Intent(ACT_CampusCardApply.this, ACT_BuildingChose.class);
			intent1.putExtra("type", "ACT_CampusCardApply");
			intent1.putExtra("buildingName", buildingName);
			startActivity(intent1);

		} else if (i == R.id.apply_room_layout) {
			Intent intent = new Intent(ACT_CampusCardApply.this, ACT_RoomChose.class);
			if (buildingId != null) {
				intent.putExtra("buildingID", buildingId);
				intent.putExtra("buildingName", mBuildingID.getText().toString());
				intent.putExtra("type", "ACT_CampusCardApply");
			} else {
				toast("请先选择楼栋");
				return;
			}
			startActivity(intent);

		} else if (i == R.id.apply_btn) {
			if (TextUtils.isEmpty(mEditName.getText())) {
				toast("请输入姓名");
				return;
			}
			if (TextUtils.isEmpty(sexNumber)) {
				toast("请选择性别");
				return;
			}
			if (TextUtils.isEmpty(mEditStudentNumber.getText())) {
				toast("请输入身份证号码");
				return;
			}
			if (TextUtils.isEmpty(mBuildingID.getText().toString())) {
				toast("请选择楼栋");
				return;
			}
			if (TextUtils.isEmpty(mRoomID.getText().toString())) {
				toast("请选择房间");
				return;
			}
			showpop3();


		}
	}


	private void showPop() {
		View view = LayoutInflater.from(ACT_CampusCardApply.this).inflate(R.layout.pop_style_2, null);
		TextView title = (TextView) view.findViewById(R.id.pop_title);
		TextView content = (TextView) view.findViewById(R.id.pop_content);
		Button btn_ok = (Button) view.findViewById(R.id.pop_btn_confrim);
		Button btn_cacle = (Button) view.findViewById(R.id.pop_btn_cancle);
		title.setText("提示");
		content.setText("如已办卡，请直接绑卡");
		btn_ok.setText("去绑卡");
		btn_cacle.setText("立即办卡");

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
		getWindow().getDecorView().post(new Runnable() {
			@Override
			public void run() {
				popupWindow.showAtLocation(layout_all, Gravity.CENTER, 0, 0);
			}
		});

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
				//
				startActivity(new Intent(ACT_CampusCardApply.this,
						ACT_CampusCardBind.class));
				finish();
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
		View view = LayoutInflater.from(ACT_CampusCardApply.this).inflate(R.layout.pop_style_3, null);
		TextView title = (TextView) view.findViewById(R.id.pop_3_content);
		Button btn = (Button) view.findViewById(R.id.pop_3_btn);
		title.setGravity(Gravity.CENTER_HORIZONTAL);
		title.setText("\u3000"+"请前往自助领卡机领卡");
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
				finish();
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

	/**
	 * 选择其他金额
	 */
	private String pass;
	private void showpop3() {
		View view = LayoutInflater.from(ACT_CampusCardApply.this).inflate(R.layout.pop_style_4, null);
		final AutoCompleteTextView value = (AutoCompleteTextView) view.findViewById(R.id.pop_4_value);
		Button btn_ok = (Button) view.findViewById(R.id.pop_4_confrim);
		Button btn_cancle = (Button) view.findViewById(R.id.pop_4_cancle);
		TextView tv = (TextView) view.findViewById(R.id.pop_4_title);
		tv.setText("提示:请输入6位数交易密码");
		final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
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
				submitDataToserver( pass);
				/*HashMap<String,String> map =new HashMap<String, String>();
				map.put("telPhone",userInfo.getTelPhone());
				map.put("LogPwd",pass);
				sendPostRequest(AlterPass,map);*/

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
