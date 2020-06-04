package net.nornick.groby.repository;

import net.nornick.groby.model.City;
import net.nornick.groby.model.Occupation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "occupation", path = "occupations")
public interface OccupationRepository extends CrudRepository<Occupation, Long> {
    List<Occupation> findByName(String name);
}