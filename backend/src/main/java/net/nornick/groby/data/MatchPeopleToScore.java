package net.nornick.groby.data;

import lombok.extern.slf4j.Slf4j;
import net.nornick.groby.model.Person;
import java.util.List;
import java.util.Map;

@Slf4j
public class MatchPeopleToScore {
//    person_score.csv file format
//    page_title,page_length,incoming_links,interwiki_links,street_count,Score,
//    Adam_Mickiewicz,54232,2203,5,707,412,

    public void matchPeopleToScore(Map<String, Person> peopleMap, String peoplesScorePath) throws Exception {
        List<String[]> lines = new ReadCSV().readCSV(peoplesScorePath);
        peopleMap
                .forEach((k, v) -> matchAndRemove(lines, v));
    }

    public void matchAndRemove(List<String[]> lines, Person person){
        String[] name;
        for (String[] line : lines) {
            if (line[0].contains("("))
                name = line[0].substring(0, line[0].indexOf('(')).split("_");
            else
                name = line[0].split("_");
            //log.debug("First: {} Last: {}", name[0], name[name.length-1]);

            if (name[0].equalsIgnoreCase(person.getFirstName()) &&  name[name.length-1].equalsIgnoreCase(person.getLastName())){
                person.setScore(Integer.parseInt(line[5]));
                lines.remove(line);
                return;
            }
        }
        log.debug("No score: {} ", person);

    }
}
