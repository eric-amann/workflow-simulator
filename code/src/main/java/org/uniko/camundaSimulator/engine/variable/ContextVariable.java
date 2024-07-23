package org.uniko.camundaSimulator.engine.variable;

import lombok.Getter;
import lombok.Setter;
import org.uniko.camundaSimulator.engine.context.Context;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ContextVariable implements ProcessVariable {

    private List<String> taskDefinitionIds;
    private String name;
    private Context context;
    private int column;

    public ContextVariable(ProcessVariableBuilder builder) {
        this.name = builder.getName();
        this.taskDefinitionIds = builder.getTaskDefinitionIds();
        this.context = builder.getContext();
        this.column = builder.getColumn();
    }

    public String getValue(LocalDateTime timestamp) {
        return context.getValue(timestamp, column);
    }
}
