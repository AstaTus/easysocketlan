package com.astatus.easysocketlan.listener;

/**
 * Created by Administrator on 2017/10/22.
 */

public interface ILanConnectServerListener extends ISocketReadListener, ISocketWriteListener {

    void onConnectStart();

    void onConnect(String ip);

    void onConnectError(String error);
}
