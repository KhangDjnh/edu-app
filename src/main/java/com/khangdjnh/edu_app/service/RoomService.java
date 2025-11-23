package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.room.JoinRoomCreation;
import com.khangdjnh.edu_app.dto.request.room.RoomCreationRequest;
import com.khangdjnh.edu_app.dto.response.JoinRoomResponse;
import com.khangdjnh.edu_app.dto.response.RoomActiveResponse;
import com.khangdjnh.edu_app.dto.response.RoomResponse;
import com.khangdjnh.edu_app.entity.ClassEntity;
import com.khangdjnh.edu_app.entity.JoinRoomHistory;
import com.khangdjnh.edu_app.entity.Room;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.enums.JoinRoomStatus;
import com.khangdjnh.edu_app.enums.RoomStatus;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.mapper.RoomMapper;
import com.khangdjnh.edu_app.repository.ClassRepository;
import com.khangdjnh.edu_app.repository.JoinRoomHistoryRepository;
import com.khangdjnh.edu_app.repository.RoomRepository;
import com.khangdjnh.edu_app.repository.UserRepository;
import com.khangdjnh.edu_app.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final JoinRoomHistoryRepository joinRoomHistoryRepository;

    @Transactional(rollbackFor = Exception.class)
    public RoomResponse createRoom(RoomCreationRequest request) {
        Room newRoom = roomMapper.toRoom(request);
        newRoom.setCreatedBy(SecurityUtils.getCurrentUserEmail());
        newRoom.setRoomCode(generateRoomCode(request.getClassId()));
        newRoom.setStatus(RoomStatus.STARTED);
        newRoom.setIsActive(true);
        newRoom.setStartTime(LocalDateTime.now());
        newRoom = roomRepository.save(newRoom);

        //create join room history
        User user = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        JoinRoomHistory newJoinRoomHistory = JoinRoomHistory.builder()
                .roomId(newRoom.getId())
                .userId(user.getId())
                .joinedAt(LocalDateTime.now())
                .build();
        joinRoomHistoryRepository.save(newJoinRoomHistory);
        log.info("Room created: {}", newRoom.getRoomCode());
        return roomMapper.toRoomResponse(newRoom);
    }

    @Transactional(rollbackFor = Exception.class)
    public JoinRoomResponse joinRoom(JoinRoomCreation request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        JoinRoomHistory newJoinRoomHistory = JoinRoomHistory.builder()
                .roomId(room.getId())
                .userId(user.getId())
                .joinRoomStatus(JoinRoomStatus.ONLINE)
                .joinedAt(LocalDateTime.now())
                .build();
        newJoinRoomHistory = joinRoomHistoryRepository.save(newJoinRoomHistory);
        return toJoinRoomResponse(room, user, newJoinRoomHistory);
    }

    @Transactional(rollbackFor = Exception.class)
    public JoinRoomResponse leftRoom(JoinRoomCreation request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        JoinRoomHistory joinRoomHistory = joinRoomHistoryRepository
                .findTopByUserIdAndRoomIdOrderByIdDesc(request.getUserId(), request.getRoomId());
        joinRoomHistory.setLeftAt(LocalDateTime.now());
        joinRoomHistory.setJoinRoomStatus(JoinRoomStatus.OFFLINE);
        joinRoomHistory = joinRoomHistoryRepository.save(joinRoomHistory);
        return toJoinRoomResponse(room, user, joinRoomHistory);
    }

    @Transactional(rollbackFor = Exception.class)
    public RoomResponse calloutRoom(Long roomId){
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        room.setStatus(RoomStatus.FINISHED);
        room.setFinishTime(LocalDateTime.now());
        room = roomRepository.save(room);

        //set left out for all student in room
        LocalDateTime now = LocalDateTime.now();
        List<JoinRoomHistory> listUserHistoryInRoom = joinRoomHistoryRepository
                .findByRoomIdAndJoinRoomStatus(roomId, JoinRoomStatus.ONLINE);
        listUserHistoryInRoom.forEach(joinRoomHistory -> {
            joinRoomHistory.setLeftAt(now);
            joinRoomHistory.setJoinRoomStatus(JoinRoomStatus.OFFLINE);
        });
        joinRoomHistoryRepository.saveAll(listUserHistoryInRoom);
        return roomMapper.toRoomResponse(room);
    }

    @Transactional(readOnly = true)
    public RoomActiveResponse getActiveRoom(Long classId) {
        List<Room> listActiveRooms = roomRepository.findByClassIdAndStatus(classId, RoomStatus.STARTED);
        if (!listActiveRooms.isEmpty()) {
            return RoomActiveResponse.builder()
                    .hasRoom(true)
                    .listRooms(listActiveRooms
                            .stream()
                            .map(roomMapper::toRoomResponse)
                            .toList()
                    )
                    .build();
        }
        return RoomActiveResponse.builder().hasRoom(false).build();
    }

    private String generateRoomCode(Long classId) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        String classCode = classEntity.getClassCode();
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();
        long index = roomRepository.count();
        return classCode + String.format("%02d%02d%03d", month, year % 100, index + 1);
    }

    private JoinRoomResponse toJoinRoomResponse(Room room, User user, JoinRoomHistory joinRoomHistory) {
        return JoinRoomResponse.builder()
                .username(user.getUsername())
                .userEmail(user.getEmail())
                .roomName(room.getRoomName())
                .roomCode(room.getRoomCode())
                .joinedAt(joinRoomHistory.getJoinedAt())
                .leftAt(joinRoomHistory.getLeftAt())
                .build();
    }

}
