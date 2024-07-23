package org.uniko.camundaSimulator.engine.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkerConfig {

    private String id;
    private Integer amount;
    private String community;
    private String group;
    private String role;
    private Double baselinePerformance;
    private String scheduleId;
    private List<String> allowedTaskIds = new ArrayList<>();
    private Boolean completeTask;
}
