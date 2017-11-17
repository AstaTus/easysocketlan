package com.astatus.easysocketlan.listener;

/**
 * Created by Administrator on 2017/10/22.
 */

public interface ILanSearchClientListener {
    void onSearchStart();

    void onSearchSuccess();

    void onSearchError(String error);
}
