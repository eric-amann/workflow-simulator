package org.uniko.camundaSimulator.engine.agent.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.uniko.camundaSimulator.engine.context.Modifier;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public abstract class Schedule {

    protected WeekdayDefinition defaultWeekday;
    protected List<WeekdayDefinition> weekdayDefinitions;
    protected MonthDefinition defaultMonth;
    protected List<MonthDefinition> monthDefinitions;
    protected List<Modifier> modifiers;

    public Schedule(WeekdayDefinition defaultWeekday, List<WeekdayDefinition> weekdayDefinitions, MonthDefinition defaultMonth, List<MonthDefinition> monthDefinitions, List<Modifier> modifiers) {
        this.defaultWeekday = defaultWeekday;
        this.weekdayDefinitions = weekdayDefinitions;
        this.defaultMonth = defaultMonth;
        this.monthDefinitions = monthDefinitions;
        this.modifiers = modifiers;
    }

    public Boolean isActive(LocalDateTime currentTime) {
         return isMonthActive(currentTime) && isWeekdayActive(currentTime);
    }

    private boolean isMonthActive(LocalDateTime currentTime) {
        // Find matching month definition
        MonthDefinition monthDefinition = monthDefinitions.stream()
                .filter(md -> md.month.equals(currentTime.getMonth()))
                .findFirst()
                .orElse(this.defaultMonth);

        // Find possible modificator for inactive days
        Integer[] inactiveDays = monthDefinition.inactiveDays;
        inactiveDays = modifiers.stream()
                .filter(mod -> mod.getTarget().name().contains(currentTime.getMonth().name().toUpperCase()))
                .findFirst()
                .map(mod -> mod.apply(currentTime, new Integer[0]))
                .orElse(inactiveDays);

        // Check if current day is inactive day
        return Arrays.stream(inactiveDays).noneMatch(d -> d.equals(currentTime.getDayOfMonth()));
    }

    private boolean isWeekdayActive(LocalDateTime currentTime) {

        // Find matching weekday definition
        WeekdayDefinition weekdayDefinition = weekdayDefinitions.stream()
                .filter(wd -> wd.dayOfWeek.equals(currentTime.getDayOfWeek()))
                .findFirst()
                .orElse(this.defaultWeekday);

        // Find possible modifiers
        List<Modifier> mods = modifiers.stream()
                .filter(mod -> mod.getTarget().name().contains(currentTime.getDayOfWeek().name().toUpperCase()))
                .toList();

        // Apply modifiers
        LocalTime startTime = weekdayDefinition.getStartTime();
        Duration activeHours = weekdayDefinition.getActiveHours();
        for (Modifier mod : mods) {
            if(mod.getTarget().name().contains("START_TIME")) startTime = mod.apply(currentTime, startTime);
            else if(mod.getTarget().name().contains("ACTIVE_HOURS")) activeHours = mod.apply(currentTime, activeHours);
        }
        WeekdayDefinition modifiedDefinition = new WeekdayDefinition(activeHours, startTime);

        // Check if current time is active
        return !modifiedDefinition.startTime.isAfter(currentTime.toLocalTime())
                && !modifiedDefinition.getEndTime().isBefore(currentTime.toLocalTime())
                || modifiedDefinition.activeHours.compareTo(Duration.ofHours(24)) >= 0;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class WeekdayDefinition {
        protected DayOfWeek dayOfWeek;
        protected Duration activeHours;
        protected LocalTime startTime;

        public WeekdayDefinition(Duration activeHours, LocalTime startTime) {
            this.activeHours = activeHours;
            this.startTime = startTime;
        }

        public LocalTime getEndTime() {
            if (startTime.until(LocalTime.MAX, ChronoUnit.SECONDS) < activeHours.get(ChronoUnit.SECONDS)) {
                return LocalTime.MAX;
            }
            return startTime.plus(activeHours);
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class MonthDefinition {
        protected Month month;
        protected Integer[] inactiveDays;

        public MonthDefinition(Integer[] inactiveDays) {
            this.inactiveDays = inactiveDays;
        }
    }
}
