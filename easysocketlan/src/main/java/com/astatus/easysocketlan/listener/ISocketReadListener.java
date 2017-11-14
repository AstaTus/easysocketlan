package com.astatus.easysocketlan.listener;

/**
 * Created by Administrator on 2017/10/22.
 */

public interface ISocketReadListener {
    void onReadStart(String id);

    void onRead(String id);
}
