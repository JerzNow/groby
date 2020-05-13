package net.nornick.groby.controller;

import net.nornick.groby.model.City;
import net.nornick.groby.repository.CityRepository;
import net.nornick.groby.util.ItemNotFoundException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class CityController {
    private final CityRepository cityRepository;
    private final CityResourceAssembler cityResourceAssembler;

    CityController(CityRepository cityRepository, CityResourceAssembler cityResourceAssembler) {
        this.cityResourceAssembler = cityResourceAssembler;
        this.cityRepository = cityRepository;
    }

    // Aggregate root
    @GetMapping("/cities")
    CollectionModel<EntityModel<City>> all() {

        List<EntityModel<City>> cities = StreamSupport.stream(cityRepository.findAll().spliterator(), false)
                .map(cityResourceAssembler::toModel)
                .collect(Collectors.toList());
        return new CollectionModel<>(cities,
                linkTo(methodOn(CityController.class).all()).withSelfRel());
    }

    // Single item
    @GetMapping("/cities/{id}")
    EntityModel<City> one(@PathVariable Long id) {

        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("/cities/",id));
        return cityResourceAssembler.toModel(city);
    }


}
