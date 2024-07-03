package org.cs304.backend.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;


public class Encryption {

    public static String encrypt(String password) {
        Digester sha256 = new Digester(DigestAlgorithm.SHA256);
        return sha256.digestHex(password);
//        return password;
    }

    public static void main(String[] args) {
        System.out.println(encrypt("88888888"));
    }
}
