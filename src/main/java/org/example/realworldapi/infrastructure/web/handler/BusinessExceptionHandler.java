package org.example.realworldapi.infrastructure.web.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.example.realworldapi.application.web.model.response.ErrorResponse;
import org.example.realworldapi.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BusinessExceptionHandler {

  private final Map<
          Class<? extends BusinessException>, Function<BusinessException, ResponseEntity<ErrorResponse>>>
      exceptionMapper;

  public BusinessExceptionHandler() {
    this.exceptionMapper = configureExceptionMapper();
  }

  private Map<
          Class<? extends BusinessException>, Function<BusinessException, ResponseEntity<ErrorResponse>>>
      configureExceptionMapper() {

    final var handlerMap =
        new HashMap<
            Class<? extends BusinessException>,
            Function<BusinessException, ResponseEntity<ErrorResponse>>>();

    handlerMap.put(EmailAlreadyExistsException.class, this::conflict);
    handlerMap.put(UserNotFoundException.class, this::notFound);
    handlerMap.put(InvalidPasswordException.class, this::unauthorized);
    handlerMap.put(UsernameAlreadyExistsException.class, this::conflict);
    handlerMap.put(TagNotFoundException.class, this::notFound);
    handlerMap.put(ArticleNotFoundException.class, this::notFound);
    handlerMap.put(ModelValidationException.class, this::unprocessableEntity);

    return handlerMap;
  }

  private ResponseEntity<ErrorResponse> notFound(BusinessException businessException) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(businessException));
  }

  private ResponseEntity<ErrorResponse> conflict(BusinessException businessException) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse(businessException));
  }

  private ResponseEntity<ErrorResponse> unauthorized(BusinessException businessException) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse(businessException));
  }

  private ResponseEntity<ErrorResponse> unprocessableEntity(BusinessException businessException) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(errorResponse(businessException));
  }

  private ErrorResponse errorResponse(BusinessException businessException) {
    return new ErrorResponse(businessException.getMessages());
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException businessException) {
    return this.exceptionMapper.get(businessException.getClass()).apply(businessException);
  }
}
