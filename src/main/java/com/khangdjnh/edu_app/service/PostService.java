package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.post.PostCreationRequest;
import com.khangdjnh.edu_app.dto.post.PostResponse;
import com.khangdjnh.edu_app.dto.response.ClassResponse;
import com.khangdjnh.edu_app.dto.response.FileRecordResponse;
import com.khangdjnh.edu_app.entity.*;
import com.khangdjnh.edu_app.enums.Emotion;
import com.khangdjnh.edu_app.enums.ParentType;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final CloudflareR2Service cloudflareR2Service;
    private final UserRepository userRepository;
    private final FileRecordRepository fileRecordRepository;
    private final ClassRepository classRepository;
    private final EmotionCounterRepository emotionCounterRepository;
    private final CommentRepository commentRepository;
    private final PostTypeRepository postTypeRepository;

    @Transactional(rollbackFor = Exception.class)
    public PostResponse createPost(PostCreationRequest request) {
        MultipartFile file = request.getFile();
        FileRecordResponse fileRecord = file == null ? null : cloudflareR2Service.uploadFile(file);
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        PostType postType = postTypeRepository.findById(request.getPostTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_TYPE_NOT_FOUND));
        User poster = getUserById(request.getPoster());
        ClassPost post = ClassPost.builder()
                .userId(request.getPoster())
                .classEntity(classEntity)
                .postType(postType)
                .postTitle(request.getPostTitle())
                .postContent(request.getPostContent())
                .attachFileId(fileRecord == null ? null : fileRecord.getId())
                .postIcon(request.getPostIcon())
                .postBackground(request.getPostBackground())
                .build();
        post = postRepository.save(post);

        EmotionCounter emotionCounter = EmotionCounter.builder()
                .emotion(Emotion.LIKE)
                .parentType(ParentType.POST)
                .parentId(post.getId())
                .quantity(0L)
                .build();
        emotionCounterRepository.save(emotionCounter);
        return toPostResponse(post, poster, fileRecord);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAll(Long clasId){
        List<PostResponse> result = new ArrayList<>();
        List<ClassPost> listPost = postRepository.findByClassEntityId(clasId);
        for(ClassPost post : listPost){
            FileRecordResponse fileRecord = post.getAttachFileId() == null
                    ? null
                    : getFileRecordResponse(
                    fileRecordRepository.findById(post.getAttachFileId())
                            .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND))
            );
            PostResponse response = toPostResponse(post, getUserById(post.getUserId()), fileRecord);
            result.add(response);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        ClassPost post = postRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        FileRecordResponse fileRecord = post.getAttachFileId() == null
            ? null
            : getFileRecordResponse(
                fileRecordRepository.findById(post.getAttachFileId())
                    .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND))
            );
        return toPostResponse(post, getUserById(post.getUserId()), fileRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    public PostResponse updatePost(Long postId, PostCreationRequest request) {
        ClassPost post = postRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        User poster = getUserById(request.getPoster());
        post.setPostTitle(request.getPostTitle());
        post.setPostContent(request.getPostContent());
        FileRecordResponse fileRecord = null;
        if(post.getAttachFileId() != null) {
            fileRecord = cloudflareR2Service.uploadFile(request.getFile());
            post.setAttachFileId(fileRecord.getId());
        }
        if(!request.getPostTypeId().equals(post.getPostType().getId())) {
            PostType postType = postTypeRepository.findById(request.getPostTypeId())
                    .orElseThrow(() -> new AppException(ErrorCode.POST_TYPE_NOT_FOUND));
            post.setPostType(postType);
        }
        if(post.getPostIcon() != null) post.setPostIcon(request.getPostIcon());
        if(post.getPostBackground() != null) post.setPostBackground(request.getPostBackground());
        post = postRepository.save(post);
        return toPostResponse(post, poster , fileRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long postId){
        ClassPost post = postRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        postRepository.delete(post);
    }

    private PostResponse toPostResponse(ClassPost post, User poster, FileRecordResponse fileRecord) {
        List<EmotionCounter> listEmotionCounter =
                emotionCounterRepository.findByParentIdAndParentType(post.getId(), ParentType.POST);
        Map<Emotion, Long> emotionCounter = listEmotionCounter
                .stream()
                .collect(Collectors.toMap(EmotionCounter::getEmotion, EmotionCounter::getQuantity));
        int commentCount = commentRepository.countByPostId(post.getId());
        ClassResponse classResponse = toClassResponse(post.getClassEntity());
        return PostResponse.builder()
                .id(post.getId())
                .poster(poster)
                .classResponse(classResponse)
                .postTitle(post.getPostTitle())
                .postContent(post.getPostContent())
                .attachFile(fileRecord)
                .postIcon(post.getPostIcon())
                .postBackground(post.getPostBackground())
                .createdAt(post.getCreatedAt())
                .emotionCounter(emotionCounter)
                .commentCount(commentCount)
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

    private ClassResponse toClassResponse(ClassEntity classEntity) {
        return ClassResponse.builder()
                .id(classEntity.getId())
                .code(classEntity.getCode())
                .name(classEntity.getName())
                .semester(classEntity.getSemester())
                .description(classEntity.getDescription())
                .createdAt(classEntity.getCreatedAt())
                .build();
    }

    private User getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return (User) Hibernate.unproxy(user);
    }
}
