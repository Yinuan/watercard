package com.klcxkj.reshui.entry;

/**
 * autor:OFFICE-ADMIN
 * time:2017/9/21
 * email:yinjuan@klcxkj.com
 * description:有没有卡机
 */

public class CardSelfIsOk {

    /**
     * msg : 成功
     * hasChargeDev : 0
     * success : true
     */
    private String msg;
    private int hasChargeDev;
    private String success;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setHasChargeDev(int hasChargeDev) {
        this.hasChargeDev = hasChargeDev;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public int getHasChargeDev() {
        return hasChargeDev;
    }

    public String getSuccess() {
        return success;
    }
}
