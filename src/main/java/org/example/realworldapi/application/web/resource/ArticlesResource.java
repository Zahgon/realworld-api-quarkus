package org.example.realworldapi.application.web.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.example.realworldapi.application.web.model.request.NewArticleRequest;
import org.example.realworldapi.application.web.model.request.NewCommentRequest;
import org.example.realworldapi.application.web.model.request.UpdateArticleRequest;
import org.example.realworldapi.application.web.model.response.ArticleResponse;
import org.example.realworldapi.application.web.model.response.CommentResponse;
import org.example.realworldapi.application.web.resource.utils.ResourceUtils;
import org.example.realworldapi.domain.feature.*;
import org.example.realworldapi.domain.model.article.ArticleFilter;
import org.example.realworldapi.domain.model.comment.DeleteCommentInput;
import org.example.realworldapi.domain.model.constants.ValidationMessages;
import org.example.realworldapi.infrastructure.web.qualifiers.NoWrapRootValueObjectMapper;
import org.example.realworldapi.infrastructure.web.security.annotation.Secured;
import org.example.realworldapi.infrastructure.web.security.context.SecurityContext;
import org.example.realworldapi.infrastructure.web.security.profile.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/articles")
@Validated
@AllArgsConstructor
public class ArticlesResource {

  private final FindArticlesByFilter findArticlesByFilter;
  private final CreateArticle createArticle;
  private final FindMostRecentArticlesByFilter findMostRecentArticlesByFilter;
  private final FindArticleBySlug findArticleBySlug;
  private final UpdateArticleBySlug updateArticleBySlug;
  private final DeleteArticleBySlug deleteArticleBySlug;
  private final CreateComment createComment;
  private final DeleteComment deleteComment;
  private final FindCommentsByArticleSlug findCommentsByArticleSlug;
  private final FavoriteArticle favoriteArticle;
  private final UnfavoriteArticle unfavoriteArticle;
  @NoWrapRootValueObjectMapper ObjectMapper objectMapper;
  private final ResourceUtils resourceUtils;

  @GetMapping(path = "/feed", produces = MediaType.APPLICATION_JSON_VALUE)
  @Secured({Role.USER, Role.ADMIN})
  public ResponseEntity<String> feed(
      @RequestParam(name = "offset", defaultValue = "0") int offset,
      @RequestParam(name = "limit", defaultValue = "0") int limit,
      SecurityContext securityContext)
      throws JsonProcessingException {
    final var loggedUserId = resourceUtils.getLoggedUserId(securityContext);
    final var articlesFilter =
        new ArticleFilter(offset, resourceUtils.getLimit(limit), loggedUserId, null, null, null);
    final var articlesPageResult = findMostRecentArticlesByFilter.handle(articlesFilter);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            objectMapper.writeValueAsString(
                resourceUtils.articlesResponse(articlesPageResult, loggedUserId)));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Secured(optional = true)
  public ResponseEntity<String> getArticles(
      @RequestParam(name = "offset", defaultValue = "0") int offset,
      @RequestParam(name = "limit", defaultValue = "0") int limit,
      @RequestParam(name = "tag", required = false) List<String> tags,
      @RequestParam(name = "author", required = false) List<String> authors,
      @RequestParam(name = "favorited", required = false) List<String> favorited,
      SecurityContext securityContext)
      throws JsonProcessingException {
    final var loggedUserId = resourceUtils.getLoggedUserId(securityContext);
    final var filter =
        new ArticleFilter(
            offset, resourceUtils.getLimit(limit), loggedUserId, tags, authors, favorited);
    final var articlesPageResult = findArticlesByFilter.handle(filter);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            objectMapper.writeValueAsString(
                resourceUtils.articlesResponse(articlesPageResult, loggedUserId)));
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  @Secured({Role.ADMIN, Role.USER})
  public ResponseEntity<ArticleResponse> create(
      @RequestBody @Valid @NotNull(message = ValidationMessages.REQUEST_BODY_MUST_BE_NOT_NULL)
          NewArticleRequest newArticleRequest,
      SecurityContext securityContext) {
    final var loggedUserId = resourceUtils.getLoggedUserId(securityContext);
    final var article = createArticle.handle(newArticleRequest.toNewArticleInput(loggedUserId));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(resourceUtils.articleResponse(article, loggedUserId));
  }

  @GetMapping(path = "/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ArticleResponse> findBySlug(
      @PathVariable("slug") @NotBlank(message = ValidationMessages.SLUG_MUST_BE_NOT_BLANK)
          String slug) {
    final var article = findArticleBySlug.handle(slug);
    return ResponseEntity.status(HttpStatus.OK).body(resourceUtils.articleResponse(article, null));
  }

  @PutMapping(
      path = "/{slug}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  @Secured({Role.ADMIN, Role.USER})
  public ResponseEntity<ArticleResponse> update(
      @PathVariable("slug") @NotBlank String slug,
      @RequestBody @Valid @NotNull UpdateArticleRequest updateArticleRequest,
      SecurityContext securityContext) {
    final var loggedUserId = resourceUtils.getLoggedUserId(securityContext);
    final var updatedArticle =
        updateArticleBySlug.handle(updateArticleRequest.toUpdateArticleInput(loggedUserId, slug));
    return ResponseEntity.status(HttpStatus.OK)
        .body(resourceUtils.articleResponse(updatedArticle, null));
  }

  @DeleteMapping(path = "/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  @Secured({Role.ADMIN, Role.USER})
  public ResponseEntity<Void> delete(
      @PathVariable("slug") @NotBlank(message = ValidationMessages.SLUG_MUST_BE_NOT_BLANK)
          String slug,
      SecurityContext securityContext) {
    final var loggedUserId = resourceUtils.getLoggedUserId(securityContext);
    deleteArticleBySlug.handle(loggedUserId, slug);
    return ResponseEntity.ok().build();
  }

  @GetMapping(path = "/{slug}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
  @Secured(optional = true)
  public ResponseEntity<String> getCommentsBySlug(
      @PathVariable("slug") @NotBlank(message = ValidationMessages.SLUG_MUST_BE_NOT_BLANK)
          String slug,
      SecurityContext securityContext)
      throws JsonProcessingException {
    final var loggedUserId = resourceUtils.getLoggedUserId(securityContext);
    final var comments = findCommentsByArticleSlug.handle(slug);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            objectMapper.writeValueAsString(
                resourceUtils.commentsResponse(comments, loggedUserId)));
  }

  @PostMapping(
      path = "/{slug}/comments",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  @Secured({Role.ADMIN, Role.USER})
  public ResponseEntity<CommentResponse> createComment(
      @PathVariable("slug") @NotBlank(message = ValidationMessages.SLUG_MUST_BE_NOT_BLANK)
          String slug,
      @RequestBody @Valid NewCommentRequest newCommentRequest,
      SecurityContext securityContext) {
    final var loggedUserId = resourceUtils.getLoggedUserId(securityContext);
    final var comment =
        createComment.handle(newCommentRequest.toNewCommentInput(loggedUserId, slug));
    return ResponseEntity.status(HttpStatus.OK)
        .body(resourceUtils.commentResponse(comment, loggedUserId));
  }

  @DeleteMapping(path = "/{slug}/comments/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  @Secured({Role.ADMIN, Role.USER})
  public ResponseEntity<Void> deleteComment(
      @PathVariable("slug") @NotBlank(message = ValidationMessages.SLUG_MUST_BE_NOT_BLANK)
          String slug,
      @PathVariable("id") @NotNull(message = ValidationMessages.COMMENT_ID_MUST_BE_NOT_NULL) UUID id,
      SecurityContext securityContext) {
    final var loggedUserId = resourceUtils.getLoggedUserId(securityContext);
    deleteComment.handle(new DeleteCommentInput(id, loggedUserId, slug));
    return ResponseEntity.ok().build();
  }

  @PostMapping(path = "/{slug}/favorite", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  @Secured({Role.ADMIN, Role.USER})
  public ResponseEntity<ArticleResponse> favoriteArticle(
      @PathVariable("slug") @NotBlank(message = ValidationMessages.SLUG_MUST_BE_NOT_BLANK)
          String slug,
      SecurityContext securityContext) {
    final var loggedUserId = resourceUtils.getLoggedUserId(securityContext);
    favoriteArticle.handle(slug, loggedUserId);
    final var article = findArticleBySlug.handle(slug);
    return ResponseEntity.status(HttpStatus.OK)
        .body(resourceUtils.articleResponse(article, loggedUserId));
  }

  @DeleteMapping(path = "/{slug}/favorite", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  @Secured({Role.ADMIN, Role.USER})
  public ResponseEntity<ArticleResponse> unfavoriteArticle(
      @PathVariable("slug") @NotBlank(message = ValidationMessages.SLUG_MUST_BE_NOT_BLANK)
          String slug,
      SecurityContext securityContext) {
    final var loggedUserId = resourceUtils.getLoggedUserId(securityContext);
    unfavoriteArticle.handle(slug, loggedUserId);
    final var article = findArticleBySlug.handle(slug);
    return ResponseEntity.status(HttpStatus.OK)
        .body(resourceUtils.articleResponse(article, loggedUserId));
  }
}
