package com.emmanuel.user_service.mapper;

import com.emmanuel.user_service.dto.CreateUserResponse;
import com.emmanuel.user_service.dto.SignUpRequest;
import com.emmanuel.user_service.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "accountNonExpired", constant = "true")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "credentialsNonExpired", constant = "true")
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    User toEntity(SignUpRequest request);

    CreateUserResponse toDto(User user);
}
