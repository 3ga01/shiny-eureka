package com.emmanuel.user_service.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Set;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  private Set<Role> roles;

  @ElementCollection(fetch = FetchType.EAGER)
  @Column(length = 1024)
  private Set<String> refreshTokens;

  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  private Set<Permission> permissions;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private Instant updatedAt;

  @CreatedBy
  @Column(updatable = false)
  private String createdBy;

  @LastModifiedBy private String updatedBy;

  @Column(nullable = false)
  private boolean enabled = true;

  @Column(nullable = false)
  private boolean accountNonExpired = true;

  @Column(nullable = false)
  private boolean credentialsNonExpired = true;

  @Column(nullable = false)
  private boolean accountNonLocked = true;

  @Column(nullable = false)
  private boolean deleted = false;

  private String logoUrl;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  private Instant lastLoginAt;

  private Instant lastActivityAt;

  private Integer loginCount;

  private Integer failedLoginAttempts;

  @Version private Long version;
}
