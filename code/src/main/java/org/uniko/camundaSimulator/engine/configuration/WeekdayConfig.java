package org.uniko.camundaSimulator.engine.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeekdayConfig {

    private String[] weekday;
    //ISO 8601
    private Duration activeHours;
    private LocalTime startTime;
}
