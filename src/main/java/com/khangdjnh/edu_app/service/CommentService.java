package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.comment.CommentReplyResponse;
import com.khangdjnh.edu_app.dto.comment.CommentRequest;
import com.khangdjnh.edu_app.dto.comment.CommentResponse;
import com.khangdjnh.edu_app.dto.post.PostResponse;
import com.khangdjnh.edu_app.dto.response.FileRecordResponse;
import com.khangdjnh.edu_app.entity.ClassPost;
import com.khangdjnh.edu_app.entity.FileRecord;
import com.khangdjnh.edu_app.entity.PostComment;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.CommentRepository;
import com.khangdjnh.edu_app.repository.FileRecordRepository;
import com.khangdjnh.edu_app.repository.PostRepository;
import com.khangdjnh.edu_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final FileRecordRepository fileRecordRepository;
    private final PostRepository postRepository;

    @Transactional(rollbackFor = Exception.class)
    public CommentResponse createComment(CommentRequest request) {
        User user = getUserById(request.getUserId());
        ClassPost post = getPostById(request.getPostId());
        PostComment comment = PostComment.builder()
                .user(user)
                .post(post)
                .content(request.getContent())
                .replyTo(request.getReplyTo())
                .emotion(request.getEmotion())
                .build();
        comment = commentRepository.save(comment);
        return toCommentResponse(comment);
    }

    @Transactional(readOnly = true)
    public CommentResponse getComment(Long commentId) {
        return toCommentResponse(getPostCommentById(commentId));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getAllComment(Long postId){
        List<PostComment> listComments = commentRepository.findByPostId(postId);
        return listComments.stream()
                .map(this::toCommentResponse)
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId) {
        PostComment comment = getPostCommentById(commentId);
        log.info("Delete comment {}", comment.getContent());
        commentRepository.delete(comment);
    }


    private CommentResponse toCommentResponse(PostComment comment) {
        ClassPost post = comment.getPost();
        User poster = getUserById(comment.getUser().getId());
        FileRecordResponse fileRecord = post.getAttachFileId() == null
        ? null
        : getFileRecordResponse(
                fileRecordRepository.findById(post.getAttachFileId())
                        .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND))
        );
        return CommentResponse.builder()
                .id(comment.getId())
                .post(toPostResponse(comment.getPost(), poster, fileRecord))
                .userComment(comment.getUser())
                .content(comment.getContent())
                .replyTo(comment.getReplyTo() == null
                        ? null
                        :toCommentReplyResponse(getPostCommentById(comment.getReplyTo()))
                )
                .emotion(comment.getEmotion())
                .updatedAt(comment.getUpdatedAt())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    private CommentReplyResponse toCommentReplyResponse(PostComment comment) {
        return CommentReplyResponse.builder()
                .id(comment.getId())
                .userComment(comment.getUser())
                .content(comment.getContent())
                .build();
    }

    private PostResponse toPostResponse(ClassPost post, User poster, FileRecordResponse fileRecord) {
        return PostResponse.builder()
                .id(post.getId())
                .poster(poster)
                .postTitle(post.getPostTitle())
                .postContent(post.getPostContent())
                .attachFile(fileRecord)
                .postIcon(post.getPostIcon())
                .postBackground(post.getPostBackground())
                .createdAt(post.getCreatedAt())
                .build();
    }

    private FileRecordResponse getFileRecordResponse(FileRecord file) {
        return FileRecordResponse.builder()
                .id(file.getId())
                .folder(file.getFolder())
                .uploadedBy(file.getUploadedBy())
                .fileSize(file.getFileSize())
                .fileType(file.getFileType())
                .fileName(file.getFileName())
                .fileUrl(file.getFileUrl())
                .uploadedAt(file.getUploadedAt())
                .build();
    }

    private PostComment getPostCommentById(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private User getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return (User) Hibernate.unproxy(user);
    }

    private ClassPost getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    }

}
