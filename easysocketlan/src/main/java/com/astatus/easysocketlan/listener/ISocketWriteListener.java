package com.astatus.easysocketlan.listener;

/**
 * Created by Administrator on 2017/10/22.
 */

public interface ISocketWriteListener {
    void onWriteStart(String id);

    void onWrite(String id);
}
