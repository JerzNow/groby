package com.znane_groby.backend.repository;

import com.znane_groby.backend.model.City;
import com.znane_groby.backend.model.Graveyard;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GraveyardRepository extends CrudRepository<Graveyard, Long> {

    List<Graveyard> findByName(String name);
    Graveyard findById(long id);
    List<Graveyard> findByCity(Optional<City> city);
}


