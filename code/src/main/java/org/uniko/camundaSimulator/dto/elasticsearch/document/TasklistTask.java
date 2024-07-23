package org.uniko.camundaSimulator.dto.elasticsearch.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

@Getter
@Setter
@Document(indexName = "tasklist-task-8.4.0_", writeTypeHint = WriteTypeHint.FALSE)
public class TasklistTask {

    public String id;
    public String tenantId;
    public Long key;
    public Integer partitionId;
    public String bpmnProcessId;
    public String processDefinitionId;
    public String flowNodeBpmnId;
    public String flowNodeInstanceId;
    public String processInstanceId;
    public String creationTime;
    public String completionTime;
    public String state;
    public String assignee;
    public Object candidateGroups;
    public Object candidateUsers;
    public Object formKey;
    public Object formId;
    public Object formVersion;
    public Boolean isFormEmbedded;
    public String followUpDate;
    public String dueDate;
}
