package org.example.realworldapi.infrastructure.repository.hibernate.jpa;

import java.util.Optional;
import java.util.UUID;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.FavoriteRelationshipEntity;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.FavoriteRelationshipEntityKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRelationshipEntityJpaRepository
    extends JpaRepository<FavoriteRelationshipEntity, FavoriteRelationshipEntityKey> {

  long countByArticleIdAndUserId(UUID articleId, UUID userId);

  long countByArticleId(UUID articleId);

  Optional<FavoriteRelationshipEntity> findFirstByArticleIdAndUserId(UUID articleId, UUID userId);
}
