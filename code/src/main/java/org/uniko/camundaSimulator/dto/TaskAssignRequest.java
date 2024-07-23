package org.uniko.camundaSimulator.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskAssignRequest {

    private String assignee;
    private Boolean allowOverrideAssignment;
}
