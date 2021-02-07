package com.tmb.test.register.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationConfig {
    private JwtConfig jwt;

    @Getter
    @Setter
    public static class JwtConfig {
        private String publicKeyFile;
        private String privateKeyFile;
        private String salt;
    }
}
