package com.tmb.test.register.service;

import com.tmb.test.register.configuration.ApplicationConfig;
import com.tmb.test.register.repository.UserRepository;
import com.tmb.test.register.repository.entity.UserEntity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    private ApplicationConfig applicationConfig;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService service;

    @BeforeEach
    void testSetup() {

        lenient().when(userRepository.findUsersByEmailAndPassword(eq("username-01"), anyString()))
                .thenReturn(Optional.of(UserEntity.builder()
                        .userId(1)
                        .build()));

        lenient().when(userRepository.findUsersByEmailAndPassword(eq("username-02"), anyString()))
                .thenReturn(Optional.empty());

        ApplicationConfig.JwtConfig jwtConfig = new ApplicationConfig.JwtConfig();
        jwtConfig.setSalt("foo-bar");
        jwtConfig.setPrivateKeyFile("classpath:private_key_pkcs8.pem");
        jwtConfig.setPublicKeyFile("classpath:public_key.pem");

        when(applicationConfig.getJwt()).thenReturn(jwtConfig);
    }

    @Test
    public void testCreateAndVerifyTokenSuccess() throws Exception {
        String token = service.signToken("username-01", "foo-bar");

        Assertions.assertNotNull(token);

        String userId = service.verifyToken(token);

        Assertions.assertEquals("1", userId);
    }

    @Test
    public void testNoUserFound() {
        Assertions.assertThrows(Exception.class, () -> service.signToken("username-02", "foo-bar"));
    }

    @Test
    public void testVerifyTokenFailWhenTokenExpired() {
        String expiredToken = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJhdXRob3JpemVyIiwibmJmIjoxNjEyMTM3NjAwLCJleHAiOjE1ODA1MTUy" +
                "MDAsImlkIjoyfQ.Ssb8CXPV6DWwNtbLaOAWVqXiQtNCJ6hR2qEvBLyY5APSkXpo847qd-Pp4HntUftE0jH_uOaZ4sT7NEyUN1ZJU" +
                "4cHFex1otSE4MfKVI4NLf2pKR0uJXO3I3-SewFbHN7RfvvHGxwDHn0KLIpCu-5p6jo-mDz92yiGQefE_FIXFbFLXckXpMHYUw8CT" +
                "GxU3edaAKKsuVF9yYXL2WjGCwrG2JkgqXyQ7dvG3MXZQSJqTo7Q6SfjZVoTQHuM4USX-owbEzbMtUmDXO27LYm5fNAtXiMwe1FVQ" +
                "QI3Yr_xEUizNsaV3XXmb5QSmsqucFztz2C_DJxgxntocIVRQ9trnuVhDCWpsleJ4WMTOoSC4eJLv_HGwS-AsqycyR8VcKvAxfFhjv" +
                "z7c4bMxGsGTgy4992I2je_OIWr6JN62hHL2VPwp6c7zvgetwBMBnYWVmPLHzk8ACvFLGGzjZrWBO_2Jej3dVocwiMygAnKae_Dhb6" +
                "80MOMjXatTqS7DjhJAy2wW-dcKhwLuAXXrtj5XjQfG8lPgtoKTsOgTd4_d1_LXBd7LmGCUqIxtrtNppkQ7Neh5pjMbnxcQSxaDBYM1" +
                "pqOghCw5RJc0Yjd7VArEjnQUkYQ7Rq2taR7B4pPfcu3GNOCur6GRFuKS9GxM-EpzEBam2hShF2kIzrHdj1yWy-RWHs2Ysc";

        Assertions.assertThrows(Exception.class, () -> service.verifyToken(expiredToken));
    }

    @Test
    public void testVerifyTokenFailWhenRequiredClaimsMissing() {
        String missingClaimToken = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJhdXRob3JpemVyLTEiLCJuYmYiOjE2MTIxMzc2MDAsImV4cCI6MTczODM2ODAwMCwiaWQiOjJ9" +
                ".aZcKQebeX2ep1zbjxkP7dgwhz5AOSuZ9EjiTRjXqXlyQDUsmWrZHOtU4SGzP2OYvmV9pt9-qlG96sMKHPdSUxLj-bM5fvLggMSyuDOXfKqjMd" +
                "at3qfRIVF0MAi3Kz5GTbDAdii7NhI7GKJ_K4W89H1iFFsAvtK4o9xq-guypaf5v_w3VY8g2sOk_flfeyWncP-S8jV9RrkkjyjQMTPVBbEHZr8i" +
                "1ty1OFV0AEwq1VewAHauP9CcS4c6IvTZk2cC6VDocdO1oubbm-E5nFKTzyKP8tOQTZv_qci5_tx5VHWGUy6KJ79gOtNsNYGb8xyr2KU4_pOph0" +
                "LmWVlJVhAfvEoVoOI-SoiR1Cv9rtecTFqLv7d06RKGHrk-rWM38OcBW0kZwun0ETANwGM6-aGyWpcfyLTpp-ZlaVfuGp2XKsHdlhQvs7lFrGHh" +
                "QWyo14JNN1j-9rsaVFxUtLMQYm8PmWGMp2Io_mmuOJv-QBnsIPtw4LXMJjNQ5xh0wT8b90eBU--uLR3C74UeZNhofQugoUa7Lt4bUacFstrZsfi" +
                "iBVsaquZxX3erSHEwk90bK-Q2nGZ6bywzF9zNN9_SGSUWtFllvFklFyNp16GH0-kZFw7iftGSBoGyKEt5V0x-gfUCO84at2vBbU9iAIPdVtdhEk" +
                "QuTreZDM-dDHpirNiCX_S4";

        Assertions.assertThrows(Exception.class, () -> service.verifyToken(missingClaimToken));
    }
}
