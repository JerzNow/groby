package net.nornick.groby.model;


import lombok.*;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @Column(length=400)
    private String url;
    //FileFormat fileFormat;
    int height;
    int width;
    @ManyToOne
    @ToString.Exclude
    private Person personPhoto;
    @ManyToOne
    @ToString.Exclude
    private Person gravePhoto;

    @ToString.Include
    public String printPersonPhoto() {
        if (personPhoto != null)
            return "Person: " + personPhoto.getFirstName()+personPhoto.getLastName();
        else
            return "";
    }

    @ToString.Include
    public String printGravePhoto() {
        if (gravePhoto != null)
            return "Grave: " + gravePhoto.getFirstName()+gravePhoto.getLastName();
        else
            return "";
    }
}

