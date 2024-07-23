package org.uniko.camundaSimulator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class Task {

    private String id;

    private String name;

    private String taskDefinitionId;

    private String processName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
    private OffsetDateTime creationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
    private OffsetDateTime completionDate;

    private String assignee;

    private TaskState taskState;

    private List<String> sortValues;

    private Boolean isFirst;

    private String formKey;

    private String formId;

    private Long formVersion;

    private Boolean isFormEmbedded;

    private String processDefinitionKey;

    private String processInstanceKey;

    private String tenantId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
    private OffsetDateTime dueDate;

    private OffsetDateTime followUpDate;

    private List<String> candidateGroups;

    private List<String> candidateUsers;

    private List<Variable> variables;
}
