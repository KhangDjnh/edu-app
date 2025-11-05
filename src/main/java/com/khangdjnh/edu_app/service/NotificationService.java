package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.message.NotificationMessage;
import com.khangdjnh.edu_app.entity.Notice;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.enums.NoticeType;
import com.khangdjnh.edu_app.repository.NoticeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {

    SimpMessagingTemplate messagingTemplate;
    NoticeRepository noticeRepository;

    @Transactional(rollbackFor = Exception.class)
    public void sendLeaveNotice(User receiver, String content) {
        Notice notice = Notice.builder()
                .receiver(receiver)
                .content(content)
                .createdAt(LocalDateTime.now())
                .read(false)
                .type(NoticeType.LEAVE_REQUEST)
                .build();

        noticeRepository.save(notice);

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + receiver.getId(),
                new NotificationMessage(content, receiver.getId())
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendNewAssignmentNotice(User receiver, String assignmentTitle) {
        String content = "Bài tập mới: " + assignmentTitle;

        Notice notice = Notice.builder()
                .receiver(receiver)
                .content(content)
                .createdAt(LocalDateTime.now())
                .read(false)
                .type(NoticeType.ASSIGNMENT_NEW)
                .build();

        noticeRepository.save(notice);

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + receiver.getId(),
                new NotificationMessage(content, receiver.getId())
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendAssignmentDeadlineNotice(User receiver, String assignmentTitle, LocalDateTime deadline) {
        String content = "Bài tập \"" + assignmentTitle + "\" sắp hết hạn lúc " + deadline.toString();

        Notice notice = Notice.builder()
                .receiver(receiver)
                .content(content)
                .createdAt(LocalDateTime.now())
                .read(false)
                .type(NoticeType.ASSIGNMENT_DEADLINE)
                .build();

        noticeRepository.save(notice);

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + receiver.getId(),
                new NotificationMessage(content, receiver.getId())
        );
    }


}