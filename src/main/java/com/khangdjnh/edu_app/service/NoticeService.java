package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.message.MessageResponse;
import com.khangdjnh.edu_app.entity.Notice;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.NoticeRepository;
import com.khangdjnh.edu_app.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoticeService {
    NoticeRepository noticeRepository;
    UserRepository userRepository;

    @Transactional(readOnly = true)
    public MessageResponse getNoticeById (Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOTICE_NOT_FOUND));
        return MessageResponse.builder()
                .id(notice.getId())
                .content(notice.getContent())
                .createAt(notice.getCreatedAt())
                .read(notice.getRead())
                .type(notice.getType())
                .senderUserName(notice.getSenderUserName())
                .senderUserEmail(notice.getSenderUserEmail())
                .senderUserFullName(notice.getSenderUserFullName())
                .entityType(notice.getEntityType())
                .entityId(notice.getEntityId())
                .build();
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getAllNoticesByUserId (Long userId) {
        if(!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return noticeRepository.findAllByReceiverIdOrderByReadAscCreatedAtDesc(userId)
                .stream()
                .map(notice -> MessageResponse.builder()
                        .id(notice.getId())
                        .content(notice.getContent())
                        .createAt(notice.getCreatedAt())
                        .read(notice.getRead())
                        .type(notice.getType())
                        .senderUserName(notice.getSenderUserName())
                        .senderUserEmail(notice.getSenderUserEmail())
                        .senderUserFullName(notice.getSenderUserFullName())
                        .entityType(notice.getEntityType())
                        .entityId(notice.getEntityId())
                        .build())
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public void markNoticeAsRead (Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOTICE_NOT_FOUND));
        notice.setRead(true);
        noticeRepository.save(notice);
    }

    @Transactional(readOnly = true)
    public long getAllUnreadNoticeCount(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return noticeRepository.countByReceiverAndRead(user, false);
    }
}
