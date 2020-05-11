package net.nornick.groby.configuration;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class ReadCSV {

    public List<String[]> readCSV(String fileName) throws Exception {
        InputStream inputStream = getClass()
                .getClassLoader().getResourceAsStream(fileName);
        return parseCSV(new InputStreamReader(inputStream));
    }

    private List<String[]> parseCSV(Reader reader) throws Exception {
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .build();
        try (CSVReader csvReader = new CSVReaderBuilder(reader)
                .withSkipLines(1)
                .withCSVParser(parser)
                .build()) {
            return csvReader.readAll();
        }
    }

}
