package com.anhnhvcoder.spring_shopping_cart.utils;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class VNPayUtils {

    public static String hmacSHA512(final String key, final String data){
        try {
            if(key == null || data == null){
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb =  new StringBuilder(2 * result.length);
            for(byte b : result){
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception e) {
            return "";
        }
    }

    public static String getRandomNumber(int length){
        Random rd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i =0 ; i< length; i++){
            sb.append(chars.charAt(rd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String getPaymentUrl(Map<String, String> paramsMap, boolean encodeKey){
        return paramsMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .sorted(Map.Entry.comparingByKey())
                .map(entry ->
                        (encodeKey ? URLEncoder.encode(entry.getKey()
                                , StandardCharsets.US_ASCII)
                                : entry.getKey()) + "=" +
                                URLEncoder.encode(entry.getValue()
                                        , StandardCharsets.US_ASCII))
                .collect(Collectors.joining("&"));
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress;
        try{
            ipAddress = request.getHeader("X-FORWARDED-FOR");
            if(ipAddress == null){
                ipAddress = request.getRemoteAddr();
            }
        }catch (Exception e){
            ipAddress = "Invalid IP:" + e.getMessage();
        }
        return ipAddress;

    }
}
