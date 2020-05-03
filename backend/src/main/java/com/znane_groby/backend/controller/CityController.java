package com.znane_groby.backend.controller;

import com.znane_groby.backend.model.City;
import com.znane_groby.backend.repository.CityRepository;
import com.znane_groby.backend.util.ItemNotFoundException;
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

@RestController
public class CityController {
    private final CityRepository repository;
    private final CityResourceAssembler assembler;

    CityController(CityRepository repository, CityResourceAssembler assembler) {
        this.assembler = assembler;
        this.repository = repository;
    }

    // Aggregate root
    @GetMapping("/cities")
    CollectionModel<EntityModel<City>> all() {

        List<EntityModel<City>> cities = StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return new CollectionModel<>(cities,
                linkTo(methodOn(CityController.class).all()).withSelfRel());
    }

    // Single item
    @GetMapping("/cities/{id}")
    EntityModel<City> one(@PathVariable Long id) {

        City city = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("/cities/",id));
        return assembler.toModel(city);
    }

    @PostMapping("/cities")
    ResponseEntity<?> newCity(@RequestBody City newCity) throws URISyntaxException {
        EntityModel<City> entityModel = assembler.toModel(repository.save(newCity));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);

    }

    @PutMapping("/cities/{id}")
    ResponseEntity<?> replaceCity(@RequestBody City newCity, @PathVariable Long id)
            throws URISyntaxException {

        City updatedCity =  repository.findById(id)
                .map(city -> {
                    city.setName(newCity.getName());
                    city.setWikiName(newCity.getWikiName());
                    return repository.save(city);
                })
                .orElseGet(() -> {
                    newCity.setId(id);
                    return repository.save(newCity);
                });
        EntityModel<City> entityModel = assembler.toModel(updatedCity);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/cities/{id}")
    void deleteCity(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
