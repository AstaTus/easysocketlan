package com.astatus.easysocketlan;

import com.astatus.easysocketlan.listener.ILanSocketDisconnectListener;
import com.astatus.easysocketlan.listener.ISocketListener;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/10/22.
 */

public class LanSocket {
    private PacketHandlerManager mHandlerMgr;

    private Observable<Packet> mReadObservale;
    private Observable<Integer> mWriteObservale;

    private Disposable mReadDisposable;
    private Disposable mWriteDisposable;

    private ISocketListener mSocketListener;

    private ILanSocketDisconnectListener mDisconnectListener;

    private Socket mSocket;

    private String mName = "";

    private String mIp = "";

    private int mPort = 0;


    private LinkedBlockingQueue<Packet> mWaitPackets = new LinkedBlockingQueue<>(100);;


    LanSocket(PacketHandlerManager handlerManager,
              ISocketListener socketListener,
              ILanSocketDisconnectListener socketDisconnectListener,
              Socket socket){
        mHandlerMgr = handlerManager;
        mSocketListener = socketListener;
        mDisconnectListener = socketDisconnectListener;
        mSocket = socket;
        mIp = mSocket.getInetAddress().getHostAddress();
        mPort = mSocket.getPort();
    }

    public void init(){

        //read thread
        mReadObservale = Observable.create(new SocketReadObservable(mSocket));
        mReadObservale.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Packet>(){

                    @Override
                    public void onSubscribe(Disposable d) {
                        mReadDisposable = d;
                        mSocketListener.onReadStart(getId());
                    }

                    @Override
                    public void onNext(Packet packet) {
                        if (mHandlerMgr != null){
                            PacketHandler handler = mHandlerMgr.getHandler(packet.getCode());
                            if (handler != null){
                                handler.parser(getId(), mName, packet);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mSocketListener.onDisconnect(getId(), e.getMessage());
                        mDisconnectListener.onDisconnect(getId());

                        mSocketListener = null;
                        mDisconnectListener = null;
                    }

                    @Override
                    public void onComplete() {

                    }
                });


        //write thread
        mWriteObservale = Observable.create(new SocketWriteObservable(mSocket, mWaitPackets));
        mWriteObservale.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>(){

                    @Override
                    public void onSubscribe(Disposable d) {

                        mWriteDisposable = d;
                        mSocketListener.onWriteStart(getId());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        mSocketListener.onWrite(getId());
                    }

                    @Override
                    public void onError(Throwable e) {
                        mSocketListener.onDisconnect(getId(), e.getMessage());
                        mDisconnectListener.onDisconnect(getId());
                    }

                    @Override
                    public void onComplete() {
                        mSocketListener.onDisconnect(getId(), "");
                        mDisconnectListener.onDisconnect(getId());
                    }
                });

    }

    public String getIP(){
        return mIp;
    }

    public String getId(){
        return getIP() + '_' + getPort();
    }

    public int getPort(){
        return  mPort;
    }

    public void destroy(){

        try{
            if (mSocket != null){
                mSocket.close();
                mSocket = null;
            }

        }catch (IOException e){

        }

//        if (mReadDisposable != null){
//            mReadDisposable.dispose();
//            mReadDisposable = null;
//        }
//
//        if (mWriteDisposable != null){
//            mWriteDisposable.dispose();
//            mWriteDisposable = null;
//        }

//        if (mSocketListener != null){
//            //mSocketListener.onDisconnect(getId(), "");
//            mSocketListener = null;
//        }
//
//        if (mDisconnectListener != null){
//            //mDisconnectListener.onDisconnect(getId());
//            mDisconnectListener = null;
//        }

        mHandlerMgr = null;
    }

    public void setName(String name){
        mName = name;
    }

    public String getName(){
        return mName;
    }

    public boolean send(Packet packet){
        if (mWaitPackets.offer(packet)){
            return false;
        }

        return true;
    }
}
