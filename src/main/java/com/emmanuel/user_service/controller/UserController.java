package com.emmanuel.user_service.controller;

import com.emmanuel.user_service.dto.filter.UserFilter;
import com.emmanuel.user_service.dto.response.UserResponse;
import com.emmanuel.user_service.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping
  public Page<UserResponse> getAllUsers(
      @PageableDefault(size = 10, sort = "id") Pageable pageable) {
    return userService.getUsers(pageable);
  }

  @GetMapping("/{userId}")
  public UserResponse getUserById(@PathVariable Long userId) {
    return userService.getUserById(userId);
  }

  @GetMapping("/search")
  public Page<UserResponse> searchUsers(
      @RequestParam String searchTerm, @PageableDefault(size = 10, sort = "id") Pageable pageable) {
    return userService.searchUsers(searchTerm, pageable);
  }

  @GetMapping("/filter")
  public Page<UserResponse> filterUsers(
      @ModelAttribute UserFilter filter,
      @PageableDefault(size = 10, page = 0, sort = "id") Pageable pageable) {
    return userService.filterUsers(filter, pageable);
  }
}
