package com.znane_groby.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Graveyard extends RepresentationModel<Graveyard> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String wikiName;
    private String name;
    @ManyToOne
    private City city;


    private double longitude;
    private double latitude;

    @Override
    public String toString() {
        return String.format(
                "Graveyard[id = %d, wiki name = %s, name= %s, city= %s, longitude = %f, latitude = %f]",
                id, wikiName, name, city.getName(), longitude, latitude);
    }

}

