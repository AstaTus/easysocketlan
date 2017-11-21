package com.astatus.easysocketlan;

import android.util.Log;

import com.astatus.easysocketlan.entity.ServerDeviceEntity;
import com.astatus.easysocketlan.entity.ClientDeviceEntity;
import com.astatus.easysocketlan.listener.ILanClientListener;
import com.astatus.easysocketlan.listener.ILanSocketDisconnectListener;
import com.google.gson.Gson;

import java.net.Socket;
import java.util.function.Consumer;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

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

    public LanClient(String name, int searchPort, ILanClientListener listener) {

        mName = name;

        mSearchPort = searchPort;

        mLanClientListener = listener;

        mPcketHandlerManager = new PacketHandlerManager();

        RxJavaPlugins.setErrorHandler(new io.reactivex.functions.Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d("LanClient", "accept: ");
            }
        });

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
                        mLanClientListener.onSearchSuccess();
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

    public void stop(){
        destroySearch();
        destroyConnect();
    }

    class LanSocketDisconnectListener implements ILanSocketDisconnectListener{

        @Override
        public void onDisconnect(String id) {
//            destroyConnect();
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


                        mLocalIP = socket.getLocalAddress().getHostAddress();
                        mSocketPort = socket.getLocalPort();
                        mLanSocket = new LanSocket(mPcketHandlerManager, mLanClientListener, new LanSocketDisconnectListener(), socket);
                        mLanSocket.setName(mName);
                        mLanSocket.init();
                        sendVerificationEntity();

                        mLanClientListener.onConnected(mLocalIP + '_' + mSocketPort);
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

        ClientDeviceEntity entity = new ClientDeviceEntity();
        entity.setIp(mLocalIP);
        entity.setName(mName);
        entity.setPort(mSocketPort);
        Gson gson = new Gson();
        String json = gson.toJson(entity, ClientDeviceEntity.class);
        send(CmsgCode.CMSG_INTERNAL_VERIFICATION_CODE, json);
    }

    public void send(int code, String json){
        Packet packet = new Packet(code, json);
        mLanSocket.send(packet);
    }

    public void destroy(){
        stop();

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

        if (mLanSocket != null){
            mLanSocket.destroy();
            mLanSocket = null;
        }
    }
}
