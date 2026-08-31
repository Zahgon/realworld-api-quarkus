package org.example.realworldapi.infrastructure.web.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.example.realworldapi.application.web.model.response.ErrorResponse;
import org.example.realworldapi.infrastructure.web.exception.ForbiddenException;
import org.example.realworldapi.infrastructure.web.exception.InfrastructureException;
import org.example.realworldapi.infrastructure.web.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InfrastructureExceptionHandler {

  private final Map<
          Class<? extends InfrastructureException>,
          Function<InfrastructureException, ResponseEntity<ErrorResponse>>>
      exceptionMapper;

  public InfrastructureExceptionHandler() {
    this.exceptionMapper = configureExceptionMapper();
  }

  private Map<
          Class<? extends InfrastructureException>,
          Function<InfrastructureException, ResponseEntity<ErrorResponse>>>
      configureExceptionMapper() {
    final var exceptionMap =
        new HashMap<
            Class<? extends InfrastructureException>,
            Function<InfrastructureException, ResponseEntity<ErrorResponse>>>();
    exceptionMap.put(ForbiddenException.class, this::forbidden);
    exceptionMap.put(UnauthorizedException.class, this::unauthorized);
    return exceptionMap;
  }

  private ResponseEntity<ErrorResponse> forbidden(InfrastructureException infrastructureException) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(errorResponse(HttpStatus.FORBIDDEN.getReasonPhrase()));
  }

  private ResponseEntity<ErrorResponse> unauthorized(
      InfrastructureException infrastructureException) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(errorResponse(HttpStatus.UNAUTHORIZED.getReasonPhrase()));
  }

  private ErrorResponse errorResponse(String message) {
    return new ErrorResponse(message);
  }

  @ExceptionHandler(InfrastructureException.class)
  public ResponseEntity<ErrorResponse> handleInfrastructureException(
      InfrastructureException infrastructureException) {
    return this.exceptionMapper
        .get(infrastructureException.getClass())
        .apply(infrastructureException);
  }
}
