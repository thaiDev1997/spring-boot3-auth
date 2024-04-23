package com.example.auth.service;

import com.example.auth.dto.request.AuthenticationRequest;
import com.example.auth.entity.InvalidatedToken;
import com.example.auth.enums.Permission;
import com.example.auth.enums.Role;
import com.example.auth.exception.ErrorCode;
import com.example.auth.exception.ResponseException;
import com.example.auth.repository.InvalidatedTokenRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
public class AuthenticationService {

    @Value(value = "${jwt.signerKey}")
    private String signerKey;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;

    private static final String ENCODED_PASSWORD_PSEUDO = "$2a$12$QQxWOB5gRAxgztfPO2b4Tur0IdufpL.WXlq.jModkrckYQsNsar/K";

    public String authenticate(AuthenticationRequest authenticationRequest) {
        String username = authenticationRequest.getUsername();
        if (!"admin".equals(authenticationRequest.getUsername())) {
            throw new ResponseException(ErrorCode.USER_NOT_EXISTED);
        }
        if (!passwordEncoder.matches(authenticationRequest.getPassword(), ENCODED_PASSWORD_PSEUDO)) {
            throw new ResponseException(ErrorCode.INVALID_PASSWORD);
        }
        return generateToken(username);
    }

    private String generateToken(String username) {
        // JWT Header
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        Role[] roles = new Role[]{Role.USER, Role.ADMIN};
        Permission[] permissions = new Permission[]{Permission.GET_ALL_USERS, Permission.GET_USER, Permission.DELETE_USER};
        // JTW Payload
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("abc.com")
                .jwtID(UUID.randomUUID().toString())
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .claim("scope", buildUserScopes(roles, permissions))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(signerKey));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }

    private String buildUserScopes(Role[] roles, Permission[] permissions) {
        final String spaceDelimiter = " ";
        StringJoiner scopeJoiner = new StringJoiner(spaceDelimiter);
        for (Role role : roles) {
            scopeJoiner.add("ROLE_" + role.name());
        }
        for (Permission permission : permissions) {
            scopeJoiner.add(permission.name());
        }
        return scopeJoiner.toString();
    }

    public boolean verifyToken(String token) {
        try {
            JWSVerifier verifier = new MACVerifier(signerKey);
            SignedJWT signedJWT = SignedJWT.parse(token);
            var verified = signedJWT.verify(verifier);
            return verified && signedJWT.getJWTClaimsSet().getExpirationTime().after(new Date())
                    && !invalidatedTokenRepository.existById(signedJWT.getJWTClaimsSet().getJWTID());
        } catch (RuntimeException | JOSEException | ParseException e) {
            return false;
        }
    }

    public void logout(Jwt jwt) {
        String jti = jwt.getClaimAsString("jti");
        Date expiryTime = new Date(jwt.getExpiresAt().getEpochSecond());
        InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jti).expiryTime(expiryTime).build();
        invalidatedTokenRepository.save(invalidatedToken);
        // TODO: apply Cron Job -> remove redundant invalidatedToken rows
    }
}
