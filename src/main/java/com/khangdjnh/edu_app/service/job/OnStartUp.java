package com.khangdjnh.edu_app.service.job;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;

@Component
@Slf4j
public class OnStartUp {

    @PostConstruct
    public void initSchedule() {
        try {
            log.info("Application is starting!");
            log.info("Server's instant: {}", Instant.now());
            log.info("Server's time: {}", LocalDateTime.now());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
