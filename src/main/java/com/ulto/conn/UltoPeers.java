package com.ulto.conn;


import java.io.DataInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class UltoPeers {

    public enum PeerType {
        TCP,
        HTTP
    }

    public class UltoPeer {
        private Socket socket;

        private String peerId;

        private UltoPeer connectedTo;
        private PeerType peerType = PeerType.TCP;
        private BlockingQueue<UltoPeer> siblings = new LinkedBlockingQueue<>();
        private UTask siblingAction;
        private byte[] initialData = new byte[]{};

        public UltoPeer(Socket socket) {
            this.socket = socket;
        }

        public void setConnectedPeer(UltoPeer peer) {
            this.connectedTo = peer;
        }

        public Socket getSocket() {
            return this.socket;
        }

        public UltoPeer getConnectedPeer() {
            return this.connectedTo;
        }

        public PeerType getPeerType() {
            return peerType;
        }

        public void setPeerType(PeerType type) {
            this.peerType = type;
        }

        public void addSiblingPeer(UltoPeer peer) {
            if (this.peerType == PeerType.HTTP) {
                siblings.add(peer);
                try {
                    siblingAction.action(peer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public UltoPeer getLatestSibling() {
            if (this.peerType == PeerType.HTTP) {
                return siblings.poll();
            }
            return null;
        }


        public void onSiblingConnectionAdd(UTask task) {
            siblingAction = task;
        }

        public void setInitialData(byte[] data) {
            this.initialData = data;
        }

        public byte[] getInitialData() {
            return this.initialData;
        }

        public String getPeerId() {
            return peerId;
        }

        public void setPeerId(String peerId) {
            this.peerId = peerId;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Peer ").append(socket.toString());
            if (connectedTo != null) {
                builder.append(" connected to ")
                        .append(connectedTo.socket.toString());
            }

            return builder.toString();
        }
    }

    private HashMap<String, UltoPeer> peersMap;

    private Object lock = new Object();

    public UltoPeers() {
        peersMap = new HashMap<>();
    }

    public UltoPeer get(String key) {
        synchronized (lock) {
            return peersMap.get(key);
        }
    }


    public void add(String key, UltoPeer value) {
        synchronized (lock) {
            peersMap.put(key, value);
        }
    }

    public void remove(String key) {
        synchronized (lock) {
            peersMap.remove(key);
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        peersMap.entrySet().stream().forEachOrdered(entry -> buffer.append(entry.getKey()).append(" ").append("-->").append(" ").append(entry.getValue()).append('\n'));
        return buffer.toString();
    }

}
