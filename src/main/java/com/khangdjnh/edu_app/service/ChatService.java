package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.chat.*;
import com.khangdjnh.edu_app.dto.response.FileRecordResponse;
import com.khangdjnh.edu_app.entity.Conversation;
import com.khangdjnh.edu_app.entity.FileRecord;
import com.khangdjnh.edu_app.entity.Message;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.ConversationRepository;
import com.khangdjnh.edu_app.repository.FileRecordRepository;
import com.khangdjnh.edu_app.repository.MessageRepository;
import com.khangdjnh.edu_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final FileRecordRepository fileRecordRepository;
    private final CloudflareR2Service cloudflareR2Service;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<ConversationResponse> getAllChatConversation(Long userId, Long cursor, int limit) {
        User user = getUserById(userId);
        log.info("get all chat by {}", user.getUsername());
        Pageable pageable = PageRequest.of(0, limit);
        List<Conversation> listConversation = conversationRepository.loadConversation(userId, cursor, pageable);
        Map<Long, Long> toUserIdMapByConv = new HashMap<>();
        listConversation.forEach(conversation -> {
            if (conversation.getFirstUser().equals(userId)) {
                toUserIdMapByConv.put(conversation.getId(), conversation.getSecondUser());
            } else {
                toUserIdMapByConv.put(conversation.getId(), conversation.getFirstUser());
            }
        });
        List<User> listToUsers = userRepository.findAllById(toUserIdMapByConv.values());
        Map<Long, User> mapToUser = listToUsers.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        return listConversation.stream()
                .map(conversation ->
                        toConversationResponse(
                                conversation,
                                mapToUser.get(toUserIdMapByConv.get(conversation.getId()))
                        )
                ).toList();
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getMessageFromConversation(Long conversationId, Long cursor, int limit) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));
        Long firstUserId = conversation.getFirstUser();
        User firstUser = getUserById(firstUserId);
        Long secondUserId = conversation.getSecondUser();
        User secondUser = getUserById(secondUserId);
        Pageable pageable = PageRequest.of(0, limit);
        List<Message> listMessages = messageRepository.loadMessages(conversationId, cursor, pageable);
        List<MessageResponseDTO> result = new ArrayList<>();
        for (Message message : listMessages) {
            Long sender = message.getSender();
            if (sender.equals(firstUserId)) {
                result.add(toMessageResponseDTO(message, firstUser));
            } else {
                result.add(toMessageResponseDTO(message, secondUser));
            }
        }
        return result;
    }

    @Transactional
    public List<MessageResponseDTO> getConversation(Long currentUserId, Long toUserId) {
        User firstUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        User secondUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Conversation conversation = conversationRepository.findByFirstUserIdAndSecondUserId(currentUserId, toUserId);
        if (conversation == null) {
            conversation = Conversation.builder()
                    .isActive(true)
                    .firstUser(currentUserId)
                    .secondUser(toUserId)
                    .build();
            conversation = conversationRepository.save(conversation);
        }
        int limit = 20;
        Pageable pageable = PageRequest.of(0, limit);
        List<Message> listMessages = messageRepository.loadMessages(conversation.getId(), null, pageable);
        List<MessageResponseDTO> result = new ArrayList<>();
        for (Message message : listMessages) {
            Long sender = message.getSender();
            if (sender.equals(currentUserId)) {
                result.add(toMessageResponseDTO(message, firstUser));
            } else {
                result.add(toMessageResponseDTO(message, secondUser));
            }
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public MessageResponseDTO sendMessage(MessageCreationRequest request) {
        Long senderId = request.getSender();
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));
        User receiver = userRepository.findById(
                Objects.equals(senderId, conversation.getFirstUser())
                        ? conversation.getSecondUser()
                        : conversation.getFirstUser()
        ).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        MultipartFile file = request.getFile();
        FileRecordResponse fileRecord = file == null ? null : cloudflareR2Service.uploadFile(file);

        Message message = Message.builder()
                .sender(senderId)
                .conversationId(conversation.getId())
                .content(request.getContent())
                .attachFileId(fileRecord == null ? null : fileRecord.getId())
                .replyTo(request.getReplyTo())
                .emotion(null)
                .createdAt(LocalDateTime.now())
                .build();

        message = messageRepository.save(message);

        MessageResponseDTO messageResponseDTO = toMessageResponseDTO(message, sender);

        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        messagingTemplate.convertAndSend("/topic/conversation/" + conversation.getId(), messageResponseDTO);

        notificationService.sendNewMessageNotification(receiver, message);
        return messageResponseDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    public MessageResponseDTO deleteMessage(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));
        User sender = userRepository.findById(message.getSender())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        message.setEmotion(null);
        message.setContent("Tin nhắn lại đã bị xóa!");
        message.setAttachFileId(null);
        message.setEmotion(null);
        message.setReplyTo(null);
        message = messageRepository.save(message);
        return toMessageResponseDTO(message, sender);
    }

    // To Response Method
    private MessageResponseDTO toMessageResponseDTO(Message message, User sender) {
        return MessageResponseDTO.builder()
                .id(message.getId())
                .sender(toMessageUserDTO(sender))
                .content(message.getContent())
                .attachFileId(message.getAttachFileId() == null ? null : getFileFromMessage(message))
                .replyTo(message.getReplyTo() == null ? null : toMessageReplyDTO(message))
                .emotion(message.getEmotion())
                .createdAt(message.getCreatedAt())
                .build();
    }

    private FileRecordResponse getFileFromMessage(Message message) {
        Long fileRecordId = message.getAttachFileId();
        FileRecord file = fileRecordRepository.findById(fileRecordId)
                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND));
        return FileRecordResponse.builder()
                .fileName(file.getFileName())
                .fileType(file.getFileType())
                .fileSize(file.getFileSize())
                .fileUrl(file.getFileUrl())
                .folder(file.getFolder())
                .uploadedAt(file.getUploadedAt())
                .uploadedBy(file.getUploadedBy())
                .build();
    }

    private ConversationResponse toConversationResponse(Conversation conversation, User toUser) {
        return ConversationResponse.builder()
                .id(conversation.getId())
                .toUser(toMessageUserDTO(toUser))
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private MessageUserDTO toMessageUserDTO(User user) {
        return MessageUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatar(user.getAvatar())
                .build();
    }

    private MessageReplyDTO toMessageReplyDTO(Message replyMessage) {
        Long previousMessageId = replyMessage.getReplyTo();
        Message previousMessage = messageRepository.findById(previousMessageId)
                .orElseThrow(() -> new AppException(ErrorCode.REPLY_TO_MESSAGE_NOT_FOUND));
        User sender = getUserById(previousMessage.getSender());
        return MessageReplyDTO.builder()
                .id(previousMessage.getId())
                .sender(toMessageUserDTO(sender))
                .content(previousMessage.getContent())
                .build();
    }
}
