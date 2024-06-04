package com.microserviceproj.service;
import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.microserviceproj.entity.Company;
import com.microserviceproj.entity.OTPEntity;
import com.microserviceproj.exception.OtpServiceException;
import com.microserviceproj.repository.CompanyRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final JavaMailSender mailSender;
    private final CompanyRepository companyRepository;
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

    @Transactional
    public void generateAndSendOtp(String email) {
        try {
            int otp = ThreadLocalRandom.current().nextInt(100000, 999999);
            LocalTime expirationTime = LocalTime.now().plusMinutes(1); // OTP expires after 1 minute

            Optional<Company> optionalCompany = companyRepository.findByEmail(email);
            if (optionalCompany.isPresent()) {
                Company company = optionalCompany.get();

                OTPEntity otpEntity = company.getOtp();
                if (otpEntity == null) {
                    otpEntity = new OTPEntity();
                    otpEntity.setCompany(company);
                }
                otpEntity.setOtp(otp);
                otpEntity.setTime(expirationTime);
                company.setOtp(otpEntity);

                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setSubject("Your OTP Code");
                message.setText("Your OTP code is: " + otp + ". It is valid for 1 minute.");

                mailSender.send(message);
                logger.info("OTP sent to email: {}", email);
            } else {
                throw new OtpServiceException("Company not found for email: " + email);
            }
        } catch (Exception e) {
            logger.error("Failed to send OTP to email: {}", email, e);
            throw new OtpServiceException("Failed to send OTP", e);
        }
    }

    @Transactional
    public boolean validateOtp(String email, String otp) {
        Optional<Company> optionalCompany = companyRepository.findByEmail(email);
        if (optionalCompany.isPresent()) {
            Company company = optionalCompany.get();
            OTPEntity otpEntity = company.getOtp();
            if (otpEntity != null && String.valueOf(otpEntity.getOtp()).equals(otp) &&
                    LocalTime.now().isBefore(otpEntity.getTime())) {
                company.setOtp(null);  // Clear OTP after successful validation
                return true;
            }
        }
        return false;
    }
}
