package com.microserviceproj.response;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ResponseGenerator {
	
	private static final Logger logger = LoggerFactory.getLogger(ResponseGenerator.class);

    public ResponseEntity<Response> successResponse(TransactionContext context, Object object, HttpStatus httpStatus) {
        HttpHeaders headers = generateHeaders(context);
        Response response = new Response();
        Data data = new Data();
        data.setObject(object);
        response.setData(data);
        response.setTimeStamp(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
        logger.debug("Success response generated for transaction with correlationId: {}", context.getCorrelationId());
        return new ResponseEntity<>(response, headers, httpStatus);
    }

    public ResponseEntity<Response> errorResponse(TransactionContext context, String errorMessage, HttpStatus httpStatus) {
        HttpHeaders headers = generateHeaders(context);
        Error error = new Error();
        error.setCode(httpStatus.toString() + "0001");
        error.setReason(errorMessage);
        Response response = new Response();
        response.setError(error);
        response.setTimeStamp(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
        logger.error("Error response generated for transaction with correlationId: {}. Error: {}", context.getCorrelationId(), errorMessage);
        return new ResponseEntity<>(response, headers, httpStatus);
    }

    private HttpHeaders generateHeaders(TransactionContext context) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("correlationId", context.getCorrelationId() != null ? context.getCorrelationId() : "demo");
        headers.add("ApplicationLabel", context.getApplicationLabel() != null ? context.getApplicationLabel() : "demo");
        headers.add("Content-Type", "application/json");
        return headers;
    }

    public TransactionContext generateTransactionContext(HttpHeaders httpHeaders) {
        TransactionContext context = new TransactionContext();
        if (httpHeaders == null) {
            context.setCorrelationId("demo");
            context.setApplicationLabel("demo");
            return context;
        }

        context.setCorrelationId(httpHeaders.getFirst("correlationId") != null ? httpHeaders.getFirst("correlationId") : "demo");
        context.setApplicationLabel(httpHeaders.getFirst("ApplicationLabel") != null ? httpHeaders.getFirst("ApplicationLabel") : "demo");
        return context;
    }
}

