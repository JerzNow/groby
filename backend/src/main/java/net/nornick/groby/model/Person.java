package net.nornick.groby.model;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String wikiName;
    private String firstName;
    private String fullNativeName;
    private String lastName;
    private ZonedDateTime birth;
    private ZonedDateTime  death;
    @ManyToOne
    private Occupation occupation;
    @ManyToOne
    private Cemetery cemetery;

    private double longitude;
    private double latitude;

    private int score;

}

