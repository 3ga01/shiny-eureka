package com.emmanuel.user_service.dto.filter;

import com.emmanuel.user_service.model.user.Role;
import java.util.Set;

public record UserFilter(Set<Role> roles, Boolean enabled, Boolean deleted) {}
