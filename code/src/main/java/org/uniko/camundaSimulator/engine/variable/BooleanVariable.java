package org.uniko.camundaSimulator.engine.variable;

import lombok.Getter;
import lombok.Setter;
import org.uniko.camundaSimulator.engine.context.ContextApplicationTarget;
import org.uniko.camundaSimulator.engine.context.Modifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class BooleanVariable implements ProcessVariable {

    private List<String> taskDefinitionIds;
    private String name;
    private double chance;
    private List<Modifier> modifiers;


    public BooleanVariable(ProcessVariableBuilder builder) {
        this.name = builder.getName();
        this.taskDefinitionIds = builder.getTaskDefinitionIds();
        this.chance = builder.getChance();
        this.modifiers = builder.getModifiers();
    }

    public String getValue(LocalDateTime timestamp) {

        Random random = new Random();
        return Boolean.valueOf(random.nextDouble(1, 101) <= applyModificators(timestamp,chance,ContextApplicationTarget.CHANCE)).toString();
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
