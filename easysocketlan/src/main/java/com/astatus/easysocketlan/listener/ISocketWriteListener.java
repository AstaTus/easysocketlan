package com.astatus.easysocketlan.listener;

/**
 * Created by Administrator on 2017/10/22.
 */

public interface ISocketWriteListener {
    void onWriteStart(String ip, String name);

    void onWrite(String ip, String name);

    void onWriteError(String ip, String name, String error);
}
