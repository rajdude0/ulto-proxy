package com.ulto.client;

import com.ulto.util.UltoTunnel;

import java.io.IOException;
import java.net.Socket;

public class UltoClient extends Thread {

    private int localPort;
    private String remoteHost;
    private int remotePort;

    private Socket localSocket;
    private Socket remoteSocket;

    private String type = "TCP";

    public UltoClient(int localPort, String remoteHost, int remotePort, String type) throws IOException {
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        if(type!=null) {
            this.type = type;
        }
        localSocket = new Socket("localhost", localPort);
        remoteSocket = new Socket(remoteHost, remotePort);
        remoteSocket.getOutputStream().write(type.getBytes());
        remoteSocket.getOutputStream().flush();
    }

    public boolean isLocalConnected() {
        return !localSocket.isClosed();
    }

    public boolean isRemoteConnected() {
        return !remoteSocket.isClosed();
    }


    public void run() {
        while(true) {
                if(!isLocalConnected() || !isRemoteConnected()) {
                    break;
                }
              try {
                  UltoTunnel.pipe(localSocket, remoteSocket, new byte[] {});
              } catch (IOException ie) {
                  ie.printStackTrace();;
              }
        }
    }
}
