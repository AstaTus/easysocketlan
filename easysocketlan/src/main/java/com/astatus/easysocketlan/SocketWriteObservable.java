package com.astatus.easysocketlan;

import com.astatus.easysocketlan.Packet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Administrator on 2017/10/20.
 */

public class SocketWriteObservable implements ObservableOnSubscribe<Integer> {

    private DataOutputStream dataOutputStream;
    private Socket mSocket;

    private LinkedBlockingQueue<Packet> mPacketQueue;

    SocketWriteObservable(Socket socket, LinkedBlockingQueue<Packet> packetQueue)
    {
        mSocket = socket;
        mPacketQueue = packetQueue;
    }

    @Override
    public void subscribe(ObservableEmitter<Integer> emmiter) throws Exception {

        try {
            dataOutputStream = new DataOutputStream(mSocket.getOutputStream());

            while (true){

                Packet packet = mPacketQueue.take();

                dataOutputStream.writeInt(packet.getCode());
                dataOutputStream.writeInt(packet.getLength());
                byte[] bytes = packet.getJsonBytes();
                dataOutputStream.write(bytes, 0, bytes.length);
                dataOutputStream.flush();
                emmiter.onNext(packet.getCode());
            }
        }catch (IOException e){
            emmiter.onError(e);
        }
    }
}
