package net.nornick.groby.controller;

import net.nornick.groby.model.Cemetery;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
class CemeteryResourceAssembler implements RepresentationModelAssembler<Cemetery, EntityModel<Cemetery>> {
    @Override
    public EntityModel<Cemetery> toModel(Cemetery cemetery) {

        return new EntityModel<>(cemetery,
                linkTo(methodOn(CemeteryController.class).one(cemetery.getId())).withSelfRel(),
                linkTo(methodOn(CemeteryController.class).all()).withRel("cemeteries"),
                linkTo(methodOn(CityController.class).one(cemetery.getCity().getId())).withRel("city")
        );
    }
}