package com.klcxkj.reshui.entry;

/**
 * autor:OFFICE-ADMIN
 * time:2017/9/1
 * email:yinjuan@klcxkj.com
 * description:
 */

public class BuildingAndRoomName {
   private Room room;
    private Ban build;

    public BuildingAndRoomName(Room room, Ban build) {
        this.room = room;
        this.build = build;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Ban getBuild() {
        return build;
    }

    public void setBuild(Ban build) {
        this.build = build;
    }
}
