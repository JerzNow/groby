package net.nornick.groby.repository;

import net.nornick.groby.model.FileFormat;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

public class FileFormatConverter {
    @Converter(autoApply = true)
    public class CategoryConverter implements AttributeConverter<FileFormat, String> {

        @Override
        public String convertToDatabaseColumn(FileFormat fileFormat) {
            if (fileFormat == null) {
                return null;
            }
            return fileFormat.getCode();
        }

        @Override
        public FileFormat convertToEntityAttribute(String code) {
            if (code == null) {
                return null;
            }
            return Stream.of(FileFormat.values())
                    .filter(f -> f.getCode().equals(code))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}
