package com.microserviceproj.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microserviceproj.dto.CompanyDto;
import com.microserviceproj.entity.Company;
import com.microserviceproj.service.CompanyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company")
public class CompanyController {
	
	private final CompanyService companyService;
	
	@PostMapping("/create")
	public Company createCompany(@RequestBody CompanyDto companyDto) {
		return companyService.createCompany(companyDto);
		
	}
	
	

}
