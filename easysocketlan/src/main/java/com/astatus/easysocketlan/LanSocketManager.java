package com.astatus.easysocketlan;

import com.astatus.easysocketlan.entity.VerificationEntity;
import com.astatus.easysocketlan.listener.ILanSocketDisconnectListener;
import com.astatus.easysocketlan.listener.ISocketServerListener;

import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/10/22.
 */

class LanSocketManager {
    //<IP,LanSocket>
    private HashMap<String, LanSocket> mUnverificationLanSockets = new HashMap<>();
    //<Name, LanSocket>
    private HashMap<String, LanSocket> mVerificationLanSockets = new HashMap<>();

    private com.astatus.easysocketlan.PacketHandlerManager mPacketHandlerManager;

    private ISocketServerListener mSocketServerListener;

    protected class LanSocketDisconnectListener implements ILanSocketDisconnectListener {


        private void onUnverificationDisconnect(String ip){
            if (mUnverificationLanSockets.containsKey(ip)){
                LanSocket socket = mUnverificationLanSockets.get(ip);
                mUnverificationLanSockets.remove(ip);
                socket.destroy();
            }
        }

        private void onVerificationDisconnect(String name){
            if (mVerificationLanSockets.containsKey(name)){
                LanSocket socket = mVerificationLanSockets.get(name);
                mVerificationLanSockets.remove(name);
                socket.destroy();
            }
        }

        @Override
        public void onDisconnect(String ip, String name) {
            if (name == ""){
                onUnverificationDisconnect(ip);
            }else{
                onVerificationDisconnect(name);
            }
        }
    }

    private LanSocketDisconnectListener mDisconnectListener = new LanSocketDisconnectListener();

    LanSocketManager(PacketHandlerManager packetHandlerManager, ISocketServerListener socketServerListener){
        mPacketHandlerManager = packetHandlerManager;
        mSocketServerListener = socketServerListener;

        mPacketHandlerManager.addHandler(
                new com.astatus.easysocketlan.PacketHandler<VerificationEntity>(CmsgCode.CMSG_INTERNAL_VERIFICATION_CODE, VerificationEntity.class) {
            @Override
            protected void parserError(String message) {

            }

            @Override
            protected void handle(VerificationEntity entity) {
                verificationLanSocket(entity.getName(), entity.getIp());
                mSocketServerListener.onVerification(entity.getName());
            }
        });
    }


    public void addLanSocket(Socket socket){

        LanSocket lan_socket = new LanSocket(mPacketHandlerManager, mSocketServerListener, mDisconnectListener, socket);
        mUnverificationLanSockets.put(lan_socket.getIP(), lan_socket);
        lan_socket.init();
    }

    public LanSocket getUnverificationLanSocket(String ip){
        return mUnverificationLanSockets.get(ip);

    }

    public LanSocket getVerificationLanSocket(String name){
        return mVerificationLanSockets.get(name);
    }


    public Boolean verificationLanSocket(String name, String ip){
        LanSocket socket = getUnverificationLanSocket(ip);
        if (socket != null){
            mUnverificationLanSockets.remove(ip);
            mVerificationLanSockets.put(name, socket);
            return true;
        }

        return false;
    }
}
