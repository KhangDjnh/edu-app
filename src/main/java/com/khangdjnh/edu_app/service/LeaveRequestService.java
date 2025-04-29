package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.LeaveRequestCreate;
import com.khangdjnh.edu_app.dto.response.LeaveRequestResponse;
import com.khangdjnh.edu_app.entity.ClassEntity;
import com.khangdjnh.edu_app.entity.LeaveRequest;
import com.khangdjnh.edu_app.enums.LeaveRequestStatus;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.ClassRepository;
import com.khangdjnh.edu_app.repository.LeaveRequestRepository;
import com.khangdjnh.edu_app.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LeaveRequestService {
    LeaveRequestRepository leaveRequestRepository;
    UserRepository userRepository;
    ClassRepository classRepository;

    //Create leave request
    public LeaveRequestResponse createLeaveRequest(LeaveRequestCreate request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userKeycloakId = authentication.getName();
        var user = userRepository.findByKeycloakUserId(userKeycloakId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        LeaveRequest leaveRequest = LeaveRequest.builder()
                .classEntity(classEntity)
                .student(user)
                .reason(request.getReason())
                .leaveDate(request.getLeaveDate())
                .status(LeaveRequestStatus.PENDING)
                .build();
        leaveRequestRepository.save(leaveRequest);
        return LeaveRequestResponse.builder()
                .id(leaveRequest.getId())
                .classId(request.getClassId())
                .studentId(user.getId())
                .leaveDate(request.getLeaveDate())
                .reason(request.getReason())
                .status(LeaveRequestStatus.PENDING)
                .build();
    }

    //Get Leave Request by id
    public LeaveRequestResponse getLeaveRequestById(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LEAVE_REQUEST_NOT_FOUND));
        return LeaveRequestResponse.builder()
                .id(leaveRequest.getId())
                .studentId(leaveRequest.getStudent().getId())
                .classId(leaveRequest.getClassEntity().getId())
                .leaveDate(leaveRequest.getLeaveDate())
                .reason(leaveRequest.getReason())
                .status(leaveRequest.getStatus())
                .build();
    }

    //Teacher approve leave request
    public LeaveRequestResponse approveLeaveRequest(Long id){
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LEAVE_REQUEST_NOT_FOUND));
        leaveRequest.setStatus(LeaveRequestStatus.APPROVED);
        leaveRequestRepository.save(leaveRequest);
        return LeaveRequestResponse.builder()
                .id(leaveRequest.getId())
                .studentId(leaveRequest.getStudent().getId())
                .classId(leaveRequest.getClassEntity().getId())
                .leaveDate(leaveRequest.getLeaveDate())
                .reason(leaveRequest.getReason())
                .status(leaveRequest.getStatus())
                .build();
    }

    //Teacher reject leave request
    public LeaveRequestResponse rejectLeaveRequest(Long id){
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LEAVE_REQUEST_NOT_FOUND));
        leaveRequest.setStatus(LeaveRequestStatus.REJECTED);
        leaveRequestRepository.save(leaveRequest);
        return LeaveRequestResponse.builder()
                .id(leaveRequest.getId())
                .studentId(leaveRequest.getStudent().getId())
                .classId(leaveRequest.getClassEntity().getId())
                .leaveDate(leaveRequest.getLeaveDate())
                .reason(leaveRequest.getReason())
                .status(leaveRequest.getStatus())
                .build();
    }

    //Teacher get all leave request in class
    public List<LeaveRequestResponse> getAllLeaveRequestInClass(Long classId) {
        if (!classRepository.existsById(classId)) {
            throw new AppException(ErrorCode.CLASS_NOT_FOUND);
        }
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByClassEntityId(classId);

        return leaveRequests.stream()
                .map(leaveRequest -> LeaveRequestResponse.builder()
                        .id(leaveRequest.getId())
                        .studentId(leaveRequest.getStudent().getId())
                        .classId(leaveRequest.getClassEntity().getId())
                        .leaveDate(leaveRequest.getLeaveDate())
                        .reason(leaveRequest.getReason())
                        .status(leaveRequest.getStatus())
                        .build())
                .toList();
    }

}
