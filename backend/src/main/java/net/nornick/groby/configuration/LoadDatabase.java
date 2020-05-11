package net.nornick.groby.configuration;

import net.nornick.groby.model.City;
import net.nornick.groby.model.Cemetery;
import net.nornick.groby.repository.CityRepository;
import net.nornick.groby.repository.CemeteryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Configuration
@Slf4j
public class LoadDatabase {
    CemeteryRepository cemeteryRepository;
    CityRepository cityRepository;

    @Bean
    CommandLineRunner initDatabase(CemeteryRepository cemeteryRepository, CityRepository cityRepository) {
        return args -> {
            this.cityRepository = cityRepository;
            this.cemeteryRepository = cemeteryRepository;
            (new UploadCemeteriesCSV()).uploadCemeteriesCSV("graveyards/cemeteries.csv", cemeteryRepository,cityRepository);
        };
    }
}
