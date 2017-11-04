package com.astatus.easysocketlan;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/10/20.
 */

public class PacketHandlerManager {

    private HashMap<Integer, com.astatus.easysocketlan.PacketHandler<?>> mParsers;

    PacketHandlerManager(){
        mParsers = new HashMap();
    }

    public void addHandler(com.astatus.easysocketlan.PacketHandler handler){

        mParsers.put(handler.getCode(), handler);
    }

    public void removeHandler(int code){
        if (mParsers.containsKey(code))
        mParsers.remove(code);
    }

    protected com.astatus.easysocketlan.PacketHandler getHandler(int code){
        if (mParsers.containsKey(code))
            return mParsers.get(code);

        return null;
    }
}
