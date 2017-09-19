package com.klcxkj.reshui.entry;

/**
 * autor:OFFICE-ADMIN
 * time:2017/9/6
 * email:yinjuan@klcxkj.com
 * description:充值记录一条数据
 */

public class RechargeRecording {


    /**
     * DT : 2016-08-09 21:01:56.0
     * DMoney : 100
     * FlagName : 充值
     */
    private String DT;
    private double DMoney;
    private String FlagName;

    public RechargeRecording(String DT, double DMoney, String flagName) {
        this.DT = DT;
        this.DMoney = DMoney;
        FlagName = flagName;
    }

    public String getDT() {
        return DT;
    }

    public void setDT(String DT) {
        this.DT = DT;
    }

    public double getDMoney() {
        return DMoney;
    }

    public void setDMoney(double DMoney) {
        this.DMoney = DMoney;
    }

    public String getFlagName() {
        return FlagName;
    }

    public void setFlagName(String flagName) {
        FlagName = flagName;
    }
}
