package com.khangdjnh.edu_app.mapper;

import com.khangdjnh.edu_app.dto.request.UserCreateRequest;
import com.khangdjnh.edu_app.dto.request.UserUpdateRequest;
import com.khangdjnh.edu_app.dto.response.UserResponse;
import com.khangdjnh.edu_app.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreateRequest request);

    UserResponse toUserResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromRequest(@MappingTarget User user, UserUpdateRequest request);
}
