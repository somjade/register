package com.tmb.test.register.service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserRegistration {

    @Getter
    @Setter
    public static class Request {
        private String email;
        private String password;
        private String title;
        private String firstName;
        private String lastName;
        private String phone;
        private Float salary;
        private String houseNo;
        private Integer moo;
        private String street;
        private String road;
        private String subDistrict;
        private String district;
        private String postCode;
        private String province;
    }

    @Getter
    @Setter
    @Builder
    public static class Response {
        private String refCode;
    }
}
