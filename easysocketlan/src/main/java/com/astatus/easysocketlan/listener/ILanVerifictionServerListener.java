package com.astatus.easysocketlan.listener;

import com.astatus.easysocketlan.entity.ClientDeviceEntity;

/**
 * Created by Administrator on 2017/11/14.
 */

public interface ILanVerifictionServerListener {
    void onVerification(ClientDeviceEntity entity);
}
