package org.example.realworldapi.infrastructure.repository.hibernate.jpa;

import java.util.List;
import java.util.UUID;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.TagRelationshipEntity;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.TagRelationshipEntityKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRelationshipEntityJpaRepository
    extends JpaRepository<TagRelationshipEntity, TagRelationshipEntityKey> {

  @Query(
      "select tagRelationship from TagRelationshipEntity as tagRelationship where tagRelationship.primaryKey.article.id = :articleId")
  List<TagRelationshipEntity> findByArticleId(@Param("articleId") UUID articleId);
}
