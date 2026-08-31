package org.example.realworldapi.infrastructure.repository.hibernate.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentEntityJpaRepository extends JpaRepository<CommentEntity, UUID> {

  Optional<CommentEntity> findFirstByIdAndAuthorId(UUID commentId, UUID authorId);

  List<CommentEntity> findByArticleId(UUID articleId);
}
