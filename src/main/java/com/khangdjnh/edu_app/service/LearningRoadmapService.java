package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.LearningRoadmapRequest;
import com.khangdjnh.edu_app.dto.response.ClassResponse;
import com.khangdjnh.edu_app.dto.response.FileRecordResponse;
import com.khangdjnh.edu_app.dto.response.LearningRoadmapResponse;
import com.khangdjnh.edu_app.entity.ClassEntity;
import com.khangdjnh.edu_app.entity.FileRecord;
import com.khangdjnh.edu_app.entity.LearningRoadmap;
import com.khangdjnh.edu_app.repository.LearningRoadmapRepository;
import com.khangdjnh.edu_app.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningRoadmapService {
    private final LearningRoadmapRepository learningRoadmapRepository;
    private final CloudflareR2Service cloudflareR2Service;

    @Transactional(rollbackFor = Exception.class)
    public LearningRoadmapResponse createLearningRoadmap(LearningRoadmapRequest request) {
        FileRecord fileRecord = request.getFile() == null ? null : cloudflareR2Service.uploadFileV2(request.getFile());
        int existingCount = learningRoadmapRepository.countByClassEntity_IdAndParentId(
                request.getClassId(),
                request.getParentId()
        );
        LearningRoadmap learningRoadmap = LearningRoadmap.builder()
                .classEntity(ClassEntity.builder().id(request.getClassId()).build())
                .title(request.getTitle())
                .description(request.getDescription())
                .fileRecord(fileRecord)
                .backgroundImage(request.getBackgroundImage())
                .iconImage(request.getIconImage())
                .createdAt(LocalDateTime.now())
                .createdBy(SecurityUtils.getCurrentUsername())
                .roadmapIndex(existingCount + 1)
                .parentId(request.getParentId())
                .build();
        learningRoadmap = learningRoadmapRepository.save(learningRoadmap);
        return toLearningRoadmapResponse(learningRoadmap);
    }

    @Transactional(readOnly = true)
    public List<LearningRoadmapResponse> getAllLearningRoadmapsInClass(Long classId) {

        List<LearningRoadmap> allRoadmaps =
                learningRoadmapRepository.findByClassEntity_Id(classId);

        // Group children theo parentId
        Map<Long, List<LearningRoadmap>> roadmapByParentId = allRoadmaps.stream()
                .filter(lr -> lr.getParentId() != null)
                .collect(Collectors.groupingBy(LearningRoadmap::getParentId));

        // Lấy root nodes (parentId == null)
        return allRoadmaps.stream()
                .filter(lr -> lr.getParentId() == null)
                .sorted(Comparator.comparing(LearningRoadmap::getRoadmapIndex))
                .map(lr -> mapToResponse(lr, roadmapByParentId))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public LearningRoadmapResponse updateLearningRoadmap(
            Long roadmapId,
            LearningRoadmapRequest request) {

        LearningRoadmap roadmap = learningRoadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new RuntimeException("LearningRoadmap not found"));

        // Upload file mới nếu có
        if (request.getFile() != null) {
            FileRecord fileRecord = cloudflareR2Service.uploadFileV2(request.getFile());
            roadmap.setFileRecord(fileRecord);
        }

        roadmap.setTitle(request.getTitle());
        roadmap.setDescription(request.getDescription());
        roadmap.setBackgroundImage(request.getBackgroundImage());
        roadmap.setIconImage(request.getIconImage());

        LearningRoadmap updated = learningRoadmapRepository.save(roadmap);

        return toLearningRoadmapResponse(updated);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteLearningRoadmap(Long roadmapId) {

        LearningRoadmap roadmap = learningRoadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new RuntimeException("LearningRoadmap not found"));

        deleteRecursively(roadmap.getId());
    }

    private void deleteRecursively(Long roadmapId) {
        List<LearningRoadmap> children =
                learningRoadmapRepository.findByParentId(roadmapId);

        for (LearningRoadmap child : children) {
            deleteRecursively(child.getId());
        }

        learningRoadmapRepository.deleteById(roadmapId);
    }

    private LearningRoadmapResponse mapToResponse(
            LearningRoadmap roadmap,
            Map<Long, List<LearningRoadmap>> roadmapByParentId) {

        List<LearningRoadmapResponse> children = roadmapByParentId
                .getOrDefault(roadmap.getId(), Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(LearningRoadmap::getRoadmapIndex))
                .map(child -> mapToResponse(child, roadmapByParentId))
                .collect(Collectors.toList());

        return LearningRoadmapResponse.builder()
                .id(roadmap.getId())
                .title(roadmap.getTitle())
                .description(roadmap.getDescription())
                .backgroundImage(roadmap.getBackgroundImage())
                .iconImage(roadmap.getIconImage())
                .roadmapIndex(roadmap.getRoadmapIndex())
                .createdBy(roadmap.getCreatedBy())
                .createdAt(roadmap.getCreatedAt())
                .children(children)
                .fileRecord(getFileRecordResponse(roadmap.getFileRecord()))
                .classResponse(toClassResponse(roadmap.getClassEntity()))
                .build();
    }


    private LearningRoadmapResponse toLearningRoadmapResponse(LearningRoadmap learningRoadmap) {
        return LearningRoadmapResponse.builder()
                .id(learningRoadmap.getId())
                .classResponse(toClassResponse(learningRoadmap.getClassEntity()))
                .title(learningRoadmap.getTitle())
                .description(learningRoadmap.getDescription())
                .fileRecord(getFileRecordResponse(learningRoadmap.getFileRecord()))
                .backgroundImage(learningRoadmap.getBackgroundImage())
                .iconImage(learningRoadmap.getIconImage())
                .createdAt(learningRoadmap.getCreatedAt())
                .createdBy(learningRoadmap.getCreatedBy())
                .roadmapIndex(learningRoadmap.getRoadmapIndex())
                .build();
    }

    private FileRecordResponse getFileRecordResponse(FileRecord file) {
        if(file == null) return null;
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
                .name(classEntity.getName())
                .code(classEntity.getCode())
                .description(classEntity.getDescription())
                .semester(classEntity.getSemester())
                .build();
    }
}
