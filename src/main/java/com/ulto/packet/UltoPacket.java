package com.ulto.packet;

import java.io.Serializable;

public class UltoPacket implements Serializable {

    private byte[] data;

    private String ultoSenderId;

    private String ultoReceiverId;


    public UltoPacket() {
        this(new byte[]{});
    }

    public UltoPacket(byte[] data) {
        this.data = data;
    }

    public UltoPacket(byte[] data, String senderId, String receiverId) {
        this(data);
        this.ultoSenderId = senderId;
        this.ultoReceiverId = receiverId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getUltoSenderId() {
        return ultoSenderId;
    }

    public void setUltoSenderId(String ultoSenderId) {
        this.ultoSenderId = ultoSenderId;
    }

    public String getUltoReceiverId() {
        return ultoReceiverId;
    }

    public void setUltoReceiverId(String ultoReceiverId) {
        this.ultoReceiverId = ultoReceiverId;
    }

}
