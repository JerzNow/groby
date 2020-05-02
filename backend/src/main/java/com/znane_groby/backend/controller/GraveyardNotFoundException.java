package com.znane_groby.backend.controller;

class GraveyardNotFoundException extends RuntimeException {
    GraveyardNotFoundException(Long id) {
        super("Could not find graveyard " + id);
    }
}