package com.ulto.main;

import com.ulto.conn.UltoBridgeHTTP;
import com.ulto.conn.UltoBridgeTCP;
import com.ulto.conn.UltoPeers;
import com.ulto.conn.UltoListenSocket;

import java.util.Scanner;

public class UltoMain {

    public static void main(String args[]) {
        UltoPeers clients = new UltoPeers();
        try {
          Thread listener = new Thread(new UltoListenSocket(clients));
          listener.start();
          UltoBridgeTCP bridge = new UltoBridgeTCP(clients);
            UltoBridgeHTTP httpBridge = new UltoBridgeHTTP();

            Scanner scanner = new Scanner(System.in);
            String input = "";
            while(!(input = scanner.nextLine()).equals("die")) {
                if(input.equals("l")) {
                    System.out.println(clients.toString());
                }
                if(input.equals("c")) {
                    String source = scanner.nextLine();
                    String destination = scanner.nextLine();
                    UltoPeers.UltoPeer sourcePeer = clients.get(source);
                    UltoPeers.UltoPeer destinationPeer = clients.get(destination);
                    if(sourcePeer.getPeerType() == UltoPeers.PeerType.HTTP) {
                        httpBridge.join(sourcePeer, destinationPeer);
                    } else {
                        bridge.join(sourcePeer, destinationPeer);
                    }
                }
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
