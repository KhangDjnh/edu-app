package com.khangdjnh.edu_app.service.job;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;

@Component
@Slf4j
public class OnStartUp {

    @PostConstruct
    public void onStartup() throws IOException {
        log.info("Server's time: {}", Instant.now());
        log.info("Local Time: {}", LocalDateTime.now());
        initUploadDir();
    }

    private void initUploadDir() throws IOException {
        String uploadDir = System.getenv("UPLOAD_DIR");
        if (uploadDir == null) uploadDir = "uploads/assignments/";
        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("Created upload directory at: {}", path.toAbsolutePath());
        }
    }
}
