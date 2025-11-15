package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.config.FeignConfig;
import com.khangdjnh.edu_app.keycloak.*;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "identity-client",
        url = "${idp.url}",
        configuration = FeignConfig.class
)
public interface IdentityClient {
    @PostMapping(
            value = "/realms/{realm}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ClientTokenExchangeResponse exchangeClientToken(@QueryMap ClientTokenExchangeParam tokenExchangeParam, @PathVariable ("realm") String realm);

    @PostMapping(
            value = "/realms/{realm}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    UserTokenExchangeResponse exchangeUserAccessToken(@QueryMap UserAccessTokenExchangeParam tokenExchangeParam, @PathVariable ("realm") String realm);

    @PostMapping(
            value = "/realms/{realm}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    UserTokenExchangeResponse exchangeUserRefreshToken(@QueryMap UserRefreshTokenExchangeParam tokenExchangeParam, @PathVariable ("realm") String realm);

    @PostMapping(
            value = "/admin/realms/{realm}/users",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(
            @RequestHeader("Authorization") String token,
            @PathVariable("realm") String realm,
            @RequestBody UserCreationParam userCreationParam);

    @PutMapping("/admin/realms/{realm}/users/{id}/reset-password")
    void resetUserPassword(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable("realm") String realm,
            @PathVariable("id") String userId,
            @RequestBody Credential credential
    );
    @GetMapping("/admin/realms/{realm}/roles/{roleName}")
    ResponseEntity<Map<String, Object>> getRoleByName(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable("realm") String realm,
            @PathVariable("roleName") String roleName
    );

    @PostMapping("/admin/realms/{realm}/users/{userId}/role-mappings/realm")
    ResponseEntity<Void> assignRoleToUser(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable("realm") String realm,
            @PathVariable("userId") String userId,
            @RequestBody List<Map<String, Object>> roles
    );
}
