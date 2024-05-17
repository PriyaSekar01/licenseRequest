package com.microserviceproj.response;

import java.util.List;

@lombok.Data
public class Error {
	
	 private String code;
	    private String reason;
	    private List<String> errorList;

}
