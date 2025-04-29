package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.LoginRequest;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.keycloak.UserAccessTokenExchangeParam;
import com.khangdjnh.edu_app.keycloak.UserTokenExchangeResponse;
import com.khangdjnh.edu_app.repository.IdentityClient;
import com.khangdjnh.edu_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class KeycloakUserTokenService {

    private final IdentityClient identityClient;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate; // inject Redis

    @Value("${idp.client-id}")
    private String clientId;

    @Value("${idp.client-secret}")
    private String clientSecret;

    private static final String TOKEN_PREFIX = "user_token:";

    public String getAccessToken(LoginRequest request) {
        String cacheKey = TOKEN_PREFIX + request.getEmail();
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        String cachedToken = ops.get(cacheKey);
        if (cachedToken != null) {
            return cachedToken;
        }

        // Nếu không có cache, thì call Keycloak lấy token
        String token = fetchTokenFromKeycloak(request);

        // Cache token lại với TTL (ví dụ 4 phút, tùy theo expires_in từ Keycloak)
        ops.set(cacheKey, token, 15, TimeUnit.MINUTES);

        return token;
    }

    private String fetchTokenFromKeycloak(LoginRequest request) {
        UserAccessTokenExchangeParam param = UserAccessTokenExchangeParam.builder()
                .grant_type("password")
                .client_id(clientId)
                .client_secret(clientSecret)
                .username(getKeycloakUsername(request.getEmail()))
                .password(request.getPassword())
                .scope("openid")
                .build();

        UserTokenExchangeResponse response = identityClient.exchangeUserAccessToken(param);
        return response.getAccessToken();
    }

    private String getKeycloakUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return user.getUsername();
    }
}
