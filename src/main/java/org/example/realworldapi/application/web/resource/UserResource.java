package org.example.realworldapi.application.web.resource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.example.realworldapi.application.web.model.request.UpdateUserRequest;
import org.example.realworldapi.application.web.model.response.UserResponse;
import org.example.realworldapi.domain.feature.FindUserById;
import org.example.realworldapi.domain.feature.UpdateUser;
import org.example.realworldapi.domain.model.constants.ValidationMessages;
import org.example.realworldapi.infrastructure.web.provider.TokenProvider;
import org.example.realworldapi.infrastructure.web.security.annotation.Secured;
import org.example.realworldapi.infrastructure.web.security.context.SecurityContext;
import org.example.realworldapi.infrastructure.web.security.profile.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Validated
@AllArgsConstructor
public class UserResource {

  private final FindUserById findUserById;
  private final UpdateUser updateUser;
  private final TokenProvider tokenProvider;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Secured({Role.ADMIN, Role.USER})
  public ResponseEntity<UserResponse> getUser(SecurityContext securityContext) {
    final var userId = UUID.fromString(securityContext.getUserPrincipal().getName());
    final var user = findUserById.handle(userId);
    final var token = tokenProvider.createUserToken(user.getId().toString());
    return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(user, token));
  }

  @PutMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  @Secured({Role.ADMIN, Role.USER})
  public ResponseEntity<UserResponse> update(
      SecurityContext securityContext,
      @RequestBody @Valid @NotNull(message = ValidationMessages.REQUEST_BODY_MUST_BE_NOT_NULL)
          UpdateUserRequest updateUserRequest) {
    final var userId = UUID.fromString(securityContext.getUserPrincipal().getName());
    final var user = updateUser.handle(updateUserRequest.toUpdateUserInput(userId));
    final var token = tokenProvider.createUserToken(user.getId().toString());
    return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(user, token));
  }
}
