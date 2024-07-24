package com.example.bucketlist.utils;

public class DefaultProfileImageUtil {

    public static String getDefaultProfileImagePath(String key) {
        String hash = SHA256Util.encrypt(key);
        return "https://gravatar.com/avatar/" + hash + "?d=identicon&s=200";
    }

}
