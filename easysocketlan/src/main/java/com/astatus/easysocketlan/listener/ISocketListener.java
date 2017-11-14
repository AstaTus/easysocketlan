package com.astatus.easysocketlan.listener;

/**
 * Created by Administrator on 2017/10/22.
 */

public interface ISocketListener extends ISocketReadListener, ISocketWriteListener {

    void onDisconnect(String id, String error);
}
