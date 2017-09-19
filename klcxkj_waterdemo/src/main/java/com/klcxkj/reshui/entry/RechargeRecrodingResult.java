package com.klcxkj.reshui.entry;

import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/9/6
 * email:yinjuan@klcxkj.com
 * description:充值记录
 */

public class RechargeRecrodingResult {

    /**
     * msg : 成功
     * obj : [{"DT":"2016-08-09 21:01:56.0","DMoney":100,"FlagName":"充值"}]
     * success : true
     * AllPage : 1
     */
    private String msg;
    private List<RechargeRecording> obj;
    private String success;
    private int AllPage;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setObj(List<RechargeRecording> obj) {
        this.obj = obj;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public void setAllPage(int AllPage) {
        this.AllPage = AllPage;
    }

    public String getMsg() {
        return msg;
    }

    public List<RechargeRecording> getObj() {
        return obj;
    }

    public String getSuccess() {
        return success;
    }

    public int getAllPage() {
        return AllPage;
    }

    public class ObjEntity {
        /**
         * DT : 2016-08-09 21:01:56.0
         * DMoney : 100
         * FlagName : 充值
         */
        private String DT;
        private int DMoney;
        private String FlagName;

        public void setDT(String DT) {
            this.DT = DT;
        }

        public void setDMoney(int DMoney) {
            this.DMoney = DMoney;
        }

        public void setFlagName(String FlagName) {
            this.FlagName = FlagName;
        }

        public String getDT() {
            return DT;
        }

        public int getDMoney() {
            return DMoney;
        }

        public String getFlagName() {
            return FlagName;
        }
    }
}
