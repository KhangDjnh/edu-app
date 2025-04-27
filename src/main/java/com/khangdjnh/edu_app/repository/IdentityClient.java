package com.khangdjnh.edu_app.repository;


import com.khangdjnh.edu_app.keycloak.*;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IdentityClient {
    @PostMapping(
            value = "/realms/education-service/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ClientTokenExchangeResponse exchangeClientToken(@QueryMap ClientTokenExchangeParam tokenExchangeParam);

    @PostMapping(
            value = "/realms/education-service/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    UserTokenExchangeResponse exchangeUserAccessToken(@QueryMap UserAccessTokenExchangeParam tokenExchangeParam);

    @PostMapping(
            value = "/realms/education-service/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    UserTokenExchangeResponse exchangeUserRefreshToken(@QueryMap UserRefreshTokenExchangeParam tokenExchangeParam);

    @PostMapping(
            value = "/admin/realms/education-service/users",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(
            @RequestHeader("Authorization") String token,
            @RequestBody UserCreationParam userCreationParam);

    @PutMapping("/admin/realms/{realm}/users/{id}/reset-password")
    void resetUserPassword(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable("realm") String realm,
            @PathVariable("id") String userId,
            @RequestBody Credential credential
    );
}
