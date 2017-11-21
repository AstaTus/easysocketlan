package com.astatus.easysocketlan;

import android.util.Log;

import com.astatus.easysocketlan.listener.ILanServerListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/10/20.
 */

public class LanServer {

    private static final String TAG = "LanServer";

    private int mSearchPort;

    private int mSocketPort;

    private ILanServerListener mLanServerListener;

    private ServerSocket mServerSocket = null;

    private Disposable mConnectDisposable;

    private LanSocketManager mLanSocketMgr;

    private PacketHandlerManager mPcketHandlerManager;

    public LanServer(int search_port, int socket_port, ILanServerListener listener){
        mSearchPort = search_port;
        mSocketPort = socket_port;

        mLanServerListener = listener;

        mPcketHandlerManager = new PacketHandlerManager();

        mLanSocketMgr = new LanSocketManager(mPcketHandlerManager, mLanServerListener);

        RxJavaPlugins.setErrorHandler(new io.reactivex.functions.Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d("LanServer", "accept: ");
            }
        });
    }

    public void send(String id, int code, String json){
        LanSocket lan_socket = mLanSocketMgr.getVerificationLanSocket(id);
        if (lan_socket != null){
            Packet packet = new Packet(code, json);
            lan_socket.send(packet);
        }else{
             Log.d(TAG, "send:name not exist:" + id);
        }
    }

    public void addHandler(PacketHandler handler){
        mPcketHandlerManager.addHandler(handler);
    }

    public void removeHandler(int code){
        mPcketHandlerManager.removeHandler(code);
    }

    public void start(){

        connect();
        search();
    }

    public void stop(){
        destroySearch();
        destroyConnect();
        mLanSocketMgr.closeAllSocket();
    }

    public void search(){

        destroySearch();

        Observable search_observable = Observable.create(new SearchServerObservable(mSearchPort, mSocketPort));
        search_observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SearchServerObserver(mLanServerListener));

    }

    public void connect(){

        destroyConnect();

        try {
            mServerSocket = new ServerSocket(mSocketPort);

            Observable connect_observable = Observable.create(new ConnectServerObservable(mServerSocket));
            connect_observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Socket>(){

                        @Override
                        public void onSubscribe(Disposable d) {
                            mConnectDisposable = d;
                            mLanServerListener.onConnectStart();
                        }

                        @Override
                        public void onNext(Socket socket) {
                            mLanServerListener.onConnect(socket.getInetAddress().getHostAddress(), socket.getPort());

                            mLanSocketMgr.addLanSocket(socket);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mLanServerListener.onConnectError(e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        }catch (IOException e){
            mLanServerListener.onConnectError(e.getMessage());
        }


    }

    public void destroy(){
        stop();
        mLanServerListener = null;
    }

    private void destroySearch(){

    }

    private void destroyConnect(){
        if (mConnectDisposable != null){
            mConnectDisposable.dispose();
            mConnectDisposable = null;
        }
    }
}
