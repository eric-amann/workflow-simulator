package org.uniko.camundaSimulator.engine.agent.schedule;

import org.uniko.camundaSimulator.engine.context.Modifier;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkSchedule {

    static WorkSchedule create() {
        return (new WorkScheduleBuilder()).build();
    }

    static WorkSchedule.Builder builder() {
        return new WorkScheduleBuilder();
    }

    Boolean isActive(LocalDateTime currentTime);

    interface Builder {

        WorkSchedule.Builder vacationDays(Integer vacationDays);

        WorkSchedule.Builder defaultWeekday(Schedule.WeekdayDefinition defaultWeekday);

        WorkSchedule.Builder weekdayDefinition(Schedule.WeekdayDefinition weekdayDefinition);

        WorkSchedule.Builder defaultMonth(Schedule.MonthDefinition defaultMonth);

        WorkSchedule.Builder monthDefinition(Schedule.MonthDefinition monthDefinition);

        WorkSchedule.Builder modifier(Modifier modifier);

        WorkSchedule.Builder modifiers(List<Modifier> modifiers);

        WorkSchedule build();
    }
}
