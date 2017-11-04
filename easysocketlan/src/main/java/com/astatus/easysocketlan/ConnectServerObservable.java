package com.astatus.easysocketlan;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Administrator on 2017/10/20.
 */

public class ConnectServerObservable implements ObservableOnSubscribe<Socket> {

    private ServerSocket mServerSocket;
    private int mPort;

    ConnectServerObservable(int port){
        mPort = port;
    }

    @Override
    public void subscribe(ObservableEmitter<Socket> emitter) throws Exception {

        try{
            mServerSocket = new ServerSocket(mPort);

            while (true){

                Socket socket = mServerSocket.accept();

                emitter.onNext(socket);
            }
        }catch (IOException e){
            emitter.onError(e);
        }
    }
}
