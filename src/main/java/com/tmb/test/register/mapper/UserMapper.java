package com.tmb.test.register.mapper;

import com.tmb.test.register.repository.entity.UserEntity;
import com.tmb.test.register.service.model.User;
import com.tmb.test.register.service.model.UserRegistration;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    @Mapping(source = "password", target="password", qualifiedByName = "createHashPassword")
    @Mapping(target = "userId", ignore = true)
    @Mapping(source = "phone", target = "refCode", qualifiedByName = "generateRefCode")
    @Mapping(source = "salary", target = "memberType", qualifiedByName = "convertSalaryToMemberType")
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    UserEntity toUserEntity(UserRegistration.Request user);


    @Mapping(target = "address", ignore = true)
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "memberType", ignore = true)
    User toUser(UserEntity user);

    @Named("convertSalaryToMemberType")
    static UserEntity.UserClassification convertSalaryToMemberType(Float salary){
        if (salary > 50000) {
            return UserEntity.UserClassification.PLATINUM;
        } else if (salary > 30000) {
            return UserEntity.UserClassification.GOLD;
        } else if (salary > 15000) {
            return UserEntity.UserClassification.SILVER;
        }
        return UserEntity.UserClassification.NOT_ALLOW;
    }

    @Named("createHashPassword")
    static String createHashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update("$2a$08$e02zo.x0cFVZq71FMp82DuGONIYc4ahKyPbAN07pdDxG3uMaXgcIu".getBytes(StandardCharsets.UTF_8));
        byte[] generatedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(generatedPassword);
    }

    @Named("generateRefCode")
    static String generateRefCode(String phoneNo) {
        String registerDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String last4Digit = phoneNo.substring(phoneNo.length() - 4);

        return String.format("%s%s", registerDate, last4Digit);
    }

    @AfterMapping
    default void combineFieldForUser(@MappingTarget User item, UserEntity userEntity) {

        List<String> combineAddress = List.of(
                Optional.ofNullable(userEntity.getHouseNo()).orElse(""),
                Optional.ofNullable(userEntity.getMoo()).isPresent() ? "Moo " + userEntity.getMoo().toString()  : "",
                Optional.ofNullable(userEntity.getStreet()).orElse(""),
                Optional.ofNullable(userEntity.getRoad()).orElse(""),
                Optional.ofNullable(userEntity.getSubDistrict()).orElse(""),
                Optional.ofNullable(userEntity.getDistrict()).orElse(""),
                Optional.ofNullable(userEntity.getProvince()).orElse(""),
                Optional.ofNullable(userEntity.getPostCode()).orElse(""));

        List<String> combineName = List.of(
                Optional.ofNullable(userEntity.getTitle()).orElse(""),
                Optional.ofNullable(userEntity.getFirstName()).orElse(""),
                Optional.ofNullable(userEntity.getLastName()).orElse("")
        );

        String address = combineAddress.stream().filter(each -> !each.isEmpty())
                .collect(Collectors.joining(" ,"));

        String fullName = combineName.stream().filter(each -> !each.isEmpty())
                .collect(Collectors.joining(" "));

        item.setAddress(address);
        item.setFullName(fullName);
        item.setMemberType(userEntity.getMemberType().name());
    }
}
