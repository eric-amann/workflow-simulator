package org.uniko.camundaSimulator.engine.variable;

import lombok.Getter;
import lombok.Setter;
import org.uniko.camundaSimulator.engine.context.ContextApplicationTarget;
import org.uniko.camundaSimulator.engine.context.Modifier;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class NumericVariable implements ProcessVariable {

    private List<String> taskDefinitionIds;
    private String name;
    private GenerationType generationType;
    private double lowerBound;
    private double upperBound;
    private double mean;
    private double standardDeviation;
    private int decimalPlaces;
    private List<Modifier> modifiers;

    public NumericVariable(ProcessVariableBuilder builder) {
        this.name = builder.getName();
        this.taskDefinitionIds = builder.getTaskDefinitionIds();
        this.generationType = builder.getGenerationType();
        this.lowerBound = builder.getLowerBound();
        this.upperBound = builder.getUpperBound();
        this.mean = builder.getMean();
        this.standardDeviation = builder.getStandardDeviation();
        this.decimalPlaces = builder.getDecimalPlaces();
        this.modifiers = builder.getModifiers();
    }

    public String getValue(LocalDateTime timestamp) {

        Random random = new Random();
        return switch (generationType) {
            case RANDOM, RANDOM_UNIFORM -> roundValue(random.nextDouble(
                    applyModificators(timestamp,lowerBound,ContextApplicationTarget.LOWER_BOUND),
                    applyModificators(timestamp,upperBound,ContextApplicationTarget.UPPER_BOUND)
            ));
            case RANDOM_NORMAL_DISTRIBUTION -> roundValue(random.nextGaussian(
                    applyModificators(timestamp,mean,ContextApplicationTarget.MEAN),
                    applyModificators(timestamp,standardDeviation,ContextApplicationTarget.STANDARD_DEVIATION)
            ));
        };
    }

    private String roundValue(double unrounded) {
        return new BigDecimal(unrounded).setScale(decimalPlaces, RoundingMode.FLOOR).toString();
    }

    private double applyModificators(LocalDateTime timestamp, double value, ContextApplicationTarget target) {

        List<Modifier> mods = modifiers.stream()
                .filter(mod -> mod.getTarget().equals(target))
                .toList();

        for (Modifier mod : mods) {
            value = mod.apply(timestamp, value);
        }
        return value;
    }
}
