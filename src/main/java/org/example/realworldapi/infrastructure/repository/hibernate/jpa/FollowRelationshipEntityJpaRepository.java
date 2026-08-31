package org.example.realworldapi.infrastructure.repository.hibernate.jpa;

import java.util.List;
import java.util.UUID;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.FollowRelationshipEntity;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.FollowRelationshipEntityKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRelationshipEntityJpaRepository
    extends JpaRepository<FollowRelationshipEntity, FollowRelationshipEntityKey> {

  @Query(
      "select count(*) from FollowRelationshipEntity as followRelationship where followRelationship.primaryKey.user.id = :currentUserId and followRelationship.primaryKey.followed.id = :followedUserId")
  long countByUserAndFollowed(
      @Param("currentUserId") UUID currentUserId, @Param("followedUserId") UUID followedUserId);

  @Query(
      "select followRelationship from FollowRelationshipEntity as followRelationship where followRelationship.primaryKey = :primaryKey")
  List<FollowRelationshipEntity> findByPrimaryKey(
      @Param("primaryKey") FollowRelationshipEntityKey primaryKey);
}
