package com.astatus.easysocketlan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/10/20.
 */

public class Packet {
    private int mCode = 0;
    private int mLen = 0;

    public String getJson() {
        return mJson;
    }

    private String mJson = "";

    public Packet(int code, String json){
        mCode = code;
        mJson = json;

        computeLength();
    }

    public int getLength(){
        return mLen;
    }

    public int getCode(){
        return mCode;
    }

    public byte[] getJsonBytes(){
        return mJson.getBytes();
    }

    private void computeLength(){
        byte[] json_bytes = mJson.getBytes();
        mLen = json_bytes.length;
    }

    public byte[] serialize() throws IOException {

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte[] json_bytes = mJson.getBytes();
        mLen = json_bytes.length;

        output.write(mCode);
        output.write(mLen);
        output.write(json_bytes);

        return output.toByteArray();

//        byte[] packet_bytes = new byte[code_bytes.length + len_bytes.length + json_bytes.length];
//        System.arraycopy(code_bytes, 0, packet_bytes, 0, code_bytes.length);
//        System.arraycopy(len_bytes, 0, packet_bytes, code_bytes.length, len_bytes.length);
//        System.arraycopy(json_bytes, 0, packet_bytes, (code_bytes.length + len_bytes.length), json_bytes.length);
//        return packet_bytes;
    }

    public static byte[] intToBytes( int value )
    {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }
}
