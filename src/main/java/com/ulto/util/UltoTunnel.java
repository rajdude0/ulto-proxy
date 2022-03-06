package com.ulto.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UltoTunnel {


    public static void pipe(Socket source, Socket destination, byte[] defaultInitialData) throws IOException {
        DataInputStream sourceInput = new DataInputStream(source.getInputStream());
        DataOutputStream sourceOutput = new DataOutputStream(source.getOutputStream());

        DataInputStream destInput = new DataInputStream(destination.getInputStream());
        DataOutputStream destOutput = new DataOutputStream(destination.getOutputStream());

        if(defaultInitialData.length  > 0) {
            destOutput.write(defaultInitialData);
            defaultInitialData = new byte[] {};
        }

        {
            int sourceAvailableBytes = sourceInput.available();
            int destinationAvailableBytes = destInput.available();
            byte[] sourceBuffer = new byte[sourceAvailableBytes];
            byte[] destinationBuffer = new byte[destinationAvailableBytes];

            sourceInput.read(sourceBuffer);


            destOutput.write(sourceBuffer);

            destInput.read(destinationBuffer);


            sourceOutput.write(destinationBuffer);

            sourceOutput.flush();
            destOutput.flush();
        }
    }
}
