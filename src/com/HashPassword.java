package com;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashPassword {

    public static String hash(String password) {
        String hashedPassword = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA3-256");
            byte[] hash = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (int x = 0; x < hash.length; x++) {
                String hex = Integer.toHexString(0xff & hash[x]);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
            hashedPassword = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedPassword;
    }

}
