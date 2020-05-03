package com.znane_groby.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class City  extends RepresentationModel<City> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String wikiName;

    private String name;



    //private String url_wiki;



    @Override
    public String toString() {
        return String.format(
                "Graveyard[id=%d, wiki_name='%s', name='%s']",
                id, wikiName, name);
    }

}