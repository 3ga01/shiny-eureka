package com.emmanuel.user_service.mapper;

import com.emmanuel.user_service.dto.request.VerificationRequest;
import com.emmanuel.user_service.dto.response.VerificationResponse;
import com.emmanuel.user_service.model.user.VerificationToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VerificationMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  VerificationToken toEntity(VerificationRequest request);

  @Mapping(source = "user.id", target = "userId")
  VerificationResponse toDto(VerificationToken token);

  //  @Mapping(target = "id", ignore = true)
  //  @Mapping(target = "createdAt", ignore = true)
  //  @Mapping(target = "updatedAt", ignore = true)
  //  void UpdateVerification(VerificationRequest request, VerificationToken token);
}
