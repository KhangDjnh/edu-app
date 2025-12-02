package com.khangdjnh.edu_app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // nơi client SUBSCRIBE
        config.setApplicationDestinationPrefixes("/app"); // prefix khi client SEND
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // XÓA .withSockJS()
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }
}