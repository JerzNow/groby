package com.znane_groby.backend.configuration;

import com.znane_groby.backend.model.City;
import com.znane_groby.backend.model.Graveyard;
import com.znane_groby.backend.repository.CityRepository;
import com.znane_groby.backend.repository.GraveyardRepository;
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
    GraveyardRepository graveyardRepository;
    CityRepository cityRepository;

    @Bean
    CommandLineRunner initDatabase(GraveyardRepository graveyardRepository, CityRepository cityRepository) {
        return args -> {
            this.cityRepository = cityRepository;
            this.graveyardRepository = graveyardRepository;
            uploadFromCsv("graveyards/cemeteries.csv");


        };
    }

    void uploadSample() {
        City city = City.builder()
                .name("Warszawa")
                .wikiName("http://www.wikidata.org/entity/Q270")
                .build();
        Graveyard powazki = Graveyard.builder()
                .name("Powązki Cywilne")
                .latitude(52.253055)
                .longitude(20.977727)
                .wikiName("Cmentarz_Powązkowski_w_Warszawie")
                .city(city)
                .build();
        Graveyard powazkiWojskowe = Graveyard.builder()
                .name("Cmentarz Wojskowy na Powązkach")
                .latitude(52.258568)
                .longitude(20.953679)
                .wikiName("Cmentarz_Wojskowy_na_Powązkach")
                .city(city)
                .build();

        log.info("Preloading " + cityRepository.save(city));
        log.info("Preloading " + graveyardRepository.save(powazki));
        log.info("Preloading " + graveyardRepository.save(powazkiWojskowe));
    }

    void uploadFromCsv(String fileName) {
        InputStream inputStream = getClass()
                .getClassLoader().getResourceAsStream(fileName);
        //using map to remove duplicates
        Map<String, City> citiesMap = new HashMap<>();
        Map<String, Graveyard> graveyardsMap = new HashMap<>();
        //Data format I am parsing
        //cemetery,cemeteryLabel,adm_location,adm_locationLabel,location
        //http://www.wikidata.org/entity/Q8510713,Cmentarz Zasłużonych Wielkopolan,http://www.wikidata.org/entity/Q268,Poznań,Point(16.9289 52.4144)
        try (Stream<String> stream = new BufferedReader(new InputStreamReader(inputStream)).lines()) {

            stream
                    .skip(1)
                    .forEach(e -> {
                        String[] fields = e.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                        if (fields.length > 3) {

                            String wikiName = fields[2];
                            String name = fields[3];
                            citiesMap.putIfAbsent(wikiName, City.builder()
                                    .name(name)
                                    .wikiName(wikiName)
                                    .build());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        inputStream = getClass()
                .getClassLoader().getResourceAsStream(fileName);
        try (Stream<String> stream = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
            stream
                    .skip(1)
                    .forEach(e -> {
                        String[] fields = e.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                        if (fields.length > 1 && !fields[0].isEmpty()) {
                            String wikiName = fields[0];
                            String name = fields[1];
                            double longitude = 0;
                            double latitude = 0;
                            String cityWikiName = "";
                            if(fields.length > 2)
                                cityWikiName = fields[2];
                            if(fields.length > 4) {
                                longitude = Double.parseDouble(fields[4].replaceAll("[^0-9.\\s]", "").split("\\s")[0]);
                                latitude = Double.parseDouble(fields[4].replaceAll("[^0-9.\\s]", "").split("\\s")[1]);
                            }
                                graveyardsMap.putIfAbsent(wikiName, Graveyard.builder()
                                        .name(name)
                                        .wikiName(wikiName)
                                        .city(citiesMap.get(cityWikiName))
                                        .longitude(longitude)
                                        .latitude(latitude)
                                        .build());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }


//        citiesMap.entrySet().stream()
//                .forEach(e -> log.info("Preloading " + cityRepository.save(e.getValue())));
//        graveyardsMap.entrySet().stream()
//                .forEach(e -> log.info("Preloading " + graveyardRepository.save(e.getValue())));

        citiesMap.entrySet()
                .forEach(e ->cityRepository.save(e.getValue()));
        graveyardsMap.entrySet()
                .forEach(e ->graveyardRepository.save(e.getValue()));

    }


}