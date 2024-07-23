package org.uniko.camundaSimulator.engine.variable;

import org.uniko.camundaSimulator.engine.context.Context;
import org.uniko.camundaSimulator.engine.context.Modifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ProcessVariable {

    static Builder builder() {
        return new ProcessVariableBuilder();
    }

    String getName();

    List<String> getTaskDefinitionIds();

    String getValue(LocalDateTime timestamp);

    interface Builder {

        ProcessVariable.Builder name(String name);

        ProcessVariable.Builder withTaskDefinitionId(String taskDefinitionId);

        ProcessVariable.Builder generationType(String generationType);

        ProcessVariable.Builder modifier(Modifier modifier);

        ProcessVariable.Builder modifiers(List<Modifier> modifiers);

        ProcessVariable.Builder lowerBound(double lowerBound);

        ProcessVariable.Builder upperBound(double upperBound);

        ProcessVariable.Builder mean(float mean);

        ProcessVariable.Builder standardDeviation(float standardDeviation);

        ProcessVariable.Builder chance(double chance);

        ProcessVariable.Builder possibilities(Map<String, Double> possibilities);

        ProcessVariable.Builder context(Context context);

        ProcessVariable.Builder column(int column);

        ProcessVariable build();
    }

}
