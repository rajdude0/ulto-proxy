package com.ulto.conn;

import com.ulto.util.SocketUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class UltoBridgeHTTP implements Runnable, IUltoBridge {

    private UltoPeers.UltoPeer source;
    private UltoPeers.UltoPeer destination;

    private byte[] initialData = new byte[] {};


    private byte[] sourceOutBuffer = null;

    private Queue<UltoPeers.UltoPeer> siblingPeerQueue = new LinkedBlockingQueue<>();


    @Override
    public void join(UltoPeers.UltoPeer source, UltoPeers.UltoPeer destination) throws Exception {
            this.destination = destination;
            this.source = source;
            this.siblingPeerQueue.add(source);

            source.onSiblingConnectionAdd((peer) -> {
                siblingPeerQueue.add(peer);
            });

        threadPoolExecutor.execute(this);
    }

    @Override
    public void run() {

        while(true) {
            if(source.getSocket().isConnected() || destination.getSocket().isConnected()) {


                   siblingPeerQueue.stream().forEach(peer -> {
                       try {
                       InputStream peerIn = peer.getSocket().getInputStream();
                       OutputStream peerOut = peer.getSocket().getOutputStream();

                       InputStream destIn = destination.getSocket().getInputStream();
                       OutputStream destOut = destination.getSocket().getOutputStream();

                        byte[] initialData = peer.getInitialData();

                        if(initialData.length > 0) {
                            destOut.write(initialData);
                            peer.setInitialData(new byte[]{ });
                        } else {
                            byte[] actualData = new byte[peerIn.available()];
                            peerIn.read(actualData);
                            destOut.write(actualData);
                        }

                        /* Bug: partial incoming data, need to find a way to either wait till full data is received or
                            Need to find a way to forward the incoming data to correct peer socket
                            perhaps use some sort of id or something, this would require creation of new data packet structure which will be send over the socket
                            in serialized form.
                         */
                        byte[] incomingResp = SocketUtil.waitForIncomingData(destIn);
                        peerOut.write(incomingResp);

                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       siblingPeerQueue.remove(peer);
                   });


            }
        }

    }
}
