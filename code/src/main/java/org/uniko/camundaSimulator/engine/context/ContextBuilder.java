package org.uniko.camundaSimulator.engine.context;

import lombok.Getter;
import org.uniko.camundaSimulator.engine.configuration.ContextConfig;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class ContextBuilder {

    private String id;
    private String filePath;
    private int timestampColumn;
    private String timestampFormat;
    private TreeMap<LocalDateTime, List<String>> records = new TreeMap<>();

    public ContextBuilder(ContextConfig config) {
        this.id = config.getId();
        this.filePath = config.getFilePath();
        this.timestampFormat = config.getTimestampFormat();
        this.timestampColumn = config.getTimestampColumn();
    }

    public ContextBuilder id(String id) {
        this.id = id;
        return this;
    }

    public ContextBuilder sourceFile(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public ContextBuilder timestampColumnNumber(int columnNumber) {
        this.timestampColumn = columnNumber;
        return this;
    }

    public ContextBuilder timestampFormat(String timestampFormat) {
        this.timestampFormat = timestampFormat;
        return this;
    }

    public Context build() {

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timestampFormat);
            TreeMap<LocalDateTime, List<String>> records = new TreeMap<>();

            List<String> lines = reader.lines().collect(Collectors.toList());

            lines.stream()
                    .map(line -> Arrays.asList(line.split(";")))
                    .forEach(line -> {
                        String timestamp = line.get(timestampColumn - 1);
                        try {
                            records.put(LocalDateTime.parse(timestamp, formatter), line);
                        } catch (DateTimeParseException ignored) {
                        }
                    });
            this.records = records;
            return new Context(this);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
