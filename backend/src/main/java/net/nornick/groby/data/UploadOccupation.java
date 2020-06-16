package net.nornick.groby.data;

import lombok.extern.slf4j.Slf4j;
import net.nornick.groby.model.Occupation;
import net.nornick.groby.repository.OccupationRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
class UploadOccupation {
    //Uploaded file format
//    person,personLabel,occupationLabel
//    http://www.wikidata.org/entity/Q9282812,Gustaw Adolf Gebethner,wydawca
    public void uploadOccupationCSV(String fileName, OccupationRepository occupationRepository) throws Exception {
        List<String[]> lines = new ReadCSV().readCSV(fileName);
        Map<String, Occupation> occupationsMap = createOccupationMap(lines);
        occupationsMap
                .forEach((k,v) -> occupationRepository.save(v));
    }

    private Map<String, Occupation> createOccupationMap(List<String[]> lines) {
        Map<String, Occupation> occupationsMap = new HashMap<>();
        for (String[] line : lines) {
            if (line.length > 2) {
                String name = line[2];
                Pattern p = Pattern.compile("([0-9])");
                Matcher m = p.matcher(name);
                if(m.find()){
                    log.debug(name);
                }

                occupationsMap.putIfAbsent(name, Occupation.builder()
                        .name(line[2].toLowerCase())
                        .build());
            }
        }
        return occupationsMap;
    }


}