package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.request.room.JoinRoomCreation;
import com.khangdjnh.edu_app.dto.request.room.RoomCreationRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/create")
    public ApiResponse<?> createRoom (@RequestBody @Valid RoomCreationRequest request){
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(roomService.createRoom(request))
                .build();
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    @PostMapping("/join")
    public ApiResponse<?> joinRoom (@RequestBody @Valid JoinRoomCreation request){
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(roomService.joinRoom(request))
                .build();
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    @PostMapping("/leave")
    public ApiResponse<?> leaveRoom (@RequestBody @Valid JoinRoomCreation request){
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(roomService.leftRoom(request))
                .build();
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    @PutMapping("/{roomId}/callout")
    public ApiResponse<?> calloutRoom (@PathVariable Long roomId){
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(roomService.calloutRoom(roomId))
                .build();
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    @GetMapping("/class/{classId}/check")
    public ApiResponse<?> checkRoom (@PathVariable Long classId){
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(roomService.getActiveRoom(classId))
                .build();
    }

}
