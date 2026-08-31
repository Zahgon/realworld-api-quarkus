package org.example.realworldapi.unit.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.jupiter.api.Test;

/**
 * Guards the naming strategies declared by the PRODUCTION configuration.
 *
 * <p>Spring Boot substitutes {@code CamelCaseToUnderscoresNamingStrategy} for Hibernate's stock
 * implementations and silently renames every column; the source project ran on Quarkus, which does
 * not, so {@code src/main/resources/application.properties} restores the stock strategies
 * explicitly. That single pair of keys is what keeps the migrated schema addressing the same tables
 * and columns as the original.
 *
 * <p>No other test reads that file. Every test runs under {@code src/test/resources}, which declares
 * its own copy of these keys, so the production configuration could be edited — or the two profiles
 * could drift apart — with the whole suite still green. This test reads the production file as a
 * resource and asserts both keys directly, and then asserts the test profile still agrees with it,
 * because two profiles quietly disagreeing about the naming strategy is the failure that would make
 * every other test misleading rather than red.
 */
public class NamingStrategyConfigurationTest {

  private static final String PHYSICAL = "spring.jpa.hibernate.naming.physical-strategy";
  private static final String IMPLICIT = "spring.jpa.hibernate.naming.implicit-strategy";

  private static final String STOCK_PHYSICAL =
      "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl";
  private static final String STOCK_IMPLICIT =
      "org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl";

  /**
   * Reads a properties file from the source tree rather than the classpath.
   *
   * <p>{@code getResourceAsStream("application.properties")} returns the file under {@code
   * src/test/resources}, which shadows the production one; a first version of this test used it and
   * passed happily while the production strategy was mutated. Surefire runs with the project
   * directory as its working directory, so the path below addresses the real file.
   */
  private Properties load(String path) throws IOException {
    final var file = Path.of(path);
    assertTrue(Files.isRegularFile(file), path + " must exist");
    final var properties = new Properties();
    try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
      properties.load(reader);
    }
    return properties;
  }

  @Test
  public void productionConfigurationKeepsHibernateStockNamingStrategies() throws IOException {
    final var production = load("src/main/resources/application.properties");

    assertEquals(
        STOCK_PHYSICAL,
        production.getProperty(PHYSICAL),
        "the production profile must keep Hibernate's physical naming strategy: Spring Boot's"
            + " default renames every column");
    assertEquals(
        STOCK_IMPLICIT,
        production.getProperty(IMPLICIT),
        "the production profile must keep Hibernate's implicit naming strategy");
  }

  @Test
  public void testProfileDeclaresTheSameNamingStrategiesAsProduction() throws IOException {
    final var production = load("src/main/resources/application.properties");
    final var test = load("src/test/resources/application.properties");

    assertEquals(
        production.getProperty(PHYSICAL),
        test.getProperty(PHYSICAL),
        "the test profile must not disagree with production about the physical naming strategy:"
            + " if it does, every schema assertion in this suite is testing a different mapping");
    assertEquals(
        production.getProperty(IMPLICIT),
        test.getProperty(IMPLICIT),
        "the test profile must not disagree with production about the implicit naming strategy");
  }
}
