package org.example.realworldapi.unit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.ArticleEntity;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.FavoriteRelationshipEntityKey;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.FollowRelationshipEntityKey;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.TagEntity;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.TagRelationshipEntityKey;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.UserEntity;
import org.junit.jupiter.api.Test;

public class EntityKeysTest {

  private ArticleEntity article(UUID id) {
    ArticleEntity articleEntity = new ArticleEntity();
    articleEntity.setId(id);
    return articleEntity;
  }

  private UserEntity user(UUID id) {
    UserEntity userEntity = new UserEntity();
    userEntity.setId(id);
    return userEntity;
  }

  private TagEntity tag(UUID id) {
    TagEntity tagEntity = new TagEntity();
    tagEntity.setId(id);
    return tagEntity;
  }

  private FavoriteRelationshipEntityKey favoriteKey(ArticleEntity articleEntity, UserEntity userEntity) {
    FavoriteRelationshipEntityKey key = new FavoriteRelationshipEntityKey();
    key.setArticle(articleEntity);
    key.setUser(userEntity);
    return key;
  }

  private FollowRelationshipEntityKey followKey(UserEntity userEntity, UserEntity followed) {
    FollowRelationshipEntityKey key = new FollowRelationshipEntityKey();
    key.setUser(userEntity);
    key.setFollowed(followed);
    return key;
  }

  @Test
  public void favoriteKeyShouldBeEqualToItself() {
    FavoriteRelationshipEntityKey key = favoriteKey(article(UUID.randomUUID()), user(UUID.randomUUID()));
    assertTrue(key.equals(key));
  }

  @Test
  public void favoriteKeyShouldNotBeEqualToNullOrOtherType() {
    FavoriteRelationshipEntityKey key = favoriteKey(article(UUID.randomUUID()), user(UUID.randomUUID()));
    assertNotEquals(key, null);
    assertNotEquals(key, "not-a-key");
  }

  @Test
  public void favoriteKeysWithSameArticleAndUserShouldBeEqual() {
    UUID articleId = UUID.randomUUID();
    UserEntity userEntity = user(UUID.randomUUID());
    FavoriteRelationshipEntityKey first = favoriteKey(article(articleId), userEntity);
    FavoriteRelationshipEntityKey second = favoriteKey(article(articleId), userEntity);
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  public void favoriteKeysWithDifferentArticleShouldNotBeEqual() {
    UserEntity userEntity = user(UUID.randomUUID());
    FavoriteRelationshipEntityKey first = favoriteKey(article(UUID.randomUUID()), userEntity);
    FavoriteRelationshipEntityKey second = favoriteKey(article(UUID.randomUUID()), userEntity);
    assertNotEquals(first, second);
  }

  @Test
  public void followKeyShouldBeEqualToItselfAndNotToNullOrOtherType() {
    FollowRelationshipEntityKey key = followKey(user(UUID.randomUUID()), user(UUID.randomUUID()));
    assertTrue(key.equals(key));
    assertNotEquals(key, null);
    assertNotEquals(key, new Object());
  }

  @Test
  public void followKeysWithSameUsersShouldBeEqual() {
    UserEntity currentUser = user(UUID.randomUUID());
    UserEntity followed = user(UUID.randomUUID());
    FollowRelationshipEntityKey first = followKey(currentUser, followed);
    FollowRelationshipEntityKey second = followKey(currentUser, followed);
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  public void followKeysWithDifferentFollowedShouldNotBeEqual() {
    UserEntity currentUser = user(UUID.randomUUID());
    FollowRelationshipEntityKey first = followKey(currentUser, user(UUID.randomUUID()));
    FollowRelationshipEntityKey second = followKey(currentUser, user(UUID.randomUUID()));
    assertNotEquals(first, second);
  }

  @Test
  public void tagRelationshipKeyShouldBeEqualToItselfAndNotToNullOrOtherType() {
    TagRelationshipEntityKey key =
        new TagRelationshipEntityKey(article(UUID.randomUUID()), tag(UUID.randomUUID()));
    assertTrue(key.equals(key));
    assertNotEquals(key, null);
    assertNotEquals(key, "other");
  }

  @Test
  public void tagRelationshipKeysWithSameArticleAndTagShouldBeEqual() {
    UUID articleId = UUID.randomUUID();
    TagEntity tagEntity = tag(UUID.randomUUID());
    TagRelationshipEntityKey first = new TagRelationshipEntityKey(article(articleId), tagEntity);
    TagRelationshipEntityKey second = new TagRelationshipEntityKey(article(articleId), tagEntity);
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  public void tagRelationshipKeysWithDifferentTagShouldNotBeEqual() {
    ArticleEntity articleEntity = article(UUID.randomUUID());
    TagRelationshipEntityKey first = new TagRelationshipEntityKey(articleEntity, tag(UUID.randomUUID()));
    TagRelationshipEntityKey second = new TagRelationshipEntityKey(articleEntity, tag(UUID.randomUUID()));
    assertNotEquals(first, second);
  }
}
