package org.example.realworldapi.infrastructure.repository.hibernate.jpa;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.example.realworldapi.domain.model.article.Article;
import org.example.realworldapi.domain.model.article.TagRelationship;
import org.example.realworldapi.domain.model.article.TagRelationshipRepository;
import org.example.realworldapi.domain.model.tag.Tag;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.EntityUtils;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.TagRelationshipEntity;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class TagRelationshipRepositoryJpa extends AbstractJpaRepository
    implements TagRelationshipRepository {

  private final TagRelationshipEntityJpaRepository tagRelationshipEntityJpaRepository;
  private final EntityUtils entityUtils;

  @Override
  public void save(TagRelationship tagRelationship) {
    final var articleEntity = findArticleEntityById(tagRelationship.getArticle().getId());
    final var tagEntity = findTagEntityById(tagRelationship.getTag().getId());
    entityManager.persist(new TagRelationshipEntity(articleEntity, tagEntity));
  }

  @Override
  public List<Tag> findArticleTags(Article article) {
    return tagRelationshipEntityJpaRepository.findByArticleId(article.getId()).stream()
        .map(entityUtils::tag)
        .collect(Collectors.toList());
  }
}
