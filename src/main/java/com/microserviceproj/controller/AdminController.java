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
import com.microserviceproj.enumeration.Status;
import com.microserviceproj.repository.CompanyRepository;
import com.microserviceproj.service.AdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;
    private final CompanyRepository companyRepository;

    @PostMapping("/decryptData")
    public ResponseEntity<Object> decryptData(@RequestBody EncryptedData request) {
        LocalDate activationDate = LocalDate.now();

        try {
            Encryption decryptedData = adminService.decryptData(request.getEncryptedData(), request.getSecretKey());
            if (decryptedData == null) {
                logger.error("Failed to decrypt data. Encrypted data: {}", request.getEncryptedData());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to decrypt data.");
            }

            Optional<Company> companyOptional = companyRepository.findByLicenseAndEmail(decryptedData.getLicense(), decryptedData.getEmail());
            if (companyOptional.isPresent()) {
                Company company = companyOptional.get();

                company.setActivationDate(activationDate);
                LocalDate expireDate = activationDate.plusDays(30);
                company.setExpireDate(expireDate);
                long daysUntilExpiration = ChronoUnit.DAYS.between(LocalDate.now(), expireDate);
                String gracePeriod = daysUntilExpiration + " days";
                company.setGracePeriod(gracePeriod);

                company.setStatus(Status.APPROVED);

                companyRepository.save(company);

                logger.info("Data decrypted successfully and approved for company: {}", company.getCompanyName());
                return ResponseEntity.ok("Data decrypted successfully and approved. Activation date and expire date set.");
            } else {
                logger.error("License or email not found in the database. License: {}, Email: {}", decryptedData.getLicense(), decryptedData.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("License or email not found in the database. License: " + decryptedData.getLicense() + ", Email: " + decryptedData.getEmail());
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input data: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid input data.");
        } catch (Exception e) {
            logger.error("Failed to decrypt data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to decrypt data.");
        }
    }

}
