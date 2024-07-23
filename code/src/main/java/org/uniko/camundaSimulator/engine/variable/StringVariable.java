package org.uniko.camundaSimulator.engine.variable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
public class StringVariable implements ProcessVariable {

    private List<String> taskDefinitionIds;
    private String name;
    private Map<String, Double> possibilities;

    public StringVariable(ProcessVariableBuilder builder) {
        this.name = builder.getName();
        this.taskDefinitionIds = builder.getTaskDefinitionIds();
        this.possibilities = builder.getPossibilities();
    }

    public String getValue(LocalDateTime timestamp) {

        List<String> pool = new ArrayList<>();
        for (Map.Entry<String, Double> string : possibilities.entrySet()) {
            for (int i = 1; i <= string.getValue(); i++) {
                pool.add(string.getKey());
            }
        }
        Random random = new Random();
        String value = pool.get(random.nextInt(pool.size()));
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
