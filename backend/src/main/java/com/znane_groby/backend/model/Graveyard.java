package com.znane_groby.backend.model;

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
    private String wiki_name;
    private double longitude;
    private double latitude;
    private String name;
    private String url;



    @Override
    public String toString() {
        return String.format(
                "Graveyard[id=%d, wiki_name='%s', name='%s', url='%s']",
                id, wiki_name, name,url);
    }

}

