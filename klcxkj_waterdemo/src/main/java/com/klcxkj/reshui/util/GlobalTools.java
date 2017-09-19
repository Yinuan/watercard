package com.klcxkj.reshui.util;

import android.content.Context;

import com.klcxkj.reshui.widget.LoadingDialogProgress;


/**
 * autor:OFFICE-ADMIN
 * time:2017/9/9
 * email:yinjuan@klcxkj.com
 * description:
 */

public class GlobalTools {

    private static class SingletonHolder {
        private static GlobalTools instance = new GlobalTools();

    }

    /**
     * 构造方法描述:获取实例
     *
     * @return 返 回 类 型:Utils
     */
    public static GlobalTools getInstance() {
        return SingletonHolder.instance;
    }

    private LoadingDialogProgress progress;
    public LoadingDialogProgress showDailog(Context context, String content)
    {
        progress = LoadingDialogProgress.show(context,content+"" , true, null);

        return progress;
    }

    /**
     * 构造方法描述:获取手机状态栏的高度
     *
     * @param context
     * @return 返 回 类 型:int
     */
    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
