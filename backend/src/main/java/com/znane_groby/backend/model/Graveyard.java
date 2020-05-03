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
    @ManyToOne
    private City city;

    private String name;
    private double longitude;
    private double latitude;
    private String url_wiki;

    @Override
    public String toString() {
        return String.format(
                "Graveyard[id=%d, wiki name='%s', name='%s', url='%s', city='%s']",
                id, wikiName, name, url_wiki, city.getName());
    }

}
