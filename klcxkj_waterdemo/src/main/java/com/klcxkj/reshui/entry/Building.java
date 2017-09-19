package com.klcxkj.reshui.entry;

import java.util.List;

/**
 * autor:OFFICE-ADMIN
 * time:2017/9/1
 * email:yinjuan@klcxkj.com
 * description:楼栋实体类
 */

public class Building {

    /**
     * msg : 成功
     * obj : [{"BuildingName":"贵州神奇学院A公寓","BuildingID":2},{"BuildingName":"B公寓c","BuildingID":6},
     * {"BuildingName":"贵州神奇学院B栋","BuildingID":9},
     * {"BuildingName":"贵州神奇学院4栋","BuildingID":12},
     * {"BuildingName":"贵州神奇学院5栋","BuildingID":13},
     * {"BuildingName":"贵州神奇学院6栋","BuildingID":14},
     * {"BuildingName":"贵州神奇学院7栋","BuildingID":15},
     * {"BuildingName":"贵州神奇学院8栋","BuildingID":16},
     * {"BuildingName":"贵州神奇学院春华楼C","BuildingID":17},
     * {"BuildingName":"贵州神奇学院C栋","BuildingID":30},{"BuildingName":"贵州神奇学院D栋","BuildingID":31}]
     * success : true
     */
    private String msg;
    private List<Ban> obj;
    private String success;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setObj(List<Ban> obj) {
        this.obj = obj;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public List<Ban> getObj() {
        return obj;
    }

    public String getSuccess() {
        return success;
    }

    public class ObjEntity {
        /**
         * BuildingName : 贵州神奇学院A公寓
         * BuildingID : 2
         */
        private String BuildingName;
        private int BuildingID;

        public void setBuildingName(String BuildingName) {
            this.BuildingName = BuildingName;
        }

        public void setBuildingID(int BuildingID) {
            this.BuildingID = BuildingID;
        }

        public String getBuildingName() {
            return BuildingName;
        }

        public int getBuildingID() {
            return BuildingID;
        }
    }
}
