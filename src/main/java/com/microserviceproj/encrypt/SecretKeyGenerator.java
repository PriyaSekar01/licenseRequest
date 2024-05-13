package com.microserviceproj.encrypt;

import java.security.NoSuchAlgorithmException;


import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SecretKeyGenerator {
	
	 public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
	        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
	        keyGenerator.init(128); // 128 bits
	        return keyGenerator.generateKey();
	    }

}
