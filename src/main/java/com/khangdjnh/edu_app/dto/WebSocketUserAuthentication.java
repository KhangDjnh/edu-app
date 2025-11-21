package com.khangdjnh.edu_app.dto;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

// Để lưu token + thông tin user trong session WebSocket
public class WebSocketUserAuthentication implements Authentication {
    private final String token;
    private final String username;

    public WebSocketUserAuthentication(String token, String username) {
        this.token = token;
        this.username = username;
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); }
    @Override public Object getCredentials() { return null; }
    @Override public Object getDetails() { return token; }
    @Override public Object getPrincipal() { return username; }
    @Override public boolean isAuthenticated() { return true; }
    @Override public void setAuthenticated(boolean isAuthenticated) {}
    @Override public String getName() { return username; }
}
