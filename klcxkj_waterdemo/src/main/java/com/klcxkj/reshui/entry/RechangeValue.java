package com.klcxkj.reshui.entry;

/**
 * autor:OFFICE-ADMIN
 * time:2017/8/29
 * email:yinjuan@klcxkj.com
 * description:充值金额大小的实体类
 */

public class RechangeValue {
    private String value;  //钱的大小
    private String isCheck; //是否选中
    private int isOthers;


    public RechangeValue(String value, String isCheck, int isOthers) {
        this.value = value;
        this.isCheck = isCheck;
        this.isOthers = isOthers;
    }

    public int getIsOthers() {
        return isOthers;
    }

    public void setIsOthers(int isOthers) {
        this.isOthers = isOthers;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(String isCheck) {
        this.isCheck = isCheck;
    }
}
