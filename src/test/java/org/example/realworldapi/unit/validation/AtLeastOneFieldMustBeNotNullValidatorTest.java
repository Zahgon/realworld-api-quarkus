package org.example.realworldapi.unit.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.example.realworldapi.infrastructure.web.validation.constraint.AtLeastOneFieldMustBeNotNull;
import org.example.realworldapi.infrastructure.web.validation.validator.AtLeastOneFieldMustBeNotNullValidator;
import org.junit.jupiter.api.Test;

public class AtLeastOneFieldMustBeNotNullValidatorTest {

  @AtLeastOneFieldMustBeNotNull(fieldNames = {"first", "second"})
  static class ExplicitFieldNames {
    private String first;
    private String second;

    ExplicitFieldNames(String first, String second) {
      this.first = first;
      this.second = second;
    }
  }

  @AtLeastOneFieldMustBeNotNull
  static class AllDeclaredFields {
    private String first;
    private String second;

    AllDeclaredFields(String first, String second) {
      this.first = first;
      this.second = second;
    }
  }

  @AtLeastOneFieldMustBeNotNull(fieldNames = {"missingField"})
  static class UnknownFieldName {
    private String first;
  }

  private AtLeastOneFieldMustBeNotNullValidator validatorFor(Class<?> annotatedClass) {
    AtLeastOneFieldMustBeNotNullValidator validator = new AtLeastOneFieldMustBeNotNullValidator();
    validator.initialize(annotatedClass.getAnnotation(AtLeastOneFieldMustBeNotNull.class));
    return validator;
  }

  @Test
  public void shouldBeValidWhenAConfiguredFieldIsNotNull() {
    assertTrue(
        validatorFor(ExplicitFieldNames.class)
            .isValid(new ExplicitFieldNames(null, "value"), null));
  }

  @Test
  public void shouldBeValidWhenTheFirstConfiguredFieldIsNotNull() {
    assertTrue(
        validatorFor(ExplicitFieldNames.class).isValid(new ExplicitFieldNames("value", null), null));
  }

  @Test
  public void shouldBeInvalidWhenEveryConfiguredFieldIsNull() {
    assertFalse(
        validatorFor(ExplicitFieldNames.class).isValid(new ExplicitFieldNames(null, null), null));
  }

  @Test
  public void shouldFallBackToEveryDeclaredFieldWhenNoFieldNamesAreConfigured() {
    assertTrue(
        validatorFor(AllDeclaredFields.class).isValid(new AllDeclaredFields(null, "value"), null));
    assertFalse(
        validatorFor(AllDeclaredFields.class).isValid(new AllDeclaredFields(null, null), null));
  }

  @Test
  public void shouldFailWhenAConfiguredFieldDoesNotExist() {
    AtLeastOneFieldMustBeNotNullValidator validator = validatorFor(UnknownFieldName.class);
    UnknownFieldName instance = new UnknownFieldName();
    assertThrows(RuntimeException.class, () -> validator.isValid(instance, null));
  }
}
