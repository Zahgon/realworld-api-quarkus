package org.example.realworldapi.infrastructure.repository.hibernate.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.ArticleEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleEntityJpaRepository extends JpaRepository<ArticleEntity, UUID> {

  long countBySlugIgnoreCase(String slug);

  Optional<ArticleEntity> findFirstBySlugIgnoreCase(String slug);

  Optional<ArticleEntity> findFirstByAuthorIdAndSlugIgnoreCase(UUID authorId, String slug);

  @Query(
      "select articles from ArticleEntity as articles inner join articles.author as author inner join author.followedBy as followedBy where followedBy.user.id = :loggedUserId order by articles.createdAt desc, articles.updatedAt desc")
  List<ArticleEntity> findMostRecentByFollowedUser(
      @Param("loggedUserId") UUID loggedUserId, Pageable pageable);

  @Query(
      "select count(*) from ArticleEntity as articles inner join articles.author as author inner join author.followedBy as followedBy where followedBy.user.id = :loggedUserId")
  long countMostRecentByFollowedUser(@Param("loggedUserId") UUID loggedUserId);
}
