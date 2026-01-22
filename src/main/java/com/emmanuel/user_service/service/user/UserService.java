package com.emmanuel.user_service.service.user;

import com.emmanuel.user_service.dto.filter.UserFilter;
import com.emmanuel.user_service.dto.request.UpdateUserRequest;
import com.emmanuel.user_service.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
  UserResponse getUserById(Long userId);

  Page<UserResponse> getUsers(Pageable pageable);

  Page<UserResponse> filterUsers(UserFilter filter, Pageable pageable);

  Page<UserResponse> searchUsers(String searchTerm, Pageable pageable);

  UserResponse updateUser(Long userId, UpdateUserRequest updateUserRequest);
}
