package com.microserviceproj.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microserviceproj.entity.Company;
import com.microserviceproj.enumeration.GraceStatus;
import com.microserviceproj.enumeration.Status;
import com.microserviceproj.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GracePeriodService {
	
	private final CompanyRepository companyRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(GracePeriodService.class);
	
	@Transactional
    @Scheduled(cron = "0 0 0 * * ?")  // Runs daily at midnight
    public void updateGracePeriod() {
        List<Company> companies = companyRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Company company : companies) {
            LocalDate activationDate = company.getActivationDate();
            if (activationDate != null) {
                LocalDate expireDate = activationDate.plusDays(30);

                if (today.isAfter(expireDate)) {
                    // Grace period starts after expiration
                    LocalDate gracePeriodStart = expireDate.plusDays(1);
                    LocalDate gracePeriodEnd = gracePeriodStart.plusDays(1);

                    if (today.isBefore(gracePeriodEnd)) {
                        // Grace period
                        company.setGracePeriod(gracePeriodEnd);
                        company.setGraceStatus(GraceStatus.ACTICE); // Assuming there is a GRACE_PERIOD status
                    } else {
                        // After grace period
                        company.setGracePeriod(gracePeriodEnd);
                        company.setStatus(Status.EXPIRED); // Set status to EXPIRED when grace period is over
                    }
                } else {
                    // Active period
                    company.setGracePeriod(null);
                    company.setStatus(Status.APPROVED); // Assuming there is an APPROVED status
                }

                companyRepository.save(company);
                logger.info("Updated grace period for company: {} to {}", company.getCompanyName(), company.getGracePeriod());
            } else {
                logger.warn("Company {} has no activation date.", company.getCompanyName());
            }
        }
	}
}