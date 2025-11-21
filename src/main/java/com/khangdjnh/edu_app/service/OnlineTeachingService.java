package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.ICEServer;
import com.khangdjnh.edu_app.dto.request.CreateRoomRequest;
import com.khangdjnh.edu_app.dto.response.CheckRoomDTO;
import com.khangdjnh.edu_app.dto.response.JoinResponse;
import com.khangdjnh.edu_app.dto.response.RoomResponse;
import com.khangdjnh.edu_app.entity.ClassEntity;
import com.khangdjnh.edu_app.entity.Room;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.enums.RoomStatus;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.ClassRepository;
import com.khangdjnh.edu_app.repository.RoomRepository;
import com.khangdjnh.edu_app.repository.UserRepository;
import com.khangdjnh.edu_app.util.SecurityUtils;
import com.khangdjnh.edu_app.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OnlineTeachingService {

    private final RoomRepository roomRepository;
    private final ClassRepository classRepository;
    private final UserRepository userRepository;

    @Transactional(rollbackFor = Exception.class)
    public RoomResponse createRoom(CreateRoomRequest request) {
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String classCode = classEntity.getClassCode();
        int roomCount = roomRepository.countByClassId(request.getClassId());
        String roomCode = classCode + String.format("%02d%02d%03d", year % 100, month, roomCount + 1);
        Room room = Room.builder()
                .classId(classEntity.getId())
                .roomCode(roomCode)
                .teacherId(request.getTeacherId())
                .roomName(request.getRoomName())
                .startTime(LocalDateTime.now())
                .subject(teacher.getPrimarySubject())
                .isActive(true)
                .description(request.getDescription())
                .status(RoomStatus.STARTED)
                .createdAt(LocalDateTime.now())
                .createdBy(SecurityUtils.getCurrentUsername())
                .build();
        roomRepository.save(room);
        return toRoomResponse(room);
    }

    public JoinResponse joinRoom(String roomCode, Long userId) {
        Room room = roomRepository.findByRoomCode(roomCode);
        if (room == null) {
            throw new AppException(ErrorCode.ROOM_NOT_FOUND);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String accessToken = SecurityUtils.getAccessToken();
        return JoinResponse.builder()
                .signalingUrl("http://localhost:8080/ws")
                .roomId(room.getId())
                .userId(userId)
                .token(accessToken)
                .userRole(StringUtil.getStringValue(user.getRole()))
                .iceServers(
                        List.of(
                                new ICEServer("stun:stun.l.google.com:19302"),
                                new ICEServer("turn:turn.edu.com:3478", "user", "pass")
                        )
                )
                .maxParticipants(50)
                .metadata(
                        Map.of(
                                "roomName", room.getRoomName(),
                                "subject", room.getSubject(),
                                "teacherId", room.getTeacherId()
                        )
                )
                .build();
    }

    public CheckRoomDTO checkRoomActive(Long classId) {
        List<Room> listActiveRooms = roomRepository.findByClassIdAndStatus(classId, RoomStatus.STARTED);
        if (!listActiveRooms.isEmpty()) {
            return CheckRoomDTO.builder()
                .hasRoom(true)
                .listRooms(
                    listActiveRooms.stream()
                        .map(this::toRoomResponse)
                        .toList()
                    )
                .build();
        }
        return CheckRoomDTO.builder()
                .hasRoom(false)
                .build();
    }

    private RoomResponse toRoomResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomCode(room.getRoomCode())
                .roomName(room.getRoomName())
                .teacherId(room.getTeacherId())
                .classId(room.getClassId())
                .subject(room.getSubject())
                .startTime(room.getStartTime())
                .description(room.getDescription())
                .status(room.getStatus())
                .createdAt(room.getCreatedAt())
                .createdBy(room.getCreatedBy())
                .build();
    }
}
