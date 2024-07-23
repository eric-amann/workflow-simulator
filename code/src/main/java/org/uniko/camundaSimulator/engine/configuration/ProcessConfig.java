package org.uniko.camundaSimulator.engine.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessConfig {

    private String id;
    private String filePath;
    //ISO 8601
    private Duration interval;
    private String scheduleId;
    private Integer startingInstances;
}
