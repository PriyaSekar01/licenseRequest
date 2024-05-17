package com.microserviceproj.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.microserviceproj.entity.Company;
import com.microserviceproj.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GracePeriodService {
	
	private final CompanyRepository companyRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(GracePeriodService.class);

    @Scheduled(cron = "0 0 0 * * ?")  // Runs daily at midnight
    public void updateGracePeriod() {
        List<Company> companies = companyRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Company company : companies) {
            LocalDate expireDate = company.getExpireDate();
            if (expireDate != null) {
                long daysUntilExpiration = ChronoUnit.DAYS.between(today, expireDate);
                if (daysUntilExpiration >= 0) {
                    String gracePeriod = daysUntilExpiration + " days";
                    company.setGracePeriod(gracePeriod);
                    companyRepository.save(company);
                    logger.info("Updated grace period for company: {} to {}", company.getCompanyName(), gracePeriod);
                } else {
                    logger.warn("Company {} has expired license.", company.getCompanyName());
                }
            }
        }
    }

}
