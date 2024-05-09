package com.microserviceproj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microserviceproj.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>{

}
