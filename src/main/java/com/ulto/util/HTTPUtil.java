package com.ulto.util;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

public class HTTPUtil {

    static public HashMap getHTTPHeaders(byte[] data) {
        BufferedReader in = null; PrintWriter out = null;
        String[] validMethods = { "GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS"};
        try {
            in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));

            String input = in.readLine();
            if(input == null) return null;
            HashMap<String, String> headers = new HashMap<>();
            // we parse the request with a string tokenizer
            StringTokenizer parse = new StringTokenizer(input, "\n");
            String httpStartLine = parse.nextToken();
            String[] METHOD_REQUEST_VERSION = httpStartLine.split(" ");
            if(METHOD_REQUEST_VERSION.length < 3 || METHOD_REQUEST_VERSION.length > 3) {
                System.out.println("Invalid HTTP request!");
                return null;
            }
            if(!METHOD_REQUEST_VERSION[2].trim().startsWith("HTTP")) {
                System.out.println("NOT A VALID HTTP REQUEST!");
                return null;
            }
            if(!Arrays.stream(validMethods).anyMatch(method -> method.equals(METHOD_REQUEST_VERSION[0]))) {
                System.out.println("NOT A VALID HTTP METHOD!");
                return null;
            }
            String localStr = "";
            while((localStr = in.readLine()) != null) {
                if(!localStr.isEmpty()) {
                    String[] splitted = localStr.split(":");
                    headers.put(splitted[0].trim(), splitted[1].trim());
                }
            }
            return headers;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
