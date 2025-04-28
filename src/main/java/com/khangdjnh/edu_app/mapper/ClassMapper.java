package com.khangdjnh.edu_app.mapper;

import com.khangdjnh.edu_app.dto.request.ClassCreateRequest;
import com.khangdjnh.edu_app.dto.request.ClassUpdateRequest;
import com.khangdjnh.edu_app.dto.response.ClassResponse;
import com.khangdjnh.edu_app.entity.ClassEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ClassMapper {
    ClassEntity toClass(ClassCreateRequest request);
    ClassResponse toClassResponse(ClassEntity classEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateClassFromRequest(@MappingTarget ClassEntity classEntity, ClassUpdateRequest request);
}
