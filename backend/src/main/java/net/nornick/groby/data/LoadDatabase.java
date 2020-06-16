package net.nornick.groby.data;

import net.nornick.groby.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class LoadDatabase {
    private CemeteryRepository cemeteryRepository;
    private CityRepository cityRepository;
    private OccupationRepository occupationRepository;
    private PersonRepository personRepository;
    private PhotoRepository photoRepository;

    @Bean
    CommandLineRunner initDatabase(
            CemeteryRepository cemeteryRepository, CityRepository cityRepository, OccupationRepository occupationRepository, PersonRepository personRepository, PhotoRepository photoRepository) {
        return args -> {
            this.cityRepository = cityRepository;
            this.cemeteryRepository = cemeteryRepository;
            this.occupationRepository = occupationRepository;
            this.personRepository = personRepository;
            this.photoRepository = photoRepository;

            loadFromCSV("graveyards/cemeteries.csv", "people/occupation.csv", "people/people_data.csv", "people/person_score.csv");
            log.debug("Loading DB completed");
        };
    }

    public void loadFromCSV(String cemeteriesPath, String occupationPath, String peopleDataPath, String peopleScorePath) throws Exception {
        (new UploadCemeteries()).uploadCemeteriesCSV(cemeteriesPath, cemeteryRepository,cityRepository);
        (new UploadOccupation()).uploadOccupationCSV(occupationPath, occupationRepository);
        (new UploadPeople()).uploadPeopleCSV(peopleDataPath, peopleScorePath, personRepository, cemeteryRepository, photoRepository);
    }

}
