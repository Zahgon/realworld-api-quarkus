package org.example.realworldapi.infrastructure.repository.hibernate.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.example.realworldapi.domain.model.article.Article;
import org.example.realworldapi.domain.model.comment.Comment;
import org.example.realworldapi.domain.model.comment.CommentRepository;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.CommentEntity;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.EntityUtils;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CommentRepositoryJpa extends AbstractJpaRepository implements CommentRepository {

  private final CommentEntityJpaRepository commentEntityJpaRepository;
  private final EntityUtils entityUtils;

  @Override
  public void save(Comment comment) {
    final var authorEntity = findUserEntityById(comment.getAuthor().getId());
    final var articleEntity = findArticleEntityById(comment.getArticle().getId());
    entityManager.persist(new CommentEntity(authorEntity, articleEntity, comment));
  }

  @Override
  public Optional<Comment> findByIdAndAuthor(UUID commentId, UUID authorId) {
    return commentEntityJpaRepository
        .findFirstByIdAndAuthorId(commentId, authorId)
        .map(entityUtils::comment);
  }

  @Override
  public void delete(Comment comment) {
    final var commentEntity = findCommentEntityById(comment.getId());
    entityManager.remove(commentEntity);
  }

  @Override
  public List<Comment> findCommentsByArticle(Article article) {
    return commentEntityJpaRepository.findByArticleId(article.getId()).stream()
        .map(entityUtils::comment)
        .collect(Collectors.toList());
  }
}
