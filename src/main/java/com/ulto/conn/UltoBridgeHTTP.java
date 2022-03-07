package com.ulto.conn;

import com.ulto.packet.UltoPacket;
import com.ulto.util.SocketUtil;

import java.io.*;
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
                       ObjectInputStream peerIn = new ObjectInputStream(peer.getSocket().getInputStream());
                       ObjectOutputStream peerOut = new ObjectOutputStream(peer.getSocket().getOutputStream());

                       ObjectInputStream destIn = new ObjectInputStream(destination.getSocket().getInputStream());
                       ObjectOutputStream destOut = new ObjectOutputStream(destination.getSocket().getOutputStream());

                        byte[] initialData = peer.getInitialData();


                        if(initialData.length > 0) {
                            UltoPacket packet = new UltoPacket(initialData, peer.getPeerId(), destination.getPeerId());
                            destOut.writeObject(packet);
                            peer.setInitialData(new byte[]{ });
                        } else {
                            // HTTP peers will send raw HTTP data, it cannot be deserialize to UltoPakcet.
                            byte[] actualData = new byte[peerIn.available()];
                            peerIn.read(actualData);
                            UltoPacket packet = new UltoPacket(actualData, peer.getPeerId(), destination.getPeerId());
                            destOut.writeObject(packet);
                        }

                        /* Bug: partial incoming data, need to find a way to either wait till full data is received or
                            Need to find a way to forward the incoming data to correct peer socket
                            perhaps use some sort of id or something, this would require creation of new data packet structure which will be send over the socket
                            in serialized form.

                            For the above to work, we need a peerId exchange protocol. Such that the client will be aware of the destination peerId, we can try using the socket address
                            but under NAT multiple client can have same address. Combination of socket and port will work.
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
