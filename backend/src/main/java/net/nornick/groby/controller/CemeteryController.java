package net.nornick.groby.controller;

import net.nornick.groby.model.Cemetery;
import net.nornick.groby.repository.CityRepository;
import net.nornick.groby.repository.CemeteryRepository;
import net.nornick.groby.util.ItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
public class CemeteryController {
    private final CemeteryRepository cemeteryRepository;
    private final CityRepository cityRepository;
    private final CemeteryResourceAssembler cemeteryResourceAssembler;

    CemeteryController(CemeteryRepository cemeteryRepository, CemeteryResourceAssembler cemeteryResourceAssembler, CityRepository cityRepository) {
        this.cemeteryResourceAssembler = cemeteryResourceAssembler;
        this.cemeteryRepository = cemeteryRepository;
        this.cityRepository = cityRepository;
    }

    // Aggregate root
    @GetMapping("/cemeteries")
    CollectionModel<EntityModel<Cemetery>> all() {

        List<EntityModel<Cemetery>> graveyards = StreamSupport.stream(cemeteryRepository.findAll().spliterator(), false)
                .map(cemeteryResourceAssembler::toModel)
                .collect(Collectors.toList());
        return new CollectionModel<>(graveyards,
                linkTo(methodOn(CemeteryController.class).all()).withSelfRel());
    }

    // Single item
    @GetMapping("/cemeteries/{id}")
    EntityModel<Cemetery> one(@PathVariable Long id) {
        Cemetery cemetery = cemeteryRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("/cemeteries/", id));
        log.info("graveyard.getCity() " + cemetery.getCity());
        return cemeteryResourceAssembler.toModel(cemetery);
    }
    // All graveyards from one city
    @GetMapping("/cemeteries/city/{id}")
    CollectionModel<EntityModel<Cemetery>> allFromCity(@PathVariable Long id) {
        List<EntityModel<Cemetery>> graveyards = cemeteryRepository.findByCity(cityRepository.findById(id)).stream()
                .map(cemeteryResourceAssembler::toModel)
                .collect(Collectors.toList());
        return new CollectionModel<>(graveyards,
                linkTo(methodOn(CemeteryController.class).allFromCity(id)).withSelfRel());
    }

    @PostMapping("/cemeteries")
    ResponseEntity<?> newGraveyard(@RequestBody Cemetery newCemetery) throws URISyntaxException {
        EntityModel<Cemetery> entityModel = cemeteryResourceAssembler.toModel(cemeteryRepository.save(newCemetery));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);

    }

    @PutMapping("/cemeteries/{id}")
    ResponseEntity<?>  replaceGraveyard(@RequestBody Cemetery newCemetery, @PathVariable Long id)
            throws URISyntaxException {

        Cemetery updatedCemetery =  cemeteryRepository.findById(id)
                .map(graveyard -> {
                    graveyard.setName(newCemetery.getName());
                    graveyard.setWikiName(newCemetery.getWikiName());
                    graveyard.setLatitude(newCemetery.getLatitude());
                    graveyard.setLongitude(newCemetery.getLongitude());
                    graveyard.setCity(newCemetery.getCity());
                    return cemeteryRepository.save(graveyard);
                })
                .orElseGet(() -> {
                    newCemetery.setId(id);
                    return cemeteryRepository.save(newCemetery);
                });
        EntityModel<Cemetery> entityModel = cemeteryResourceAssembler.toModel(updatedCemetery);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/cemeteries/{id}")
    void deleteGraveyard(@PathVariable Long id) {
        cemeteryRepository.deleteById(id);
    }
}
