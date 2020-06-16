package net.nornick.groby.data;

import lombok.extern.slf4j.Slf4j;
import net.nornick.groby.model.Cemetery;
import net.nornick.groby.model.City;
import net.nornick.groby.repository.CemeteryRepository;
import net.nornick.groby.repository.CityRepository;

@Slf4j
class UploadSampleData {
    void uploadSample(CemeteryRepository cemeteryRepository, CityRepository cityRepository) {
        City city = City.builder()
                .name("Warszawa")
                .wikiName("http://www.wikidata.org/entity/Q270")
                .build();
        Cemetery powazki = Cemetery.builder()
                .name("Powązki Cywilne")
                .latitude(52.253055)
                .longitude(20.977727)
                .wikiName("Cmentarz_Powązkowski_w_Warszawie")
                .city(city)
                .build();
        Cemetery powazkiWojskowe = Cemetery.builder()
                .name("Cmentarz Wojskowy na Powązkach")
                .latitude(52.258568)
                .longitude(20.953679)
                .wikiName("Cmentarz_Wojskowy_na_Powązkach")
                .city(city)
                .build();

        log.debug("Preloading " + cityRepository.save(city));
        log.debug("Preloading " + cemeteryRepository.save(powazki));
        log.debug("Preloading " + cemeteryRepository.save(powazkiWojskowe));
    }
}
