package org.uniko.camundaSimulator.engine.agent.schedule;

import lombok.Getter;
import org.springframework.util.Assert;
import org.uniko.camundaSimulator.engine.context.Modifier;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ProcessScheduleBuilder implements ProcessSchedule.Builder {

    private Duration interval = Duration.ofMinutes(10);
    private Schedule.WeekdayDefinition defaultWeekday;
    private List<Schedule.WeekdayDefinition> weekdayDefinitions = new ArrayList<>();
    private Schedule.MonthDefinition defaultMonth;
    private List<Schedule.MonthDefinition> monthDefinitions = new ArrayList<>();
    private final List<Modifier> modifiers = new ArrayList<>();

    @Override
    public ProcessSchedule.Builder interval(Duration interval) {
        Assert.notNull(interval, "Default interval must not be null");
        this.interval = interval;
        return this;
    }

    @Override
    public ProcessSchedule.Builder defaultWeekday(Schedule.WeekdayDefinition defaultWeekday) {
        Assert.notNull(defaultWeekday, "Default weekday definition must not be null");
        this.defaultWeekday = defaultWeekday;
        return this;
    }

    @Override
    public ProcessSchedule.Builder weekdayDefinition(Schedule.WeekdayDefinition weekdayDefinition) {
        this.initWeekdayDefinitions().add(weekdayDefinition);
        return this;
    }

    @Override
    public ProcessSchedule.Builder defaultMonth(Schedule.MonthDefinition defaultMonth) {
        Assert.notNull(defaultMonth, "Default month definition must not be null");
        this.defaultMonth = defaultMonth;
        return this;
    }

    @Override
    public ProcessSchedule.Builder monthDefinition(Schedule.MonthDefinition monthDefinition) {
        this.initMonthDefinitions().add(monthDefinition);
        return this;
    }

    @Override
    public ProcessSchedule.Builder modifier(Modifier modifier) {
        if (modifier == null) return this;
        modifiers.add(modifier);
        return this;
    }

    @Override
    public ProcessSchedule.Builder modifiers(List<Modifier> modifiers) {
        if (modifiers == null) return this;
        this.modifiers.addAll(modifiers);
        return this;
    }

    @Override
    public ProcessSchedule build() {
        defaultWeekday = defaultWeekday != null ? defaultWeekday : initDefaultWeekday();
        weekdayDefinitions = weekdayDefinitions != null ? weekdayDefinitions : initWeekdayDefinitions();
        defaultMonth = defaultMonth != null ? defaultMonth : initDefaultMonth();
        monthDefinitions = monthDefinitions != null ? monthDefinitions : this.initMonthDefinitions();
        return new ProcessScheduleImpl(this);
    }

    private Schedule.WeekdayDefinition initDefaultWeekday() {
        return new Schedule.WeekdayDefinition(Duration.ofHours(24), LocalTime.of(0, 0));
    }

    private Schedule.MonthDefinition initDefaultMonth() {
        return new Schedule.MonthDefinition(new Integer[0]);
    }

    private List<Schedule.WeekdayDefinition> initWeekdayDefinitions() {
        if (this.weekdayDefinitions == null) this.weekdayDefinitions = new ArrayList<>();
        return this.weekdayDefinitions;
    }

    private List<Schedule.MonthDefinition> initMonthDefinitions() {
        if (this.monthDefinitions == null) this.monthDefinitions = new ArrayList<>();
        return this.monthDefinitions;
    }
}
