package com.klcxkj.reshui.entry;

/**
 * autor:OFFICE-ADMIN
 * time:2017/8/31
 * email:yinjuan@klcxkj.com
 * description:更新密码的json
 */

public class UpdatePassResult {

    /**
     * msg : 成功
     * success : true
     */
    private String msg;
    private String success;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public String getSuccess() {
        return success;
    }
}
