package org.uniko.camundaSimulator.dto.elasticsearch.document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Metadata {

    private String jobType;
    private Integer jobRetries;
    private String jobWorker;
    private Object jobDeadline;
    private Object jobCustomHeaders;
    private Long jobKey;
    private String incidentErrorType;
    private String incidentErrorMessage;
    private String messageName;
    private String correlationKey;
}
