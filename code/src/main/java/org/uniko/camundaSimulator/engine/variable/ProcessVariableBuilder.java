package org.uniko.camundaSimulator.engine.variable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.uniko.camundaSimulator.engine.configuration.BooleanVariableConfig;
import org.uniko.camundaSimulator.engine.configuration.NumericVariableConfig;
import org.uniko.camundaSimulator.engine.configuration.ProcessVariableConfig;
import org.uniko.camundaSimulator.engine.configuration.StringVariableConfig;
import org.uniko.camundaSimulator.engine.context.Context;
import org.uniko.camundaSimulator.engine.context.Modifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ProcessVariableBuilder implements ProcessVariable.Builder {

    private final List<String> taskDefinitionIds = new ArrayList<>();
    private String name;
    private GenerationType generationType;
    private String dataType;
    private Context context;
    private int column;
    private final List<Modifier> modifiers = new ArrayList<>();

    // Numeric variable configuration attributes
    private double lowerBound;
    private double upperBound;
    private double mean;
    private double standardDeviation;
    private int decimalPlaces;

    // Boolean variable configuration attributes
    private Double chance;

    // String variable configuration attributes
    private Map<String, Double> possibilities;

    public ProcessVariableBuilder(ProcessVariableConfig config) {
        this.name = config.getName();
        this.taskDefinitionIds.addAll(config.getTaskIds());
        if (config.getGenerationType() != null) {
            this.generationType = GenerationType.valueOf(config.getGenerationType());
        }

        switch (config) {
            case NumericVariableConfig numericVariableConfig -> {
                this.dataType = "NUMBER";
                this.lowerBound = numericVariableConfig.getLowerBound();
                this.upperBound = numericVariableConfig.getUpperBound();
                this.mean = numericVariableConfig.getMean();
                this.standardDeviation = numericVariableConfig.getStandardDeviation();
                this.decimalPlaces = numericVariableConfig.getDecimalPlaces();
            }
            case BooleanVariableConfig booleanVariableConfig -> {
                this.dataType = "BOOLEAN";
                this.chance = booleanVariableConfig.getChance();
            }
            case StringVariableConfig stringVariableConfig -> {
                this.dataType = "STRING";
                this.possibilities = stringVariableConfig.getPossibilities();
            }
            default -> throw new IllegalStateException("Unexpected configuration class: " + config.getClass().getName());
        }
    }

    @Override
    public ProcessVariable.Builder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public ProcessVariable.Builder withTaskDefinitionId(String taskDefinitionId) {
        this.taskDefinitionIds.add(taskDefinitionId);
        return this;
    }

    @Override
    public ProcessVariable.Builder generationType(String generationType) {
        this.generationType = GenerationType.valueOf(generationType);
        return this;
    }

    @Override
    public ProcessVariable.Builder modifier(Modifier modifier) {
        if (modifier == null) return this;
        this.modifiers.add(modifier);
        return this;
    }

    @Override
    public ProcessVariable.Builder modifiers(List<Modifier> modifiers) {
        if (modifiers == null) return this;
        this.modifiers.addAll(modifiers);
        return this;
    }

    @Override
    public ProcessVariable.Builder lowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
        return this;
    }

    @Override
    public ProcessVariable.Builder upperBound(double upperBound) {
        this.upperBound = upperBound;
        return this;
    }

    @Override
    public ProcessVariable.Builder mean(float mean) {
        this.mean = mean;
        return this;
    }

    @Override
    public ProcessVariable.Builder standardDeviation(float standardDeviation) {
        this.standardDeviation = standardDeviation;
        return this;
    }

    @Override
    public ProcessVariable.Builder chance(double chance) {
        this.chance = chance;
        return this;
    }

    @Override
    public ProcessVariable.Builder possibilities(Map<String, Double> possibilities) {
        this.possibilities = possibilities;
        return this;
    }

    @Override
    public ProcessVariable.Builder context(Context context) {
        this.context = context;
        return this;
    }

    @Override
    public ProcessVariable.Builder column(int column) {
        this.column = column;
        return this;
    }

    @Override
    public ProcessVariable build() {
        return switch (this.dataType.toUpperCase()) {
            case "NUMBER" -> new NumericVariable(this);
            case "BOOLEAN" -> new BooleanVariable(this);
            case "STRING" -> new StringVariable(this);
            default -> throw new IllegalStateException("Unexpected variable data type: " + this.dataType);
        };
    }
}
