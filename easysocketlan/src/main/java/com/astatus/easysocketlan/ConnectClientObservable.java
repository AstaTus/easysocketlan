package com.astatus.easysocketlan;

import com.astatus.easysocketlan.entity.ServerDeviceEntity;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Administrator on 2017/10/20.
 */

public class ConnectClientObservable implements ObservableOnSubscribe<Socket> {

    private static final int TIMEOUT_TIME = 1000;

    private Socket mClientSocket;

    private ServerDeviceEntity mServerEntity;

    ConnectClientObservable(ServerDeviceEntity serverDeviceEntity){
        mClientSocket = new Socket();

        mServerEntity = serverDeviceEntity;
    }

    @Override
    public void subscribe(ObservableEmitter<Socket> emitter) throws Exception {
        try{
            mClientSocket.setTcpNoDelay(true);
            mClientSocket.setKeepAlive(true);

            mClientSocket.connect(new InetSocketAddress(mServerEntity.ip, mServerEntity.port));
            emitter.onNext(mClientSocket);
            emitter.onComplete();
        }catch (IOException e){
            emitter.onError(e);
        }
    }
}
