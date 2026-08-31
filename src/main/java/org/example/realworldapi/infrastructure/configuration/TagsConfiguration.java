package org.example.realworldapi.infrastructure.configuration;

import org.example.realworldapi.domain.feature.CreateTag;
import org.example.realworldapi.domain.feature.FindTags;
import org.example.realworldapi.domain.feature.FindTagsByNameCreateIfNotExists;
import org.example.realworldapi.domain.feature.impl.CreateTagImpl;
import org.example.realworldapi.domain.feature.impl.FindTagsByNameCreateIfNotExistsImpl;
import org.example.realworldapi.domain.feature.impl.FindTagsImpl;
import org.example.realworldapi.domain.model.tag.TagBuilder;
import org.example.realworldapi.domain.model.tag.TagRepository;
import org.example.realworldapi.domain.validator.ModelValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TagsConfiguration {

  @Bean
  public FindTags findTags(TagRepository tagRepository) {
    return new FindTagsImpl(tagRepository);
  }

  @Bean
  public CreateTag createTag(TagRepository tagRepository, TagBuilder tagBuilder) {
    return new CreateTagImpl(tagRepository, tagBuilder);
  }

  @Bean
  public FindTagsByNameCreateIfNotExists findTagsByNameCreateIfNotExists(
      TagRepository tagRepository, CreateTag createTag) {
    return new FindTagsByNameCreateIfNotExistsImpl(tagRepository, createTag);
  }

  @Bean
  public TagBuilder tagBuilder(ModelValidator modelValidator) {
    return new TagBuilder(modelValidator);
  }
}
