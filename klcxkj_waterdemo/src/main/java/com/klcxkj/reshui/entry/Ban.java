package com.klcxkj.reshui.entry;

public class Ban extends BaseBo{
	private String BuildingID;
	private String BuildingName;

	public Ban(String buildingID, String buildingName) {
		BuildingID = buildingID;
		BuildingName = buildingName;
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
	
}
