package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.request.CreateRoomRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.CheckRoomDTO;
import com.khangdjnh.edu_app.dto.response.JoinResponse;
import com.khangdjnh.edu_app.dto.response.RoomResponse;
import com.khangdjnh.edu_app.service.OnlineTeachingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class OnlineTeachingController {
    private final OnlineTeachingService onlineTeachingService;

    @PostMapping("")
    public ApiResponse<RoomResponse> createRoom(@RequestBody CreateRoomRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .message("Success")
                .code(1000)
                .result(onlineTeachingService.createRoom(request))
                .build();
    }

    @GetMapping("/{roomCode}/join")
    public ApiResponse<JoinResponse> joinRoom(
            @PathVariable String roomCode,
            @RequestParam Long userId
    ) {
        return ApiResponse.<JoinResponse>builder()
                .message("Success")
                .code(1000)
                .result(onlineTeachingService.joinRoom(roomCode, userId))
                .build();
    }

    @GetMapping("/check/{classId}")
    public ApiResponse<CheckRoomDTO> checkRoom(@PathVariable Long classId) {
        return ApiResponse.<CheckRoomDTO>builder()
                .message("Success")
                .code(1000)
                .result(onlineTeachingService.checkRoomActive(classId))
                .build();
    }
}
