package org.example.realworldapi.infrastructure.repository.hibernate.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.example.realworldapi.domain.model.article.FavoriteRelationship;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.*;

public abstract class AbstractJpaRepository {

  @PersistenceContext protected EntityManager entityManager;

  protected UserEntity findUserEntityById(UUID id) {
    return entityManager.find(UserEntity.class, id);
  }

  protected TagEntity findTagEntityById(UUID id) {
    return entityManager.find(TagEntity.class, id);
  }

  protected ArticleEntity findArticleEntityById(UUID id) {
    return entityManager.find(ArticleEntity.class, id);
  }

  protected CommentEntity findCommentEntityById(UUID id) {
    return entityManager.find(CommentEntity.class, id);
  }

  protected FavoriteRelationshipEntity findFavoriteRelationshipEntityByKey(
      FavoriteRelationship favoriteRelationship) {

    final var userEntity = findUserEntityById(favoriteRelationship.getUser().getId());
    final var articleEntity = findArticleEntityById(favoriteRelationship.getArticle().getId());

    final var favoriteRelationshipEntityKey = new FavoriteRelationshipEntityKey();
    favoriteRelationshipEntityKey.setUser(userEntity);
    favoriteRelationshipEntityKey.setArticle(articleEntity);

    return entityManager.find(FavoriteRelationshipEntity.class, favoriteRelationshipEntityKey);
  }

  protected boolean isNotEmpty(List<?> list) {
    return list != null && !list.isEmpty();
  }

  protected List<String> toUpperCase(List<String> subjectList) {
    return subjectList.stream().map(String::toUpperCase).collect(Collectors.toList());
  }
}
