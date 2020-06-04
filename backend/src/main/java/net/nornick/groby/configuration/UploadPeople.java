package net.nornick.groby.configuration;

import lombok.extern.slf4j.Slf4j;
import net.nornick.groby.model.Cemetery;
import net.nornick.groby.model.Person;
import net.nornick.groby.model.Photo;
import net.nornick.groby.repository.CemeteryRepository;
import net.nornick.groby.repository.PersonRepository;
import net.nornick.groby.repository.PhotoRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
class UploadPeople {
    //    Uploaded file format
//    person,personLabel,name_in_native_language,cemetery,cemeteryLabel,birthDate,deathDate,grave_photo
//    http://www.wikidata.org/entity/Q246296,Maciej Rataj,Maciej Rataj,http://www.wikidata.org/entity/Q559257,Cmentarz w Palmirach,1884-02-19T00:00:00Z,1940-06-21T00:00:00Z,http://commons.wikimedia.org/wiki/Special:FilePath/Palmiry%20cemetery%2020080713%2002.jpg
    public void uploadPeopleCSV(String peopleDataPath, String peoplesScorePath, PersonRepository personRepository, CemeteryRepository cemeteryRepository, PhotoRepository photoRepository) throws Exception {
        Map<String, Cemetery> cemeteriesMap = getStringCemeteryMap(cemeteryRepository);
        List<String[]> lines = new ReadCSV().readCSV(peopleDataPath);

        Map<String, Photo> photosMap = createGravesPhotosMap(lines);
        Map<String, Person> peopleMap = createPeopleMap(lines, cemeteriesMap, photosMap);
        (new MatchPeopleToScore()).matchPeopleToScore(peopleMap, peoplesScorePath);
//        peopleMap
//                .forEach((k, v) -> log.debug("Person: {}", v));
        peopleMap
                .forEach((k, v) -> personRepository.save(v));
//        photosMap
//                .forEach((k, v) -> log.debug("Photo: {}", v.getUrl()));
        photosMap
                .forEach((k, v) -> photoRepository.save(v));
    }

    private Map<String, Cemetery> getStringCemeteryMap(CemeteryRepository cemeteryRepository) {
        List<Cemetery> cemeteries = cemeteryRepository.findAll();
        return cemeteries.stream().collect(
                Collectors.toMap(Cemetery::getWikiName, cemetery -> cemetery));
    }

    private Map<String, Person> createPeopleMap(List<String[]> lines, Map<String, Cemetery> cemeteriesMap, Map<String, Photo> photosMap) {
        Map<String, Person> peopleMap = new HashMap<>();
        String[] name;
        String wikiName;
        for (String[] line : lines) {
            if (line.length > 4) {
                wikiName = line[0];
                if (line[1].contains("("))
                    name = line[1].substring(0, line[1].indexOf('(')).split(" ");
                else
                    name = line[1].split(" ");
                peopleMap.putIfAbsent(wikiName, Person.builder()
                        .firstName(name[0])
                        .lastName(name[name.length - 1])
                        .fullNativeName(line[2])
                        .wikiName(wikiName)
                        .cemetery(cemeteriesMap.get(line[3]))
                        .birth(ZonedDateTime.parse(line[5]))
                        .death(ZonedDateTime.parse(line[6]))
                        .build());
                photosMap.get(line[7]).setGravePhoto(peopleMap.get(wikiName));
            }
        }
        return peopleMap;
    }

    private Map<String, Photo> createGravesPhotosMap(List<String[]> lines) {
        Map<String, Photo> photosMap = new HashMap<>();
        for (String[] line : lines) {
            photosMap.putIfAbsent(line[7], Photo.builder()
                    .url(line[7])
                    .name(URLDecoder.decode(line[7].substring(line[7].lastIndexOf('/') + 1), StandardCharsets.UTF_8))
                    .build());
        }
        return photosMap;
    }






}