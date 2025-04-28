package com.khangdjnh.edu_app.mapper;

import com.khangdjnh.edu_app.dto.request.DocumentUpdateRequest;
import com.khangdjnh.edu_app.dto.response.DocumentResponse;
import com.khangdjnh.edu_app.entity.Document;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    DocumentResponse toDocumentResponse(Document document);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDocumentFromRequest(@MappingTarget Document document, DocumentUpdateRequest request);
}
