package org.uniko.camundaSimulator.dto.elasticsearch.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

@Getter
@Setter
@Document(indexName = "operate-event-8.3.0_", writeTypeHint = WriteTypeHint.FALSE)
public class OperateEvent {

    @Id
    private String id;
    private Long key;
    private Integer partitionId;
    private Long processDefinitionKey;
    private Long processInstanceKey;
    private String bpmnProcessId;
    private String flowNodeId;
    private Long flowNodeInstanceKey;
    private String eventSourceType;
    private String eventType;
    private String dateTime;
    private Metadata metadata;
    private String tenantId;
}
