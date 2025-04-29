package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.attendance.AttendanceCreateRequest;
import com.khangdjnh.edu_app.dto.request.attendance.AttendanceStudentRequest;
import com.khangdjnh.edu_app.dto.request.attendance.AttendanceUpdateRequest;
import com.khangdjnh.edu_app.dto.response.AttendanceCreateResponse;
import com.khangdjnh.edu_app.dto.response.AttendanceResponse;
import com.khangdjnh.edu_app.entity.Attendance;
import com.khangdjnh.edu_app.entity.ClassEntity;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.AttendanceRepository;
import com.khangdjnh.edu_app.repository.ClassRepository;
import com.khangdjnh.edu_app.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceService {
    AttendanceRepository attendanceRepository;
    ClassRepository classRepository;
    UserRepository userRepository;

    @Transactional
    public AttendanceCreateResponse createClassAttendance(AttendanceCreateRequest request) {
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        List<Long> attendanceIds = new ArrayList<>();

        for (AttendanceStudentRequest attendanceRequest : request.getAttendances()) {
            User student = userRepository.findById(attendanceRequest.getStudentId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            Attendance attendance = Attendance.builder()
                    .attendanceDate(request.getAttendanceDate())
                    .classEntity(classEntity)
                    .student(student)
                    .status(attendanceRequest.getStatus())
                    .build();

            Attendance savedAttendance = attendanceRepository.save(attendance);

            attendanceIds.add(savedAttendance.getId());
        }

        return AttendanceCreateResponse.builder()
                .classId(request.getClassId())
                .attendanceDate(request.getAttendanceDate())
                .attendanceIds(attendanceIds)
                .build();
    }

    public AttendanceResponse updateAttendance (AttendanceUpdateRequest request, Long id) {
        Attendance attendance = attendanceRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ATTENDANCE_NOT_FOUND));
        attendance.setStatus(request.getStatus());
        attendance.setAttendanceDate(request.getAttendanceDate());
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .classId(attendance.getClassEntity().getId())
                .studentId(attendance.getStudent().getId())
                .attendanceDate(request.getAttendanceDate())
                .status(request.getStatus())
                .build();
    }

    public AttendanceResponse getAttendanceById (Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ATTENDANCE_NOT_FOUND));
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .classId(attendance.getClassEntity().getId())
                .studentId(attendance.getStudent().getId())
                .attendanceDate(attendance.getAttendanceDate())
                .status(attendance.getStatus())
                .build();
    }

    public List<AttendanceResponse> getAllAttendanceInClass(Long classId , LocalDate attendanceDate) {
        return attendanceRepository
                .findByClassEntityIdAndAttendanceDate(classId, attendanceDate)
                .stream()
                .map(attendance -> AttendanceResponse.builder()
                        .id(attendance.getId())
                        .classId(attendance.getClassEntity().getId())
                        .studentId(attendance.getStudent().getId())
                        .attendanceDate(attendance.getAttendanceDate())
                        .status(attendance.getStatus())
                        .build())
                .toList();
    }

    public List<AttendanceResponse> getAllAttendanceInClassAndStudent(Long studentId, Long classId) {
        return attendanceRepository
                .findByStudentIdAndClassEntity_Id(studentId, classId)
                .stream()
                .map(attendance -> AttendanceResponse.builder()
                        .id(attendance.getId())
                        .classId(attendance.getClassEntity().getId())
                        .studentId(attendance.getStudent().getId())
                        .attendanceDate(attendance.getAttendanceDate())
                        .status(attendance.getStatus())
                        .build())
                .toList();
    }

}
