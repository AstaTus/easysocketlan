package com.astatus.easysocketlan;

import com.astatus.easysocketlan.Packet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Administrator on 2017/10/20.
 */

public class SocketWriteObservable implements ObservableOnSubscribe<Integer> {

    private DataOutputStream dataOutputStream;
    private Socket mSocket;

    private final static int QUEUE_TIME_OUT = 1;

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

            while (!emmiter.isDisposed()){

                Packet packet = mPacketQueue.poll(QUEUE_TIME_OUT, TimeUnit.SECONDS);
                if (packet != null){
                    dataOutputStream.writeInt(packet.getCode());
                    dataOutputStream.writeInt(packet.getLength());
                    byte[] bytes = packet.getJsonBytes();
                    dataOutputStream.write(bytes, 0, bytes.length);
                    dataOutputStream.flush();
                    emmiter.onNext(packet.getCode());
                }
            }
            
        }catch (IOException e){
            emmiter.onError(e);
        }
    }
}
