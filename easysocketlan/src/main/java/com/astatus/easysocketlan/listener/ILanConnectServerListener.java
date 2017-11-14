package com.astatus.easysocketlan.listener;

/**
 * Created by Administrator on 2017/10/22.
 */

public interface ILanConnectServerListener {

    void onConnectStart();

    void onConnect(String ip, int port);

    void onConnectError(String error);
}
