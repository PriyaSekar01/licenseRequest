package com.microserviceproj.encrypt;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.microserviceproj.dto.EncryptedData;



@Service
public class EncryptionService {
	
	private SecretKey secretkey;

        private static final String ALGORITHM = "AES";

        public EncryptedData encrypt(String data) {
            try {
                // Generate a new AES secret key
                SecretKey secretKey = generateSecretKey();

                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                byte[] encryptedData = cipher.doFinal(data.getBytes("UTF-8"));

                String encodedEncryptedData = Base64.getEncoder().encodeToString(encryptedData);
                String encodedSecretKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

                return new EncryptedData(encodedEncryptedData, encodedSecretKey);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private SecretKey generateSecretKey() throws NoSuchAlgorithmException {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256); // AES key size can be 128, 192, or 256
            return keyGen.generateKey();
        }



        public String decrypt(String encryptedData, String base64EncodedKey) {
            try {
                byte[] decodedKey = Base64.getDecoder().decode(base64EncodedKey);
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                SecretKeySpec keySpec = new SecretKeySpec(decodedKey, ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, keySpec);
                byte[] decodedEncryptedData = Base64.getDecoder().decode(encryptedData);
                byte[] decryptedData = cipher.doFinal(decodedEncryptedData);
                return new String(decryptedData, StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
}