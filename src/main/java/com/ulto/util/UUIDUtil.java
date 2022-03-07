package com.ulto.util;

import java.util.UUID;

public class UUIDUtil {

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static String generateUUID(String fromString) {
        return UUID.fromString(fromString).toString();
    }
}
