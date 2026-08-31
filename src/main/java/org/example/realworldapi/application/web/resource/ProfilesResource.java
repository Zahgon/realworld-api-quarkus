package org.example.realworldapi.application.web.resource;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.example.realworldapi.application.web.model.response.ProfileResponse;
import org.example.realworldapi.application.web.resource.utils.ResourceUtils;
import org.example.realworldapi.domain.feature.FollowUserByUsername;
import org.example.realworldapi.domain.feature.UnfollowUserByUsername;
import org.example.realworldapi.domain.model.constants.ValidationMessages;
import org.example.realworldapi.infrastructure.web.security.annotation.Secured;
import org.example.realworldapi.infrastructure.web.security.context.SecurityContext;
import org.example.realworldapi.infrastructure.web.security.profile.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profiles")
@Validated
@AllArgsConstructor
public class ProfilesResource {

  private final FollowUserByUsername followUserByUsername;
  private final UnfollowUserByUsername unfollowUserByUsername;
  private final ResourceUtils resourceUtils;

  @GetMapping(path = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Secured(optional = true)
  public ResponseEntity<ProfileResponse> getProfile(
      @PathVariable("username") @NotBlank(message = ValidationMessages.USERNAME_MUST_BE_NOT_BLANK)
          String username,
      SecurityContext securityContext) {
    final var loggedUserId = resourceUtils.getLoggedUserId(securityContext);
    final var profileResponse = resourceUtils.profileResponse(username, loggedUserId);
    return ResponseEntity.status(HttpStatus.OK).body(profileResponse);
  }

  @PostMapping(path = "/{username}/follow", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  @Secured({Role.USER, Role.ADMIN})
  public ResponseEntity<ProfileResponse> follow(
      @PathVariable("username") @NotBlank(message = ValidationMessages.USERNAME_MUST_BE_NOT_BLANK)
          String username,
      SecurityContext securityContext) {
    final var loggedUserId = resourceUtils.getLoggedUserId(securityContext);
    followUserByUsername.handle(loggedUserId, username);
    return ResponseEntity.status(HttpStatus.OK)
        .body(resourceUtils.profileResponse(username, loggedUserId));
  }

  @DeleteMapping(path = "/{username}/follow", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  @Secured({Role.USER, Role.ADMIN})
  public ResponseEntity<ProfileResponse> unfollow(
      @PathVariable("username") @NotBlank(message = ValidationMessages.USERNAME_MUST_BE_NOT_BLANK)
          String username,
      SecurityContext securityContext) {
    final var loggedUserId = resourceUtils.getLoggedUserId(securityContext);
    unfollowUserByUsername.handle(loggedUserId, username);
    return ResponseEntity.status(HttpStatus.OK)
        .body(resourceUtils.profileResponse(username, loggedUserId));
  }
}
