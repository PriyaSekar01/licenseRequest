package com.microserviceproj.service;



import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import com.microserviceproj.dto.CompanyDto;
import com.microserviceproj.dto.EncryptedData;
import com.microserviceproj.encrypt.EncryptionService;
import com.microserviceproj.entity.Company;
import com.microserviceproj.repository.CompanyRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
	
	private final CompanyRepository repository;
	
	private  final LicenseGenerator licenseGenerator;
	
	private final EncryptionService encryptionService;

	public Company createCompany(CompanyDto companyDto) {
        Company company = Company.builder()
                .companyName(companyDto.getCompanyName())
                .email(companyDto.getEmail())
                .gracePeriod(companyDto.getGracePeriod())
                .status(companyDto.getStatus())
                // Assign provided creation time
                .build();
        return repository.save(company);
    }

	
	
    public Company generateLicense(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
        Company company = repository.findById(id)
        	
                                    .orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + id));
        String generatedLicense = licenseGenerator.generateLicense(company);
        
        company.setLicense(generatedLicense);
        
    
        return repository.save(company);
    }

    
    public ResponseEntity<EncryptedData> encryptEmailLicense(String companyName) {
        Company companyObj = new Company();
        Optional<Company> companyOptional = repository.findByCompanyName(companyName);
        if (companyOptional.isPresent()) {
            Company company = companyOptional.get();
            EncryptedData encryptedData = encryptionService.encryptEmailAndLicense(company.getEmail(), company.getLicense());
            return ResponseEntity.ok(encryptedData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    
    

	
	
	

}
