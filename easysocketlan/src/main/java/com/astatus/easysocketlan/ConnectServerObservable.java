package com.astatus.easysocketlan;

import android.util.Log;

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
            String localIP = mServerSocket.getInetAddress().getHostAddress();
            Log.d("ConnectServerObservable", localIP);
            while (true){

                Socket socket = mServerSocket.accept();

                socket.setTcpNoDelay(true);
                socket.setKeepAlive(true);

                emitter.onNext(socket);
            }
        }catch (IOException e){
            emitter.onError(e);
        }
    }
}
