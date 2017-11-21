package com.astatus.easysocketlan;

import com.astatus.easysocketlan.entity.ClientDeviceEntity;
import com.astatus.easysocketlan.listener.ILanSocketDisconnectListener;
import com.astatus.easysocketlan.listener.ISocketServerListener;

import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/10/22.
 */

class LanSocketManager {
    //<IP_PORT, LanSocket>
    private HashMap<String, LanSocket> mUnverificationLanSockets = new HashMap<>();
    //<IP_PORT, LanSocket>
    private HashMap<String, LanSocket> mVerificationLanSockets = new HashMap<>();

    private com.astatus.easysocketlan.PacketHandlerManager mPacketHandlerManager;

    private ISocketServerListener mSocketServerListener;

    protected class LanSocketDisconnectListener implements ILanSocketDisconnectListener {


        private void onUnverificationDisconnect(String id) {
            if (mUnverificationLanSockets.containsKey(id)) {
                LanSocket socket = mUnverificationLanSockets.get(id);
                mUnverificationLanSockets.remove(id);
                socket.destroy();
            }
        }

        private void onVerificationDisconnect(String id) {
            if (mVerificationLanSockets.containsKey(id)) {
                LanSocket socket = mVerificationLanSockets.get(id);
                mVerificationLanSockets.remove(id);
                socket.destroy();
            }
        }

        @Override
        public void onDisconnect(String id) {
            onUnverificationDisconnect(id);
            onVerificationDisconnect(id);
        }
    }

    private LanSocketDisconnectListener mDisconnectListener = new LanSocketDisconnectListener();

    LanSocketManager(PacketHandlerManager packetHandlerManager, ISocketServerListener socketServerListener){
        mPacketHandlerManager = packetHandlerManager;
        mSocketServerListener = socketServerListener;

        mPacketHandlerManager.addHandler(
                new PacketHandler<ClientDeviceEntity>(CmsgCode.CMSG_INTERNAL_VERIFICATION_CODE, ClientDeviceEntity.class) {
                    @Override
                    protected void parserError(String id, String name, String message) {

                    }

                    @Override
                    protected void handle(String id, String name, ClientDeviceEntity entity) {
                        boolean result = verificationLanSocket(entity);
                        mSocketServerListener.onVerification(entity, result);
                    }

        });
    }


    public void addLanSocket(Socket socket){

        LanSocket lan_socket = new LanSocket(mPacketHandlerManager, mSocketServerListener, mDisconnectListener, socket);

        mUnverificationLanSockets.put(lan_socket.getId(), lan_socket);
        lan_socket.init();
    }

    public LanSocket getUnverificationLanSocket(String id){
        return mUnverificationLanSockets.get(id);

    }

    public LanSocket getVerificationLanSocket(String id){
        return mVerificationLanSockets.get(id);
    }


    private Boolean verificationLanSocket(ClientDeviceEntity entity){

        String id = entity.getId();

        LanSocket socket = getUnverificationLanSocket(id);

        if (socket != null){
            mUnverificationLanSockets.remove(id);
            mVerificationLanSockets.put(id, socket);

            return true;
        }

        return false;
    }

    public void  closeAllSocket(){
        for (LanSocket lan: mUnverificationLanSockets.values()){
            lan.destroy();
        }

        for (LanSocket lan: mVerificationLanSockets.values()){
            lan.destroy();
        }

        mUnverificationLanSockets.clear();
        mVerificationLanSockets.clear();

    }
}
