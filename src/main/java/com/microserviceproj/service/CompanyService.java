package com.microserviceproj.service;




import java.util.Optional;

import org.springframework.stereotype.Service;


import com.microserviceproj.dto.CompanyDto;
import com.microserviceproj.dto.EncryptedData;

import com.microserviceproj.encrypt.SecretKeyGenerator;
import com.microserviceproj.entity.Company;
import com.microserviceproj.enumeration.Status;
import com.microserviceproj.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
	
	private final CompanyRepository repository;
	
	private  final LicenseGenerator licenseGenerator;
	
	private final SecretKeyGenerator encryptionService;

	public Company createCompany(CompanyDto companyDto) {
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
    }
	
    
	public EncryptedData encryptEmailLicense(String companyName) {
	    Optional<Company> companyOptional = repository.findByCompanyName(companyName);
	    if (companyOptional.isPresent()) {
	        Company company = companyOptional.get();
	        
	        // Encrypt email and license
	        EncryptedData encryptedData = encryptionService.encrypt(company.getEmail() + ";" + company.getLicense());
	        
	        if (encryptedData != null) {
	            // Set status to "REQUEST"
	            company.setStatus(Status.REQUEST);
	            repository.save(company); // Assuming you're using JPA or some ORM
	            
	            return encryptedData;
	        } else {
	            // Handle encryption failure by throwing an IllegalArgumentException
	            throw new IllegalArgumentException("Encryption failed for company: " + companyName);
	        }
	    } else {
	        return null;
	    }
	}

}
	    

