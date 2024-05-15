package com.microserviceproj.controller;



import javax.security.auth.login.AccountNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microserviceproj.dto.CompanyDto;

import com.microserviceproj.dto.EncryptedData;
import com.microserviceproj.entity.Company;
import com.microserviceproj.service.CompanyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company")
public class CompanyController {

   
    private final  CompanyService companyService;


   
    @PostMapping("/create")
    public ResponseEntity<Company> createCompany(@RequestBody CompanyDto companyDto) {
        try {
             // Get current time
            Company createdCompany = companyService.createCompany(companyDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCompany);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    
    @PostMapping("/encryptEmailLicense")
    public ResponseEntity<EncryptedData> encryptEmailLicense(@RequestParam String companyName) {
        EncryptedData encryptedData = companyService.encryptEmailLicense(companyName);
        if (encryptedData != null) {
            return ResponseEntity.ok(encryptedData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
   
   

}

	  
	  

