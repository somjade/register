package com.tmb.test.register.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
public class BaseErrorResponse {

    @JsonIgnore
    private final HttpStatus status;
    private final int statusCode;
    private final String message;

    public BaseErrorResponse(HttpStatus status) {
        this.status = status;
        this.statusCode = this.status.value();
        this.message = this.status.getReasonPhrase();
    }

    public BaseErrorResponse(HttpStatus status, String message) {
        this.status = status;
        this.statusCode = this.status.value();
        this.message = message;
    }
}