package com.moe.neko;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
    private final static char[] hex_arr="0123456789abcdef".toCharArray();
    public static byte[] md5(String str){
        try {
            return MessageDigest.getInstance("md5").digest(str.getBytes());
        } catch (NoSuchAlgorithmException e) {}
        return new byte[0];
    }
    public static String hex(byte[] data){
        StringBuilder sb=new StringBuilder();
        for(byte i:data){
            sb.append(hex_arr[(i&0xf0)>>>4]).append(hex_arr[0xf&i]);
        }
        return sb.toString();
    }
    public static String upKey(String data){
        return hex(md5(data));
    }
}
