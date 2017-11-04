package com.astatus.easysocketlan.listener;

/**
 * Created by Administrator on 2017/10/22.
 */

public interface ILanSearchClientListener {
    void onSearchStart();

    void onFind();

    void onSearchError(String error);
}
