package org.uniko.camundaSimulator.engine.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.uniko.camundaSimulator.engine.agent.schedule.*;
import org.uniko.camundaSimulator.engine.context.Modifier;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleConfig {

    private String id;
    private List<WeekdayConfig> weekdayDefinitions = new ArrayList<>();
    private List<MonthConfig> monthDefinitions = new ArrayList<>();

    public WorkSchedule buildWorkSchedule() {
        return buildSchedule(new WorkScheduleImpl(new WorkScheduleBuilder()));
    }

    public ProcessSchedule buildProcessSchedule(Duration interval, List<Modifier> modifiers) {
        ProcessScheduleBuilder builder = new ProcessScheduleBuilder();
        builder.interval(interval);
        builder.modifiers(modifiers);
        return buildSchedule(new ProcessScheduleImpl(builder));
    }

    private <T extends Schedule> T buildSchedule(T schedule) {

        Schedule.WeekdayDefinition defaultWeekday = weekdayDefinitions.stream()
                .filter(definition -> Arrays.asList(definition.getWeekday()).contains("0")
                        || Arrays.stream(definition.getWeekday()).anyMatch("default"::equalsIgnoreCase))
                .findFirst()
                .map(definition -> new Schedule.WeekdayDefinition(definition.getActiveHours(), definition.getStartTime()))
                .orElse(new Schedule.WeekdayDefinition(Duration.ofHours(24), LocalTime.MIDNIGHT));
        schedule.setDefaultWeekday(defaultWeekday);

        Schedule.MonthDefinition defaultMonth = monthDefinitions.stream()
                .filter(definition -> Arrays.asList(definition.getMonth()).contains("0")
                        || Arrays.stream(definition.getMonth()).anyMatch("default"::equalsIgnoreCase))
                .findFirst()
                .map(definition -> new Schedule.MonthDefinition(definition.getInactiveDays()))
                .orElse(new Schedule.MonthDefinition(new Integer[0]));
        schedule.setDefaultMonth(defaultMonth);

        weekdayDefinitions.stream()
                .filter(definition -> Arrays.stream(definition.getWeekday()).noneMatch("0"::equals)
                        && Arrays.stream(definition.getWeekday()).noneMatch("default"::equalsIgnoreCase))
                .forEach(definition -> {
                    DayOfWeek dayOfWeek;
                    for (String day : definition.getWeekday()) {
                        if (day.matches("-?\\d+")) dayOfWeek = DayOfWeek.of(Integer.parseInt(day));
                        else dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
                        schedule.getWeekdayDefinitions().add(new Schedule.WeekdayDefinition(dayOfWeek, definition.getActiveHours(), definition.getStartTime()));
                    }
                });

        monthDefinitions.stream()
                .filter(definition -> Arrays.stream(definition.getMonth()).noneMatch("0"::equals)
                        && Arrays.stream(definition.getMonth()).noneMatch("default"::equalsIgnoreCase))
                .forEach(definition -> {
                    Month month;
                    for (String m : definition.getMonth()) {
                        if (m.matches("-?\\d+")) month = Month.of(Integer.parseInt(m));
                        else month = Month.valueOf(m.toUpperCase());
                        schedule.getMonthDefinitions().add(new Schedule.MonthDefinition(month, definition.getInactiveDays()));
                    }
                });

        return schedule;
    }
}
