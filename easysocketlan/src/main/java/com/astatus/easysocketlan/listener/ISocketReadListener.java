package com.astatus.easysocketlan.listener;

/**
 * Created by Administrator on 2017/10/22.
 */

public interface ISocketReadListener {
    void onReadStart(String ip, String name);

    void onRead(String ip, String name);

    void onReadError(String ip, String name, String error);
}
