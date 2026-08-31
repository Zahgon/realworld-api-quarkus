package org.example.realworldapi.infrastructure.web.handler;

import jakarta.validation.ConstraintViolationException;
import org.example.realworldapi.application.web.model.response.ErrorResponse;
import org.example.realworldapi.domain.model.constants.ValidationMessages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

/**
 * Spring MVC replacement for the JAX-RS {@code BeanValidationExceptionMapper}. Spring reports bean
 * validation failures through several exception types depending on where the constraint lives, so
 * each of them is funnelled into the same {@code 422} + {@code ErrorResponse} contract the original
 * mapper produced.
 */
@RestControllerAdvice
public class BeanValidationExceptionHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {

    ErrorResponse errorResponse = new ErrorResponse();

    e.getConstraintViolations()
        .iterator()
        .forEachRemaining(
            contraint -> {
              errorResponse.getBody().add(contraint.getMessage());
            });

    return unprocessableEntity(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e) {

    ErrorResponse errorResponse = new ErrorResponse();

    e.getAllErrors()
        .forEach(objectError -> errorResponse.getBody().add(objectError.getDefaultMessage()));

    return unprocessableEntity(errorResponse);
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ErrorResponse> handleHandlerMethodValidation(
      HandlerMethodValidationException e) {

    ErrorResponse errorResponse = new ErrorResponse();

    e.getAllErrors()
        .forEach(messageSourceResolvable -> errorResponse.getBody().add(
            messageSourceResolvable.getDefaultMessage()));

    return unprocessableEntity(errorResponse);
  }

  /**
   * The original resources declared {@code @NotNull(message = REQUEST_BODY_MUST_BE_NOT_NULL)} on
   * the request body parameter. Spring rejects an absent or unreadable body before validation runs,
   * so the same message is emitted here.
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleMissingRequestBody(HttpMessageNotReadableException e) {
    return unprocessableEntity(new ErrorResponse(ValidationMessages.REQUEST_BODY_MUST_BE_NOT_NULL));
  }

  private ResponseEntity<ErrorResponse> unprocessableEntity(ErrorResponse errorResponse) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
  }
}
