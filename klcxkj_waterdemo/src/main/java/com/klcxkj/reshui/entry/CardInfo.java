package com.klcxkj.reshui.entry;


/**
 * autor:OFFICE-ADMIN
 * time:2017/8/31
 * email:yinjuan@klcxkj.com
 * description:卡片信息
 */
public class CardInfo extends BaseBo{
	private String AccountMoney;
	private String BuildingID;
	private String BuildingName;
	private String CardID;
	private String Deposit;
	private String EmployeeID;
	private String EmployeeName;
	private String EmployeeNum;
	private String Identifier;
	private String PrefillMoney;
	private String RoomID;
	private String RoomName;
	private String SexID;
	private String nCardStatusID;
	private String nCardValue;

	public String getSexID() {
		return SexID;
	}
	public void setSexID(String sexID) {
		SexID = sexID;
	}
	public String getAccountMoney() {
		return AccountMoney;
	}
	public void setAccountMoney(String accountMoney) {
		AccountMoney = accountMoney;
	}
	public String getBuildingID() {
		return BuildingID;
	}
	public void setBuildingID(String buildingID) {
		BuildingID = buildingID;
	}
	public String getBuildingName() {
		return BuildingName;
	}
	public void setBuildingName(String buildingName) {
		BuildingName = buildingName;
	}
	public String getCardID() {
		return CardID;
	}
	public void setCardID(String cardID) {
		CardID = cardID;
	}
	public String getDeposit() {
		return Deposit;
	}
	public void setDeposit(String deposit) {
		Deposit = deposit;
	}
	public String getEmployeeID() {
		return EmployeeID;
	}
	public void setEmployeeID(String employeeID) {
		EmployeeID = employeeID;
	}
	public String getEmployeeName() {
		return EmployeeName;
	}
	public void setEmployeeName(String employeeName) {
		EmployeeName = employeeName;
	}
	public String getEmployeeNum() {
		return EmployeeNum;
	}
	public void setEmployeeNum(String employeeNum) {
		EmployeeNum = employeeNum;
	}
	public String getIdentifier() {
		return Identifier;
	}
	public void setIdentifier(String identifier) {
		Identifier = identifier;
	}
	public String getPrefillMoney() {
		return PrefillMoney;
	}
	public void setPrefillMoney(String prefillMoney) {
		PrefillMoney = prefillMoney;
	}
	public String getRoomID() {
		return RoomID;
	}
	public void setRoomID(String roomID) {
		RoomID = roomID;
	}
	public String getRoomName() {
		return RoomName;
	}
	public void setRoomName(String roomName) {
		RoomName = roomName;
	}
	public String getnCardStatusID() {
		return nCardStatusID;
	}
	public void setnCardStatusID(String nCardStatusID) {
		this.nCardStatusID = nCardStatusID;
	}
	public String getnCardValue() {
		return nCardValue;
	}
	public void setnCardValue(String nCardValue) {
		this.nCardValue = nCardValue;
	}

}
