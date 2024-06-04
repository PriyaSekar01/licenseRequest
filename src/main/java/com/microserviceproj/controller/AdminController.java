package com.microserviceproj.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microserviceproj.dto.EncryptedData;
import com.microserviceproj.dto.Encryption;
import com.microserviceproj.entity.Company;
import com.microserviceproj.enumeration.GraceStatus;
import com.microserviceproj.enumeration.Status;
import com.microserviceproj.repository.CompanyRepository;
import com.microserviceproj.response.Response;
import com.microserviceproj.response.ResponseGenerator;
import com.microserviceproj.response.TransactionContext;
import com.microserviceproj.service.AdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private  final  AdminService adminService;
    
    private final CompanyRepository companyRepository;
    
    private final ResponseGenerator  responseGenerator;

    @PostMapping("/decryptData")
    public ResponseEntity<Response> decryptData(@RequestBody EncryptedData request) {
        TransactionContext context = responseGenerator.generateTransactionContext(null);
        LocalDate activationDate = LocalDate.now();

        try {
            Encryption decryptedData = adminService.decryptData(request.getEncryptedData(), request.getSecretKey());
            if (decryptedData == null) {
                logger.error("Failed to decrypt data. Encrypted data: {}", request.getEncryptedData());
                return responseGenerator.errorResponse(context, "Failed to decrypt data.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Optional<Company> companyOptional = companyRepository.findByLicenseAndEmail(decryptedData.getLicense(), decryptedData.getEmail());
            if (companyOptional.isPresent()) {
            	Company company = companyOptional.get();

            	company.setActivationDate(activationDate);
            	LocalDate expireDate = activationDate.plusDays(1); // Set expiration date 30 days from activation
            	company.setExpireDate(expireDate);
            	company.setGraceStatus(GraceStatus.ACTIVE);

            	// Calculate grace period
            	LocalDate gracePeriodStart = expireDate.plusDays(0);
            	LocalDate gracePeriodEnd = gracePeriodStart.plusDays(1);
            	company.setGracePeriod(gracePeriodEnd); // Set the end date of the grace period

            	company.setStatus(Status.APPROVED);

                companyRepository.save(company);

                logger.info("Data decrypted successfully and approved for company: {}", company.getCompanyName());
                return responseGenerator.successResponse(context, "Data decrypted successfully and approved. Activation date and expiration date set.", HttpStatus.OK);
            } else {
                String errorMessage = "License or email not found in the database. License: " + decryptedData.getLicense() + ", Email: " + decryptedData.getEmail();
                logger.error(errorMessage);
                return responseGenerator.errorResponse(context, errorMessage, HttpStatus.UNAUTHORIZED);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input data: {}", e.getMessage());
            return responseGenerator.errorResponse(context, "Invalid input data.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Failed to decrypt data: {}", e.getMessage());
            return responseGenerator.errorResponse(context, "Failed to decrypt data.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
