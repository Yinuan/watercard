package com.klcxkj.reshui.entry;

import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/9/1
 * email:yinjuan@klcxkj.com
 * description:服务后台返回的房间号
 */

public class RoomResult {

    /**
     * msg : 成功
     * obj : [{"RoomName":"020201020202","RoomID":8}]
     * success : true
     */
    private String msg;
    private List<Room> obj;
    private String success;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setObj(List<Room> obj) {
        this.obj = obj;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public List<Room> getObj() {
        return obj;
    }

    public String getSuccess() {
        return success;
    }

    public class ObjEntity {
        /**
         * RoomName : 020201020202
         * RoomID : 8
         */
        private String RoomName;
        private int RoomID;

        public void setRoomName(String RoomName) {
            this.RoomName = RoomName;
        }

        public void setRoomID(int RoomID) {
            this.RoomID = RoomID;
        }

        public String getRoomName() {
            return RoomName;
        }

        public int getRoomID() {
            return RoomID;
        }
    }
}
