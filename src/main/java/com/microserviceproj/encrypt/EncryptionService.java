package com.microserviceproj.encrypt;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.microserviceproj.dto.EncryptedData;

@Service
public class EncryptionService {
	
	public EncryptedData encryptEmailAndLicense(String email,String company) {
	    try {
	        if (company == null) {
	            throw new IllegalArgumentException("Company information is missing.");
	        }

	        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	        keyPairGenerator.initialize(2048);
	        KeyPair keyPair = keyPairGenerator.genKeyPair();
	        PublicKey publicKey = keyPair.getPublic();

	        SecretKey secretKey = SecretKeyGenerator.generateSecretKey();

	        Cipher aesCipher = Cipher.getInstance("AES");
	        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
	        byte[] emailLicenseBytes = (email + "||" + company).getBytes(); // Combine email and license
	        byte[] encryptedEmailLicenseBytes = aesCipher.doFinal(emailLicenseBytes);

	        Cipher rsaCipher = Cipher.getInstance("RSA");
	        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
	        byte[] encryptedSecretKey = rsaCipher.doFinal(secretKey.getEncoded());

	        EncryptedData response = new EncryptedData();
	        response.setEncryptedData(Base64.getEncoder().encodeToString(encryptedEmailLicenseBytes));
	        response.setSecretKey(Base64.getEncoder().encodeToString(encryptedSecretKey));

	        return response;
	    } catch (IllegalArgumentException e) {
	        System.err.println(e.getMessage());
	        return null;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}



}
