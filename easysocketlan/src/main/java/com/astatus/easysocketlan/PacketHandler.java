package com.astatus.easysocketlan;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by Administrator on 2017/10/20.
 */

public abstract class PacketHandler<T> {

    private int mCode;
    private Class<T> mClass;

    public int getCode(){
        return mCode;
    }

    public PacketHandler(int code, Class<T> cls){
        mCode = code;
        mClass = cls;
    }

    protected void parser(Packet packet){
        try {

            T entity = new Gson().fromJson(packet.getJson(), mClass);
            handle(entity);
        }catch (JsonSyntaxException e){
            parserError(e.getMessage());
        }
    }

    protected abstract void parserError(String message);

    protected abstract void handle(T entity);
}
