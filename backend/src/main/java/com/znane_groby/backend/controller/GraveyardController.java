package com.znane_groby.backend.controller;

import com.znane_groby.backend.model.Graveyard;
import com.znane_groby.backend.repository.CityRepository;
import com.znane_groby.backend.repository.GraveyardRepository;
import com.znane_groby.backend.util.ItemNotFoundException;
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
public class GraveyardController {
    private final GraveyardRepository repository;
    private final CityRepository cityRepository;
    private final GraveyardResourceAssembler assembler;

    GraveyardController(GraveyardRepository repository, GraveyardResourceAssembler assembler,CityRepository cityRepository) {
        this.assembler = assembler;
        this.repository = repository;
        this.cityRepository = cityRepository;
    }

    // Aggregate root
    @GetMapping("/graveyards")
    CollectionModel<EntityModel<Graveyard>> all() {

        List<EntityModel<Graveyard>> graveyards = StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return new CollectionModel<>(graveyards,
                linkTo(methodOn(GraveyardController.class).all()).withSelfRel());
    }

    // Single item
    @GetMapping("/graveyards/{id}")
    EntityModel<Graveyard> one(@PathVariable Long id) {
        Graveyard graveyard = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("/graveyards/", id));
        log.info("graveyard.getCity() " + graveyard.getCity());
        return assembler.toModel(graveyard);
    }
    // All graveyards from one city
    @GetMapping("/graveyards/city/{id}")
    CollectionModel<EntityModel<Graveyard>> allFromCity(@PathVariable Long id) {
        List<EntityModel<Graveyard>> graveyards = repository.findByCity(cityRepository.findById(id)).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return new CollectionModel<>(graveyards,
                linkTo(methodOn(GraveyardController.class).allFromCity(id)).withSelfRel());
    }

    @PostMapping("/graveyards")
    ResponseEntity<?> newGraveyard(@RequestBody Graveyard newGraveyard) throws URISyntaxException {
        EntityModel<Graveyard> entityModel = assembler.toModel(repository.save(newGraveyard));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);

    }

    @PutMapping("/graveyards/{id}")
    ResponseEntity<?>  replaceGraveyard(@RequestBody Graveyard newGraveyard, @PathVariable Long id)
            throws URISyntaxException {

        Graveyard updatedGraveyard =  repository.findById(id)
                .map(graveyard -> {
                    graveyard.setName(newGraveyard.getName());
                    graveyard.setWikiName(newGraveyard.getWikiName());
                    graveyard.setLatitude(newGraveyard.getLatitude());
                    graveyard.setLongitude(newGraveyard.getLongitude());
                    graveyard.setCity(newGraveyard.getCity());
                    return repository.save(graveyard);
                })
                .orElseGet(() -> {
                    newGraveyard.setId(id);
                    return repository.save(newGraveyard);
                });
        EntityModel<Graveyard> entityModel = assembler.toModel(updatedGraveyard);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/graveyards/{id}")
    void deleteGraveyard(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
