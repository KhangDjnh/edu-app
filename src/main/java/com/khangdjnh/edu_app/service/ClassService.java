package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.classentity.ClassCreateRequest;
import com.khangdjnh.edu_app.dto.request.classentity.ClassUpdateRequest;
import com.khangdjnh.edu_app.dto.response.ClassResponse;
import com.khangdjnh.edu_app.entity.ClassEntity;
import com.khangdjnh.edu_app.enums.ClassType;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.mapper.ClassMapper;
import com.khangdjnh.edu_app.repository.ClassRepository;
import com.khangdjnh.edu_app.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassService {
    UserRepository userRepository;
    ClassRepository classRepository;
    ClassMapper classMapper;

    @Transactional(rollbackFor = Exception.class)
    public ClassResponse createClass(ClassCreateRequest request) {
        if(classRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.CLASS_EXISTED);
        }
        ClassEntity classEntity = classMapper.toClass(request);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userKeycloakId = authentication.getName();
        var user = userRepository.findByKeycloakUserId(userKeycloakId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        classEntity.setTeacher(user);
        ClassResponse classResponse = classMapper.toClassResponse(classRepository.save(classEntity));
        classResponse.setTeacherId(user.getId());
        classResponse.setTeacherName(user.getFirstName() + " " + user.getLastName());
        return classResponse;
    }

    @Transactional(readOnly = true)
    public ClassResponse getClassById(Long classId) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        ClassResponse classResponse = classMapper.toClassResponse(classEntity);
        classResponse.setTeacherId(classEntity.getTeacher().getId());
        classResponse.setTeacherName(classEntity.getTeacher().getFirstName() + " " + classEntity.getTeacher().getLastName());
        return classResponse;
    }

    @Transactional(readOnly = true)
    public List<ClassResponse> getAllClasses() {
        return classRepository.findAll().stream().map(classMapper::toClassResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ClassResponse> getSuggestedClasses() {
        return classRepository.findByClassTypeOrderByCreatedAtDesc(ClassType.COURSE)
                .stream()
                .map(classMapper::toClassResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClassResponse> getClassByTeacher() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userKeycloakId = authentication.getName();
        var teacher = userRepository.findByKeycloakUserId(userKeycloakId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return classRepository.findAllByTeacherIdOrderByCreatedAtDesc(teacher.getId()).stream().map(classMapper::toClassResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ClassResponse> searchClassesByKeyword(String keyword) {
        return classRepository.searchByKeyword(keyword).stream()
                .map(classMapper::toClassResponse)
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public ClassResponse updateClassById(Long id, ClassUpdateRequest request) {
        ClassEntity classEntity = classRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        classMapper.updateClassFromRequest(classEntity, request);
        return classMapper.toClassResponse(classRepository.save(classEntity));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteClassById(Long id) {
        if(!classRepository.existsById(id)) {
            throw new AppException(ErrorCode.CLASS_NOT_FOUND);
        }
        classRepository.deleteById(id);
    }
}
