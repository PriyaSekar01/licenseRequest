package com.microserviceproj.service;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.microserviceproj.dto.CompanyDto;
import com.microserviceproj.dto.EncryptedData;
import com.microserviceproj.encrypt.SecretKeyGenerator;
import com.microserviceproj.entity.Company;
import com.microserviceproj.enumeration.Status;
import com.microserviceproj.repository.CompanyRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private LicenseGenerator licenseGenerator;

    @Mock
    private OtpService otpService;

    @Mock
    private SecretKeyGenerator encryptionService;

    @InjectMocks
    private CompanyService companyService;

    @Test
    public void testCreateCompanySuccess() {
        // Arrange
        CompanyDto companyDto = new CompanyDto();
        companyDto.setCompanyName("Test Company");
        companyDto.setEmail("test@example.com");
        companyDto.setAddress("123 Test Street");

        Company company = Company.builder()
                .companyName("Test Company")
                .email("test@example.com")
                .address("123 Test Street")
                .build();

        when(companyRepository.save(any(Company.class))).thenReturn(company);
        when(licenseGenerator.generateLicense(any(Company.class))).thenReturn("LICENSE123");

        // Act
        Company result = companyService.createCompany(companyDto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Company", result.getCompanyName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("123 Test Street", result.getAddress());
        assertEquals("LICENSE123", result.getLicense());
        assertEquals(Status.CREATE, result.getStatus());

        verify(companyRepository, times(1)).save(any(Company.class));
        verify(otpService, times(1)).generateAndSendOtp("test@example.com");
    }

    @Test
    public void testEncryptEmailLicenseSuccess() {
        // Arrange
        String companyName = "Test Company";
        Company company = Company.builder()
                .companyName(companyName)
                .email("test@example.com")
                .license("LICENSE123")
                .build();

        when(companyRepository.findByCompanyName(companyName)).thenReturn(java.util.Optional.of(company));
        when(encryptionService.encrypt("test@example.com;LICENSE123")).thenReturn(new EncryptedData("encrypted", companyName));

        // Act
        EncryptedData result = companyService.encryptEmailLicense(companyName);

        // Assert
        assertNotNull(result);
        assertEquals("encrypted", result.getEncryptedData());
        assertEquals(Status.REQUEST, company.getStatus());

        verify(companyRepository, times(1)).findByCompanyName(companyName);
        verify(encryptionService, times(1)).encrypt("test@example.com;LICENSE123");
        verify(companyRepository, times(1)).save(company);
    }
}