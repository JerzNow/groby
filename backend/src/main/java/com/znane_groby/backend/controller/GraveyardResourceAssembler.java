package com.znane_groby.backend.controller;

import com.znane_groby.backend.model.Graveyard;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
class GraveyardResourceAssembler  implements RepresentationModelAssembler<Graveyard, EntityModel<Graveyard>> {
    @Override
    public EntityModel<Graveyard> toModel(Graveyard graveyard) {

        return new EntityModel<>(graveyard,
                linkTo(methodOn(GraveyardController.class).one(graveyard.getId())).withSelfRel(),
                linkTo(methodOn(GraveyardController.class).all()).withRel("graveyards"),
               // linkTo(methodOn(GraveyardController.class).allFromCity(graveyard.getCity().getId())).withRel("test"),
                linkTo(methodOn(CityController.class).one(graveyard.getCity().getId())).withRel("city")
        );
    }
}