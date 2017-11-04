package com.astatus.easysocketlan;

import com.astatus.easysocketlan.listener.ILanServerListener;

import java.net.Socket;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/10/20.
 */

public class LanServer {

    private int mSearchPort;

    private int mSocketPort;

    private ILanServerListener mLanServerListener;

    private Observable mSearchObservalble;

    private Observable mConnectObservalble;

    private LanSocketManager mLanSocketMgr;

    private PacketHandlerManager mPcketHandlerManager;

    public LanServer(int search_port, int socket_port, ILanServerListener listener){
        mSearchPort = search_port;
        mSocketPort = socket_port;

        mLanServerListener = listener;

        mPcketHandlerManager = new PacketHandlerManager();

        mLanSocketMgr = new LanSocketManager(mPcketHandlerManager, mLanServerListener);
    }

    public void send(String name, int code, String json){
        LanSocket lan_socket = mLanSocketMgr.getVerificationLanSocket(name);
        Packet packet = new Packet(code, json);
        lan_socket.send(packet);
    }

    public void addHandler(com.astatus.easysocketlan.PacketHandler handler){
        mPcketHandlerManager.addHandler(handler);
    }

    public void removeHandler(int code){
        mPcketHandlerManager.removeHandler(code);
    }

    public void start(){
        search();
        connect();
    }

    public void search(){

        destroySearch();

        mSearchObservalble = Observable.create(new SearchServerObservable(mSearchPort));
        mSearchObservalble.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SearchServerObserver(mLanServerListener));
    }

    public void connect(){

        destroyConnect();

        mConnectObservalble = Observable.create(new ConnectServerObservable(mSocketPort));
        mConnectObservalble.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Socket>(){

                    @Override
                    public void onSubscribe(Disposable d) {
                        mLanServerListener.onConnectStart();
                    }

                    @Override
                    public void onNext(Socket socket) {
                        mLanServerListener.onConnect(socket.getInetAddress().toString());

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
    }

    public void destroy(){
        destroySearch();
        destroyConnect();
        mLanServerListener = null;
    }

    private void destroySearch(){
        if (mSearchObservalble != null){
            mSearchObservalble.unsubscribeOn(Schedulers.io());
            mSearchObservalble = null;
        }
    }

    private void destroyConnect(){
        if (mConnectObservalble != null){
            mConnectObservalble.unsubscribeOn(Schedulers.io());
            mConnectObservalble = null;
        }
    }
}
