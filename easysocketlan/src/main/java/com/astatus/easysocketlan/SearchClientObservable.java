package com.astatus.easysocketlan;

import com.astatus.easysocketlan.entity.SearchEntity;
import com.astatus.easysocketlan.entity.ServerDeviceEntity;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Administrator on 2017/10/23.
 */

public class SearchClientObservable implements ObservableOnSubscribe<ServerDeviceEntity> {

    private int mPort;

    SearchClientObservable(int port){
        mPort = port;
    }

    @Override
    public void subscribe(ObservableEmitter<ServerDeviceEntity> emitter) throws Exception {

        DatagramSocket socket = new DatagramSocket(mPort);

        byte[] data = new byte[1024];
        DatagramPacket pack = new DatagramPacket(data, data.length);

        //waitting server search
        while (true){

            try {
                socket.receive(pack);

                String json = new String(pack.getData(), 0 , pack.getLength());
                SearchEntity entity  = new Gson().fromJson(json, SearchEntity.class);

                if (entity.getIdentification().equals("POWER_LAN_SEARCH_REQUEST")){

                    ServerDeviceEntity main_entity = new ServerDeviceEntity();
                    main_entity.ip = pack.getAddress().getHostAddress();
                    main_entity.port = entity.getConnectPort();
                    emitter.onNext(main_entity);

                    break;
                }else{
                    continue;
                }

            }catch (JsonSyntaxException e){
                continue;
            }catch (IOException e) {
                emitter.onError(e);
                break;
            }
        }
        emitter.onComplete();
        socket.close();

    }
}
