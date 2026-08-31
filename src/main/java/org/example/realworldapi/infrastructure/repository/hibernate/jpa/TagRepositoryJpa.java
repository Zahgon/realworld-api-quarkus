package org.example.realworldapi.infrastructure.repository.hibernate.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.example.realworldapi.domain.model.tag.Tag;
import org.example.realworldapi.domain.model.tag.TagRepository;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.EntityUtils;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.TagEntity;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class TagRepositoryJpa extends AbstractJpaRepository implements TagRepository {

  private final TagEntityJpaRepository tagEntityJpaRepository;
  private final EntityUtils entityUtils;

  @Override
  public List<Tag> findAllTags() {
    return tagEntityJpaRepository.findAll().stream()
        .map(entityUtils::tag)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Tag> findByName(String name) {
    return tagEntityJpaRepository.findFirstByNameIgnoreCase(name.trim()).map(entityUtils::tag);
  }

  @Override
  public void save(Tag tag) {
    entityManager.persist(new TagEntity(tag));
  }

  @Override
  public List<Tag> findByNames(List<String> names) {
    return tagEntityJpaRepository.findByUpperCaseNames(toUpperCase(names)).stream()
        .map(entityUtils::tag)
        .collect(Collectors.toList());
  }
}
