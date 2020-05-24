package net.nornick.groby.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;

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
    private String lastName;
    private int score;
    @ManyToOne
    private Grave grave;
    @ManyToOne
    private Occupation occupation;
    @ManyToOne
    private Photo photo;

}

