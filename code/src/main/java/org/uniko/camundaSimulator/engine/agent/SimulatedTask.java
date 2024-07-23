package org.uniko.camundaSimulator.engine.agent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.uniko.camundaSimulator.engine.context.ContextApplicationTarget;
import org.uniko.camundaSimulator.engine.context.Modifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


@Setter
@AllArgsConstructor
public class SimulatedTask {

    @Getter
    private String taskId;
    private Duration processingTime;
    @Getter
    private List<Modifier> modifiers;

    public Duration getProcessingTime(LocalDateTime timestamp) {
        Duration processingTime = this.processingTime;
        List<Modifier> mods = modifiers.stream()
                .filter(mod -> mod.getTarget().equals(ContextApplicationTarget.PROCESSING_TIME))
                .toList();

        for (Modifier mod : mods) {
            processingTime = mod.apply(timestamp, processingTime);
        }
        return processingTime;
    }
}
