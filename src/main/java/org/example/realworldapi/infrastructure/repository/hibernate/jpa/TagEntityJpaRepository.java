package org.example.realworldapi.infrastructure.repository.hibernate.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.realworldapi.infrastructure.repository.hibernate.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagEntityJpaRepository extends JpaRepository<TagEntity, UUID> {

  Optional<TagEntity> findFirstByNameIgnoreCase(String name);

  @Query("select tags from TagEntity as tags where upper(tags.name) in (:names)")
  List<TagEntity> findByUpperCaseNames(@Param("names") List<String> names);
}
