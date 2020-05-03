package com.znane_groby.backend.repository;

import com.znane_groby.backend.model.City;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CityRepository extends CrudRepository<City, Long> {
    List<City> findByName(String name);
    City findById(long id);
    City findByWikiName(String wikiName);
}


