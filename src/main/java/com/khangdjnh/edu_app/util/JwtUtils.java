package com.khangdjnh.edu_app.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.List;

@Component
@Slf4j
public class JwtUtils {

    @Value("${idp.jwks-url}")
    private String jwksUrl;

    public boolean validateToken(String token) {
        try {
            // 1. Parse JWT
            SignedJWT signedJWT = SignedJWT.parse(token);

            // 2. Decode payload để check expiration
            String payloadJson = signedJWT.getPayload().toString();
            var payload = new ObjectMapper().readTree(payloadJson);
            long exp = payload.get("exp").asLong();
            long now = System.currentTimeMillis() / 1000;
            if (exp <= now) return false; // token hết hạn

            // 3. Kiểm tra signature
            RemoteJWKSet<SecurityContext> jwkSet = new RemoteJWKSet<>(new URL(jwksUrl));

            // Lấy kid từ header JWT
            String kid = signedJWT.getHeader().getKeyID();

            // Chọn key phù hợp
            JWKMatcher matcher = new JWKMatcher.Builder()
                    .keyUse(KeyUse.SIGNATURE)
                    .keyID(kid)
                    .algorithm(JWSAlgorithm.RS256)
                    .build();

            // Use RemoteJWKSet.get(selector, context) to fetch matching keys
            List<JWK> keys = jwkSet.get(new JWKSelector(matcher), null);

            if (keys.isEmpty()) return false;

            RSAKey rsaKey = (RSAKey) keys.get(0);
            RSAPublicKey publicKey = rsaKey.toRSAPublicKey();
            boolean verified = signedJWT.verify(new com.nimbusds.jose.crypto.RSASSAVerifier(publicKey));

            return verified;

        } catch (ParseException | JOSEException | java.io.IOException e) {
            return false;
        }
    }

    public boolean validateExpirationToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            // Decode payload
            String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            var payload = new com.fasterxml.jackson.databind.ObjectMapper().readTree(payloadJson);

            long exp = payload.get("exp").asLong();
            long now = System.currentTimeMillis() / 1000;

            return exp > now;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3)
                throw new AppException(ErrorCode.TOKEN_INVALID);

            // Decode payload
            String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            var payload = new com.fasterxml.jackson.databind.ObjectMapper().readTree(payloadJson);

            return payload.get("sub").asText();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }
}