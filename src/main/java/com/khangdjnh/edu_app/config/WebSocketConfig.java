package com.khangdjnh.edu_app.config;

import com.khangdjnh.edu_app.dto.WebSocketUserAuthentication;
import com.khangdjnh.edu_app.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtils jwtUtils;

    public WebSocketConfig(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // nơi client SUBSCRIBE
        config.setApplicationDestinationPrefixes("/app"); // prefix khi client SEND
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:3000")
                .addInterceptors(new TokenHandshakeInterceptor())
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request,
                                                      WebSocketHandler wsHandler,
                                                      Map<String, Object> attributes) {
                        // Trả về auth từ interceptor
                        return (Principal) attributes.get("user");
                    }
                })
                .withSockJS();
    }

    // Nếu dùng Spring Security thì cần thêm cái này nữa
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("*");
            }
        };
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                // Chỉ xử lý gói CONNECT từ client
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String jwt = authHeader.substring(7);

                        try {
                            // Validate + parse JWT (bạn đang dùng JwtUtils của mình)
                            if (jwtUtils.validateToken(jwt)) {

                                // Lấy username/subject từ token (tùy bạn config trong JwtUtils)
                                String username = jwtUtils.getUsernameFromToken(jwt);
                                // Hoặc nếu bạn dùng sub (Keycloak default)
                                // String username = jwtUtils.getSubjectFromToken(jwt);

                                // Tạo Principal (có thể là UsernamePasswordAuthenticationToken)
                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(
                                                username,           // Principal (username hoặc userId)
                                                null,               // credentials (không cần vì đã có token)
                                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                                        );

                                // Gán thêm thông tin JWT vào details nếu cần dùng sau này
                                authentication.setDetails(new WebSocketUserAuthentication(jwt, username));

                                // *** QUAN TRỌNG: Gắn Principal vào STOMP session ***
                                accessor.setUser(authentication);

                                // Log để debug
                                log.info("WebSocket authenticated user: {}", username);
                            } else {
                                log.warn("Invalid JWT token from WebSocket client");
                                accessor.setUser(null); // từ chối
                            }
                        } catch (Exception e) {
                            log.error("JWT validation failed: {}", e.getMessage());
                            accessor.setUser(null);
                        }
                    } else {
                        // Không có token → có thể cho guest hoặc từ chối
                        log.warn("WebSocket CONNECT without Authorization header");
                        // accessor.setUser(null); // nếu bắt buộc login
                    }
                }

                return message;
            }
        });
    }
}