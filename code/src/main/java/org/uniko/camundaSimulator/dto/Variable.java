package org.uniko.camundaSimulator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Variable {
    private String id;

    private String name;

    private Object value;

    private VariableType type;
}
