package net.nornick.groby.repository;

import net.nornick.groby.model.City;
import net.nornick.groby.model.Cemetery;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CemeteryRepository extends CrudRepository<Cemetery, Long> {

    List<Cemetery> findByName(String name);
    List<Cemetery> findByCity(Optional<City> city);
}


