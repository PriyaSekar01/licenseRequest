package com.microserviceproj.service;



import org.springframework.stereotype.Service;


import com.microserviceproj.dto.CompanyDto;
import com.microserviceproj.entity.Company;
import com.microserviceproj.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
	
	private final CompanyRepository repository;
	
	private  final LicenseGenerator licenseGenerator;

    public Company createCompany(CompanyDto companyDto) {
        Company company = Company.builder()
                .companyName(companyDto.getCompanyName())
                .email(companyDto.getEmail())
                .gracePeriod(companyDto.getGracePeriod())
                .status(companyDto.getStatus())
                .build();
        company.setLicense(LicenseGenerator.generateLicense(company));
        return repository.save(company);
    }

	
	
	

}
