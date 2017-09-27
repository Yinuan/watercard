package com.klcxkj.reshui.entry;

/**
 * autor:OFFICE-ADMIN
 * time:2017/9/25
 * email:yinjuan@klcxkj.com
 * description:自助申卡成功
 */

public class ApplyCard {

    /**
     * msg : 自助申卡成功
     * success : true
     * EmployeeID : 7082
     */
    private String msg;
    private String success;
    private int EmployeeID;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public void setEmployeeID(int EmployeeID) {
        this.EmployeeID = EmployeeID;
    }

    public String getMsg() {
        return msg;
    }

    public String getSuccess() {
        return success;
    }

    public int getEmployeeID() {
        return EmployeeID;
    }
}
