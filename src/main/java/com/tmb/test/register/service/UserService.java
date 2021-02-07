package com.tmb.test.register.service;

import com.tmb.test.register.exception.MemberTypeNotAllowException;
import com.tmb.test.register.exception.UserNotFoundException;
import com.tmb.test.register.exception.UserRefCodeNotFoundException;
import com.tmb.test.register.mapper.UserMapper;
import com.tmb.test.register.repository.UserRepository;
import com.tmb.test.register.repository.entity.UserEntity;
import com.tmb.test.register.service.model.User;
import com.tmb.test.register.service.model.UserRegistration;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Transactional
    public UserRegistration.Response createUser(UserRegistration.Request request) {

        UserEntity entity = mapper.toUserEntity(request);

        if (entity.getMemberType() == UserEntity.UserClassification.NOT_ALLOW)
            throw new MemberTypeNotAllowException("Reject salary below minimum allowing");

        UserEntity result = userRepository.save(entity);

        return UserRegistration.Response.builder()
                .refCode(result.getRefCode())
                .build();
    }

    @Transactional(readOnly = true)
    public User getUserByIdAndRefCode(Integer userId, String refCode) {
        return mapper.toUser(userRepository.findUsersByUserIdAndRefCode(userId, refCode)
                .orElseThrow(() -> new UserRefCodeNotFoundException("Could not found user by id and ref_code in database")));
    }

    @Transactional(readOnly = true)
    public User getUserById(Integer userId) {
        return mapper.toUser(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Could not found user by id in database")));
    }
}
