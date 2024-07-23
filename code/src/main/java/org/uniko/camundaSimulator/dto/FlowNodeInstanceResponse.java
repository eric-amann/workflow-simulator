package org.uniko.camundaSimulator.dto;

import io.camunda.operate.model.FlowNodeInstance;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class FlowNodeInstanceResponse {

    private Set<FlowNodeInstance> items;
    private long[] sortValues;
    private Integer total;
}
