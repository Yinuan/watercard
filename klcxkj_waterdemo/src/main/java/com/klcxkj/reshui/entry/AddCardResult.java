package com.klcxkj.reshui.entry;

/**
 * autor:OFFICE-ADMIN
 * time:2017/9/4
 * email:yinjuan@klcxkj.com
 * description:自助补卡
 */

public class AddCardResult {

    /**
     * msg : 成功
     * obj : 0
     * success : true
     */
    private String msg;
    private int obj;
    private String success;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setObj(int obj) {
        this.obj = obj;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public int getObj() {
        return obj;
    }

    public String getSuccess() {
        return success;
    }
}
