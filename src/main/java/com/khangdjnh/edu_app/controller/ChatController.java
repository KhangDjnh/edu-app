package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.chat.MessageCreationRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    public final ChatService chatService;

    @GetMapping("/conversation")
    public ApiResponse<?> getAllChatConversations(
            @RequestParam Long userId,
            @RequestParam(required = false) Long before,
            @RequestParam(defaultValue = "20") int limit
    ){
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(chatService.getAllChatConversation(userId, before, limit))
                .build();
    }

    @GetMapping("/messages")
    public ApiResponse<?> getMessageFromConversation(
            @RequestParam Long conversationId,
            @RequestParam(required = false) Long before,
            @RequestParam(defaultValue = "20") int limit
    ){
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(chatService.getMessageFromConversation(conversationId, before, limit))
                .build();
    }

    @PutMapping("/conversation")
    public ApiResponse<?> getConversation(
            @RequestParam Long currentUserId,
            @RequestParam Long toUserId
    ){
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(chatService.getConversation(currentUserId, toUserId))
                .build();
    }

    @PostMapping(value = "/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> sendMessage(@ModelAttribute @Valid MessageCreationRequest request){
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(chatService.sendMessage(request))
                .build();
    }

    @DeleteMapping("messages/{messageId}")
    public ApiResponse<?> deleteConversation(@PathVariable Long messageId){
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(chatService.deleteMessage(messageId))
                .build();
    }
}
