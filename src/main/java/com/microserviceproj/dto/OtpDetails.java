package com.microserviceproj.dto;

import java.time.LocalDateTime;

public class OtpDetails {
	
	 private final String otp;
     private final LocalDateTime expirationTime;

     public OtpDetails(String otp, LocalDateTime expirationTime) {
         this.otp = otp;
         this.expirationTime = expirationTime;
     }

     public String getOtp() {
         return otp;
     }

     public LocalDateTime getExpirationTime() {
         return expirationTime;
     }
 
}
