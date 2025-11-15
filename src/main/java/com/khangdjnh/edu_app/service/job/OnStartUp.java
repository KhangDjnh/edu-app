package com.khangdjnh.edu_app.service.job;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OnStartUp {

    @Autowired
    private Keycloak keycloakAdmin;

    @Value("${idp.realm}")
    private String realm;

    @PostConstruct
    public void onStartup() throws IOException {
        testLogin();
//        initUploadDir();
    }



    private void initUploadDir() throws IOException {
        log.info("Server's instant: {}", Instant.now());
        log.info("Server's time: {}", LocalDateTime.now());
        String uploadDir = System.getenv("UPLOAD_DIR");
        if (uploadDir == null) uploadDir = "uploads/assignments/";
        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("Created upload directory at: {}", path.toAbsolutePath());
        }
    }

    private void testLogin() {
        try {
            List<RoleRepresentation> roles = keycloakAdmin.realm(realm).roles().list();
            System.out.println("Đăng nhập thành công! Số role trong realm: " + roles.size());
        } catch (Exception e) {
            System.err.println("Không thể đăng nhập Keycloak: " + e.getMessage());
        }
    }
}
