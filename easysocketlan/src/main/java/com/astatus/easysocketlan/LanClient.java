package com.astatus.easysocketlan;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.astatus.easysocketlan.entity.ServerDeviceEntity;
import com.astatus.easysocketlan.entity.VerificationEntity;
import com.astatus.easysocketlan.listener.ILanClientListener;
import com.astatus.easysocketlan.listener.ILanSocketDisconnectListener;
import com.google.gson.Gson;

import java.net.Socket;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/10/20.
 */

public class LanClient {

    private String mName;

    private int mSearchPort;

    private int mSocketPort;

    private String mLocalIP = "";

    private ILanClientListener mLanClientListener;

    private LanSocket mLanSocket;

    private PacketHandlerManager mPcketHandlerManager;

    private Observable mSearchObservalble;

    private Observable mConnectObservalbe;

    private ServerDeviceEntity mServerDeviceEntity = null;

    public LanClient(String name, int searchPort, int socketPort, ILanClientListener listener){

        mName = name;

        mSearchPort = searchPort;

        mSocketPort = socketPort;

        mLanClientListener = listener;

        mPcketHandlerManager = new PacketHandlerManager();
    }

    public void addHandler(com.astatus.easysocketlan.PacketHandler handler){
        mPcketHandlerManager.addHandler(handler);
    }

    public void removeHandler(int code){
        mPcketHandlerManager.removeHandler(code);
    }

    public void start(){
        destroySearch();

        mSearchObservalble = Observable.create(new SearchClientObservable(mSearchPort));
        mSearchObservalble.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ServerDeviceEntity>(){

                    @Override
                    public void onSubscribe(Disposable d) {
                        mLanClientListener.onSearchStart();
                    }

                    @Override
                    public void onNext(ServerDeviceEntity serverDeviceEntity) {
                        mServerDeviceEntity = serverDeviceEntity;
                        mLanClientListener.onFind();
                        connect();

                    }

                    @Override
                    public void onError(Throwable e) {
                        mLanClientListener.onSearchError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    class LanSocketDisconnectListener implements ILanSocketDisconnectListener{

        @Override
        public void onDisconnect(String ip, String name) {
            destroyConnect();
        }
    }

    public void connect(){

        destroyConnect();

        mConnectObservalbe = Observable.create(new ConnectClientObservable(mServerDeviceEntity));
        mConnectObservalbe.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Socket>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        mLanClientListener.onConnectStart();
                    }

                    @Override
                    public void onNext(Socket socket) {

                        mLanClientListener.onConnectting();
                        mLocalIP = socket.getLocalAddress().getHostAddress();
                        mLanSocket = new LanSocket(mPcketHandlerManager, mLanClientListener, new LanSocketDisconnectListener(), socket);
                        mLanSocket.setName(mName);
                        mLanSocket.init();
                        sendVerificationEntity();
                    }

                    @Override
                    public void onError(Throwable e) {
                         mLanClientListener.onConnectError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void sendVerificationEntity(){

        VerificationEntity entity = new VerificationEntity();
        entity.setIp(mLocalIP);
        entity.setName(mName);
        Gson gson = new Gson();
        String json = gson.toJson(entity, VerificationEntity.class);
        send(CmsgCode.CMSG_INTERNAL_VERIFICATION_CODE, json);
    }

    public void send(int code, String json){
        Packet packet = new Packet(code, json);
        mLanSocket.send(packet);
    }

    public void destroy(){
        destroySearch();
        destroyConnect();
        destorySocket();

        mLanClientListener = null;
    }

    private void destroySearch(){
        if (mSearchObservalble != null){
            mSearchObservalble.unsubscribeOn(Schedulers.io());
            mSearchObservalble = null;
        }
    }

    private void destroyConnect(){
        if (mConnectObservalbe != null){
            mConnectObservalbe.unsubscribeOn(Schedulers.io());
            mConnectObservalbe = null;
        }
    }

    private void destorySocket(){
        if (mLanSocket != null){
            mLanSocket.destroy();
            mLanSocket = null;
        }
    }
}
