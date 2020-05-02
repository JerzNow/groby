package com.znane_groby.backend.repository;

import com.znane_groby.backend.model.Graveyard;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GraveyardRepository extends CrudRepository<Graveyard, Long> {

    List<Graveyard> findByName(String name);
    Graveyard findById(long id);

}


