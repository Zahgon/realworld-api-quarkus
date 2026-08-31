package org.example.realworldapi.infrastructure.repository.hibernate.jpa;

import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.example.realworldapi.domain.model.article.Article;
import org.example.realworldapi.domain.model.article.FavoriteRelationship;
import org.example.realworldapi.domain.model.article.FavoriteRelationshipRepository;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.EntityUtils;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.FavoriteRelationshipEntity;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class FavoriteRelationshipRepositoryJpa extends AbstractJpaRepository
    implements FavoriteRelationshipRepository {

  private final FavoriteRelationshipEntityJpaRepository favoriteRelationshipEntityJpaRepository;
  private final EntityUtils entityUtils;

  @Override
  public boolean isFavorited(Article article, UUID currentUserId) {
    return favoriteRelationshipEntityJpaRepository.countByArticleIdAndUserId(
            article.getId(), currentUserId)
        > 0;
  }

  @Override
  public long favoritesCount(Article article) {
    return favoriteRelationshipEntityJpaRepository.countByArticleId(article.getId());
  }

  @Override
  public Optional<FavoriteRelationship> findByArticleIdAndUserId(
      UUID articleId, UUID currentUserId) {
    return favoriteRelationshipEntityJpaRepository
        .findFirstByArticleIdAndUserId(articleId, currentUserId)
        .map(entityUtils::favoriteRelationship);
  }

  @Override
  public void save(FavoriteRelationship favoriteRelationship) {
    final var userEntity = findUserEntityById(favoriteRelationship.getUser().getId());
    final var articleEntity = findArticleEntityById(favoriteRelationship.getArticle().getId());
    entityManager.persist(new FavoriteRelationshipEntity(userEntity, articleEntity));
  }

  @Override
  public void delete(FavoriteRelationship favoriteRelationship) {
    final var favoriteRelationshipEntity =
        findFavoriteRelationshipEntityByKey(favoriteRelationship);
    entityManager.remove(favoriteRelationshipEntity);
  }
}
