package org.example.realworldapi.infrastructure.configuration;

import org.example.realworldapi.domain.feature.*;
import org.example.realworldapi.domain.feature.impl.CreateCommentImpl;
import org.example.realworldapi.domain.feature.impl.DeleteCommentImpl;
import org.example.realworldapi.domain.feature.impl.FindCommentByIdAndAuthorImpl;
import org.example.realworldapi.domain.feature.impl.FindCommentsByArticleSlugImpl;
import org.example.realworldapi.domain.model.comment.CommentBuilder;
import org.example.realworldapi.domain.model.comment.CommentRepository;
import org.example.realworldapi.domain.validator.ModelValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommentsConfiguration {

  @Bean
  public CreateComment createComment(
      CommentRepository commentRepository,
      FindUserById findUserById,
      FindArticleBySlug findArticleBySlug,
      CommentBuilder commentBuilder) {
    return new CreateCommentImpl(
        commentRepository, findUserById, findArticleBySlug, commentBuilder);
  }

  @Bean
  public DeleteComment deleteComment(
      FindCommentByIdAndAuthor findCommentByIdAndAuthor, CommentRepository commentRepository) {
    return new DeleteCommentImpl(findCommentByIdAndAuthor, commentRepository);
  }

  @Bean
  public FindCommentByIdAndAuthor findCommentByIdAndAuthor(CommentRepository commentRepository) {
    return new FindCommentByIdAndAuthorImpl(commentRepository);
  }

  @Bean
  public FindCommentsByArticleSlug findCommentsByArticleSlug(
      FindArticleBySlug findArticleBySlug, CommentRepository commentRepository) {
    return new FindCommentsByArticleSlugImpl(findArticleBySlug, commentRepository);
  }

  @Bean
  public CommentBuilder commentBuilder(ModelValidator modelValidator) {
    return new CommentBuilder(modelValidator);
  }
}
