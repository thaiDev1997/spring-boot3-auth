package com.example.auth.configuration;

import com.example.auth.exception.ErrorCode;
import com.example.auth.exception.ResponseException;
import com.example.auth.service.AuthenticationService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomJwtDecoder implements JwtDecoder, BeanNameAware, ApplicationContextAware, InitializingBean {
    private String beanName;

    @Value(value = "${jwt.signerKey}")
    private String signerKey;

    private final AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder;

    public CustomJwtDecoder(AuthenticationService authenticationService) {
        log.info("1) Constructor injection is invoked");
        this.authenticationService = authenticationService;
    }

    @PostConstruct
    public void init() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), MacAlgorithm.HS256.getName());
        nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS256).build();
        log.info("4) @PostConstruct: " + beanName + " is invoked");
    }

    @PreDestroy
    public void cleanup() {
        log.info("7) @PreDestroy: " + beanName + " is invoked");
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        var verified = authenticationService.verifyToken(token);
        if (!verified) {
            throw new ResponseException(ErrorCode.UNAUTHENTICATED);
        }

        // Get the existing claims from the JWT
        Jwt decodedJwt = nimbusJwtDecoder.decode(token);
        Map<String, Object> claims = new HashMap<>(decodedJwt.getClaims());

        // Add your custom claim to the JWT payload
        claims.put("custom_key", "custom_value");

        return new Jwt(token, decodedJwt.getIssuedAt(), decodedJwt.getExpiresAt(), decodedJwt.getHeaders(), claims);
    }

    @Override
    public void setBeanName(String name) {
        log.info("2) BeanNameAware: Setting bean name is \"" + name + "\"");
        this.beanName = name;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("-> ApplicationContextAware: Setting application context");
    }

    @Override
    public void afterPropertiesSet() {
        log.info("5) afterPropertiesSet of " + beanName + " is invoked");
    }
}
