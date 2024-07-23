package org.uniko.camundaSimulator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskSearchRequest {

    private TaskState taskState;
    private Boolean assigned;
    private String assignee;
    private String taskDefinitionId;
    private String candidateGroup;
    private String candidateUser;
    private String processDefinitionKey;
    private String processInstanceKey;
    @NonNull
    private Integer pageSize;
    private DateFilter followUpDate;
    private DateFilter dueDate;
    private List<Variable> taskVariables;
    private List<String> tenantIds;
    private List<TaskOrderBy> sort;
    private List<String> searchAfter;
    private List<String> searchAfterOrEqual;
    private List<String> searchBefore;
    private List<String> searchBeforeOrEqual;
    private List<IncludeVariable> includeVariables;
}
