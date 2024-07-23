package org.uniko.camundaSimulator.engine.agent.schedule;

import org.uniko.camundaSimulator.engine.context.ContextApplicationTarget;
import org.uniko.camundaSimulator.engine.context.Modifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class ProcessScheduleImpl extends Schedule implements ProcessSchedule {

    private final Duration interval;

    public ProcessScheduleImpl(ProcessScheduleBuilder builder) {
        super(builder.getDefaultWeekday(), builder.getWeekdayDefinitions(), builder.getDefaultMonth(), builder.getMonthDefinitions(), builder.getModifiers());
        this.interval = builder.getInterval();
    }

    @Override
    public LocalDateTime nextInstanceTrigger(LocalDateTime previousTrigger) {
        do {
            previousTrigger = previousTrigger.plus(modifyInterval(previousTrigger,interval));
        } while (!super.isActive(previousTrigger));
        return previousTrigger;
    }

    private Duration modifyInterval(LocalDateTime timestamp, Duration interval) {
        List<Modifier> mods = modifiers.stream()
                .filter(mod -> mod.getTarget().equals(ContextApplicationTarget.INTERVAL))
                .toList();

        for (Modifier mod : mods) {
            interval = mod.apply(timestamp, interval);
        }
        return interval;
    }
}
