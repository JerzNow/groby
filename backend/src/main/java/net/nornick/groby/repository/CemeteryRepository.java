package net.nornick.groby.repository;

import net.nornick.groby.model.City;
import net.nornick.groby.model.Cemetery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "cemetery", path = "cemeteries")
public interface CemeteryRepository extends JpaRepository<Cemetery, Long> {

    List<Cemetery> findByName(String name);
    List<Cemetery> findByCity(Optional<City> city);
}


