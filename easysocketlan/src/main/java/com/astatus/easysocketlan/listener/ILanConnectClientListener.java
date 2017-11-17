package com.astatus.easysocketlan.listener;

/**
 * Created by Administrator on 2017/10/22.
 */

public interface ILanConnectClientListener {

    void onConnectStart();

    void onConnectError(String error);

    void onConnected(String id);
}
