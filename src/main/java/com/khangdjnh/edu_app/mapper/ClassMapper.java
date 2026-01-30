package com.khangdjnh.edu_app.mapper;

import com.khangdjnh.edu_app.dto.request.classentity.ClassCreateRequest;
import com.khangdjnh.edu_app.dto.request.classentity.ClassUpdateRequest;
import com.khangdjnh.edu_app.dto.response.ClassResponse;
import com.khangdjnh.edu_app.entity.ClassEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClassMapper {
    ClassEntity toClass(ClassCreateRequest request);

    @Mapping(source = "teacher.id", target = "teacherId")
    @Mapping(source = "classType", target = "classType")
    @Mapping(source = "powerBy", target = "powerBy")
    @Mapping(source = "classIntroduction", target = "classIntroduction")
    ClassResponse toClassResponse(ClassEntity classEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateClassFromRequest(@MappingTarget ClassEntity classEntity, ClassUpdateRequest request);
}
