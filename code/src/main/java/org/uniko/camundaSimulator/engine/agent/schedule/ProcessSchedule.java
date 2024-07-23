package org.uniko.camundaSimulator.engine.agent.schedule;

import org.uniko.camundaSimulator.engine.context.Modifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface ProcessSchedule {

    static ProcessSchedule create() {
        return (new ProcessScheduleBuilder()).build();
    }

    static ProcessSchedule.Builder builder() {
        return new ProcessScheduleBuilder();
    }

    LocalDateTime nextInstanceTrigger(LocalDateTime previousTrigger);

    interface Builder {

        ProcessSchedule.Builder interval(Duration interval);

        ProcessSchedule.Builder defaultWeekday(Schedule.WeekdayDefinition defaultWeekday);

        ProcessSchedule.Builder weekdayDefinition(Schedule.WeekdayDefinition weekdayDefinition);

        ProcessSchedule.Builder defaultMonth(Schedule.MonthDefinition defaultMonth);

        ProcessSchedule.Builder monthDefinition(Schedule.MonthDefinition monthDefinition);

        ProcessSchedule.Builder modifier(Modifier modifier);

        ProcessSchedule.Builder modifiers(List<Modifier> modifiers);

        ProcessSchedule build();
    }
}
