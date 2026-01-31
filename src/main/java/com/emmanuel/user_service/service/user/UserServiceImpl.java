package com.emmanuel.user_service.service.user;

import com.emmanuel.user_service.dto.filter.UserFilter;
import com.emmanuel.user_service.dto.request.UpdateUserRequest;
import com.emmanuel.user_service.dto.response.UserResponse;
import com.emmanuel.user_service.mapper.UserMapper;
import com.emmanuel.user_service.model.user.User;
import com.emmanuel.user_service.repository.UserRepository;
import com.emmanuel.user_service.specification.UserSpecifications;
import com.emmanuel.user_service.utility.ErrorMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserResponse getUserById(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException(ErrorMessages.USER_NOT_FOUND));
    return userMapper.toDto(user);
  }

  @Override
  public Page<UserResponse> getUsers(Pageable pageable) {
    Page<User> users = userRepository.findAll(pageable);
    return users.map(userMapper::toDto);
  }

  @Override
  public Page<UserResponse> filterUsers(UserFilter filter, Pageable pageable) {
    var spec = UserSpecifications.build(filter);

    Page<User> users = userRepository.findAll(spec, pageable);

    return users.map(userMapper::toDto);
  }

  @Override
  public Page<UserResponse> searchUsers(String searchTerm, Pageable pageable) {
    var spec = UserSpecifications.search(searchTerm);
    Page<User> users = userRepository.findAll(spec, pageable);
    return users.map(userMapper::toDto);
  }

  @Override
  @Transactional
  public UserResponse updateUser(Long userId, UpdateUserRequest updateUserRequest) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException(ErrorMessages.USER_NOT_FOUND));

    userMapper.updateUser(updateUserRequest, user);

    user = userRepository.save(user);

    return userMapper.toDto(user);
  }
}
