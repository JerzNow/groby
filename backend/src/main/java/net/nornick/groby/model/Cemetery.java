package net.nornick.groby.model;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cemetery extends RepresentationModel<Cemetery> {
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
        if(city != null)
            return String.format(
                    "Cemetery[id = %d, wiki name = %s, name= %s, city= %s, longitude = %f, latitude = %f]",
                    id, wikiName, name, city.getName(), longitude, latitude);
        else
            return String.format(
                    "Cemetery[id = %d, wiki name = %s, name= %s,longitude = %f, latitude = %f]",
                    id, wikiName, name, longitude, latitude);
    }

}

