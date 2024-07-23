package org.uniko.camundaSimulator.dto.elasticsearch.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

@Getter
@Setter
@Document(indexName = "operate-flownode-instance-8.3.1_", writeTypeHint = WriteTypeHint.FALSE)
public class OperateFlowNodeInstance {

    private String id;
    private Long key;
    private Integer partitionId;
    private String flowNodeId;
    private String startDate;
    private String endDate;
    private String state;
    private String type;
    private Long incidentKey;
    private Long processInstanceKey;
    private Long processDefinitionKey;
    private String bpmnProcessId;
    private String treePath;
    private Integer level;
    private Integer position;
    private Boolean incident;
    private String tenantId;
}
