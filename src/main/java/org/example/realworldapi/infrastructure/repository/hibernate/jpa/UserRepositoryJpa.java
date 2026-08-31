package org.example.realworldapi.infrastructure.repository.hibernate.jpa;

import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.example.realworldapi.domain.model.user.User;
import org.example.realworldapi.domain.model.user.UserRepository;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.EntityUtils;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class UserRepositoryJpa extends AbstractJpaRepository implements UserRepository {

  private final UserEntityJpaRepository userEntityJpaRepository;
  private final EntityUtils entityUtils;

  @Override
  public void save(User user) {
    entityManager.persist(new UserEntity(user));
  }

  @Override
  public boolean existsBy(String field, String value) {
    return entityManager
            .createQuery(
                "select count(*) from UserEntity as users where upper(users."
                    + field
                    + ") = :value",
                Long.class)
            .setParameter("value", value.toUpperCase().trim())
            .getSingleResult()
        > 0;
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return userEntityJpaRepository.findFirstByEmailIgnoreCase(email.trim()).map(entityUtils::user);
  }

  @Override
  public Optional<User> findUserById(UUID id) {
    return userEntityJpaRepository.findById(id).map(entityUtils::user);
  }

  @Override
  public boolean existsUsername(UUID excludeId, String username) {
    return userEntityJpaRepository.countByIdNotAndUsernameIgnoreCase(excludeId, username.trim()) > 0;
  }

  @Override
  public boolean existsEmail(UUID excludeId, String email) {
    return userEntityJpaRepository.countByIdNotAndEmailIgnoreCase(excludeId, email.trim()) > 0;
  }

  @Override
  public void update(User user) {
    final var userEntity = findUserEntityById(user.getId());
    userEntity.update(user);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userEntityJpaRepository
        .findFirstByUsernameIgnoreCase(username.trim())
        .map(entityUtils::user);
  }
}
