package org.uniko.camundaSimulator.engine.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@Getter
@Setter
@AllArgsConstructor
public class Modifier {

    private Context context;
    private int column;
    private ContextApplicationType applicationType;
    private ContextApplicationTarget target;

    public double apply(LocalDateTime timestamp, double value) {
        try {
            return applyNumber(value, Double.parseDouble(context.getValue(timestamp, column).replaceAll(",", "."))).doubleValue();
        } catch (NullPointerException | NumberFormatException e) {
            return value;
        }
    }

    public int apply(LocalDateTime timestamp, int value) {
        try {
            return applyNumber(value, Double.parseDouble(context.getValue(timestamp, column).replaceAll(",", "."))).intValue();
        } catch (NullPointerException | NumberFormatException e) {
            return value;
        }
    }

    public float apply(LocalDateTime timestamp, float value) {
        try {
            return applyNumber(value, Double.parseDouble(context.getValue(timestamp, column).replaceAll(",", "."))).floatValue();
        } catch (NullPointerException | NumberFormatException e) {
            return value;
        }
    }

    public long apply(LocalDateTime timestamp, long value) {
        try {
            return applyNumber(value, Double.parseDouble(context.getValue(timestamp, column).replaceAll(",", "."))).longValue();
        } catch (NullPointerException | NumberFormatException e) {
            return value;
        }
    }

    public Number apply(LocalDateTime timestamp, Number value) {
        try {
            return applyNumber(value, Double.parseDouble(context.getValue(timestamp, column).replaceAll(",", ".")));
        } catch (NullPointerException | NumberFormatException e) {
            return value;
        }
    }

     public Number applyNumber(Number value, Number contextValue) {
        try {
            return switch (applicationType) {
                case MULTIPLY -> value.doubleValue() * contextValue.doubleValue();
                case DIVIDE -> value.doubleValue() / contextValue.doubleValue();
                case ADD -> value.doubleValue() + contextValue.doubleValue();
                case SUBTRACT -> value.doubleValue() - contextValue.doubleValue();
                case EXPONENTIATION -> Math.pow(value.doubleValue(), contextValue.doubleValue());
                case REPLACE -> contextValue;
            };
        } catch (NullPointerException | NumberFormatException e) {
            return value;
        }
    }

    public boolean apply(LocalDateTime timestamp, boolean value) {

        if (applicationType.equals(ContextApplicationType.REPLACE)) {
            return Boolean.parseBoolean(context.getValue(timestamp,column));
        } else {
            throw new IllegalStateException("Unexpected context application type: " + applicationType);
        }
    }

    public String apply(LocalDateTime timestamp, String value) {

        if (applicationType.equals(ContextApplicationType.REPLACE)) {
            return context.getValue(timestamp, column);
        } else {
            throw new IllegalStateException("Unexpected context application type: " + applicationType);
        }
    }

    public LocalDateTime apply(LocalDateTime timestamp, LocalDateTime value) {
        try {
            return switch (applicationType) {
                case ADD -> value.plus(Duration.parse(context.getValue(timestamp,column)));
                case SUBTRACT -> value.minus(Duration.parse(context.getValue(timestamp,column)));
                case REPLACE -> LocalDateTime.parse(context.getValue(timestamp,column));
                default -> throw new IllegalStateException("Unexpected context application type: " + applicationType);
            };
        } catch (NullPointerException | DateTimeParseException e) {
            return value;
        }
    }

    public LocalTime apply(LocalDateTime timestamp, LocalTime value) {
        try {
            return switch (applicationType) {
                case ADD -> value.plus(Duration.parse(context.getValue(timestamp,column)));
                case SUBTRACT -> value.minus(Duration.parse(context.getValue(timestamp,column)));
                case REPLACE -> LocalTime.parse(context.getValue(timestamp,column));
                default -> throw new IllegalStateException("Unexpected context application type: " + applicationType);
            };
        } catch (NullPointerException | DateTimeParseException e) {
            return value;
        }
    }

    public Duration apply(LocalDateTime timestamp, Duration value) {
        try {
            return switch (applicationType) {
                case MULTIPLY -> multiplyDuration(value, Double.parseDouble(context.getValue(timestamp, column).replaceAll(",", ".")));
                case DIVIDE -> divideDuration(value, Double.parseDouble(context.getValue(timestamp, column).replaceAll(",", ".")));
                case ADD -> value.plus(Duration.parse(context.getValue(timestamp, column)));
                case SUBTRACT -> value.minus(Duration.parse(context.getValue(timestamp, column)));
                case REPLACE -> Duration.parse(context.getValue(timestamp, column));
                default -> throw new IllegalStateException("Unexpected context application type: " + applicationType);
            };
        } catch (NullPointerException | DateTimeParseException e) {
            return value;
        }
    }

    private Duration multiplyDuration(Duration duration, Double factor) {
        Long seconds = duration.getSeconds();
        return Duration.ofSeconds (Math.round(seconds * factor));
    }

    private Duration divideDuration(Duration duration, Double factor) {
        Long seconds = duration.getSeconds();
        return Duration.ofSeconds (Math.round(seconds / factor));
    }

    public Integer[] apply(LocalDateTime timestamp, Integer[] value) {
        if (applicationType.equals(ContextApplicationType.REPLACE)) {
            String[] items = context.getValue(timestamp, column).replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
            Integer[] result = new Integer[items.length];

            for (int i = 0; i < items.length; i++) {
                try {
                    result[i] = Integer.parseInt(items[i]);
                } catch (NumberFormatException nfe) {

                };
            }
            return result;
        } else {
            throw new IllegalStateException("Unexpected context application type: " + applicationType);
        }
    }
}
