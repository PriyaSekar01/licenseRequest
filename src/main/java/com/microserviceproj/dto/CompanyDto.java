package com.microserviceproj.dto;

import com.microserviceproj.enumeration.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {
	private String companyName;
	
	private String email;
	
	private String gracePeriod;
	
	private Status status;
	
	
	

}
