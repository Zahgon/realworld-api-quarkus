package org.example.realworldapi.unit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import org.example.realworldapi.infrastructure.repository.hibernate.jpa.utils.SimpleQueryBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SimpleQueryBuilderTest {

  @Test
  @DisplayName("should render only the query statements when no where statement was added")
  void shouldRenderQueryWithoutWhereClause() {
    SimpleQueryBuilder queryBuilder = new SimpleQueryBuilder();
    queryBuilder.addQueryStatement("select articles from ArticleEntity as articles");

    assertEquals("select articles from ArticleEntity as articles", queryBuilder.toQueryString());
  }

  @Test
  @DisplayName("should skip the statements and the callback when the condition is false")
  void shouldSkipConditionalStatements() {
    SimpleQueryBuilder queryBuilder = new SimpleQueryBuilder();
    queryBuilder.addQueryStatement("from ArticleEntity as articles");
    AtomicBoolean callbackExecuted = new AtomicBoolean(false);

    queryBuilder.updateQueryStatementConditional(
        false,
        "inner join articles.tags as tags",
        "upper(tag.name) in (:tags)",
        () -> callbackExecuted.set(true));

    assertEquals("from ArticleEntity as articles", queryBuilder.toQueryString());
    assertEquals(false, callbackExecuted.get());
  }

  @Test
  @DisplayName("should append the statements and run the callback when the condition is true")
  void shouldAppendConditionalStatements() {
    SimpleQueryBuilder queryBuilder = new SimpleQueryBuilder();
    queryBuilder.addQueryStatement("from ArticleEntity as articles");
    AtomicBoolean callbackExecuted = new AtomicBoolean(false);

    queryBuilder.updateQueryStatementConditional(
        true,
        "inner join articles.tags as tags",
        "upper(tag.name) in (:tags)",
        () -> callbackExecuted.set(true));

    assertEquals(
        "from ArticleEntity as articles inner join articles.tags as tags"
            + " where upper(tag.name) in (:tags)",
        queryBuilder.toQueryString());
    assertTrue(callbackExecuted.get());
  }

  @Test
  @DisplayName("should join multiple where statements with 'and'")
  void shouldJoinMultipleWhereStatementsWithAnd() {
    SimpleQueryBuilder queryBuilder = new SimpleQueryBuilder();
    queryBuilder.addQueryStatement("from ArticleEntity as articles");

    queryBuilder.updateQueryStatementConditional(
        true, "inner join articles.tags as tags", "upper(tag.name) in (:tags)", () -> {});
    queryBuilder.updateQueryStatementConditional(
        true,
        "inner join articles.author as authors",
        "upper(authors.username) in (:authors)",
        () -> {});

    assertEquals(
        "from ArticleEntity as articles inner join articles.tags as tags"
            + " inner join articles.author as authors"
            + " where upper(tag.name) in (:tags) and upper(authors.username) in (:authors)",
        queryBuilder.toQueryString());
  }
}
