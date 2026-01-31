package com.emmanuel.user_service.mapper;

import com.emmanuel.user_service.dto.request.SignUpRequest;
import com.emmanuel.user_service.dto.request.UpdateUserRequest;
import com.emmanuel.user_service.dto.response.UserResponse;
import com.emmanuel.user_service.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
  @Mapping(target = "enabled", constant = "false")
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

  UserResponse toDto(User user);

  void updateUser(UpdateUserRequest updateUserRequest, @MappingTarget User user);
}
