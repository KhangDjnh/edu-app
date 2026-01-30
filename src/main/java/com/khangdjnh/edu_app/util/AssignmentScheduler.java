package com.khangdjnh.edu_app.util;

import com.khangdjnh.edu_app.entity.Assignment;
import com.khangdjnh.edu_app.entity.ClassStudent;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.repository.AssignmentRepository;
import com.khangdjnh.edu_app.repository.ClassStudentRepository;
import com.khangdjnh.edu_app.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentScheduler {

    AssignmentRepository assignmentRepository;
    NotificationService notificationService;
    ClassStudentRepository classStudentRepository;

    @Scheduled(cron = "0 0 8 * * *") // 8h sáng hàng ngày
    public void notifyAssignmentsAboutToExpire() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);

        List<Assignment> assignments = assignmentRepository.findByEndAtBetween(now, tomorrow);

        for (Assignment assignment : assignments) {
            Long classId = assignment.getClassEntity().getId();
            List<User> students = classStudentRepository.findByClassEntity_IdAndIsConfirmed(classId, true)
                    .stream()
                    .map(ClassStudent::getStudent)
                    .toList();

            for (User student : students) {
                notificationService.sendAssignmentDeadlineNotice(
                        student,
                        assignment.getTitle(),
                        assignment.getEndAt(),
                        assignment.getId()
                );
            }
        }
    }
}

