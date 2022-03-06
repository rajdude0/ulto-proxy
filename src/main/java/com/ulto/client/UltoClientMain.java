package com.ulto.client;

import java.io.IOException;
import java.util.Arrays;

public class UltoClientMain {

    public static void main(String args[]) {
       int localPort = Integer.parseInt(args[0]);
       String remoteHost = args[1];
       int remotePort = Integer.parseInt(args[2]);
       String type = args[3];

       try {
           UltoClient client = new UltoClient(localPort, remoteHost, remotePort, type);
           Thread clientThread = new Thread(client);
           clientThread.start();
       } catch (IOException ieo) {
           ieo.printStackTrace();
       }
    }
}
