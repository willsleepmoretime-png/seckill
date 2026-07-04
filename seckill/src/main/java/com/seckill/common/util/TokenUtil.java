package com.seckill.common.util;


import java.util.UUID;

public class TokenUtil {

    private TokenUtil() {}

    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
