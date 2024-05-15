package com.microserviceproj.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.microserviceproj.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>{

	Optional<Company> findByCompanyName(String companyName);

	Optional<Company> findByLicenseAndEmail(String license, String email);

	
}
