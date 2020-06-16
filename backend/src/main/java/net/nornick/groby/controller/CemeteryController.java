package net.nornick.groby.controller;

import lombok.extern.slf4j.Slf4j;
import net.nornick.groby.model.Cemetery;
import net.nornick.groby.repository.CemeteryRepository;
import net.nornick.groby.repository.CityRepository;
import net.nornick.groby.util.ItemNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
public class CemeteryController {
    private final CemeteryRepository cemeteryRepository;
    private final CityRepository cityRepository;

    CemeteryController(CemeteryRepository cemeteryRepository, CityRepository cityRepository) {
        this.cemeteryRepository = cemeteryRepository;
        this.cityRepository = cityRepository;
    }

    // Aggregate root
    @GetMapping("/cemeteries")
    List<Cemetery> all() {
        return cemeteryRepository.findAll();
    }

    // Single item
    @GetMapping("/cemeteries/{id}")
    Cemetery one(@PathVariable Long id) {
        return cemeteryRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("/cemeteries/", id));
    }

    // All graveyards from one city
    @GetMapping("/cemeteries/city/{id}")
    List<Cemetery> allFromCity(@PathVariable Long id) {
        return cemeteryRepository.findByCity(cityRepository.findById(id));
    }

    @PostMapping("/cemeteries")
    Cemetery newCemetery(@RequestBody Cemetery newCemetery)  {
        return cemeteryRepository.save(newCemetery);
    }

    @PutMapping("/cemeteries/{id}")
    Cemetery replaceCemetery(@RequestBody Cemetery newCemetery, @PathVariable Long id) {
        return cemeteryRepository.findById(id)
                .map(graveyard -> {
                    graveyard.setName(newCemetery.getName());
                    graveyard.setWikiName(newCemetery.getWikiName());
                    graveyard.setLatitude(newCemetery.getLatitude());
                    graveyard.setLongitude(newCemetery.getLongitude());
                    graveyard.setCity(newCemetery.getCity());
                    return cemeteryRepository.save(graveyard);
                })
                .orElseGet(() -> {
                    newCemetery.setId(id);
                    return cemeteryRepository.save(newCemetery);
                });
    }



    @DeleteMapping("/cemeteries/{id}")
    void deleteGraveyard(@PathVariable Long id) {
        cemeteryRepository.deleteById(id);
    }
}
