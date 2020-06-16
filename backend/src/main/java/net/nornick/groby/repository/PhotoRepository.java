package net.nornick.groby.repository;

import net.nornick.groby.model.Photo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "photo", path = "photos")
public interface PhotoRepository extends CrudRepository<Photo, Long> {
    List<Photo> findByName(String name);
}
