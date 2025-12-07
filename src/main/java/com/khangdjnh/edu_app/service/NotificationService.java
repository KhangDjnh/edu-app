package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.message.NotificationMessage;
import com.khangdjnh.edu_app.entity.ClassPost;
import com.khangdjnh.edu_app.entity.Message;
import com.khangdjnh.edu_app.entity.Notice;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.enums.NoticeType;
import com.khangdjnh.edu_app.repository.NoticeRepository;
import com.khangdjnh.edu_app.util.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
                .senderUserName(SecurityUtils.getCurrentUsername())
                .senderUserEmail(SecurityUtils.getCurrentUserEmail())
                .senderUserFullName(SecurityUtils.getCurrentUserFullName())
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
                .senderUserName(SecurityUtils.getCurrentUsername())
                .senderUserEmail(SecurityUtils.getCurrentUsername())
                .senderUserFullName(SecurityUtils.getCurrentUserFullName())
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
                .senderUserName(SecurityUtils.getCurrentUsername())
                .senderUserEmail(SecurityUtils.getCurrentUserEmail())
                .senderUserFullName(SecurityUtils.getCurrentUserFullName())
                .build();

        noticeRepository.save(notice);

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + receiver.getId(),
                new NotificationMessage(content, receiver.getId())
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendNewMessageNotification(User receiver, Message message) {
        String content = "Bạn có tin nhắn mới từ " + receiver.getFirstName() + " " + receiver.getLastName()
                + ": " + message.getContent();

        Notice notice = Notice.builder()
                .receiver(receiver)
                .content(content)
                .createdAt(LocalDateTime.now())
                .read(false)
                .type(NoticeType.NEW_MESSAGE)
                .senderUserName(SecurityUtils.getCurrentUsername())
                .senderUserEmail(SecurityUtils.getCurrentUserEmail())
                .senderUserFullName(SecurityUtils.getCurrentUserFullName())
                .build();

        noticeRepository.save(notice);

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + receiver.getId(),
                new NotificationMessage(content, receiver.getId())
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendNewPostNotification(List<User> receivers, ClassPost post) {
        String content = "Lớp học " + post.getClassEntity().getName() + " có bài đăng mới! Có tiêu đề"
                + ": " + post.getPostTitle();
        List<Notice> listNotices = new ArrayList<>();
        for(User receiver : receivers) {
            Notice notice = Notice.builder()
                    .receiver(receiver)
                    .content(content)
                    .createdAt(LocalDateTime.now())
                    .read(false)
                    .type(NoticeType.NEW_POST)
                    .senderUserName(SecurityUtils.getCurrentUsername())
                    .senderUserEmail(SecurityUtils.getCurrentUserEmail())
                    .senderUserFullName(SecurityUtils.getCurrentUserFullName())
                    .build();
            listNotices.add(notice);
            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + receiver.getId(),
                    new NotificationMessage(content, receiver.getId())
            );
        }

        noticeRepository.saveAll(listNotices);
    }

}