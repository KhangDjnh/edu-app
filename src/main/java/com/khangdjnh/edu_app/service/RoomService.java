package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.room.JoinRoomCreation;
import com.khangdjnh.edu_app.dto.request.room.RoomCreationRequest;
import com.khangdjnh.edu_app.dto.response.JoinRoomResponse;
import com.khangdjnh.edu_app.dto.response.RoomActiveResponse;
import com.khangdjnh.edu_app.dto.response.RoomResponse;
import com.khangdjnh.edu_app.entity.*;
import com.khangdjnh.edu_app.enums.AttendanceStatus;
import com.khangdjnh.edu_app.enums.JoinRoomStatus;
import com.khangdjnh.edu_app.enums.LeaveRequestStatus;
import com.khangdjnh.edu_app.enums.RoomStatus;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.mapper.RoomMapper;
import com.khangdjnh.edu_app.repository.*;
import com.khangdjnh.edu_app.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final NotificationService notificationService;
    private final ClassStudentRepository classStudentRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    @Transactional(rollbackFor = Exception.class)
    public RoomResponse createRoom(RoomCreationRequest request) {
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        Room newRoom = roomMapper.toRoom(request);
        newRoom.setCreatedBy(SecurityUtils.getCurrentUserEmail());
        newRoom.setRoomCode(generateRoomCode(request.getClassId()));
        newRoom.setStatus(RoomStatus.STARTED);
        newRoom.setIsActive(true);
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

        // get data students in class
        List<ClassStudent> listStudents = classStudentRepository.findByClassEntity_Id(request.getClassId());
        List<User> students = listStudents.stream().map(ClassStudent::getStudent).toList();

        //create attendance in class
        List<Attendance> listAttendances = new ArrayList<>();
        List<LeaveRequest> leaveRequests = leaveRequestRepository
                .findByClassEntity_IdAndLeaveDateAndStatus(request.getClassId(), LocalDate.now(), LeaveRequestStatus.APPROVED);
        List<Long> studentIdsOnLeave = leaveRequests.stream()
                .map(leaveRequest -> leaveRequest.getStudent().getId())
                .toList();
        for (User student : students) {
            Attendance attendance = Attendance.builder()
                    .student(student)
                    .classEntity(classEntity)
                    .attendanceDate(LocalDate.now())
                    .status(AttendanceStatus.ABSENT)
                    .build();
            if(studentIdsOnLeave.contains(student.getId())) {
                attendance.setStatus(AttendanceStatus.PRESENT);
            }
            listAttendances.add(attendance);
        }
        attendanceRepository.saveAll(listAttendances);

        //notify to all students
        notificationService.sendNewRoomNotification(students, user, newRoom);
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

        //join room successfully -> update student's attendance
        Attendance attendance = attendanceRepository.findByStudentIdAndClassEntity_IdAndAttendanceDate(
                request.getUserId(),
                room.getClassId(),
                LocalDate.now()
        );
        if(attendance != null) {
            attendance.setStatus(AttendanceStatus.PRESENT);
            attendanceRepository.save(attendance);
        }
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
    public RoomResponse calloutRoom(Long roomId) {
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

    @Transactional(rollbackFor = Exception.class)
    public RoomResponse saveClassRoomPath(Long roomId, String path) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        room.setClassRoomPath(path);
        room = roomRepository.save(room);
        return roomMapper.toRoomResponse(room);
    }


    @Transactional(readOnly = true)
    public RoomActiveResponse getActiveRoom(Long classId) {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<Room> listActiveRooms = roomRepository.findByClassIdAndStatusAndExam(classId, RoomStatus.STARTED, null);
        listActiveRooms.forEach(room -> room.setClassRoomPath(generateClassRoomPathForUser(room.getClassRoomPath(), user.getId(), userEmail)));
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

    private String generateClassRoomPathForUser(String rootUrl, Long userId, String userName) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(rootUrl);

        return builder
                .replaceQueryParam("userId", userId)
                .replaceQueryParam("userName", userName)
                .build()
                .toUriString();
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
