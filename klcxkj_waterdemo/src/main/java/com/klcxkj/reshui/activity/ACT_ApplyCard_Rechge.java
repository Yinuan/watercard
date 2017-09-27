package com.klcxkj.reshui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.klcxkj.klcxkj_waterdemo.R;
import com.klcxkj.reshui.entry.BaseBo;
import com.klcxkj.reshui.entry.CardInfo;
import com.klcxkj.reshui.entry.OrderInfo;
import com.klcxkj.reshui.entry.UserInfo;
import com.klcxkj.reshui.pay.PayResult;
import com.klcxkj.reshui.tools.StringConfig;
import com.klcxkj.reshui.util.AppPreference;
import com.klcxkj.reshui.util.GlobalTools;
import com.klcxkj.reshui.widget.LoadingDialogProgress;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;



public class ACT_ApplyCard_Rechge extends ACT_Network {

    private TextView content;
    private Button btn;
    private UserInfo userInfo;
    private CardInfo mCardInfo;
    private LoadingDialogProgress progress;
    private String urlGetAlipayInfor = StringConfig.BASE_URL+"tPrjInfo/getAlipayInfor2?";

    private OrderInfo mOrderInfo;
    private String urlReturnUrlApp;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;

    private String perMonney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act__apply_card__rechge);
        initData();
        initview();
        bindView();
    }

    private void initData() {
        mCardInfo = AppPreference.getInstance().getCardInfo();
        userInfo =AppPreference.getInstance().getUserInfo();
    }

    private void bindView() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("PrjID", userInfo.getPrjID()+"");
                params.put("EmployeeName",mCardInfo.getEmployeeName());
                params.put("EmployeeID", mCardInfo.getEmployeeID()+"");
                params.put("CardID", mCardInfo.getCardID()+"");
                params.put("totalFee", "0.01");
                params.put("ServerIP", userInfo.getServerIP());
                params.put("ServerPort", userInfo.getServerPort()+"");
                sendPostRequest(urlGetAlipayInfor, params);
                btn.setEnabled(false);
                progress = GlobalTools.getInstance().showDailog(ACT_ApplyCard_Rechge.this,"准备中..");
            }
        });
    }

    private void initview() {
        showMenu("预充值");
        Intent in =getIntent();
        perMonney =in.getStringExtra("perMonny");
        content = (TextView) findViewById(R.id.apply_card_rechge_content);
        btn = (Button) findViewById(R.id.apply_card_rechge_btn);
        content.setText("您已成功提交申请，本业务需预充"+perMonney+"元，才可领卡。");
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
        btn.setEnabled(true);
    }



    @Override
    protected void handleResponse(String url, JSONObject json) {
        super.handleResponse(url, json);
        progress.dismiss();
        BaseBo result = new Gson().fromJson(json.toString(), BaseBo.class);
        if(result.isSuccess()){
            if (url.contains(urlGetAlipayInfor)) {
                Gson gson = new Gson();
                mOrderInfo = gson.fromJson(json.toString(), OrderInfo.class);
                urlReturnUrlApp = mOrderInfo.getReturn_url();
                pay();
            }else if (url.contains(urlReturnUrlApp)){
                //toast(result.getMsg());
            }
        }else{
            try {
                toast(json.getString("msg"));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        btn.setEnabled(true);
    }



    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Intent intent = new Intent(getApplicationContext(), ACT_CampusCardApplyRechageReport.class);
                        intent.putExtra("money", "0.01");
                        intent.putExtra("rechageType","cardGet");
                        startActivity(intent);
                        toast("充值成功");
                        finish();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            toast("支付结果确认中");

                        } else if (TextUtils.equals(resultStatus, "6001"))  {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            toast("您已取消支付，支付未完成");

                        } else {
                            toast("支付失败");

                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    toast("检查结果为：" + msg.obj);
                    break;
                }
                default:
                    break;
            }
        };
    };

    /**
     * call alipay sdk pay. 调用SDK支付
     *
     */
    public void pay() {
        if (mOrderInfo == null || TextUtils.isEmpty(mOrderInfo.getOrderdes())) {
            return;
        }
        // 订单
//		String orderInfo = getOrderInfo("校园热水充值", "该测试商品的详细描述", edit_money.getText().toString());
//
//		// 对订单做RSA 签名
//		String sign = sign(orderInfo);
//		try {
//			// 仅需对sign 做URL编码
//			sign = URLEncoder.encode(sign, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}

        // 完整的符合支付宝参数规范的订单信息
//		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
//				+ getSignType();

        final String payInfo = mOrderInfo.getOrderdes();

//		final String payInfo = "_input_charset=utf-8&body=项目2zs支付宝app充值0.01元&it_b_pay=30m&notify_url=http://localhost:8081/roadPay/aliPay/notifyUrl_App&out_trade_no=201610081513090697895&partner=2088711300455398&payment_type=1&seller_id=szklcxkj@163.com&service=mobile.securitypay.pay&subject=项目2zs充值0.01元&total_fee=0.01&sign=L5pXrnjrSIzVku1iNesKQW39gxO9cBRn5pq17TQFyo2f%2FJ2MZJd4r1HVGtiDmXiLmhxnRr1GuvyyON%2Fu%2By17WjXbqTq3cp3Eow4SFiDgJRMs7OBl%2BvdQHj5gS%2Bv6bO4NpNiPaBtq9KxXsdAQh0Qnla5RsutP0eYYtCAgOLKePe8%3D&sign_type=RSA";

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(ACT_ApplyCard_Rechge.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
                if (TextUtils.isEmpty(result)) {
                    return;
                }
                PayResult payResult = new PayResult(result);
                HashMap<String, String> params = getParams(payResult.getResult());
                params.put("resultStatus", payResult.getResultStatus());
//				params.put("memo", payResult.getMemo());
                params.put("ServerIP", userInfo.getServerIP());
                params.put("ServerPort", userInfo.getServerPort()+"");
                sendPostRequest(urlReturnUrlApp, params);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    private HashMap<String, String> getParams(String result) {
        HashMap<String, String> params = new HashMap<String, String>();
        String[] ccc = result.split("&");
        for (String string2 : ccc) {
            String[] ddd = string2.split("=");
            if (ddd.length > 1) {
                params.put(ddd[0], ddd[1].replace("\"", ""));
            }
        }
        return params;
    }
    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     *
     */
    public void check(View v) {
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(ACT_ApplyCard_Rechge.this);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();

    }

    /**
     * get the sdk version. 获取SDK版本号
     *
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * create the order info. 创建订单信息
     *
     */
    public String getOrderInfo(String subject, String body, String price) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + mOrderInfo.getPartner()+ "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + mOrderInfo.getSeller_email() + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + mOrderInfo.getOrderID() + "\"";   //getOutTradeNo()

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + mOrderInfo.getNotify_url() + "\"";

//		orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
//				+ "\"";
        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }
}
