package net.nornick.groby.repository;

import net.nornick.groby.model.City;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "city", path = "cities")
public interface CityRepository extends CrudRepository<City, Long> {
    List<City> findByName(String name);
    City findByWikiName(String wikiName);
}


