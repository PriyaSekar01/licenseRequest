package com.microserviceproj.service;

import org.springframework.stereotype.Service;

import com.microserviceproj.dto.Encryption;
import com.microserviceproj.encrypt.SecretKeyGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final SecretKeyGenerator secretKeyGenerator;
	
	
	public Encryption decryptData(String encryptedData, String secretKey) {
        try {
            return secretKeyGenerator.decrypt(encryptedData, secretKey);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid input data.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt data.");
        }
    }

}
