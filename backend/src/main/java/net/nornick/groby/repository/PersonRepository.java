package net.nornick.groby.repository;

import net.nornick.groby.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "person", path = "people")
public interface PersonRepository extends CrudRepository<Person, Long> {
    List<Person> findByWikiName(String wikiName);
}
