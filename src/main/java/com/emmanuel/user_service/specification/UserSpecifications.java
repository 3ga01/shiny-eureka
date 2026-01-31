package com.emmanuel.user_service.specification;

import com.emmanuel.user_service.dto.filter.UserFilter;
import com.emmanuel.user_service.model.user.Role;
import com.emmanuel.user_service.model.user.User;
import jakarta.persistence.criteria.SetJoin;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {

  public static Specification<User> hasRoles(Set<Role> roles) {
    return (root, query, cb) -> {
      if (roles == null || roles.isEmpty()) return null;
      SetJoin<User, Role> roleJoin = root.joinSet("roles");
      return roleJoin.in(roles);
    };
  }

  public static Specification<User> isEnabled(Boolean enabled) {
    return (root, query, cb) -> enabled == null ? null : cb.equal(root.get("enabled"), enabled);
  }

  public static Specification<User> isDeleted(Boolean deleted) {
    return (root, query, cb) -> deleted == null ? null : cb.equal(root.get("deleted"), deleted);
  }

  public static Specification<User> search(String term) {
    return (root, query, cb) -> {
      if (term == null || term.isBlank()) return null;

      String like = "%" + term.toLowerCase() + "%";

      return cb.or(
          cb.like(cb.lower(root.get("username")), like),
          cb.like(cb.lower(root.get("email")), like),
          cb.like(cb.lower(root.get("firstName")), like),
          cb.like(cb.lower(root.get("lastName")), like));
    };
  }

  // Combine all filters dynamically
  public static Specification<User> build(UserFilter filter) {
    return Specification.where(hasRoles(filter.roles()))
        .and(isEnabled(filter.enabled()))
        .and(isDeleted(filter.deleted()));
  }
}
