package com.microserviceproj.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	
	
	private final AdminService adminService;
	
	private final CompanyRepository  companyRepository;
	
	@PutMapping("/decryptData")
	public ResponseEntity<Object> decryptData(@RequestBody EncryptedData request) {
		LocalDate activationDate = LocalDate.now();
		     
	    try {
	        Encryption decryptedData = adminService.decryptData(request.getEncryptedData(), request.getSecretKey());
	        if (decryptedData == null) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	        Optional<Company> companyOptional = companyRepository.findByLicenseAndEmail(decryptedData.getLicense(), decryptedData.getEmail());
	        if (companyOptional.isPresent()) {
	            Company company = companyOptional.get();
	       
	            company.setActivationDate(activationDate);
	            // Set expire date to 30 days from activation date
	            LocalDate expireDate = activationDate.plusDays(1);
	            company.setExpireDate(expireDate);
	            // Calculate grace period
	            long daysUntilExpiration = ChronoUnit.DAYS.between(LocalDate.now(), expireDate);
	            String gracePeriod = daysUntilExpiration + " days";
	            company.setGracePeriod(gracePeriod);
	            
	            company.setStatus(Status.APPROVED);
	            
	            companyRepository.save(company);
	            return ResponseEntity.ok("Data decrypted successfully and approved. Activation date, expire date, and grace period set.");
	        } else {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("License or email not found in the database.");
	        }
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body("Invalid input data.");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to decrypt data.");
	    }
	}
	
}
