package com.khangdjnh.edu_app.keycloak;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRefreshTokenExchangeParam {
    String grant_type;
    String client_id;
    String client_secret;
    String refresh_token;
    String scope;
}
