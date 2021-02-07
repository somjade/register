package com.tmb.test.register.service;

import com.tmb.test.register.configuration.ApplicationConfig;
import com.tmb.test.register.exception.TokenExpiredException;
import com.tmb.test.register.exception.UserNotFoundException;
import com.tmb.test.register.repository.UserRepository;
import com.tmb.test.register.repository.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private static final String NEW_LINE = "(\\r\\n|\\r|\\n|\\n\\r)";
    private static final String ISSUER = "authorizer";
    private static final String ZONE_ID = "Asia/Bangkok";

    private final ApplicationConfig appConfig;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public String signToken(String username, String password) throws Exception {
        Instant signTokenAt = LocalDateTime.now().atZone(ZoneId.of(ZONE_ID)).toInstant();
        UserEntity user = userRepository.findUsersByEmailAndPassword(username, createHashPassword(password))
                .orElseThrow(() -> new UserNotFoundException("Could not found user in database"));

        Map<String, String> claims = Map.ofEntries(
                Map.entry("id", user.getUserId().toString())
        );

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(ISSUER)
                .setNotBefore(Date.from(signTokenAt))
                .setExpiration(Date.from(signTokenAt.plusSeconds(900)))
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    public String verifyToken(String token) throws Exception {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .requireIssuer(ISSUER)
                    .setSigningKey(getPublicKey())
                    .build()
                    .parseClaimsJws(token);

            return claims.getBody().get("id").toString();
        } catch (ExpiredJwtException | MissingClaimException | IncorrectClaimException e) {
            throw new TokenExpiredException("Token is expired");
        }
    }

    private String createHashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(appConfig.getJwt().getSalt().getBytes(StandardCharsets.UTF_8));
        byte[] generatedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(generatedPassword);
    }

    private PublicKey getPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, URISyntaxException {
        String publicKeyContent = new String(Files.readAllBytes(ResourceUtils.getFile(appConfig.getJwt().getPublicKeyFile()).toPath()));
        publicKeyContent = publicKeyContent.replaceAll(NEW_LINE, "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "");
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpecX509);
    }

    private PrivateKey getPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, URISyntaxException {
        String privateKeyContent = new String(Files.readAllBytes(ResourceUtils.getFile(appConfig.getJwt().getPrivateKeyFile()).toPath()));
        privateKeyContent = privateKeyContent.replaceAll(NEW_LINE, "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "");
        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpecPKCS8);
    }
}
