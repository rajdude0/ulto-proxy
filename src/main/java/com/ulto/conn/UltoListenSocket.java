package com.ulto.conn;

import com.ulto.util.HTTPUtil;
import com.ulto.util.SocketUtil;


import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

public class UltoListenSocket extends Thread {
    // change this to read from a property file
    private int port = 8888;
    private String host = "0.0.0.0";


    private ServerSocket socket;
    private boolean keepListening = true;
    private UltoPeers clients;

    public UltoListenSocket(UltoPeers clientsMap) throws IOException  {
            socket = new ServerSocket(port, 0,  InetAddress.getByName(host));
            clients = clientsMap;
    }


    public void run() {
        while(keepListening) {
            try {
                Socket client = socket.accept();
                //client.setReceiveBufferSize(65536);
               //client.setSendBufferSize(65536);
                System.out.println("Client Connected" + client.getInetAddress().getHostName());
                client.setSoTimeout(2000);

                byte[] finalData = SocketUtil.waitForIncomingData(client.getInputStream());

                HashMap<String, String> headers = HTTPUtil.getHTTPHeaders(finalData);
                if (headers != null) {
                    String hostname = headers.get("Host");
                    UltoPeers.UltoPeer httpPeer = clients.new UltoPeer(client);
                    httpPeer.setPeerType(UltoPeers.PeerType.HTTP);
                    if(clients.get(hostname) == null) {
                        clients.add(hostname, httpPeer);
                        httpPeer.setInitialData(finalData);
                    } else {
                        UltoPeers.UltoPeer peer = clients.get(hostname);
                        peer.addSiblingPeer(clients.new UltoPeer(client));
                        peer.setInitialData(finalData);
                    }
                } else {

                    UUID uuid = UUID.randomUUID();
                    clients.add(uuid.toString(), clients.new UltoPeer(client));
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

}
