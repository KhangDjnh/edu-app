package com.khangdjnh.edu_app.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    EXISTED_USER(1001, "User already existed", HttpStatus.CONFLICT),
    USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
    INVALID_USERNAME_OR_PASSWORD(1003, "Invalid username or password", HttpStatus.UNAUTHORIZED),
    INVALID_USERNAME(1004, "Invalid username", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1005, "Invalid password", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1006, "Invalid token", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(1007, "User is not authenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1008, "User is not authorized", HttpStatus.FORBIDDEN),
    INVALID_KEY(1009, "Invalid key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1010, "User already existed", HttpStatus.CONFLICT),
    EMAIL_EXISTED_IN_DATABASE(1011, "Email already existed in database", HttpStatus.CONFLICT),
    USERNAME_IS_MISSING(1012, "Username is missing", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_SAME_AS_OLD(1013, "New password is same as old password", HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_IS_INCORRECT(1014, "Old password is incorrect", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED_IN_PENDING(1015, "Email already existed in pending", HttpStatus.CONFLICT),
    CONFIRM_MAIL_TOKEN_IS_INVALID_OR_EXPIRED(1015, "Confirm mail token is invalid or expired", HttpStatus.BAD_REQUEST),
    CONFIRM_MAIL_TOKEN_IS_EXPIRED(1016, "Confirm mail token is expired", HttpStatus.BAD_REQUEST),
    CLASS_EXISTED(1017, "Class code already existed", HttpStatus.CONFLICT),
    CLASS_NOT_FOUND(1018, "Class not found", HttpStatus.NOT_FOUND),
    DOCUMENT_NOT_FOUND(1019, "Document not found", HttpStatus.NOT_FOUND),
    LEAVE_REQUEST_NOT_FOUND(1020, "Document not found", HttpStatus.NOT_FOUND),
    ATTENDANCE_NOT_FOUND(1021, "Attendance not found", HttpStatus.NOT_FOUND),
    NOTICE_NOT_FOUND(1022, "Notice not found", HttpStatus.NOT_FOUND),
    QUESTION_NOT_FOUND(1023, "Exam question not found", HttpStatus.NOT_FOUND),
    EXAM_NOT_FOUND(1024, "Exam not found", HttpStatus.NOT_FOUND),
    SUBMISSION_NOT_FOUND(1025, "Submission not found", HttpStatus.NOT_FOUND),
    NO_ANSWER_FOUND_IN_SUBMISSION(1026, "No answer found in submission", HttpStatus.NOT_FOUND),
    SCORES_NOT_FOUND(1027, "Scores not found", HttpStatus.NOT_FOUND),
    ASSIGNMENT_NOT_FOUND(1031, "Assignment not found", HttpStatus.NOT_FOUND),
    FILE_NOT_FOUND(1032, "File not found", HttpStatus.NOT_FOUND),
    CLASS_STUDENT_NOT_FOUND(1033, "Class student not found", HttpStatus.NOT_FOUND),


    FAILED_TO_GENERATE_PDF(1025, "Failed to generate pdf", HttpStatus.INTERNAL_SERVER_ERROR),

    INVALID_EXAM_TIME(1026, "Start time must be before end time", HttpStatus.BAD_REQUEST),
    NOT_ENOUGH_QUESTIONS(1027, "Not enough questions for this level", HttpStatus.BAD_REQUEST),
    ALREADY_SUBMITTED(1029, "Already submitted", HttpStatus.BAD_REQUEST),
    EXAM_NOT_AVAILABLE(1028, "Exam not available", HttpStatus.BAD_REQUEST),
    INVALID_QUESTION_LIST(1030, "List question id is empty", HttpStatus.BAD_REQUEST),

    INVALID_ATTENDANCE_STATUS(1031, "Invalid attendance status", HttpStatus.BAD_REQUEST),
    NOT_FOUND_ROLE(1032, "Role not found in identity provider", HttpStatus.NOT_FOUND),

    ROOM_NOT_FOUND(1033, "Room not found", HttpStatus.NOT_FOUND),
    JOIN_ROOM_HISTORY_NOT_FOUND(1034, "User join room history not found", HttpStatus.NOT_FOUND),
    CONVERSATION_NOT_FOUND(1035, "Conversation not found", HttpStatus.NOT_FOUND),
    REPLY_TO_MESSAGE_NOT_FOUND(1036, "Message to reply to is not found", HttpStatus.NOT_FOUND),
    UPLOAD_FILE_FAIL(1037, "Fail to Upload file", HttpStatus.INTERNAL_SERVER_ERROR),
    MESSAGE_NOT_FOUND(1038, "Message not found", HttpStatus.NOT_FOUND),
    ;

    int code;
    String message;
    HttpStatusCode httpStatusCode;
    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
