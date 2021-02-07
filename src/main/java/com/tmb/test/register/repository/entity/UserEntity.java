package com.tmb.test.register.repository.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USERS")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserEntity {
    public enum UserClassification {
        PLATINUM,
        GOLD,
        SILVER,
        NOT_ALLOW
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", unique = true, nullable = false)
    private Integer userId;

    @Column(name = "REF_CODE", nullable = false)
    private String refCode;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "FIRSTNAME")
    private String firstName;

    @Column(name = "LASTNAME")
    private String lastName;

    @Column(name="PHONE")
    private String phone;

    @Column(name="SALARY", nullable = false)
    private Float salary;

    @Column(name="HOUSE_NO")
    private String houseNo;

    @Column(name="MOO")
    private Integer moo;

    @Column(name="STREET")
    private String street;

    @Column(name="ROAD")
    private String road;

    @Column(name="SUB_DISTRICT")
    private String subDistrict;

    @Column(name="DISTRICT")
    private String district;

    @Column(name="POSTCODE")
    private String postCode;

    @Column(name="PROVINCE")
    private String province;

    @Enumerated(EnumType.STRING)
    @Column(name="MEMBER_TYPE")
    private UserClassification memberType;

    @Column(name="CREATED_DATE")
    private final LocalDateTime createdDate = LocalDateTime.now();

    @Column(name="UPDATED_DATE")
    private LocalDateTime updatedDate;
}
