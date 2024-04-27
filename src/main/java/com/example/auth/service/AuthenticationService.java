package com.example.auth.service;

import com.example.auth.dto.auth.AuthUser;
import com.example.auth.dto.request.AuthenticationRequest;
import com.example.auth.entity.Account;
import com.example.auth.entity.InvalidatedToken;
import com.example.auth.entity.Permission;
import com.example.auth.entity.Role;
import com.example.auth.exception.ErrorCode;
import com.example.auth.exception.ResponseException;
import com.example.auth.repository.AccountRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.example.auth.enums.Permission.*;
import static com.example.auth.enums.Role.ADMIN;

@Service
public class AuthenticationService {

    @Value(value = "${jwt.signerKey}")
    private String signerKey;

    private final PasswordEncoder passwordEncoder;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    public AuthenticationService(@Lazy PasswordEncoder passwordEncoder, InvalidatedTokenRepository invalidatedTokenRepository,
                                 AccountRepository accountRepository, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
        this.accountRepository = accountRepository;
        this.authenticationManager = authenticationManager;
    }

    public String authenticate(AuthenticationRequest authenticationRequest) {
        String username = authenticationRequest.getUsername();
        Optional<Account> accountOptional = accountRepository.findByUsername(username);
        if (accountOptional.isEmpty()) {
            throw new ResponseException(ErrorCode.USER_NOT_EXISTED);
        }
        Account account = accountOptional.get();
        if (!passwordEncoder.matches(authenticationRequest.getPassword(), account.getEncodedPassword())) {
            throw new ResponseException(ErrorCode.INVALID_PASSWORD);
        }
        return generateToken(account);
    }

    public String authenticateByUserDetailsService(AuthenticationRequest authenticationRequest) {
        String username = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(usernamePassword);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AuthUser userDetails = (AuthUser) authentication.getPrincipal();
        return generateToken(userDetails.getAccount());
    }

    private String generateToken(Account account) {
        String username = account.getUsername();
        // JWT Header
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        Set<Role> roles = account.getRoles();
        // Role[] roles = new Role[]{Role.USER, Role.ADMIN};
        Set<Permission> permissions = new HashSet<>();
        for (Role role : roles) {
            permissions.addAll(role.getPermissions());
        }
        // JTW Payload
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("self.com")
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

    public String generateAdminTokenTestScope() {
        Role adminRole = Role.builder().name(ADMIN.name()).description("Administrator").build();
        // Permissions
        Permission getAllUsers = Permission.builder().name(GET_ALL_USERS.name()).build();
        Permission getUser = Permission.builder().name(GET_USER.name()).build();
        Permission createUser = Permission.builder().name(CREATE_USER.name()).build();
        Permission editUser = Permission.builder().name(EDIT_USER.name()).build();
        Permission deleteUser = Permission.builder().name(DELETE_USER.name()).build();

        Set<Permission> allPermissions = Set.of(getAllUsers, getUser, createUser, editUser, deleteUser);
        adminRole.setPermissions(allPermissions);
        return generateToken(Account.builder().username("admin").roles(Set.of(adminRole)).build());
    }

    private String buildUserScopes(Set<Role> roles, Set<Permission> permissions) {
        final String spaceDelimiter = " ";
        StringJoiner scopeJoiner = new StringJoiner(spaceDelimiter);
        for (Role role : roles) {
            scopeJoiner.add("ROLE_" + role.getName());
        }
        for (Permission permission : permissions) {
            scopeJoiner.add(permission.getName());
        }
        return scopeJoiner.toString();
    }

    public boolean verifyToken(String token) {
        try {
            JWSVerifier verifier = new MACVerifier(signerKey);
            SignedJWT signedJWT = SignedJWT.parse(token);
            var verified = signedJWT.verify(verifier);
            return verified && signedJWT.getJWTClaimsSet().getExpirationTime().after(new Date())
                    && !invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID());
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

    public String refreshToken(Jwt jwt) {
        String username = jwt.getSubject();
        Account account = accountRepository.getReferenceById(username);
        String newToken = generateToken(account);
        logout(jwt);
        return newToken;
    }
}
