package com.znane_groby.backend.configuration;

import com.znane_groby.backend.model.Graveyard;
import com.znane_groby.backend.repository.GraveyardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(GraveyardRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(Graveyard.builder()
                    .name("Powązki Cywilne")
                    .latitude(52.253055)
                    .longitude(20.977727)
                    .wiki_name("Cmentarz_Powązkowski_w_Warszawie")
                    .url("https://pl.wikipedia.org/wiki/Cmentarz_Powązkowski_w_Warszawie")
                    .build())
            );
            log.info("Preloading " + repository.save(Graveyard.builder()
                    .name("Cmentarz Wojskowy na Powązkach")
                    .latitude(52.258568)
                    .longitude(20.953679)
                    .wiki_name("Cmentarz_Wojskowy_na_Powązkach")
                    .url("https://pl.wikipedia.org/wiki/Cmentarz_Wojskowy_na_Powązkach")
                    .build())
            );
        };
    }
}