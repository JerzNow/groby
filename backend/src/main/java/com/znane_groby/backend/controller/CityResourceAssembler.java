package com.znane_groby.backend.controller;

import com.znane_groby.backend.model.City;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class CityResourceAssembler  implements RepresentationModelAssembler<City, EntityModel<City>> {
    @Override
    public EntityModel<City> toModel(City city) {

        return new EntityModel<>(city,
                linkTo(methodOn(CityController.class).one(city.getId())).withSelfRel(),
                linkTo(methodOn(GraveyardController.class).allFromCity(city.getId())).withRel("graveyards"),
                linkTo(methodOn(CityController.class).all()).withRel("cities"));

    }
}