package org.example.realworldapi.unit.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.realworldapi.application.web.model.response.ErrorResponse;
import org.example.realworldapi.infrastructure.web.exception.ForbiddenException;
import org.example.realworldapi.infrastructure.web.exception.UnauthorizedException;
import org.example.realworldapi.infrastructure.web.handler.InfrastructureExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class InfrastructureExceptionHandlerTest {

  private InfrastructureExceptionHandler infrastructureExceptionHandler;

  @BeforeEach
  void beforeEach() {
    infrastructureExceptionHandler = new InfrastructureExceptionHandler();
  }

  @Test
  void shouldMapForbiddenExceptionTo403() {
    ResponseEntity<ErrorResponse> response =
        infrastructureExceptionHandler.handleInfrastructureException(new ForbiddenException());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getBody()).containsExactly("Forbidden");
  }

  @Test
  void shouldMapUnauthorizedExceptionTo401() {
    ResponseEntity<ErrorResponse> response =
        infrastructureExceptionHandler.handleInfrastructureException(new UnauthorizedException());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getBody()).containsExactly("Unauthorized");
  }
}
