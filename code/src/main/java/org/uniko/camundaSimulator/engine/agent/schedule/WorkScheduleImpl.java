package org.uniko.camundaSimulator.engine.agent.schedule;

public class WorkScheduleImpl extends Schedule implements WorkSchedule {

    private final Integer vacationDays;

    public WorkScheduleImpl(WorkScheduleBuilder builder) {
        super(builder.getDefaultWeekday(), builder.getWeekdayDefinitions(), builder.getDefaultMonth(), builder.getMonthDefinitions(), builder.getModifiers());
        this.vacationDays = builder.getVacationDays();
    }
}
