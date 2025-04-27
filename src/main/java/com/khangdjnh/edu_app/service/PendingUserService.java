package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.UserCreateRequest;
import com.khangdjnh.edu_app.entity.PendingUser;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.PendingUserRepository;
import com.khangdjnh.edu_app.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PendingUserService {
    PendingUserRepository pendingUserRepository;
    UserRepository userRepository;
    EmailService emailService;
    PasswordEncoder passwordEncoder;

    public void createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED_IN_DATABASE);
        }
        if (pendingUserRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED_IN_PENDING);
        }

        // 1. Generate random token
        String token = UUID.randomUUID().toString();

        // 2. Save pending user
        PendingUser pendingUser = PendingUser.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dob(request.getDob())
                .confirmationToken(token)
                .tokenExpiration(LocalDateTime.now().plusHours(24))
                .build();
        pendingUserRepository.save(pendingUser);

        // 3. Send confirmation email
        emailService.sendConfirmationEmail(request.getEmail(), token);
    }
}
