package com.ulto.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SocketUtil {

    public static byte[] waitForIncomingData(InputStream inputStream) throws IOException {
        int first = 0;
        try {
            first = inputStream.read();
        } catch (SocketTimeoutException ste) {
        }
        byte[] data = new byte[inputStream.available()];
        inputStream.read(data);
        byte[] finalData = new byte[data.length + 1];
        finalData[0] = (byte) first;
        for (int i = 0; i < data.length; i++) {
            finalData[i + 1] = data[i];
        }
     return finalData;
    }
}
