package com.bluebud.info;

public class LitemallInfo {
    public LitemallInfo(String dscMallLogin, String dscMallUserId, String dscMallOpenId, String dscMallToken) {
        this.dscMallLogin = dscMallLogin;
        this.dscMallUserId = dscMallUserId;
        this.dscMallOpenId = dscMallOpenId;
        this.dscMallToken = dscMallToken;
    }

    public String dscMallLogin;//商城链接
    public String dscMallUserId;//大商创的user_id
    public String dscMallOpenId;//大商创openId
    public String dscMallToken;//大商创token
}
