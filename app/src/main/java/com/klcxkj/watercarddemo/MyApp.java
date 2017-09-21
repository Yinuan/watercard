package com.klcxkj.watercarddemo;

import android.app.Application;

import com.klcxkj.reshui.tools.StringConfig;
import com.klcxkj.reshui.util.AppPreference;

/**
 * autor:OFFICE-ADMIN
 * time:2017/9/15
 * email:yinjuan@klcxkj.com
 * description:
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //缓存
        AppPreference.getInstance().init(getApplicationContext());
        //支付宝配置文件设置
        StringConfig.BASE_URL =Config.BASE_URL;
        StringConfig.RSA_PRIVATE =Config.RSA_PRIVATE;
    }
}
