package org.example.realworldapi.infrastructure.configuration;

import jakarta.validation.Validator;
import org.example.realworldapi.domain.feature.*;
import org.example.realworldapi.domain.feature.impl.*;
import org.example.realworldapi.domain.model.provider.HashProvider;
import org.example.realworldapi.domain.model.user.FollowRelationshipRepository;
import org.example.realworldapi.domain.model.user.UserModelBuilder;
import org.example.realworldapi.domain.model.user.UserRepository;
import org.example.realworldapi.domain.validator.ModelValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsersConfiguration {

  @Bean
  public CreateUser createUser(
      UserRepository userRepository, HashProvider hashProvider, UserModelBuilder userBuilder) {
    return new CreateUserImpl(userRepository, hashProvider, userBuilder);
  }

  @Bean
  public UpdateUser updateUser(
      FindUserById findUserById, UserRepository userRepository, ModelValidator modelValidator) {
    return new UpdateUserImpl(findUserById, userRepository, modelValidator);
  }

  @Bean
  public FindUserById findUserById(UserRepository userRepository) {
    return new FindUserByIdImpl(userRepository);
  }

  @Bean
  public LoginUser loginUser(UserRepository userRepository, HashProvider hashProvider) {
    return new LoginUserImpl(userRepository, hashProvider);
  }

  @Bean
  public FindUserByUsername findUserByUsername(UserRepository userRepository) {
    return new FindUserByUsernameImpl(userRepository);
  }

  @Bean
  public IsFollowingUser isFollowingUser(FollowRelationshipRepository usersFollowedRepository) {
    return new IsFollowingUserImpl(usersFollowedRepository);
  }

  @Bean
  public FollowUserByUsername followUserByUsername(
      FindUserById findUserById,
      FindUserByUsername findUserByUsername,
      FollowRelationshipRepository followRelationshipRepository) {
    return new FollowUserByUsernameImpl(
        findUserById, findUserByUsername, followRelationshipRepository);
  }

  @Bean
  public UnfollowUserByUsername unfollowUserByUsername(
      FindUserById findUserById,
      FindUserByUsername findUserByUsername,
      FollowRelationshipRepository followRelationshipRepository) {
    return new UnfollowUserByUsernameImpl(
        findUserById, findUserByUsername, followRelationshipRepository);
  }

  @Bean
  public UserModelBuilder userModelBuilder(ModelValidator modelValidator) {
    return new UserModelBuilder(modelValidator);
  }

  @Bean
  public ModelValidator modelValidator(Validator validator) {
    return new ModelValidator(validator);
  }
}
