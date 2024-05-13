package com.microserviceproj.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.microserviceproj.enumeration.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name="company")
public class Company {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    @Column(name="id")
	    private Long id;

	    @Column(name="company_name")
	    private String companyName;

	    @Column(name="email")
	    private String email;
	    
	    @Column(name="address")
	    private String address;

	    @Column(name="grace_period")
	    private String gracePeriod;

	    @Column(name = "status")
	    @Enumerated(EnumType.STRING)
	    private Status status;

	    @CreatedDate
	    @Column(name = "activation_date", updatable = false)
	    private LocalDateTime activationDate;

	    @Column(name="expire_date")
	    private LocalDate expireDate;

	    @Column(name="license")
	    private String license;
	
	
	

}
