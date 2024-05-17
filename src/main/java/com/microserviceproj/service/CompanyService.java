package com.microserviceproj.service;




import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import com.microserviceproj.dto.CompanyDto;
import com.microserviceproj.dto.EncryptedData;

import com.microserviceproj.encrypt.SecretKeyGenerator;
import com.microserviceproj.entity.Company;
import com.microserviceproj.enumeration.Status;
import com.microserviceproj.exception.CompanyNotFoundException;
import com.microserviceproj.exception.CompanyServiceException;
import com.microserviceproj.exception.EncryptionException;
import com.microserviceproj.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);

    private final CompanyRepository repository;
    private final LicenseGenerator licenseGenerator;
    private final SecretKeyGenerator encryptionService;

    public Company createCompany(CompanyDto companyDto) {
        try {
        	Company company = Company.builder()
                    .companyName(companyDto.getCompanyName())
                    .email(companyDto.getEmail())
                    .address(companyDto.getAddress())
                    .gracePeriod(companyDto.getGracePeriod())
                    .build();

            // Generate license
            String license = licenseGenerator.generateLicense(company);
            company.setLicense(license);
            company.setStatus(Status.CREATE);

            return repository.save(company);
        } catch (Exception e) {
            logger.error("Failed to create company: {}", e.getMessage());
            throw new CompanyServiceException("Failed to create company", e);
        }
    }

    public EncryptedData encryptEmailLicense(String companyName) {
        Company company = repository.findByCompanyName(companyName)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found: " + companyName));
        try {
            EncryptedData encryptedData = encryptionService.encrypt(company.getEmail() + ";" + company.getLicense());
            company.setStatus(Status.REQUEST);
            repository.save(company);
            return encryptedData;
        } catch (Exception e) {
            logger.error("Encryption failed for company {}: {}", companyName, e.getMessage());
            throw new EncryptionException("Encryption failed for company: " + companyName, e);
        }
    }

}
	    

