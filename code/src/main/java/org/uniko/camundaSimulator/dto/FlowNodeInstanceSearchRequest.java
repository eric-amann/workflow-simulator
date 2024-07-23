package org.uniko.camundaSimulator.dto;

import io.camunda.operate.search.FlowNodeInstanceFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FlowNodeInstanceSearchRequest {

    private FlowNodeInstanceFilter filter;
    private int size;
    private long[] searchAfter;
}
