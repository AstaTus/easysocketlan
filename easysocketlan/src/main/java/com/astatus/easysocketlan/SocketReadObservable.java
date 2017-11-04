package com.astatus.easysocketlan;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Administrator on 2017/10/20.
 */

public class SocketReadObservable implements ObservableOnSubscribe<Packet> {

    private DataInputStream mDataInputStream;

    private Socket mSocket;

    public SocketReadObservable(Socket socket){
        mSocket = socket;

    }

    @Override
    public void subscribe(ObservableEmitter<Packet> emitter) throws Exception {

        byte[] message_bytes = new byte[1024 * 1024];
        try {
            mDataInputStream = new DataInputStream(mSocket.getInputStream());

            while (true){
                int code = mDataInputStream.readInt();
                int len = mDataInputStream.readInt();
                mDataInputStream.readFully(message_bytes, 0, len);
                String message = new String(message_bytes, 0, len);

                Packet packet = new Packet(code, message);

                emitter.onNext(packet);

            }
        }catch (IOException e){
            emitter.onError(e);
        }
    }
}
