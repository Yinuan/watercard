package com.klcxkj.reshui.entry;


import java.io.Serializable;

/**
 * autor:OFFICE-ADMIN
 * time:2017/8/31
 * email:yinjuan@klcxkj.com
 * description:基础解析类
 */
public class BaseBo implements Serializable {
	private boolean success = false;
	private String msg ="";
	private String errorCode="";
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
