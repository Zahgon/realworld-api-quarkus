package org.example.realworldapi.infrastructure.configuration;

import org.example.realworldapi.application.web.model.response.ArticlesResponse;
import org.example.realworldapi.application.web.model.response.CommentsResponse;
import org.example.realworldapi.application.web.model.response.TagsResponse;
import org.example.realworldapi.infrastructure.web.validation.validator.AtLeastOneFieldMustBeNotNullValidator;
import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * Reflection hints for the ahead-of-time (native image) build.
 *
 * <p>Spring AOT derives Jackson binding hints from controller signatures. The three response types
 * registered here are serialized manually through {@code ObjectMapper#writeValueAsString}, so AOT
 * cannot discover them. The constraint validator is instantiated reflectively by Hibernate
 * Validator. Both were covered by {@code @RegisterForReflection} in the original build.
 */
public class NativeRuntimeHints implements RuntimeHintsRegistrar {

  private final BindingReflectionHintsRegistrar bindingRegistrar =
      new BindingReflectionHintsRegistrar();

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    bindingRegistrar.registerReflectionHints(
        hints.reflection(), TagsResponse.class, ArticlesResponse.class, CommentsResponse.class);
    hints
        .reflection()
        .registerType(
            AtLeastOneFieldMustBeNotNullValidator.class,
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
  }
}
