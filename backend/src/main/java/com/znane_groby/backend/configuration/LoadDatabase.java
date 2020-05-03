package com.znane_groby.backend.configuration;

import com.znane_groby.backend.model.City;
import com.znane_groby.backend.model.Graveyard;
import com.znane_groby.backend.repository.CityRepository;
import com.znane_groby.backend.repository.GraveyardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;

@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(GraveyardRepository repository, CityRepository cityRepository) {
        return args -> {
            City city = City.builder()
                    .name("Warszawa")
                    .wikiName("http://www.wikidata.org/entity/Q270")
                    .build();
            Graveyard powazki = Graveyard.builder()
                    .name("Powązki Cywilne")
                    .latitude(52.253055)
                    .longitude(20.977727)
                    .wikiName("Cmentarz_Powązkowski_w_Warszawie")
                    .url_wiki("https://pl.wikipedia.org/wiki/Cmentarz_Powązkowski_w_Warszawie")
                    .city(city)
                    .build();
            Graveyard powazkiWojskowe = Graveyard.builder()
                    .name("Cmentarz Wojskowy na Powązkach")
                    .latitude(52.258568)
                    .longitude(20.953679)
                    .wikiName("Cmentarz_Wojskowy_na_Powązkach")
                    .url_wiki("https://pl.wikipedia.org/wiki/Cmentarz_Wojskowy_na_Powązkach")
                    .city(city)
                    .build();

            log.info("Preloading " + cityRepository.save(city));
            log.info("Preloading " + repository.save(powazki));
            log.info("Preloading " + repository.save(powazkiWojskowe));
            showTables();
        };
    }

    @Autowired
    protected DataSource dataSource;

    public void showTables() throws Exception {
        DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
        ResultSet tables = metaData.getTables(null, null, null, new String[] { "TABLE" });
        while (tables.next()) {
            String tableName=tables.getString("TABLE_NAME");
            log.info(tableName);
            ResultSet columns = metaData.getColumns(null,  null,  tableName, "%");
            while (columns.next()) {
                String columnName=columns.getString("COLUMN_NAME");
                log.info("\t" + columnName);
            }
        }
    }
}