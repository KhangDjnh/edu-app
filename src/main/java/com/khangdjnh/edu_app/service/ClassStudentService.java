package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.classstudent.StudentJoinClassRequest;
import com.khangdjnh.edu_app.dto.response.ClassResponse;
import com.khangdjnh.edu_app.dto.response.StudentJoinClassResponse;
import com.khangdjnh.edu_app.dto.response.UserAttendanceResponse;
import com.khangdjnh.edu_app.dto.response.UserResponse;
import com.khangdjnh.edu_app.entity.ClassEntity;
import com.khangdjnh.edu_app.entity.ClassStudent;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.mapper.ClassMapper;
import com.khangdjnh.edu_app.mapper.UserMapper;
import com.khangdjnh.edu_app.repository.ClassRepository;
import com.khangdjnh.edu_app.repository.ClassStudentRepository;
import com.khangdjnh.edu_app.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassStudentService {
    ClassRepository classRepository;
    UserRepository userRepository;
    ClassStudentRepository classStudentRepository;
    UserMapper userMapper;
    ClassMapper classMapper;

    @Transactional(rollbackFor = Exception.class)
    public StudentJoinClassResponse createClassStudent(StudentJoinClassRequest request) {
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        User student = userRepository.findByUsername(request.getStudentUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        ClassStudent classStudent = new ClassStudent();
        classStudent.setClassEntity(classEntity);
        classStudent.setStudent(student);
        classStudent.setIsConfirmed(true);
        classStudentRepository.save(classStudent);
        return StudentJoinClassResponse.builder()
                .classId(request.getClassId())
                .studentId(student.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getStudentsByClassId(Long classId) {
        if(!classStudentRepository.existsByClassEntity_Id(classId)) {
            throw new AppException(ErrorCode.CLASS_NOT_FOUND);
        }
        List<ClassStudent> classStudents = classStudentRepository.findByClassEntity_IdAndIsConfirmed(classId, true);

        return classStudents.stream()
                .map(classStudent -> userMapper.toUserResponse(classStudent.getStudent()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserAttendanceResponse> getStudentAttendancesByClassId(Long classId) {
        if(!classStudentRepository.existsByClassEntity_Id(classId)) {
            throw new AppException(ErrorCode.CLASS_NOT_FOUND);
        }
        List<ClassStudent> classStudents = classStudentRepository.findByClassEntity_IdAndIsConfirmed(classId, true);

        return classStudents.stream()
                .map(this::toUserAttendanceResponse)
                .toList();
    }

    private UserAttendanceResponse toUserAttendanceResponse (ClassStudent classStudent) {
        return UserAttendanceResponse.builder()
                .presentNumber(classStudent.getPresentNumber())
                .lateNumber(classStudent.getLateNumber())
                .absenceNumber(classStudent.getAbsenceNumber())
                .userResponse(userMapper.toUserResponse(classStudent.getStudent()))
                .build();
    }

    @Transactional(readOnly = true)
    public List<ClassResponse> getAllClassesStudentIn(Long studentId) {
        List<ClassStudent> classStudents = classStudentRepository.findByStudent_IdOrderByJoinAtDesc(studentId);
        if(classStudents == null || classStudents.isEmpty()) {
            return new ArrayList<>();
        }
        return classStudents.stream()
                .map(classStudent -> classMapper.toClassResponse(classStudent.getClassEntity()))
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public StudentJoinClassResponse joinClassByCode(String code) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userKeycloakId = authentication.getName();
        var student = userRepository.findByKeycloakUserId(userKeycloakId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        ClassEntity classEntity = classRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        ClassStudent classStudent = new ClassStudent();
        classStudent.setClassEntity(classEntity);
        classStudent.setStudent(student);
        classStudent.setIsConfirmed(false);
        classStudentRepository.save(classStudent);
        return StudentJoinClassResponse.builder()
                .classId(classEntity.getId())
                .studentId(student.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllStudentsIsNotConfirmed(Long classId) {
        return classStudentRepository.findByClassEntity_IdAndIsConfirmed(classId, false)
                .stream()
                .map(classStudent -> userMapper.toUserResponse(classStudent.getStudent()))
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public void confirmStudentInClass(Long classId, Long studentId) {
        ClassStudent classStudent = classStudentRepository.findByClassEntity_IdAndStudent_Id(classId, studentId);
        if(classStudent == null) {
            throw new AppException(ErrorCode.CLASS_STUDENT_NOT_FOUND);
        }
        if(classStudent.getIsConfirmed()) {
            throw new AppException(ErrorCode.CLASS_STUDENT_ALREADY_CONFIRMED);
        }
        classStudent.setIsConfirmed(true);
        classStudentRepository.save(classStudent);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteClassStudent(Long classId, Long studentId) {
        ClassStudent classStudent = classStudentRepository.findByClassEntity_IdAndStudent_Id(classId, studentId);
        classStudentRepository.delete(classStudent);
    }

}
