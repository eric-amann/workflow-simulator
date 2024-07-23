package org.uniko.camundaSimulator.dto.elasticsearch.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

import java.util.List;

@Getter
@Setter
@Document(indexName = "operate-list-view-8.3.0_", writeTypeHint = WriteTypeHint.FALSE)
public class OperateListView {

    private String id;
    private Long key;
    private Integer partitionId;
    private Long processDefinitionKey;
    private String processName;
    private Integer processVersion;
    private String bpmnProcessId;
    private String startDate;
    private String endDate;
    private String state;
    private List<String> batchOperationIds;
    private Long parentProcessInstanceKey;
    private Long parentFlowNodeInstanceKey;
    private String treePath;
    private Boolean incident;
    private String tenantId;
    private JoinRelation joinRelation;
    private Long processInstanceKey;
}
