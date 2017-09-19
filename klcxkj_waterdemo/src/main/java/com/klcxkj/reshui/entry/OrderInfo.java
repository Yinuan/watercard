package com.klcxkj.reshui.entry;

/**
 * autor:OFFICE-ADMIN
 * time:2017/8/31
 * email:yinjuan@klcxkj.com
 * description:调用支付的实体类
 */
public class OrderInfo extends BaseBo{
	private String key;
	private String notify_url;
	private String returnUrl;
	private String orderdes;
	
	private String orderID;
	private String partner;
	private String seller_email;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getOrderdes() {
		return orderdes;
	}
	public void setOrderdes(String orderdes) {
		this.orderdes = orderdes;
	}
	
	public String getReturn_url() {
		return returnUrl;
	}
	public void setReturn_url(String return_url) {
		this.returnUrl = return_url;
	}
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	public String getPartner() {
		return partner;
	}
	public void setPartner(String partner) {
		this.partner = partner;
	}
	public String getSeller_email() {
		return seller_email;
	}
	public void setSeller_email(String seller_email) {
		this.seller_email = seller_email;
	}
}
