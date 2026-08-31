package org.example.realworldapi.infrastructure.repository.hibernate.jpa;

import java.util.Optional;
import java.util.UUID;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEntityJpaRepository extends JpaRepository<UserEntity, UUID> {

  Optional<UserEntity> findFirstByEmailIgnoreCase(String email);

  Optional<UserEntity> findFirstByUsernameIgnoreCase(String username);

  long countByIdNotAndUsernameIgnoreCase(UUID excludeId, String username);

  long countByIdNotAndEmailIgnoreCase(UUID excludeId, String email);
}
