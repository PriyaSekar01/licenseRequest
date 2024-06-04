package com.microserviceproj.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.microserviceproj.dto.CompanyDto;

import com.microserviceproj.dto.EncryptedData;
import com.microserviceproj.entity.Company;
import com.microserviceproj.exception.CompanyNotFoundException;
import com.microserviceproj.exception.EncryptionException;
import com.microserviceproj.response.Response;
import com.microserviceproj.response.ResponseGenerator;
import com.microserviceproj.response.TransactionContext;
import com.microserviceproj.service.CompanyService;
import com.microserviceproj.service.OtpService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);

    private final CompanyService companyService;
    private final ResponseGenerator responseGenerator;
    
    private final OtpService otpService;
    
    @PostMapping("/create")
    public ResponseEntity<Response> createCompany(@RequestBody CompanyDto companyDto) {
        TransactionContext context = responseGenerator.generateTransactionContext(null);
        try {
            Company createdCompany = companyService.createCompany(companyDto);
            return responseGenerator.successResponse(context, createdCompany, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to create company: {}", e.getMessage(), e);
            return responseGenerator.errorResponse(context, "Failed to create company", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @PostMapping("/verify-otp")
    public ResponseEntity<Response> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        TransactionContext context = responseGenerator.generateTransactionContext(null);
        try {
            boolean isValid = otpService.validateOtp(email, otp);
            if (isValid) {
                return responseGenerator.successResponse(context, "OTP verified successfully", HttpStatus.OK);
            } else {
                return responseGenerator.errorResponse(context, "Invalid or expired OTP", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Failed to verify OTP: {}", e.getMessage());
            return responseGenerator.errorResponse(context, "Failed to verify OTP", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/encryptEmailLicense")
    public ResponseEntity<Response> encryptEmailLicense(@RequestParam String companyName) {
        TransactionContext context = responseGenerator.generateTransactionContext(null);
        try {
            EncryptedData encryptedData = companyService.encryptEmailLicense(companyName);
            return responseGenerator.successResponse(context, encryptedData, HttpStatus.OK);
        } catch (EncryptionException e) {
            logger.error("Encryption failed: {}", e.getMessage());
            return responseGenerator.errorResponse(context, "Encryption failed", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (CompanyNotFoundException e) {
            logger.error("Company not found: {}", e.getMessage());
            return responseGenerator.errorResponse(context, "Company not found", HttpStatus.NOT_FOUND);
        }
    }
}
