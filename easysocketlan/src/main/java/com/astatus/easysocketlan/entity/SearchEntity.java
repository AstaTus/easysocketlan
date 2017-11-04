package com.astatus.easysocketlan.entity;

/**
 * Created by Administrator on 2017/10/20.
 */

public class SearchEntity {
    public String getIdentification() {
        return identification;
    }


    private String identification = "POWER_LAN_SEARCH_REQUEST";

    public int getConnectPort() {
        return connectPort;
    }

    public void setConnectPort(int connectPort) {
        this.connectPort = connectPort;
    }

    private int connectPort;

}
