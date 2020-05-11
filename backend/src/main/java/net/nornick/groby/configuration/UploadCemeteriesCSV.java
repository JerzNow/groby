package net.nornick.groby.configuration;

import lombok.extern.slf4j.Slf4j;
import net.nornick.groby.model.Cemetery;
import net.nornick.groby.model.City;
import net.nornick.groby.repository.CemeteryRepository;
import net.nornick.groby.repository.CityRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
class UploadCemeteriesCSV {
    //Uploaded file format
    //cemetery,cemeteryLabel,adm_location,adm_locationLabel,location
    //http://www.wikidata.org/entity/Q8510713,Cmentarz Zasłużonych Wielkopolan,http://www.wikidata.org/entity/Q268,Poznań,Point(16.9289 52.4144)
    public void uploadCemeteriesCSV(String fileName, CemeteryRepository cemeteryRepository, CityRepository cityRepository) throws Exception {
        List<String[]> lines = new ReadCSV().readCSV(fileName);

        Map<String, City> citiesMap = createCitiesMap(lines);
        Map<String, Cemetery> cemeteriesMap = createCemeteriesMap(lines, citiesMap);
        citiesMap
                .forEach((k,v) -> cityRepository.save(v));
        cemeteriesMap
                .forEach((k,v) -> cemeteryRepository.save(v));
    }

    private Map<String, City> createCitiesMap(List<String[]> lines) {
        Map<String, City> citiesMap = new HashMap<>();
        for (String[] line : lines) {
            if (line.length > 3) {
                String wikiName = line[2];
                citiesMap.putIfAbsent(wikiName, City.builder()
                        .name(line[3])
                        .wikiName(wikiName)
                        .build());
            }
        }
        return citiesMap;
    }

    private Map<String, Cemetery> createCemeteriesMap(List<String[]> lines, Map<String, City> citiesMap) {
        Map<String, Cemetery> cemeteriesMap = new HashMap<>();
        String wikiName;
        for (String[] line : lines) {
            if (!line[0].isEmpty() && !line[1].isEmpty()) {
                wikiName = line[0];
                Cemetery.CemeteryBuilder cemeteryBuilder = Cemetery.builder()
                        .name(line[1])
                        .wikiName(wikiName);
                if (!line[2].isEmpty())
                    cemeteryBuilder = cemeteryBuilder.city(citiesMap.get(line[2]));
                if (!line[4].isEmpty()) {
                    String[] parsedCoordinates = parseCoordinates(line[4]);
                    cemeteryBuilder = cemeteryBuilder
                            .longitude(Double.parseDouble(parsedCoordinates[0]))
                            .latitude(Double.parseDouble(parsedCoordinates[1]));
                }
                cemeteriesMap.putIfAbsent(wikiName, cemeteryBuilder.build());
            }
        }
        return cemeteriesMap;
    }

    private String[] parseCoordinates(String s) {
        return s.replaceAll("[^0-9.\\s]", "").split("\\s");
    }
}