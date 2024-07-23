package org.uniko.camundaSimulator.engine.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.uniko.camundaSimulator.engine.context.ContextApplicationTarget;
import org.uniko.camundaSimulator.engine.context.ContextApplicationType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModifierConfig {
    private String contextId;
    private int column;
    private String targetId;
    private String targetAttribute;
    private String modificationType;

    public ContextApplicationTarget getContextApplicationTarget() {
        return switch (targetAttribute) {
            // Worker
            case "name" -> ContextApplicationTarget.NAME;
            case "baselinePerformance" -> ContextApplicationTarget.PERFORMANCE;

            // Process
            case "interval" -> ContextApplicationTarget.INTERVAL;

            // Task
            case "processingTime" -> ContextApplicationTarget.PROCESSING_TIME;

            // Numeric variable
            case "lowerBound" -> ContextApplicationTarget.LOWER_BOUND;
            case "upperBound" -> ContextApplicationTarget.UPPER_BOUND;
            case "mean" -> ContextApplicationTarget.MEAN;
            case "standardDeviation" -> ContextApplicationTarget.STANDARD_DEVIATION;

            // Boolean variable
            case "chance" -> ContextApplicationTarget.CHANCE;

            // Schedule
            case "defaultStartTime" -> ContextApplicationTarget.DEFAULT_START_TIME;
            case "mondayStartTime" -> ContextApplicationTarget.MONDAY_START_TIME;
            case "tuesdayStartTime" -> ContextApplicationTarget.TUESDAY_START_TIME;
            case "wednesdayStartTime" -> ContextApplicationTarget.WEDNESDAY_START_TIME;
            case "thursdayStartTime" -> ContextApplicationTarget.THURSDAY_START_TIME;
            case "fridayStartTime" -> ContextApplicationTarget.FRIDAY_START_TIME;
            case "saturdayStartTime" -> ContextApplicationTarget.SATURDAY_START_TIME;
            case "sundayStartTime" -> ContextApplicationTarget.SUNDAY_START_TIME;

            case "defaultActiveHours" -> ContextApplicationTarget.DEFAULT_ACTIVE_HOURS;
            case "mondayActiveHours" -> ContextApplicationTarget.MONDAY_ACTIVE_HOURS;
            case "tuesdayActiveHours" -> ContextApplicationTarget.TUESDAY_ACTIVE_HOURS;
            case "wednesdayActiveHours" -> ContextApplicationTarget.WEDNESDAY_ACTIVE_HOURS;
            case "thursdayActiveHours" -> ContextApplicationTarget.THURSDAY_ACTIVE_HOURS;
            case "fridayActiveHours" -> ContextApplicationTarget.FRIDAY_ACTIVE_HOURS;
            case "saturdayActiveHours" -> ContextApplicationTarget.SATURDAY_ACTIVE_HOURS;
            case "sundayActiveHours" -> ContextApplicationTarget.SUNDAY_ACTIVE_HOURS;

            case "defaultInactiveDays" -> ContextApplicationTarget.DEFAULT_INACTIVE_DAYS;
            case "januaryInactiveDays" -> ContextApplicationTarget.JANUARY_INACTIVE_DAYS;
            case "februaryInactiveDays" -> ContextApplicationTarget.FEBRUARY_INACTIVE_DAYS;
            case "marchInactiveDays" -> ContextApplicationTarget.MARCH_INACTIVE_DAYS;
            case "aprilInactiveDays" -> ContextApplicationTarget.APRIL_INACTIVE_DAYS;
            case "mayInactiveDays" -> ContextApplicationTarget.MAY_INACTIVE_DAYS;
            case "juneInactiveDays" -> ContextApplicationTarget.JUNE_INACTIVE_DAYS;
            case "julyInactiveDays" -> ContextApplicationTarget.JULY_INACTIVE_DAYS;
            case "augustInactiveDays" -> ContextApplicationTarget.AUGUST_INACTIVE_DAYS;
            case "septemberInactiveDays" -> ContextApplicationTarget.SEPTEMBER_INACTIVE_DAYS;
            case "octoberInactiveDays" -> ContextApplicationTarget.OCTOBER_INACTIVE_DAYS;
            case "novemberInactiveDays" -> ContextApplicationTarget.NOVEMBER_INACTIVE_DAYS;
            case "decemberInactiveDays" -> ContextApplicationTarget.DECEMBER_INACTIVE_DAYS;
            default -> throw new IllegalStateException("Unexpected context application target: " + targetAttribute);
        };
    }

    public ContextApplicationType getContextApplicationType() {
        return ContextApplicationType.valueOf(modificationType);
    }
}
