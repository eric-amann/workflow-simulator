package org.uniko.camundaSimulator.engine.context;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
public class Context {

    private final String id;
    private final TreeMap<LocalDateTime, List<String>> records;

    public Context(ContextBuilder builder) {
        this.id = builder.getId();
        this.records = builder.getRecords();
    }

    public String getValue(LocalDateTime timestamp, int column) {

        List<LocalDateTime> keyTimestamps = records.keySet().stream().toList();
        Map.Entry<LocalDateTime, List<String>> entry = records.ceilingEntry(timestamp);
        return entry.getValue().get(column - 1);

        /*
        // Find the nearest timestamp in records
        Optional<String> recordValue = records.entrySet().stream()
                .sorted()
                .filter(line -> line.getKey().isEqual(timestamp) || line.getKey().isAfter(timestamp))
                .reduce((entry, next) -> entry.getKey().isBefore(next.getKey()) ? entry : next)
                .map(Map.Entry::getValue)
                .stream()
                .map(columns -> columns.get(column - 1))
                .findFirst();

        return recordValue.get();*/
    }
}
