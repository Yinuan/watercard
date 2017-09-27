package com.klcxkj.reshui.activity;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.klcxkj.klcxkj_waterdemo.R;
import com.klcxkj.reshui.entry.AddCardResult;
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
 * Description:自助补卡
 */
public class ACT_FillCard extends ACT_Network {
	private String fill_url = StringConfig.BASE_URL +"tStudent/selfHelpAutoReMakeCard?";
	private TextView cardId;
	private TextView cardName;
	private TextView cardSex;
	private TextView cardIDCard;
	private TextView cardBuilding;
	private TextView cardRoom;
	private Button mButtonBindNext;
	private CardInfo cardInfo;
	private UserInfo userInfo;
	private LoadingDialogProgress progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_campus_card_fill);

		initView();
		initdata();
		bindEvent();



	}

	private void initdata() {
		cardInfo = AppPreference.getInstance().getCardInfo();
		userInfo =AppPreference.getInstance().getUserInfo();
		if (cardInfo !=null){
			cardId.setText(cardInfo.getCardID()+"");
			cardName.setText(cardInfo.getEmployeeName());
			int sexI = Integer.valueOf(cardInfo.getSexID());
			String sexNm="";
			switch (sexI){
				case 0:
					sexNm ="女";
					break;
				case 1:
					sexNm ="男";
					break;
			}
			cardSex.setText(sexNm);
			cardIDCard.setText(cardInfo.getIdentifier());
			cardBuilding.setText(cardInfo.getBuildingName());
			cardRoom.setText(cardInfo.getRoomName());
		}
	}

	private void initView() {
		cardId = (TextView) findViewById(R.id.fill_cardId);
		cardName = (TextView) findViewById(R.id.fill_cardName);
		cardSex = (TextView) findViewById(R.id.fill_cardSex);
		cardIDCard = (TextView) findViewById(R.id.fill_cardIDCard);
		cardBuilding = (TextView) findViewById(R.id.fill_cardBuilding);
		cardRoom = (TextView) findViewById(R.id.fill_cardRoom);
		mButtonBindNext = (Button) findViewById(R.id.button_submit);
	}

	private void bindEvent() {
			showMenu("自助补卡");


		mButtonBindNext.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				showPopOut();

			}
		});

	}


	@Override
	protected void handleErrorResponse(String url, VolleyError error) {
		super.handleErrorResponse(url, error);
		mButtonBindNext.setEnabled(true);
		if (error instanceof TimeoutError) {
			toast(R.string.timeout_error);
		} else {
			toast(R.string.operate_error);
		}
		progress.dismiss();
	}

	@Override
	protected void handleResponse(String url, JSONObject json) {
		super.handleResponse(url, json);
		progress.dismiss();
		Log.d("ACT_FillCard", "json:" + json);
		Gson gson =new Gson();
		AddCardResult result =gson.fromJson(json.toString(),AddCardResult.class);
		if (result.getSuccess().equals("true")){
			if (result.getObj()==0){
				toast("补卡成功，请及时领取新卡");
			}else {
				toast(result.getMsg());
			}

		}else {
			if (result.getObj() ==-1){
				toast("补卡失败，未找到人员信息");
			}else if (result.getObj()==-2){
				toast("补卡失败，卡状态不正确");
			}else if (result.getObj() ==-3){
				toast("补卡异常");
			}
		}
		finish();
	}

	private void showPopOut() {
		View view1 = LayoutInflater.from(ACT_FillCard.this).inflate(R.layout.pop_style_2, null);
		TextView title = (TextView) view1.findViewById(R.id.pop_title);
		TextView content = (TextView) view1.findViewById(R.id.pop_content);
		Button btn_ok = (Button) view1.findViewById(R.id.pop_btn_confrim);
		Button btn_cacle = (Button) view1.findViewById(R.id.pop_btn_cancle);
		title.setText("提示");
		content.setText(getResources().getString(R.string.dialog_content_fill_card));
		btn_ok.setText("确定");
		btn_cacle.setText("取消");

		final PopupWindow popupWindow = new PopupWindow(view1, ViewGroup.LayoutParams.WRAP_CONTENT,
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
				progress = GlobalTools.getInstance().showDailog(ACT_FillCard.this,"提交..");
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("PrjID",userInfo.getPrjID()+"");
				params.put("EmployeeID", userInfo.getEmployeeID()+"");
				params.put("ServerIP", userInfo.getServerIP());
				params.put("ServerPort", userInfo.getServerPort()+"");
				sendPostRequest(fill_url, params);
				mButtonBindNext.setEnabled(false);
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
}
