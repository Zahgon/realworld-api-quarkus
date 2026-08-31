package org.example.realworldapi.infrastructure.configuration;

import org.example.realworldapi.domain.feature.*;
import org.example.realworldapi.domain.feature.impl.*;
import org.example.realworldapi.domain.model.article.ArticleModelBuilder;
import org.example.realworldapi.domain.model.article.ArticleRepository;
import org.example.realworldapi.domain.model.article.FavoriteRelationshipRepository;
import org.example.realworldapi.domain.model.article.TagRelationshipRepository;
import org.example.realworldapi.domain.model.provider.SlugProvider;
import org.example.realworldapi.domain.validator.ModelValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArticlesConfiguration {

  @Bean
  public CreateArticle createArticle(
      FindUserById findUserById,
      ArticleRepository articleRepository,
      ArticleModelBuilder articleBuilder,
      CreateSlugByTitle createSlugByTitle,
      FindTagsByNameCreateIfNotExists findTagsByNameCreateIfNotExists,
      TagRelationshipRepository tagRelationshipRepository) {
    return new CreateArticleImpl(
        findUserById,
        articleRepository,
        articleBuilder,
        createSlugByTitle,
        findTagsByNameCreateIfNotExists,
        tagRelationshipRepository);
  }

  @Bean
  public UpdateArticleBySlug updateArticleBySlug(
      FindArticleBySlug findArticleBySlug,
      CreateSlugByTitle createSlugByTitle,
      ArticleRepository articleRepository,
      ModelValidator modelValidator) {
    return new UpdateArticleBySlugImpl(
        findArticleBySlug, createSlugByTitle, articleRepository, modelValidator);
  }

  @Bean
  public DeleteArticleBySlug deleteArticleBySlug(
      FindArticleByAuthorAndSlug findArticleByAuthorAndSlug, ArticleRepository articleRepository) {
    return new DeleteArticleBySlugImpl(findArticleByAuthorAndSlug, articleRepository);
  }

  @Bean
  public FindArticleById findArticleById(ArticleRepository articleRepository) {
    return new FindArticleByIdImpl(articleRepository);
  }

  @Bean
  public FindArticleByAuthorAndSlug findArticleByAuthorAndSlug(
      ArticleRepository articleRepository) {
    return new FindArticleByAuthorAndSlugImpl(articleRepository);
  }

  @Bean
  public FindArticleBySlug findArticleBySlug(ArticleRepository articleRepository) {
    return new FindArticleBySlugImpl(articleRepository);
  }

  @Bean
  public FindArticleTags findArticleTags(TagRelationshipRepository tagRelationshipRepository) {
    return new FindArticleTagsImpl(tagRelationshipRepository);
  }

  @Bean
  public FindMostRecentArticlesByFilter findMostRecentArticlesByFilter(
      ArticleRepository articleRepository) {
    return new FindMostRecentArticlesByFilterImpl(articleRepository);
  }

  @Bean
  public FindArticlesByFilter findArticlesByFilter(ArticleRepository articleRepository) {
    return new FindArticlesByFilterImpl(articleRepository);
  }

  @Bean
  public IsArticleFavorited isArticleFavorited(
      FavoriteRelationshipRepository favoriteRelationshipRepository) {
    return new IsArticleFavoritedImpl(favoriteRelationshipRepository);
  }

  @Bean
  public ArticleFavoritesCount articleFavoritesCount(
      FindArticleById findArticleById,
      FavoriteRelationshipRepository favoriteRelationshipRepository) {
    return new ArticleFavoritesCountImpl(findArticleById, favoriteRelationshipRepository);
  }

  @Bean
  public CreateSlugByTitle createSlugByTitle(
      ArticleRepository articleRepository, SlugProvider slugProvider) {
    return new CreateSlugByTitleImpl(articleRepository, slugProvider);
  }

  @Bean
  public FavoriteArticle favoriteArticle(
      FindArticleBySlug findArticleBySlug,
      FindUserById findUserById,
      FavoriteRelationshipRepository favoriteRelationshipRepository) {
    return new FavoriteArticleImpl(findArticleBySlug, findUserById, favoriteRelationshipRepository);
  }

  @Bean
  public UnfavoriteArticle unfavoriteArticle(
      FindArticleBySlug findArticleBySlug,
      FavoriteRelationshipRepository favoriteRelationshipRepository) {
    return new UnfavoriteArticleImpl(findArticleBySlug, favoriteRelationshipRepository);
  }

  @Bean
  public ArticleModelBuilder articleBuilder(ModelValidator modelValidator) {
    return new ArticleModelBuilder(modelValidator);
  }
}
