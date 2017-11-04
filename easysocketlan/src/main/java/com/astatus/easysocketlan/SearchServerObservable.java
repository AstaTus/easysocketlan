package com.astatus.easysocketlan;

import com.astatus.easysocketlan.entity.SearchEntity;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static java.lang.Thread.sleep;

/**
 * Created by Administrator on 2017/10/20.
 */

public class SearchServerObservable implements ObservableOnSubscribe<com.astatus.easysocketlan.SearchServerObservable.SearchState> {

    private final int RECEIVE_TIME_OUT = 1500;

    private String BROAD_IP = "255.255.255.255";

    private int SEARCHING_TIME = 3;

    private final long SLEEP_TIME_MILLSECOND = 1000L;


    public enum SearchState{

        SS_START,
        SS_SEARCH,
        SS_END,
    }


    private int mPort;
    private DatagramSocket mSocket = null;

    SearchServerObservable(int port){
        mPort = port;
    }

    @Override
    public void subscribe(ObservableEmitter<com.astatus.easysocketlan.SearchServerObservable.SearchState> emitter) throws Exception {

        mSocket = new DatagramSocket();

        InetAddress board_address = null;

        try{
            board_address = InetAddress.getByName(BROAD_IP);
        }catch (UnknownHostException unknown_exception){
            emitter.onError(unknown_exception);
        }

        try {
            mSocket.setSoTimeout(RECEIVE_TIME_OUT);
            SearchEntity entity  = new SearchEntity();
            String json = new Gson().toJson(entity);
            byte[] send_data = json.getBytes(Charset.forName("UTF-8"));

            DatagramPacket send_packet =
                    new DatagramPacket(send_data, send_data.length, board_address, mPort);

            emitter.onNext(SearchState.SS_START);
            for (int i =0; i < SEARCHING_TIME; ++i){
                //send search
                mSocket.send(send_packet);

                emitter.onNext(SearchState.SS_SEARCH);

                sleep(SLEEP_TIME_MILLSECOND);
            }

            emitter.onComplete();

        }catch (SocketException socket_exception){

            emitter.onError(socket_exception);
        }
        catch (IOException io_exception) {
            emitter.onError(io_exception);
        }
        finally{
            mSocket.close();
            emitter.onNext(SearchState.SS_END);
        }
    }
}
