package com.tmb.test.register.service.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserLogin {

    @Getter
    @Setter
    public static class Request {
        private String userName;
        private String password;
    }

    @Getter
    @Setter
    @Builder
    public static class Response {
        private String accessToken;
    }
}
