package org.example.realworldapi.infrastructure.repository.hibernate.jpa;

import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.example.realworldapi.domain.model.article.Article;
import org.example.realworldapi.domain.model.article.ArticleFilter;
import org.example.realworldapi.domain.model.article.ArticleRepository;
import org.example.realworldapi.domain.model.article.PageResult;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.ArticleEntity;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.EntityUtils;
import org.example.realworldapi.infrastructure.repository.hibernate.jpa.utils.SimpleQueryBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ArticleRepositoryJpa extends AbstractJpaRepository implements ArticleRepository {

  private static final String ORDER_BY_MOST_RECENT =
      " order by articles.createdAt desc, articles.updatedAt desc";

  private final ArticleEntityJpaRepository articleEntityJpaRepository;
  private final EntityUtils entityUtils;

  @Override
  public boolean existsBySlug(String slug) {
    return articleEntityJpaRepository.countBySlugIgnoreCase(slug.trim()) > 0;
  }

  @Override
  public void save(Article article) {
    final var author = findUserEntityById(article.getAuthor().getId());
    entityManager.persist(new ArticleEntity(article, author));
    entityManager.flush();
  }

  @Override
  public Optional<Article> findArticleById(UUID id) {
    return articleEntityJpaRepository.findById(id).map(entityUtils::article);
  }

  @Override
  public Optional<Article> findBySlug(String slug) {
    return articleEntityJpaRepository
        .findFirstBySlugIgnoreCase(slug.trim())
        .map(entityUtils::article);
  }

  @Override
  public void update(Article article) {
    final var articleEntity = findArticleEntityById(article.getId());
    articleEntity.update(article);
  }

  @Override
  public Optional<Article> findByAuthorAndSlug(UUID authorId, String slug) {
    return articleEntityJpaRepository
        .findFirstByAuthorIdAndSlugIgnoreCase(authorId, slug.trim())
        .map(entityUtils::article);
  }

  @Override
  public void delete(Article article) {
    articleEntityJpaRepository.deleteById(article.getId());
  }

  @Override
  public PageResult<Article> findMostRecentArticlesByFilter(ArticleFilter articleFilter) {
    final var articlesEntity =
        articleEntityJpaRepository.findMostRecentByFollowedUser(
            articleFilter.getLoggedUserId(),
            PageRequest.of(articleFilter.getOffset(), articleFilter.getLimit()));
    final var articlesResult =
        articlesEntity.stream().map(entityUtils::article).collect(Collectors.toList());
    final var total = count(articleFilter.getLoggedUserId());
    return new PageResult<>(articlesResult, total);
  }

  @Override
  public PageResult<Article> findArticlesByFilter(ArticleFilter filter) {
    Map<String, Object> params = new LinkedHashMap<>();
    SimpleQueryBuilder findArticlesQueryBuilder = new SimpleQueryBuilder();
    findArticlesQueryBuilder.addQueryStatement("select articles from ArticleEntity as articles");
    configFilterFindArticlesQueryBuilder(
        findArticlesQueryBuilder,
        filter.getTags(),
        filter.getAuthors(),
        filter.getFavorited(),
        params);

    final var query =
        entityManager.createQuery(
            findArticlesQueryBuilder.toQueryString() + ORDER_BY_MOST_RECENT, ArticleEntity.class);
    params.forEach(query::setParameter);
    query.setFirstResult(filter.getOffset() * filter.getLimit());
    query.setMaxResults(filter.getLimit());

    final var articlesResult =
        query.getResultList().stream().map(entityUtils::article).collect(Collectors.toList());
    final var total = count(filter.getTags(), filter.getAuthors(), filter.getFavorited());
    return new PageResult<>(articlesResult, total);
  }

  @Override
  public long count(List<String> tags, List<String> authors, List<String> favorited) {
    Map<String, Object> params = new LinkedHashMap<>();
    SimpleQueryBuilder countArticlesQueryBuilder = new SimpleQueryBuilder();
    countArticlesQueryBuilder.addQueryStatement("from ArticleEntity as articles");
    configFilterFindArticlesQueryBuilder(
        countArticlesQueryBuilder, tags, authors, favorited, params);

    final var query =
        entityManager.createQuery(
            "select count(*) " + countArticlesQueryBuilder.toQueryString(), Long.class);
    params.forEach(query::setParameter);
    return query.getSingleResult();
  }

  public long count(UUID loggedUserId) {
    return articleEntityJpaRepository.countMostRecentByFollowedUser(loggedUserId);
  }

  private void configFilterFindArticlesQueryBuilder(
      SimpleQueryBuilder findArticlesQueryBuilder,
      List<String> tags,
      List<String> authors,
      List<String> favorited,
      Map<String, Object> params) {

    findArticlesQueryBuilder.updateQueryStatementConditional(
        isNotEmpty(tags),
        "inner join articles.tags as tags inner join tags.primaryKey.tag as tag",
        "upper(tag.name) in (:tags)",
        () -> params.put("tags", toUpperCase(tags)));

    findArticlesQueryBuilder.updateQueryStatementConditional(
        isNotEmpty(authors),
        "inner join articles.author as authors",
        "upper(authors.username) in (:authors)",
        () -> params.put("authors", toUpperCase(authors)));

    findArticlesQueryBuilder.updateQueryStatementConditional(
        isNotEmpty(favorited),
        "inner join articles.favorites as favorites inner join favorites.primaryKey.user as user",
        "upper(user.username) in (:favorites)",
        () -> params.put("favorites", toUpperCase(favorited)));
  }
}
