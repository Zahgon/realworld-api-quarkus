package org.example.realworldapi.unit.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.example.realworldapi.application.web.model.response.ErrorResponse;
import org.example.realworldapi.domain.exception.ArticleNotFoundException;
import org.example.realworldapi.domain.exception.BusinessException;
import org.example.realworldapi.domain.exception.EmailAlreadyExistsException;
import org.example.realworldapi.domain.exception.InvalidPasswordException;
import org.example.realworldapi.domain.exception.ModelValidationException;
import org.example.realworldapi.domain.exception.TagNotFoundException;
import org.example.realworldapi.domain.exception.UserNotFoundException;
import org.example.realworldapi.domain.exception.UsernameAlreadyExistsException;
import org.example.realworldapi.infrastructure.web.handler.BusinessExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class BusinessExceptionHandlerTest {

  private BusinessExceptionHandler businessExceptionHandler;

  @BeforeEach
  void beforeEach() {
    businessExceptionHandler = new BusinessExceptionHandler();
  }

  static List<Arguments> businessExceptions() {
    return List.of(
        Arguments.of(new EmailAlreadyExistsException(), HttpStatus.CONFLICT),
        Arguments.of(new UsernameAlreadyExistsException(), HttpStatus.CONFLICT),
        Arguments.of(new UserNotFoundException(), HttpStatus.NOT_FOUND),
        Arguments.of(new TagNotFoundException(), HttpStatus.NOT_FOUND),
        Arguments.of(new ArticleNotFoundException(), HttpStatus.NOT_FOUND),
        Arguments.of(new InvalidPasswordException(), HttpStatus.UNAUTHORIZED),
        Arguments.of(
            new ModelValidationException(List.of("first error", "second error")),
            HttpStatus.UNPROCESSABLE_ENTITY));
  }

  @ParameterizedTest
  @MethodSource("businessExceptions")
  void shouldMapBusinessExceptionToItsHttpStatus(
      BusinessException businessException, HttpStatus expectedStatus) {

    ResponseEntity<ErrorResponse> response = businessExceptionHandler.handleBusinessException(businessException);

    assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getBody())
        .containsExactlyElementsOf(businessException.getMessages());
  }

  @Test
  void shouldCarryEveryValidationMessage() {
    ModelValidationException modelValidationException =
        new ModelValidationException(List.of("title must not be blank", "body must not be blank"));

    ResponseEntity<ErrorResponse> response = businessExceptionHandler.handleBusinessException(modelValidationException);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(response.getBody().getBody())
        .containsExactly("title must not be blank", "body must not be blank");
  }
}
