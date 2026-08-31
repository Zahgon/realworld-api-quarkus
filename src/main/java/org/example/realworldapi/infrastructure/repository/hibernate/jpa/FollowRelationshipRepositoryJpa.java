package org.example.realworldapi.infrastructure.repository.hibernate.jpa;

import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.example.realworldapi.domain.model.user.FollowRelationship;
import org.example.realworldapi.domain.model.user.FollowRelationshipRepository;
import org.example.realworldapi.domain.model.user.User;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.EntityUtils;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.FollowRelationshipEntity;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.FollowRelationshipEntityKey;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class FollowRelationshipRepositoryJpa extends AbstractJpaRepository
    implements FollowRelationshipRepository {

  private final FollowRelationshipEntityJpaRepository followRelationshipEntityJpaRepository;
  private final EntityUtils entityUtils;

  @Override
  public boolean isFollowing(UUID currentUserId, UUID followedUserId) {
    return followRelationshipEntityJpaRepository.countByUserAndFollowed(
            currentUserId, followedUserId)
        > 0;
  }

  @Override
  public void save(FollowRelationship followRelationship) {
    final var userEntity = findUserEntityById(followRelationship.getUser().getId());
    final var userToFollowEntity = findUserEntityById(followRelationship.getFollowed().getId());
    entityManager.persist(new FollowRelationshipEntity(userEntity, userToFollowEntity));
    entityManager.flush();
  }

  @Override
  public Optional<FollowRelationship> findByUsers(User loggedUser, User followedUser) {
    return findUsersFollowedEntityByUsers(loggedUser, followedUser)
        .map(this::followingRelationship);
  }

  @Override
  public void remove(FollowRelationship followRelationship) {
    final var usersFollowedEntity =
        findUsersFollowedEntityByUsers(
                followRelationship.getUser(), followRelationship.getFollowed())
            .orElseThrow();
    entityManager.remove(usersFollowedEntity);
  }

  private Optional<FollowRelationshipEntity> findUsersFollowedEntityByUsers(
      User loggedUser, User followedUser) {
    final var loggedUserEntity = findUserEntityById(loggedUser.getId());
    final var followedEntity = findUserEntityById(followedUser.getId());
    return followRelationshipEntityJpaRepository
        .findByPrimaryKey(usersFollowedKey(loggedUserEntity, followedEntity))
        .stream()
        .findFirst();
  }

  private FollowRelationship followingRelationship(
      FollowRelationshipEntity followRelationshipEntity) {
    final var user = entityUtils.user(followRelationshipEntity.getUser());
    final var followed = entityUtils.user(followRelationshipEntity.getFollowed());
    return new FollowRelationship(user, followed);
  }

  private FollowRelationshipEntityKey usersFollowedKey(UserEntity user, UserEntity followed) {
    final var primaryKey = new FollowRelationshipEntityKey();
    primaryKey.setUser(user);
    primaryKey.setFollowed(followed);
    return primaryKey;
  }
}
