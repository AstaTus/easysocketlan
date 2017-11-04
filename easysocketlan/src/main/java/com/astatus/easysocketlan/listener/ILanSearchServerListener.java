package com.astatus.easysocketlan.listener;

/**
 * Created by Administrator on 2017/10/22.
 */

public interface ILanSearchServerListener {

    void onSearchStart();

    void onSearching();

    void onSearchEnd();

    void onSearchError(String error);
}
