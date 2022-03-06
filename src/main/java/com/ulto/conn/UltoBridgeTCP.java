package com.ulto.conn;

import com.ulto.util.UltoTunnel;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class UltoBridgeTCP implements  Runnable, IUltoBridge {

    private UltoPeers clients;
    private Socket source;
    private Socket destination;

    private byte[] initialData = new byte[] {};



    public UltoBridgeTCP(UltoPeers clients) {
        this.clients = clients;
    }

    public void join(UltoPeers.UltoPeer sourcePeer, UltoPeers.UltoPeer destinationPeer) throws Exception{

            sourcePeer.setConnectedPeer(destinationPeer);

            this.source = sourcePeer.getSocket();
            this.destination = destinationPeer.getSocket();

            initialData = sourcePeer.getInitialData();

            if(!isSourceConnected()) {
                throw new Exception("Source Machine Disconnected!");
            }
            if(!isDestinationConnected()) {
                throw new Exception("Destination Machine Disconnected!");
            }

            threadPoolExecutor.execute(this);
    }

    private boolean isSourceConnected() {
        return source.isConnected() || !source.isClosed();
    }

    private boolean isDestinationConnected() {
        return destination.isConnected() || !destination.isClosed();
    }


    @Override
    public void run() {
        while(true) {
            if(!isDestinationConnected() || !isSourceConnected()) {
                break;
            }

            try {

                UltoTunnel.pipe(source, destination, initialData);
                if(initialData.length > 0) {
                    initialData = new byte[] {};
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
